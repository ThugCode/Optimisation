package Arcs;
import Noeuds.Agence;
import Noeuds.Lieu;

/*
 * Arc entre une agence et un lieu
 */
public class Trajet {
	
	private Agence agence;
	private Lieu lieu;
	
	public Trajet() {}
	
	public Trajet(Agence agence, Lieu lieu) {
		this.agence = agence;
		this.lieu = lieu;
	}

	/*
	 * Retourne la distance entre les latitudes et longitudes de l'agence et du lieu
	 */
	public float getDistance() {
		
		float xaxb = this.agence.getLongitude()-this.lieu.getLongitude();
		float yayb = this.agence.getLatitude()-this.lieu.getLatitude();
		return (float) Math.sqrt(Math.pow(xaxb, 2) + Math.pow(yayb, 2));
	}
	
	/*
	 * Retourne la distance en kilometre entre l'agence et le lieu
	 */
	public float getDistanceKm() {
		
		double R = 6371;
		double lat1Rad = Math.toRadians(lieu.getLatitude());
		double lat2Rad = Math.toRadians(agence.getLatitude());
		double alf1 = Math.toRadians(agence.getLatitude() - lieu.getLatitude());
		double alf2 = Math.toRadians(agence.getLongitude() - lieu.getLongitude());
		
		double a = Math.sin(alf1/2) * Math.sin(alf1/2) +
					Math.cos(lat1Rad) * Math.cos(lat2Rad) *
					Math.sin(alf2/2) * Math.sin(alf2/2);
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		return (float) (R * c);
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
}
