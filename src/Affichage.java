import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Affichage extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public final int LARGEUR = 800;
	public final int HAUTEUR = 800;
	Carte carte;

	Affichage() {
		setSize(LARGEUR, HAUTEUR);
		
		carte = new Carte(this);
		add(carte, BorderLayout.CENTER);
	}

	void dessinerCarte(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		int pointX, pointY;

		ArrayList<Lieu> lieux = (ArrayList<Lieu>) Lieu.LireLieuxPossible();
		
		g2d.setColor(Color.red);
		
		double facteur = 1000;
		int rayon = (int)(1.5*facteur);
		g2d.scale(1/facteur, 1/facteur);
		
		for (Lieu lieu : lieux) {
			pointX = (int) (lieu.getLongitude()*50*facteur+(300*facteur));
			pointY = (int) (lieu.getLatitude()*50*facteur-(2000*facteur));
			System.out.println(pointX+":"+pointY);
			g2d.fillOval(pointX,pointY,rayon,rayon);

		}

	}
}
