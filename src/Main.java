import Affichage.InterfaceVisuelle;

/*
 * Classe de lancement de l'application
 */
public class Main {

	public static void main(String[] args) {
		
		InterfaceVisuelle a = new InterfaceVisuelle();
		a.setVisible(true);
		
		/*
		Agence a = new Agence(1, "A", "74", 6, 2, 0);
		Lieu l = new Lieu(1, "L", "69", 7, 3);
		Trajet t = new Trajet(a, l);
		System.out.println(t.getDistanceKm());
		*/
	}

}
