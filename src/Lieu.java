
public class Lieu extends Emplacement {

	private int nbPersonneAssociees;
	private boolean associe;
	
	public Lieu() {
		super();
	}
	
	public Lieu(int id, String nom, String codepostal, float longitude, float latitude) {
		super(id, nom, codepostal, longitude, latitude);
		
		this.nbPersonneAssociees = 0;
		this.associe = false;
	}
	
	public void reset() {
		this.setAssocie(false);
		this.setNbPersonneAssociees(0);
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

	public String toString() {
		return "Lieu "+this.nom+
				" ("+this.id+") cp : "+this.codepostal+
				" longitude : "+this.longitude+
				" latitude : "+this.latitude;
	}
}
