package Arcs;
import Noeuds.Agence;

/*
 * Arc entre deux agences
 */
public class Lien implements Comparable<Object> {
	
	private Agence agence1;
	private Agence agence2;
	
	public Lien() {}
	
	public Lien(Agence agence1, Agence agence2) {
		this.agence1 = agence1;
		this.agence2 = agence2;
	}

	/*
	 * Retourne l'agence voisin de l'agence en parametre dans le lien
	 */
	public Agence getVoisin(Agence agence) {
		
		if(agence == agence1)
			return agence2;
		return agence1;
	}
	
	/*
	 * Retourne la distance entre les latitudes et longitudes
	 */
	public float getDistance() {
		
		float xaxb = this.agence1.getLongitude()-this.agence2.getLongitude();
		float yayb = this.agence1.getLatitude()-this.agence2.getLatitude();
		return (float) Math.sqrt(Math.pow(xaxb, 2) + Math.pow(yayb, 2));
	}

	public Agence getLieu1() {
		return agence1;
	}
	public void setLieu1(Agence agence) {
		this.agence1 = agence;
	}
	public Agence getLieu2() {
		return agence2;
	}
	public void setLieu2(Agence agence) {
		this.agence2 = agence;
	}

	@Override
	public int compareTo(Object o) {
		Lien l = (Lien)o;
		if(this.getDistance()>l.getDistance())
			return 1;
		else if(this.getDistance()<l.getDistance())
			return -1;
		return 0;
	}
}
