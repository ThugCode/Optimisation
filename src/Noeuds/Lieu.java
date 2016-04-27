package Noeuds;

import java.util.ArrayList;

import Arcs.Trajet;

public class Lieu extends Noeud implements Comparable<Lieu> {

	private int nbPersonneAssociees;
	private boolean associe;
	private boolean retour;
	private ArrayList<Trajet> trajets;
	
	public Lieu() {
		super();
		
		this.nbPersonneAssociees = 0;
		this.associe = false;
		this.retour = true;
		this.trajets = new ArrayList<Trajet>();
	}
	
	public Lieu(int id, String nom, String codepostal, float longitude, float latitude) {
		super(id, nom, codepostal, longitude, latitude);
		
		this.nbPersonneAssociees = 0;
		this.associe = false;
		this.trajets = new ArrayList<Trajet>();
	}
	
	public void reset() {
		this.setAssocie(false);
		this.setNbPersonneAssociees(0);
	}
	
	public float distanceAgences(Lieu autre) {
		float xaxb = this.getLongitude()-autre.getLongitude();
		float yayb = this.getLatitude()-autre.getLatitude();
		return (float) Math.sqrt(Math.pow(xaxb, 2) + Math.pow(yayb, 2));
	}
	
	public int getNbPersonneAssociees() {
		return nbPersonneAssociees;
	}
	public void setNbPersonneAssociees(int nbPersonneAssociees) {
		this.nbPersonneAssociees = nbPersonneAssociees;
	}
	public boolean isAssocie() {
		return associe;
	}
	public void setAssocie(boolean associe) {
		this.associe = associe;
	}
	
	public boolean isRetour() {
		return retour;
	}

	public void setRetour(boolean retour) {
		this.retour = retour;
	}
	
	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
	public void setTrajets(ArrayList<Trajet> trajets) {
		this.trajets = trajets;
	}	

	public String toString() {
		return "Lieu "+this.nom+
				" ("+this.id+") cp : "+this.codepostal+
				" longitude : "+this.longitude+
				" latitude : "+this.latitude;
	}
	
	@Override
	public int compareTo(Lieu lieu) {
		if(this.nbPersonneAssociees > lieu.getNbPersonneAssociees())
			return 1;
		else if(this.nbPersonneAssociees < lieu.getNbPersonneAssociees())
			return -1;
		
		return 0;
	}
}
