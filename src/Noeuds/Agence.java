package Noeuds;

import java.util.ArrayList;

import Arcs.Lien;
import Arcs.Trajet;

public class Agence extends Noeud implements Comparable<Agence> {

	private int nbpersonnes;
	private ArrayList<Lien> voisins;
	private int groupe;
	private Trajet trajet;
	
	public Agence() {
		super();
		this.voisins = new ArrayList<Lien>();
		this.groupe = -1;
		this.trajet = null;
	}
	
	public Agence(int id, String nom, String codepostal, float longitude, float latitude, int nbpersonnes1) {
		super(id, nom, codepostal, longitude, latitude);
		this.nbpersonnes = nbpersonnes1;
		this.voisins = new ArrayList<Lien>();
		this.groupe = -1;
		this.trajet = null;
	}
	
	public int getNbpersonnes() {
		return nbpersonnes;
	}
	public void setNbpersonnes(int nbpersonnes1) {
		this.nbpersonnes = nbpersonnes1;
	}
	public ArrayList<Lien> getVoisins() {
		return voisins;
	}
	public void setVoisins(ArrayList<Lien> voisins) {
		this.voisins = voisins;
	}
	public int getGroupe() {
		return groupe;
	}
	public void setGroupe(int groupe) {
		this.groupe = groupe;
	}

	public Trajet getTrajet() {
		return trajet;
	}

	public void setTrajet(Trajet trajet) {
		this.trajet = trajet;
	}

	public String toString() {
		return "Agence "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}

	@Override
	public int compareTo(Agence agence) {
		if(this.nbpersonnes > agence.getNbpersonnes())
			return 1;
		else if(this.nbpersonnes < agence.getNbpersonnes())
			return -1;
		
		return 0;
	}
}
