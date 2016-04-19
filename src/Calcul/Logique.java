package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
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
	private ArrayList<Lieu> lieux;
	private GroupeAgence agences;
	private ArrayList<Agence> barycentres;
	private ArrayList<Trajet> trajets;
	private int nbLieuxMin;
	
	private float distanceTotale;
	private float prixTotal;
	private int lieuTotal;
	ArrayList<Trajet> meilleureSolution;
	
	public Logique(InterfaceVisuelle pAffichage)
	{
		affichage = pAffichage;
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		
    	agences = LireFichiers.LireAgence(filePath, 10);
    	lieux = LireFichiers.LireLieuxPossible();
    	
    	resetTrajets();
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
			prixTotal += trajet.getDistanceKm()*agence.getNbpersonnes();
			best.setNbPersonneAssociees(best.getNbPersonneAssociees()+agence.getNbpersonnes());
		}
	}
	
	/**
	 * Recherche de trajet par barycentre
	 * -> Création de groupes d'agences par voisinage
	 * -> Recherche du barycentre de chaque groupe
	 * -> Recherche du lieu le plus proche du barycentre
	 * -> Association de chaque agence avec le lieu
	 */
	public void trajetBarycentre() {
		
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
		Collections.shuffle(agences);
		
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
				prixTotal += distance*agencePersonne;
				trajet.getLieu().setNbPersonneAssociees(lieuPersonne+agencePersonne);
				
				checkLieuPersonne(trajet.getLieu());
			}
		}
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
	
	
	public void temperatureBarycentre() {
		
		trajetBarycentre();
		affichage.update();
		
		float bestPrix = prixTotal;
		ArrayList<Trajet> bestSolution = trajets;
		
		System.out.println("Prix solution 1 : " + bestPrix);
		
		for(int i = 0; i < 100; i++) {
			
			trajetBarycentre();
			
			if(prixTotal < bestPrix){
				bestSolution = trajets;
				bestPrix = prixTotal;
			}
			
			System.out.println("Prix solution "+(i+2)+" : " + prixTotal);

			affichage.update();
			
			try { Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		JOptionPane.showMessageDialog(null, "La meilleure solution donne un prix de : "+bestPrix+" €");
		
		trajets = bestSolution;
		prixTotal = bestPrix;
		
		affichage.update();
	}
	
	
	
	public void recuitBarycentre() {
		
		float distance;
		int agencePersonne;
		int nbIterations = 100;
		
		trajetBarycentre();
		affichage.update();
		
		Agence agenceADeplace = null;
		float meilleurPrix = prixTotal;
		
		meilleureSolution = trajets;
		
		System.out.println("Prix solution 1 : " + meilleurPrix);
		
		for(int i = 0; i < nbIterations; i++) {
			
			agenceADeplace = agences.get(new Random().nextInt(agences.size()));
			
			Trajet trajetEnTrop = agenceADeplace.getTrajets().get(0);
			
			trajets.remove(trajetEnTrop);
			
			distance = trajetEnTrop.getDistanceKm();
			agencePersonne = trajetEnTrop.getAgence().getNbpersonnes();
			
			distanceTotale -= distance;
			prixTotal -= distance*agencePersonne;
			
			Agence agence = trajetEnTrop.getAgence();
			agence.getTrajets().clear();
			
			recursifBarycentre(agence);
			
			if(prixTotal < meilleurPrix){
				meilleureSolution = trajets;
				meilleurPrix = prixTotal;
			}
			
		
			System.out.println("Prix solution "+(i+2)+" : " + prixTotal);

			affichage.update();
			
			try { Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	private void recursifBarycentre(Agence agenceDeplacee) {
		
		Lieu lieuPlusProche = lieuAssociePlusProche(agenceDeplacee);
		Trajet trajet = new Trajet(agenceDeplacee,lieuPlusProche);
		distanceTotale += trajet.getDistanceKm();
		prixTotal += trajet.getDistanceKm()*agenceDeplacee.getNbpersonnes();
		lieuPlusProche.setNbPersonneAssociees(trajet.getLieu().getNbPersonneAssociees()+agenceDeplacee.getNbpersonnes());
		
		lieuPlusProche.getTrajets().add(trajet);
		agenceDeplacee.getTrajets().add(trajet);
		trajets.add(trajet);
		//System.out.println("Ajout Lieu : "+trajet.getLieu().getNom() + " " + trajet.getLieu().isRetour()+ " \nAgence : "+trajet.getAgence().getNom());
		
		if(lieuPlusProche.getNbPersonneAssociees() > 60) {
			
			int difference;
			int differenceMin = Integer.MAX_VALUE;
			Trajet trajetADeplacer = null;
			
			for(Trajet trajetPossible : lieuPlusProche.getTrajets()) {
				
				difference = trajetPossible.getAgence().getNbpersonnes() - agenceDeplacee.getNbpersonnes();
				//System.out.println(difference);
				if(difference >= 0 && difference < differenceMin ) {
					differenceMin = difference;
					trajetADeplacer = trajetPossible;
				}
			}
			//System.out.println("Supprime Lieu : "+trajetADeplacer.getLieu().getNom() + " \nAgence : "+trajetADeplacer.getAgence().getNom());
			trajets.remove(trajetADeplacer);
			distanceTotale -= trajetADeplacer.getDistanceKm();
			prixTotal -= trajetADeplacer.getDistanceKm()*trajetADeplacer.getAgence().getNbpersonnes();
			lieuPlusProche.setNbPersonneAssociees(lieuPlusProche.getNbPersonneAssociees() - trajetADeplacer.getAgence().getNbpersonnes());
		
			Agence agence = trajetADeplacer.getAgence();
			agence.getTrajets().clear();
			
			affichage.update();
			
			recursifBarycentre(agence);
		}
		
		
		
		checkLieuPersonne(trajet.getLieu());
	}

	public void recuitSimule() {
		

		Thread t1 = new Thread(new Runnable() {
		     public void run() {
		     
		float distance;
		int nbAgenceMin;
		int agencePersonne;
		int nbIterations = 100;
		
		trajetAuPlusPres();
		affichage.update();
		
		Lieu lieuASupprimer = null;
		float meilleurPrix = prixTotal;
		
		meilleureSolution = trajets;
		
		System.out.println("Prix solution 1 : " + meilleurPrix);
		
		for(int i = 0; i < nbIterations; i++) {
			
			nbAgenceMin = Integer.MAX_VALUE;
			
			for(Lieu lieu : lieux) {
				if(lieu.isAssocie() 
				&& (lieu.getTrajets().size() < nbAgenceMin)){
					lieuASupprimer = lieu;
					nbAgenceMin = lieu.getTrajets().size();
				}
			}
			
			ArrayList<Trajet> temp = lieuASupprimer.getTrajets();
			lieuASupprimer.reset();
			prixTotal -= Commun.PRIX_LIEU;
			lieuTotal--;
			
			for(Trajet trajetEnTrop : temp) {
				trajets.remove(trajetEnTrop);
				
				distance = trajetEnTrop.getDistanceKm();
				agencePersonne = trajetEnTrop.getAgence().getNbpersonnes();
				
				distanceTotale -= distance;
				prixTotal -= distance*agencePersonne;
				
				Agence agence = trajetEnTrop.getAgence();
				agence.getTrajets().clear();
				
				recursifRecuit(agence, 0);
			}
			
			if(prixTotal < meilleurPrix){
				meilleureSolution = trajets;
				meilleurPrix = prixTotal;
			}
			
			System.out.println("Prix solution "+(i+2)+" : " + prixTotal);
			
			affichage.update();
			
			try { Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		
		     }
		});  
		t1.start();
	}
	
	private void recursifRecuit(Agence agenceDeplacee, int level) {
		
		Lieu lieuPlusProche = lieuAssociePlusProche(agenceDeplacee);
		Trajet trajet = new Trajet(agenceDeplacee,lieuPlusProche);
		distanceTotale += trajet.getDistanceKm();
		prixTotal += trajet.getDistanceKm()*agenceDeplacee.getNbpersonnes();
		lieuPlusProche.setNbPersonneAssociees(trajet.getLieu().getNbPersonneAssociees()+agenceDeplacee.getNbpersonnes());
		
		
		if(lieuPlusProche.getNbPersonneAssociees() > 60) {
			
			int difference;
			int differenceMin = Integer.MAX_VALUE;
			Trajet trajetADeplacer = null;
			
			if(lieuPlusProche.getTrajets().size() <= 0) {
				System.out.println("trajets lieuPlusProche vide");
			}
			System.out.println("-------------------");
			for(Trajet trajetPossible : lieuPlusProche.getTrajets()) {
				
				difference = trajetPossible.getAgence().getNbpersonnes() - agenceDeplacee.getNbpersonnes();
				System.out.println(difference);
				if(difference >= 0 && difference < differenceMin ) {
					differenceMin = difference;
					trajetADeplacer = trajetPossible;
				}
			}
			
			if(trajetADeplacer == null) {
				System.out.println("trajetADeplacer null");
			}
		
			trajets.remove(trajetADeplacer);
			distanceTotale -= trajetADeplacer.getDistanceKm();
			prixTotal -= trajetADeplacer.getDistanceKm()*trajetADeplacer.getAgence().getNbpersonnes();
			lieuPlusProche.setNbPersonneAssociees(lieuPlusProche.getNbPersonneAssociees() - trajetADeplacer.getAgence().getNbpersonnes());
		
			Agence agence = trajetADeplacer.getAgence();
			agence.getTrajets().clear();
			
			System.out.println("LEVEL :" +level);
			
			affichage.update();
			
			recursifRecuit(agence, level+1);
		}
		
		lieuPlusProche.getTrajets().add(trajet);
		agenceDeplacee.getTrajets().add(trajet);
		trajets.add(trajet);
		
		checkLieuPersonne(trajet.getLieu());
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
		
		for(Entry<BitSet, Float> b : generation.entrySet()) {
			System.out.println(b.toString());
		}
		
		recursifAlgogene(generation);
	}
	
	private void recursifAlgogene(HashMap<BitSet,Float> generation) {
		
		
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
	
	private Lieu lieuAssociePlusProche(Agence agence) {
		Trajet temp = new Trajet();
		temp.setAgence(agence);
		Lieu best = null;
		float min = Float.MAX_VALUE;
		
		for (Lieu lieu : lieux) {
			if(lieu.isAssocie() && lieu.isRetour()) {
				temp.setLieu(lieu);
				if(best == null || temp.getDistanceKm() < min) {
					best = lieu;
					min = temp.getDistanceKm();
				}
			} else {
				//System.out.println("Lieu interdit : "+lieu.getNom());
				lieu.setRetour(true);
			}
		}
		//System.out.println("-------");
		best.setRetour(false);
		return best;
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
					prix += temp.getDistanceKm()*best.getNbpersonnes()*0.8;
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

	public void setNbLieuxMin(int nbLieuxMin) {
		this.nbLieuxMin = nbLieuxMin;
	}

	public void setAgences(GroupeAgence listes) {
		resetTrajets();
		this.agences = listes;
	}
}
