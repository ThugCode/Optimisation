package Noeuds;

import java.util.ArrayList;

import Arcs.Lien;

public class Agence extends Noeud {

	private int nbpersonnes;
	private ArrayList<Lien> voisins;
	private int groupe;
	
	public Agence() {
		super();
		this.voisins = new ArrayList<Lien>();
		this.groupe = -1;
	}
	
	public Agence(int id, String nom, String codepostal, float longitude, float latitude, int nbpersonnes1) {
		super(id, nom, codepostal, longitude, latitude);
		this.nbpersonnes = nbpersonnes1;
		this.voisins = new ArrayList<Lien>();
		this.groupe = -1;
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

	public String toString() {
		return "Agence "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}
}
