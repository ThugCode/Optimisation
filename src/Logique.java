import java.util.ArrayList;

public class Logique {

	Affichage affichage;
	ArrayList<Lieu> lieux;
	ArrayList<Agence> agences;
	ArrayList<Trajet> trajets;
	
	Logique(Affichage pAffichage)
	{
    	affichage = pAffichage;
    	
    	lieux = (ArrayList<Lieu>) Lieu.LireLieuxPossible();
    	agences = (ArrayList<Agence>) Agence.LireAgence();
    	trajets = (ArrayList<Trajet>) Trajet.trajetAuHasard(agences, lieux);
	}  
}
