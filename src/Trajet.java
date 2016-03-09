import java.util.ArrayList;
import java.util.List;

public class Trajet {
	
	private int id;
	private Agence agence;
	private Lieu lieu;
	
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
	
	public static List<Trajet> trajetAuHasard(List<Agence> agences, List<Lieu> lieux) {
		
		int random;
		ArrayList<Trajet> trajets = new ArrayList<Trajet>();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			trajets.add(new Trajet(agence, lieux.get(random)));
		}
		
		return trajets;
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
}
