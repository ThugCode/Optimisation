import java.util.ArrayList;

public class Agence extends Emplacement {

	private int nbpersonnes1;
	private ArrayList<Lien> voisins;
	
	public Agence() {
		super();
		this.voisins = new ArrayList<Lien>();
	}
	
	public Agence(int id, String nom, String codepostal, float longitude, float latitude, int nbpersonnes1) {
		super(id, nom, codepostal, longitude, latitude);
		this.nbpersonnes1 = nbpersonnes1;
		this.voisins = new ArrayList<Lien>();
	}
	
	public int getNbpersonnes1() {
		return nbpersonnes1;
	}
	public void setNbpersonnes1(int nbpersonnes1) {
		this.nbpersonnes1 = nbpersonnes1;
	}
	public ArrayList<Lien> getVoisins() {
		return voisins;
	}
	public void setVoisins(ArrayList<Lien> voisins) {
		this.voisins = voisins;
	}
	
	public String toString() {
		return "Agence "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}
}
