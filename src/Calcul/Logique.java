package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

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
public class Logique extends Observable{

	private InterfaceVisuelle affichage;
	private ArrayList<Lieu> lieux;
	private ArrayList<Agence> agences;
	public ArrayList<Agence> barycentres;
	private ArrayList<Trajet> trajets;
	
	private float distanceTotale;
	private float prixTotal;
	private int lieuTotal;
	
	public Logique(InterfaceVisuelle pAffichage)
	{
		affichage = pAffichage;
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		
    	agences = LireFichiers.LireAgence(filePath);
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
	
	public void trajetAuHasard() {
		
		int random;
		resetTrajets();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			Lieu l = lieux.get(random);
			
			if(!l.isAssocie()) {
				l.setAssocie(true);
			} 
			Trajet t = new Trajet(agence, l);
			l.getTrajets().add(t);
			agence.getTrajets().add(t);
			trajets.add(t);
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
			} 
			Trajet t = new Trajet(agence, best);
			best.getTrajets().add(t);
			agence.getTrajets().add(t);
			trajets.add(t);
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
				
				if(trajet.getLieu().getNbPersonneAssociees() > Commun.MAX_PERSONNE)
					System.out.println("Au dela de " + Commun.MAX_PERSONNE + " personnes pour le lieu " 
							+ trajet.getLieu().getNom() + " (" + trajet.getLieu().getNbPersonneAssociees() + ")");
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
	
	public ArrayList<Trajet> recuitSimule() {
		int temperature = 3;
		int nbIterations = 100;
		float distance;
		int agencePersonne;
		int lieuPersonne;
		
		trajetBarycentre();
		ArrayList<Trajet> meilleureSolution = trajets;
		float meilleurPrix = prixTotal;
		
		System.out.println("Prix première solution : " + meilleurPrix);
		
		for(int i = 0; i < nbIterations; i++) {
			//Tableau des lieux à supprimer (car lié avec le moins d'agences)
			Lieu [] lieuxASupprimer = new Lieu [temperature];
			for(int j = 0; j < temperature; j++) {
				lieuxASupprimer[j] = lieux.get(j);
			}
			
			//Remplissage du tableau avec les lieux avec le moins d'agences
			for(Lieu lieu : lieux) {
				if(lieu.isAssocie()) {
					int nbAgence = lieu.getTrajets().size();
					int maxAgences = lieuxASupprimer[0].getTrajets().size();
					int indexMax = 0;

					for(int j = 1; j < temperature; j++) {
						if(lieuxASupprimer[j].getTrajets().size() > maxAgences) {
							maxAgences = lieuxASupprimer[j].getTrajets().size();
							indexMax = j;
						}
					}

					if(nbAgence < maxAgences){
						lieuxASupprimer[indexMax] = lieu;
					}
				}
			}
			
			//Déplacement des liaisons des agences
			for(int j = 0; j < temperature; j++) {
				ArrayList<Trajet> temp = lieuxASupprimer[j].getTrajets();
				lieuxASupprimer[j].setAssocie(false);
				for(Trajet t : temp) {
					trajets.remove(t);
					
					distance = t.getDistanceKm();
					agencePersonne = t.getAgence().getNbpersonnes();
					lieuPersonne = t.getLieu().getNbPersonneAssociees();
					
					distanceTotale -= distance;
					prixTotal -= Commun.PRIX_LIEU;
					prixTotal -= distance*agencePersonne;
					
					Agence agence = t.getAgence();
					agence.getTrajets().clear();
					
					Lieu lieuPlusProche = lieuLePlusProche(agence);
					Trajet trajet = new Trajet(agence,lieuPlusProche);
					lieuPlusProche.getTrajets().add(trajet);
					agence.getTrajets().add(trajet);
					trajets.add(trajet);
					
					distance = trajet.getDistanceKm();
					agencePersonne = trajet.getAgence().getNbpersonnes();
					lieuPersonne = trajet.getLieu().getNbPersonneAssociees();
					
					distanceTotale += distance;
					prixTotal += distance*agencePersonne;
					trajet.getLieu().setNbPersonneAssociees(lieuPersonne+agencePersonne);
					
					if(trajet.getLieu().getNbPersonneAssociees() > Commun.MAX_PERSONNE)
						System.out.println("Au dela de " + Commun.MAX_PERSONNE + " personnes pour le lieu " 
								+ trajet.getLieu().getNom() + " (" + trajet.getLieu().getNbPersonneAssociees() + ")");
				}
			}
			
			if(prixTotal < meilleurPrix){
				meilleureSolution = trajets;
				meilleurPrix = prixTotal;
			}
			setChanged();
			notifyObservers();
		}
		
		return meilleureSolution;
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
	
	private Lieu lieuLePlusProche(Agence agence) {
		Trajet temp = new Trajet();
		temp.setAgence(agence);
		Lieu best = null;
		float min = Float.MAX_VALUE;
		
		for (Lieu lieu : lieux) {
			if(lieu.isAssocie()) {
				temp.setLieu(lieu);
				if(best == null || temp.getDistanceKm() < min) {
					best = lieu;
					min = temp.getDistanceKm();
				}
			}
		}
		return best;
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

	public void setAgences(ArrayList<Agence> listes) {
		resetTrajets();
		this.agences = listes;
	}
}
