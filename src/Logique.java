import java.util.ArrayList;

public class Logique {

	private Affichage affichage;
	private ArrayList<Lieu> lieux;
	private ArrayList<Agence> agences;
	private ArrayList<Trajet> trajets;
	
	Logique(Affichage pAffichage)
	{
    	affichage = pAffichage;
    	
    	lieux = StaticMethods.LireLieuxPossible();
    	agences = StaticMethods.LireAgence();
    	trajets = StaticMethods.trajetAuPlusPres(agences, lieux);
	}
	
	public void trajetAuHasard() {
		trajets = StaticMethods.trajetAuHasard(agences, lieux);
	}

	public void trajetAuPlusPres() {
		trajets = StaticMethods.trajetAuPlusPres(agences, lieux);
	}
	
	public Affichage getAffichage() {
		return affichage;
	}

	public ArrayList<Lieu> getLieux() {
		return lieux;
	}

	public ArrayList<Agence> getAgences() {
		return agences;
	}

	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
}
