package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import Affichage.InterfaceVisuelle;
import Arcs.Lien;
import Arcs.Trajet;
import Commun.Commun;
import Commun.LireFichiers;
import Noeuds.Agence;
import Noeuds.Lieu;

/*
 * Classe d'application des algorithmes
 */
public class Logique {

	private InterfaceVisuelle affichage;
	private ArrayList<Lieu> lieux;
	private ArrayList<Agence> agences;
	private HashMap<Lieu, ArrayList<Trajet>> trajets;
	private int[] personnesGroupe;
	
	public Logique(InterfaceVisuelle pAffichage)
	{
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		
    	affichage = pAffichage;
    	
    	lieux = LireFichiers.LireLieuxPossible();
    	agences = LireFichiers.LireAgence(filePath);
    	trajets = new ArrayList<Trajet>();
	}
	
	public void trajetAuHasard() {
		
		int random;
		trajets = new HashMap<Lieu, ArrayList<Trajet>>();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			Lieu l = lieux.get(random);
			
			if(trajets.containsKey(l)) {
				trajets.get(l).add(new Trajet(agence,l));
			}
			else {
				ArrayList<Trajet> tmp = new ArrayList<Trajet>();	
				tmp.add(new Trajet(agence,l));
				trajets.put(l,tmp);
			}
		}
	}

	public void trajetAuPlusPres() {
		
		Lieu best;
		trajets = new HashMap<Lieu, ArrayList<Trajet>>();
		
		for (Agence agence : agences) {
			best = lieuLePlusProche(agence);
			if(trajets.containsKey(best)) {
				trajets.get(best).add(new Trajet(agence,best));
			}
			else {
				ArrayList<Trajet> tmp = new ArrayList<Trajet>();	
				tmp.add(new Trajet(agence,best));
				trajets.put(best,tmp);
			}
		}
	}
	
	public void trajetBarycentre() {
		
		trajets = new HashMap<Lieu, ArrayList<Trajet>>();
		for (Agence agence : agences) {
			agence.setGroupe(-1);
		}
		ArrayList<ArrayList<Agence>> listeGroupes = new ArrayList<ArrayList<Agence>>();
		
		int courant = 0;
		personnesGroupe = new int[agences.size()];
		
		Collections.shuffle(agences);
		for (Agence agence : agences) {
			if(agence.getGroupe() == -1) {
				recursifVoisin(listeGroupes, agence, courant);
				courant++;
			}
		}
		
		Lieu best;
		Agence barycentre = new Agence();
		float min;
		Trajet temp = new Trajet();
		
		for (ArrayList<Agence> groupe : listeGroupes) {
			float[] coord = getBarycentre(groupe);
			barycentre.setLatitude(coord[0]);
			barycentre.setLongitude(coord[1]);
			
			temp.setAgence(barycentre);
			best = null;
			min = Float.MAX_VALUE;
			for (Lieu lieu : lieux) {
				temp.setLieu(lieu);
				if(best == null || temp.getDistanceKm() < min) {
					best = lieu;
					min = temp.getDistanceKm();
				}
			}
			
			for (Agence agen : groupe) {
				if(trajets.containsKey(best)) {
					trajets.get(best).add(new Trajet(agen,best));
				}
				else {
					ArrayList<Trajet> tmp = new ArrayList<Trajet>();	
					tmp.add(new Trajet(agen,best));
					trajets.put(best,tmp);
				}
			}
		}
	}
	
	private ArrayList<ArrayList<Agence>> recursifVoisin(ArrayList<ArrayList<Agence>> listeGroupes, Agence agence, int courant ) {
		Agence voisinage;
		ArrayList<Agence> groupeTemp = new ArrayList<Agence>();
		
		if(personnesGroupe[courant]+agence.getNbpersonnes() <= Commun.MAX_PERSONNE && agence.getGroupe()==-1) {
			listeGroupes.add(courant, groupeTemp);
			groupeTemp.add(agence);
			agence.setGroupe(courant);
			personnesGroupe[courant] = agence.getNbpersonnes();
			
			for (Lien liens : agence.getVoisins()) {
				voisinage = liens.getVoisin(agence);
				if(personnesGroupe[courant]+voisinage.getNbpersonnes() <= Commun.MAX_PERSONNE && voisinage.getGroupe()==-1) {
					voisinage.setGroupe(courant);
					groupeTemp.add(voisinage);
					personnesGroupe[courant] += voisinage.getNbpersonnes();
					listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				}
			}
		}
		
		return listeGroupes;
	}
	
	public void recuitSimule() {
		int temperature = 3;
		int nbIterations = 100;
		this.trajetBarycentre();
		
		for(int i = 0; i < nbIterations; i++) {
			Lieu [] lieux = new Lieu [temperature];
			Iterator<Entry<Lieu, ArrayList<Trajet>>> entries = this.trajets.entrySet().iterator();
			for(int j = 0; j < temperature; j++) {
				Entry e = (Entry) entries.next();
				lieux[j] = (Lieu) e.getKey();
			}
			for(Entry<Lieu, ArrayList<Trajet>> entry : this.trajets.entrySet()) {
				int nbAgence = entry.getValue().size();
				int maxAgences = this.trajets.get(lieux[0]).size();
				int indexMax = 0;
				
				for(int j = 1; j < temperature; j++) {
					if(this.trajets.get(lieux[j]).size() < maxAgences) {
						maxAgences = this.trajets.get(lieux[j]).size();
						indexMax = j;
					}
				}
				
				if(nbAgence < maxAgences){
					lieux[indexMax] = entry.getKey();
				}
			}
			
			for(int j = 0; j < temperature; j++) {
				ArrayList<Trajet> temp = this.trajets.get(lieux[j]);
				this.trajets.remove(lieux[j]);
				for(Trajet t : temp) {
					Agence agence = t.getAgence();
					Lieu lieuPlusProche = this.lieuLePlusProche(agence);
					this.trajets.get(lieuPlusProche).add(new Trajet(agence,lieuPlusProche));
				}
			}
			
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
	
	private Lieu lieuLePlusProche(Agence agence) {
		Trajet temp = new Trajet();
		temp.setAgence(agence);
		Lieu best = null;
		float min = Float.MAX_VALUE;
		
		for (Lieu lieu : this.lieux) {
			temp.setLieu(lieu);
			if(best == null || temp.getDistanceKm() < min) {
				best = lieu;
				min = temp.getDistanceKm();
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
	public HashMap<Lieu, ArrayList<Trajet>> getTrajets() {
		return trajets;
	}
	
	public void setAgences(ArrayList<Agence> listes) {
		this.agences = listes;
	}
}
