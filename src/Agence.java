
public class Agence extends Emplacement {

	private int nbpersonnes1;
	
	public Agence() {
		super();
	}
	
	public Agence(int id, String nom, String codepostal, float longitude, float latitude, int nbpersonnes1) {
		super(id, nom, codepostal, longitude, latitude);
		this.nbpersonnes1 = nbpersonnes1;
	}
	
	public int getNbpersonnes1() {
		return nbpersonnes1;
	}
	public void setNbpersonnes1(int nbpersonnes1) {
		this.nbpersonnes1 = nbpersonnes1;
	}
	
	public String toString() {
		return "Agence "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}
}
