package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
	private Solution meilleureSolutionAlgogene;
	
	private Random rand;
	private boolean rafraichirCarte;
	private int temperature;
	private int iterations;
	private float distanceTotale;
	private float prixTotal;
	private int lieuTotal;
	
	/**
	 * Constructeur
	 * @param pAffichage
	 */
	public Logique(InterfaceVisuelle pAffichage)
	{
		affichage = pAffichage;
		
		//Lecture du fichier 100 agences par défaut
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		pathFichier = filePath;
		
		//Enregistrer les agences et les lieux
		lireAgences(30);
    	lieux = LireFichiers.LireLieuxPossible();
    	
    	//Initialisation des variables pour le recuit simulé
    	rand = new Random();
    	setRafraichirCarte(true);
    	setTemperature(100000);
    	setIterations(500);
    	
    	//Initialisation des trajets
    	resetTrajets();
	}
	
	/**
	 * Enregistrer les agences et leurs voisins
	 * @param nombreVoisins
	 */
	public void lireAgences(int nombreVoisins) {
		this.setAgences(LireFichiers.LireAgence(pathFichier, nombreVoisins));
	}
	
	/**
	 * Réinitialisation des variables et listes
	 */
	public void resetTrajets() {
		trajets = new ArrayList<Trajet>();
		barycentres = new ArrayList<Agence>();
		
		distanceTotale = 0;
    	prixTotal = 0;
    	lieuTotal = 0;
	}
	
	/**
	 * Vérifie si le lieu n'a pas plus de 60 personnes associées
	 * @param lieu
	 */
	private void checkLieuPersonne(Lieu lieu) {
		if(lieu.getNbPersonneAssociees() > Commun.MAX_PERSONNE)
			System.out.println("Au dela de " + Commun.MAX_PERSONNE + " personnes pour le lieu " 
					+ lieu.getNom() + " (" + lieu.getNbPersonneAssociees() + ")");
	}
	
	/**
	 * Fonction de test des trajets au hasard
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
	 * Fonction de test des trajets au plus près
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
		
		//Initialisation des variables de sauvegarde 
		//de la meilleure solution rencontrée
		int bestLieu = 0;
		float bestDistance = 0;
		float bestPrix = Float.MAX_VALUE;
		ArrayList<Trajet> bestSolution = null;
		ArrayList<Agence> bestBary = null;
		
		//Mélange de la liste des agences
		Collections.shuffle(agences);
		
		for(int i = 1; i <= iterations; i++) {
			
			//Construction des trajets par barycentre
			//Retour de l'index de l'agence à partir de laquelle
			//le récursif a effectué les groupes d'agence
			int indexSwap = trajetBarycentre();
			
			//Affichage du prix
			System.out.println("Prix solution "+i+" : " + prixTotal);
			
			//Si le prix est inférieur, on adopte la solution en temps que
			//meilleure solution et nouvelle solution de départ du recursif
			//et on enregistre les variables utiles
			if(prixTotal < bestPrix) {
				bestSolution = trajets;
				bestPrix = prixTotal;
				bestBary = barycentres;
				bestDistance = distanceTotale;
				bestLieu = lieuTotal;
				System.out.println("Solution adoptée (Meilleure)");
			} 
			else //Sinon, on effectue le calcul avec la température
			{
				//Si le random est supérieur on rejete la solution et on reprend la solution précédente
				if(rand.nextFloat() > Math.exp( -( (prixTotal - bestPrix) / temperature ) ) ) {
					Collections.swap(agences, 0, indexSwap);
					System.out.println("Solution rejetée");
				} 
				else //Sinon on adopte la solution comme nouvelle solution de départ du recursif
				{
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
		
		//Initialisation compteur de lieux ou groupes d'agences
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
		
		//Changement d'agence de départ pour le récursif
		//pour donne des résutlats toujours différents.
		//On inverse la première agence avec une agence au hasard dans la liste
		//C'est ici que l'on prend un voisin de la solution précédente.
		int indexSwap = rand.nextInt(agences.size());
		Collections.swap(agences, 0, indexSwap);
		
		//Parcours de toutes les agences pour leur trouver un groupe
		//en fonction de leurs voisins en récursif et ainsi avoir des "cercles" d'agences.
		for (Agence agence : agences) {
			if(agence.getGroupe() == -1) {
				listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				courant++;
			}
		}
		
		float[] coord;						//Coordonnees du barycentre
		float min;							//Plus petite distance
		Lieu lieuLePlusPres = null;			//Lieu le plus près de l'agence barycentre
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
			//Ajout du lieu à la solution et au prix
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
				
				//Calcul de la distance entre chaque agence et le lieu
				//et le nombre de personnes associées.
				distance = trajet.getDistanceKm();
				agencePersonne = trajet.getAgence().getNbpersonnes();
				lieuPersonne = trajet.getLieu().getNbPersonneAssociees();
				
				//Modification de la distance totale, du prix total et du nombre de personne dans le lieu
				distanceTotale += distance*agencePersonne;
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
		
		meilleureSolutionAlgogene = new Solution();
		Random r = new Random();
		Solution solution;
		
		//Liste correspondant aux solutions d'un génération
		List<Solution> generation = new ArrayList<Solution>();
		
		calculNbLieuxMin();
		
		//Génération aléatoire des premières solutions
		while(generation.size() < Commun.NB_SOLUTION_ALGOGENE) {
			BitSet bitSet = new BitSet(lieux.size());
			int nbIteration = r.nextInt(nbLieuxMin + r.nextInt(100));

			for(int j = 0; j < nbIteration; j++) {
				bitSet.set(r.nextInt(lieux.size()));
			}
			
			if(bitSet.cardinality() >= nbLieuxMin)
			{
				solution = new Solution(bitSet,new Float(0));
				generation.add(solution);
			}
		}
		
		recursifAlgogene(generation, 1);
	}
	
	private void recursifAlgogene(List<Solution> generation, int iteration) {
		List<Solution> solutions = new ArrayList<Solution>();
		Random r = new Random();
		float prix;
		float sommeInverse = 0;
		
		for(Solution solution : generation) {
			//Calcul du prix de la solution
			prix = calculPrixSolution(solution.getLieux());
			solution.setPrix(prix);
			float valeur = 1/prix;
			solution.setPropa(valeur);
			sommeInverse += valeur;

			if(prix < meilleureSolutionAlgogene.getPrix() && solution.getLieux().cardinality() >= nbLieuxMin)
				meilleureSolutionAlgogene = solution;
		}		

		//Tri dans l'ordre croissant du prix des solutions
		Collections.sort(generation,new Comparator<Solution>() {
			public int compare(Solution b1, Solution b2){
				return b1.getPrix().compareTo(b2.getPrix());
			}
		});
		
		
		//Calcul de la propabilité de choisir une solution
		for(Solution solution : generation) {
			float valeur = solution.getPropa();
			valeur = valeur/sommeInverse;
			solution.setPropa(valeur);
		}
		
		System.out.println("Génération :" + iteration + " " + generation.get(0));
		
		//Affichage de la génération
//		System.out.println("Génération :" + iteration);
//		for(Solution s : generation) {
//			System.out.println(s.toString());
//		}
						
		float propa;
		float propCumul;
		int index;
		
		//Reproduction avec selection aléatoire en fonction des propabilités
		for(int i = 0; i < Commun.NB_SOLUTION_ALGOGENE; i++) {
			propa = r.nextFloat();
			propCumul = 0;
			index = 0;
			
			for(int j = 0; j < generation.size()/2; j++) {
				propCumul += generation.get(j).getPropa();
				if(propa <= propCumul){
					break;
				} else {
					index++;
				}
			}
						
			Solution solution = generation.get(index).clone();
			
			solutions.add(solution);
		}
		
		
//		System.out.println("Solution reproduitent :" + iteration);
//		for(Solution s : solutions) {
//			System.out.println(s.toString());
//		}
		
		Solution solution;
		Solution copie;
		Solution temp;
		ArrayList<Solution> copies = new ArrayList<Solution>();	
		int indexCroissement;
		
		//Croisements et mutations en fonction d'un random
		for(int k = 0; k < solutions.size(); k++) {
			solution = solutions.get(k);
			solutions.remove(solution);
			
				//Croisement
				indexCroissement = r.nextInt(solution.getLieux().size());
				temp = solutions.get(r.nextInt(solutions.size()));
				solutions.remove(temp);
				copie = solution.clone();
				
				for(int l = indexCroissement + 1; l < lieux.size(); l++) {
					solution.getLieux().set(l, temp.getLieux().get(l));
					temp.getLieux().set(l,copie.getLieux().get(l));
				}
				
				
				if(r.nextFloat() < 0.01){
					//Mutation
					solution.getLieux().flip(r.nextInt(solution.getLieux().size()));
				}
				
				copies.add(solution);
				copies.add(temp);
		}

		//Rappel de la fonction avec la nouvelle generation
		if(iteration < 200) {
			recursifAlgogene(copies, iteration + 1);		
		} else {
			System.out.println("Meilleure solution : " + meilleureSolutionAlgogene);
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
		
		//Si il y a moins de lieu que le nombre minimum alors la solution n'est pas valable
		if(solution.cardinality() < this.nbLieuxMin - this.nbLieuxMin/2) {
			return Float.MAX_VALUE;
		}
		
		//Parcours des lieux et associations des agences les plus proches
		for (int i = solution.nextSetBit(0); i >= 0; i = solution.nextSetBit(i+1)) {
		     if (i == Integer.MAX_VALUE || i > lieux.size()) {
		         break; // or (i+1) would overflow
		     }
		     
			nonPlein = true;
			nbPersonnes = 0;
			prix += Commun.PRIX_LIEU;
			courant = lieux.get(i);
			courant.setNbPersonneAssociees(0);
			
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
				
				if(best != null){
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
				else {
					nonPlein = false;
				}
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
	public GroupeAgence getAgences() {
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
