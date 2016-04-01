package Noeuds;

import java.util.ArrayList;

import Arcs.Trajet;

public class Noeud {
	
	protected int id;
	protected String nom;
	protected String codepostal;
	protected float longitude;
	protected float latitude;
	protected ArrayList<Trajet> trajets;
	
	public Noeud() {
		super();
		this.trajets = new ArrayList<Trajet>();
	}
	
	public Noeud(int id, String nom, String codepostal, float longitude, float latitude) {
		super();
		this.id = id;
		this.nom = nom;
		this.codepostal = codepostal;
		this.longitude = longitude;
		this.latitude = latitude;
		this.trajets = new ArrayList<Trajet>();
	}

	public int getLongitudeForMap(double facteur) {
		return (int) (longitude*42*facteur+(235*facteur));
	}
	
	public int getLatitudeForMap(double facteur) {
		return (int) (latitude*62*facteur-(2610*facteur));
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodepostal() {
		return codepostal;
	}
	public void setCodepostal(String codepostal) {
		this.codepostal = codepostal;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
	public void setTrajets(ArrayList<Trajet> trajets) {
		this.trajets = trajets;
	}	
}
