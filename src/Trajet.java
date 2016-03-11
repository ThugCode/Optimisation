
public class Trajet {
	
	private int id;
	private Agence agence;
	private Lieu lieu;
	private float distanceKm;
	
	public Trajet() {
		
	}
	
	public Trajet(Agence agence, Lieu lieu) {
		this.agence = agence;
		this.lieu = lieu;
	}

	public float getDistance() {
		
		float xaxb = this.agence.getLongitude()-this.lieu.getLongitude();
		float yayb = this.agence.getLatitude()-this.lieu.getLatitude();
		return (float) Math.sqrt(Math.pow(xaxb, 2) + Math.pow(yayb, 2));
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Agence getAgence() {
		return agence;
	}

	public void setAgence(Agence agence) {
		this.agence = agence;
	}

	public Lieu getLieu() {
		return lieu;
	}

	public void setLieu(Lieu lieu) {
		this.lieu = lieu;
	}

	public float getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(float distanceKm) {
		this.distanceKm = distanceKm;
	}
}
