package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;

import Affichage.InterfaceVisuelle;
import Arcs.Lien;
import Arcs.Trajet;
import Commun.Commun;
import Commun.LireFichiers;
import Noeuds.Agence;
import Noeuds.GroupeAgence;
import Noeuds.Lieu;

/*
 * Classe d'application des algorithmes
 */
public class Logique extends Thread {

	private InterfaceVisuelle affichage;
	private String pathFichier;
	private ArrayList<Lieu> lieux;
	private GroupeAgence agences;
	private ArrayList<Agence> barycentres;
	private ArrayList<Trajet> trajets;
	private int nbLieuxMin;
	
	private float distanceTotale;
	private float prixTotal;
	private int lieuTotal;
	
	private Random rand;
	private boolean rafraichirCarte;
	private int temperature;
	private int iterations;
	
	public Logique(InterfaceVisuelle pAffichage)
	{
		affichage = pAffichage;
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		pathFichier = filePath;
		
		lireAgences(20);
    	lieux = LireFichiers.LireLieuxPossible();
    	
    	rand = new Random();
    	setRafraichirCarte(true);
    	setTemperature(1000);
    	setIterations(500);
    	
    	resetTrajets();
	}
	
	public void lireAgences(int nombreVoisins) {
		this.setAgences(LireFichiers.LireAgence(pathFichier, nombreVoisins));
	}
	
	public void resetTrajets() {
		trajets = new ArrayList<Trajet>();
		barycentres = new ArrayList<Agence>();
		
		distanceTotale = 0;
    	prixTotal = 0;
    	lieuTotal = 0;
	}
	

	private void checkLieuPersonne(Lieu lieu) {
		if(lieu.getNbPersonneAssociees() > Commun.MAX_PERSONNE)
			System.out.println("Au dela de " + Commun.MAX_PERSONNE + " personnes pour le lieu " 
					+ lieu.getNom() + " (" + lieu.getNbPersonneAssociees() + ")");
	}
	
	/**
	 * @deprecated
	 */
	public void trajetAuHasard() {
		
		int random;
		resetTrajets();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			Lieu lieu = lieux.get(random);
			
			lieu.setAssocie(true);
			
			Trajet trajet = new Trajet(agence, lieu);
			lieu.getTrajets().add(trajet);
			agence.getTrajets().add(trajet);
			trajets.add(trajet);
		}
	}

	/**
	 * @deprecated
	 */
	public void trajetAuPlusPres() {
		
		resetTrajets();
		Lieu best;
		float min;
		Trajet temp = new Trajet();
		
		for (Agence agence : agences) {
			temp.setAgence(agence);
			best = null;
			min = Float.MAX_VALUE;
			for (Lieu lieu : lieux) {
				temp.setLieu(lieu);
				if(best == null || temp.getDistanceKm() < min) {
					best = lieu;
					min = temp.getDistanceKm();
				}
			}
			
			if(!best.isAssocie()) {
				best.setAssocie(true);
				prixTotal += Commun.PRIX_LIEU;
				lieuTotal ++;
			}
			
			Trajet trajet = new Trajet(agence, best);
			best.getTrajets().add(trajet);
			agence.getTrajets().add(trajet);
			trajets.add(trajet);
			
			distanceTotale += trajet.getDistanceKm();
			prixTotal += trajet.getDistanceKm()*agence.getNbpersonnes()*Commun.PRIX_TRAJET;
			best.setNbPersonneAssociees(best.getNbPersonneAssociees()+agence.getNbpersonnes());
		}
	}
	
	/**
	 * Lancement du recuit simulé sur les barycentres.
	 */
	public void recuitSimuleBarycentre() {
		
		float bestPrix = Float.MAX_VALUE;
		float bestDistance = 0;
		int bestLieu = 0;
		ArrayList<Trajet> bestSolution = null;
		ArrayList<Agence> bestBary = null;
		
		Collections.shuffle(agences);
		
		for(int i = 1; i <= iterations; i++) {
			
			int indexSwap = trajetBarycentre();
			
			System.out.println("Prix solution "+i+" : " + prixTotal);
			
			if(prixTotal < bestPrix){
				bestSolution = trajets;
				bestPrix = prixTotal;
				bestBary = barycentres;
				bestDistance = distanceTotale;
				bestLieu = lieuTotal;
				System.out.println("Solution adoptée (Meilleure)");
			} else {
				if(rand.nextFloat() > Math.exp( -( (prixTotal - bestPrix) / temperature ) ) ) {
					Collections.swap(agences, 0, indexSwap);
					System.out.println("Solution rejetée");
				} else {
					System.out.println("Solution adoptée (Moins bonne)");
				}
			}
			
			if(rafraichirCarte) {
				//Rafraichissement de la carte
				affichage.update();
				//Attente avant d'effectuer une itération
				try { Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		
		//Affichage du meilleur résultat trouvé
		//Mise à jour des champs avec le meilleur résultat trouvé
		JOptionPane.showMessageDialog(null, "La meilleure solution donne un prix de : "+bestPrix+" €");
		trajets = bestSolution;
		prixTotal = bestPrix;
		barycentres = bestBary;
		distanceTotale = bestDistance;
		lieuTotal = bestLieu;
		affichage.update();
	}
	
	/**
	 * Recherche de trajet par barycentre
	 * -> Création de groupes d'agences par voisinage
	 * -> Recherche du barycentre de chaque groupe
	 * -> Recherche du lieu le plus proche du barycentre
	 * -> Association de chaque agence avec le lieu
	 */
	public int trajetBarycentre() {
		
		//Initialisation compteur lieux ou groupe d'agence
		int courant = 0;
		//Initialisation de la liste des groupes d'agences
		ArrayList<GroupeAgence> listeGroupes = new ArrayList<GroupeAgence>();
		//Réinitialisation des trajets
		resetTrajets();
		//Suppression des groupes pour toutes les agences
		for (Agence agence : agences) {
			agence.setGroupe(-1);
		}
		//Désassociation des lieux
		for (Lieu lieu : lieux) {
			lieu.reset();
		}
		
		//Mélange de la liste des agences pour que le récursif
		//donne des résutlats toujours différents
		int indexSwap = rand.nextInt(agences.size());
		Collections.swap(agences, 0, indexSwap);
		
		//Parcours de toutes les agences pour leur trouver un groupe
		//en fonction de leurs voisins en récursif
		for (Agence agence : agences) {
			if(agence.getGroupe() == -1) {
				listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				courant++;
			}
		}
		
		float[] coord;						//Coordonnees du barycentre
		float min;							//Plus petite distance
		Lieu lieuLePlusPres = null;			//Lieu le plus pres de l'agence barycentre
		Agence barycentre = new Agence();	//Barycentre de toutes les agences du groupe
		Trajet temp = new Trajet();			//Trajet entre lieux/barycentre pour calculer les distances
		
		//Recherche du lieu le plus proche du barycentre 
		//des agences dans chaque groupe
		for (GroupeAgence groupe : listeGroupes) {
			
			//Calcul du barycentre du groupe
			coord = getBarycentre(groupe);
			barycentre = new Agence();
			barycentre.setLatitude(coord[0]);
			barycentre.setLongitude(coord[1]);
			temp.setAgence(barycentre);
			
			barycentres.add(barycentre);
			
			//Recherche du lieu le plus proche
			min = Float.MAX_VALUE;
			for (Lieu lieu : lieux) {
				if(!lieu.isAssocie()) {
					temp.setLieu(lieu);
					if(temp.getDistanceKm() < min) {
						lieuLePlusPres = lieu;
						min = temp.getDistanceKm();
					}
				}
			}
			lieuLePlusPres.setAssocie(true);
			prixTotal += Commun.PRIX_LIEU;
			lieuTotal ++;
			
			int agencePersonne = 0;
			int lieuPersonne = 0;
			float distance = 0;
			
			//Pour toutes les agences du groupe,
			//on créer un trajet avec le lieu le plus proche
			for (Agence agence : groupe) {
				Trajet trajet = new Trajet(agence, lieuLePlusPres);
				lieuLePlusPres.getTrajets().add(trajet);
				agence.getTrajets().add(trajet);
				trajets.add(trajet);
				
				distance = trajet.getDistanceKm();
				agencePersonne = trajet.getAgence().getNbpersonnes();
				lieuPersonne = trajet.getLieu().getNbPersonneAssociees();
				
				distanceTotale += distance;
				prixTotal += distance*agencePersonne*Commun.PRIX_TRAJET;
				trajet.getLieu().setNbPersonneAssociees(lieuPersonne+agencePersonne);
				
				checkLieuPersonne(trajet.getLieu());
			}
		}
		
		return indexSwap;
	}
	
	/**
	 * Recherche récursive de voisin pour contruire les groupes d'agences
	 * Construction de "cercle" de voisin
	 * Maximisation des 60 personnes dans chaque groupe
	 * @param listeGroupes
	 * @param agence
	 * @param courant
	 * @return listeGroupes
	 */
	private ArrayList<GroupeAgence> recursifVoisin(ArrayList<GroupeAgence> listeGroupes, Agence agence, int courant) {
		
		Agence agenceVoisine;
		GroupeAgence groupeTemp;
		
		//Récupération du groupe à l'index courant
		if(listeGroupes.size() > courant)
			groupeTemp = listeGroupes.get(courant);
		else {
			groupeTemp = new GroupeAgence();
			listeGroupes.add(courant, groupeTemp);
		}
		
		//Ajout de l'agence dans le groupe si l'agence n'est pas déjà dans un groupe 
		//et que le nombre de personne du groupe ne dépasse pas 60
		if(groupeTemp.getNombrePersonne()+agence.getNbpersonnes() <= Commun.MAX_PERSONNE && agence.getGroupe()==-1) {
			
			agence.setGroupe(courant);
			groupeTemp.add(agence);
			groupeTemp.getSetNombrePersonne(agence.getNbpersonnes());
			
			//Pour chaque agence voisine, on regarde les voisins
			for (Lien liens : agence.getVoisins()) {
				agenceVoisine = liens.getVoisin(agence);
				
				//Si l'agence voisine est conforme, on l'ajoute au groupe
				if(groupeTemp.getNombrePersonne()+agenceVoisine.getNbpersonnes() <= Commun.MAX_PERSONNE 
						&& agenceVoisine.getGroupe()==-1) {
					
					agenceVoisine.setGroupe(courant);
					groupeTemp.add(agenceVoisine);
					groupeTemp.getSetNombrePersonne(agenceVoisine.getNbpersonnes());
					
					//Appel récursif des voisins
					listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				}
			}
		}
		
		return listeGroupes;
	}
	
	
	
	
	
	
	
	
	
	
	public void algogene() {
		
		Random r = new Random();
		float prix;
		
		//Liste correspondant aux solutions d'un génération
		HashMap<BitSet,Float> generation = new HashMap<BitSet,Float>();
		
		calculNbLieuxMin();
		
		//Génération aléatoire des premières solutions
		while(generation.size() < 5) {
			BitSet solution = new BitSet(lieux.size());
			int nbIteration = r.nextInt(nbLieuxMin + r.nextInt(2));

			for(int j = 0; j < nbIteration; j++) {
				solution.set(r.nextInt(lieux.size()));
			}
			if(solution.cardinality() >= nbLieuxMin)
			{
				prix = calculPrixSolution(solution);
				generation.put(solution,prix);
			}
		}
		
		recursifAlgogene(generation, 1);
	}
	
	private void recursifAlgogene(HashMap<BitSet,Float> generation, int iteration) {
		HashMap<BitSet,Float> solutions = new HashMap<BitSet,Float>();
<<<<<<< HEAD
		List<BitSet> keys;
=======
>>>>>>> origin/master
		Random r = new Random();
		float prix;
		int index;
		int sommeInverse = 0;
		int i = 0;
		BitSet temp;
		
		System.out.println("Génération :" + iteration);
		for(Entry<BitSet, Float> b : generation.entrySet()) {
			System.out.println(b.toString());
		}

		List<BitSet> cles = new ArrayList<BitSet>(generation.keySet());
		
		Collections.sort(cles,new Comparator<BitSet>() {
			public int compare(BitSet b1, BitSet b2){
				return generation.get(b1).compareTo(generation.get(b2));
			}
		});
		
		for(BitSet bitSet : cles) {
			float valeur = generation.get(bitSet);
			valeur = 1/valeur;
			sommeInverse += valeur;
			generation.put(bitSet, valeur);
		}
		
		//Reproduction avec selection aléatoire en fonction des poids
		solutions = generation;
<<<<<<< HEAD
		
		float propa = r.nextFloat();
		int j = 0;
		float propCumul = 0;
		
		while(j < cles.size() && propa > propCumul) {
			propCumul += generation.get(cles.get(j));
			j++;
		}
		
		solutions.put(cles.get(j-1), new Float(0.0));
		
		keys = new ArrayList<BitSet>(solutions.keySet());
=======
		//TODO

		ArrayList<BitSet> keys = new ArrayList<BitSet>(solutions.keySet());
>>>>>>> origin/master

		//Croisements ou mutations en fonction d'un random
		for(Entry<BitSet,Float> solution : solutions.entrySet()) {
			if(r.nextFloat() < 0.1){
				//Mutation
				solution.getKey().flip(r.nextInt(solution.getKey().size()));
			}
			else {
				//Croisement
				index = r.nextInt(solution.getKey().size());
				if(i + 1 < keys.size()){
					temp = (BitSet) keys.get(i + 1);
				} else {
					temp = (BitSet) keys.get(0);
				}
				for(int k = index; k < solution.getKey().size(); k++) {
					solution.getKey().set(k, temp.get(k));
				}
			}

			//Calcul du prix de la solution
			prix = calculPrixSolution(solution.getKey());
			solution.setValue(prix);
			i++;
		}

		//Rappel de la fonction avec la nouvelle generation
		if(iteration < 10) {
			recursifAlgogene(solutions, iteration + 1);		
		}
	}

	private static float[] getBarycentre(ArrayList<Agence> agences) {
		
		float numerateurX = 0;
		float denominateurX = 0;
		float numerateurY = 0;
		float denominateurY = 0;
		
		for(Agence agence : agences) {
			numerateurX += agence.getNbpersonnes() * agence.getLatitude();
			denominateurX += agence.getNbpersonnes();
			numerateurY += agence.getNbpersonnes() * agence.getLongitude();
			denominateurY += agence.getNbpersonnes();
		}
		
		float x = numerateurX / denominateurX;
		float y = numerateurY / denominateurY;
		return new float[] { x, y };
	}
	
	
	private float calculPrixSolution(BitSet solution) {
		//Copie de la liste des agences afin de pouvoir en supprimer
		ArrayList<Agence> agencesTmp = new ArrayList<Agence>(agences); 
		Agence best;
		Lieu courant;
		int nbPersonnes;
		float min;
		float prix = 0;
		boolean nonPlein;
		
		Trajet temp = new Trajet();
		
		//Parcours des lieux et associations des agences les plus proches
		for (int i = solution.nextSetBit(0); i >= 0; i = solution.nextSetBit(i+1)) {
			nonPlein = true;
			nbPersonnes = 0;
			prix += Commun.PRIX_LIEU;
			courant = lieux.get(i);
			
			while(nonPlein) {
				temp.setLieu(courant);
				best = null;
				min = Float.MAX_VALUE;
				
				//Determination de l'agence la plus proche
				for(Agence agence : agencesTmp) {
					temp.setAgence(agence);
					if(best == null || temp.getDistanceKm() < min) {
						best = agence;
						min = temp.getDistanceKm();
					}
				}
				
				nbPersonnes = courant.getNbPersonneAssociees() + best.getNbpersonnes();
				
				//S'il reste de la place on associe l'agence sinon on passe au lieu suivant
				if(nbPersonnes < 60) {
					agencesTmp.remove(best);
					temp.setAgence(best);
					courant.setNbPersonneAssociees(nbPersonnes);
					prix += temp.getDistanceKm()*best.getNbpersonnes()*Commun.PRIX_TRAJET;
				}
				else {
					nonPlein = false;
				}
				
			}
		     if (i == Integer.MAX_VALUE) {
		         break; // or (i+1) would overflow
		     }
		 }
		
		return prix;
	}
	
	private void calculNbLieuxMin() {
		int nbPersonnes = 0;
    	for(Agence agence : agences) {
    		nbPersonnes += agence.getNbpersonnes();
    	}
    	
    	nbLieuxMin = nbPersonnes / 60;
    	if(nbPersonnes % 60 != 0)
    		nbLieuxMin += 1;
    	
    	System.out.println("Nb Personnes : " + nbPersonnes + " Lieux Min : "+ nbLieuxMin);
	}
	
	public InterfaceVisuelle getAffichage() {
		return affichage;
	}
	public ArrayList<Lieu> getLieux() {
		return lieux;
	}
	public ArrayList<Agence> getAgences() {
		return agences;
	}
	public ArrayList<Agence> getBarycentres() {
		return barycentres;
	}

	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
	
	public float getDistanceTotale() {
		return distanceTotale;
	}

	public float getPrixTotal() {
		return prixTotal;
	}

	public int getLieuTotal() {
		return lieuTotal;
	}

	public int getNbLieuxMin() {
		return nbLieuxMin;
	}

	public boolean isRafraichirCarte() {
		return rafraichirCarte;
	}

	public int getTemperature() {
		return temperature;
	}

	public int getIterations() {
		return iterations;
	}

	public String getPathFichier() {
		return pathFichier;
	}

	public void setPathFichier(String pathFichier) {
		this.pathFichier = pathFichier;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public void setNbLieuxMin(int nbLieuxMin) {
		this.nbLieuxMin = nbLieuxMin;
	}

	public void setAgences(GroupeAgence listes) {
		resetTrajets();
		this.agences = listes;
	}
	
	public void setRafraichirCarte(boolean rafraichirCarte) {
		this.rafraichirCarte = rafraichirCarte;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
}
