package Noeuds;
public class Lieu extends Noeud {

	private int nbPersonneAssociees;
	private boolean associe;
	private boolean retour;
	
	public Lieu() {
		super();
		
		this.nbPersonneAssociees = 0;
		this.associe = false;
		this.retour = true;
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
	
	public boolean isRetour() {
		return retour;
	}

	public void setRetour(boolean retour) {
		this.retour = retour;
	}

	public String toString() {
		return "Lieu "+this.nom+
				" ("+this.id+") cp : "+this.codepostal+
				" longitude : "+this.longitude+
				" latitude : "+this.latitude;
	}
}
