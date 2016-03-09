import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Affichage extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public final int LARGEUR = 800;
	public final int HAUTEUR = 600;
	Carte carte;
	Logique logique;

	Affichage() {
		setSize(LARGEUR, HAUTEUR);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
		
		logique = new Logique(this);
		carte = new Carte(this);
		add(carte, BorderLayout.CENTER);
		

	}

	void dessinerCarte(Graphics2D g) {
		
		int pointX, pointY;

		g.setColor(Color.red);
		
		double facteur = 1000;
		int basX = (int)(790*facteur);
		int basY = (int)(565*facteur);
		int rayon = (int)(1.7*facteur);
		g.scale(1/facteur, 1/facteur);
		
		for (Lieu lieu : logique.lieux) {
			pointX = basX - (int) (lieu.getLongitude()*50*facteur+(300*facteur));
			pointY = basY - (int) (lieu.getLatitude()*50*facteur-(2000*facteur));
			g.fillOval(pointX,pointY,rayon,rayon);
		}
		
		g.setColor(Color.black);
		rayon = (int)(3*facteur);
		for (Agence agence : logique.agences) {
			pointX = basX - (int) (agence.getLongitude()*50*facteur+(300*facteur));
			pointY = basY - (int) (agence.getLatitude()*50*facteur-(2000*facteur));
			g.fillOval(pointX,pointY,rayon,rayon);
		}
			
		for (Trajet trajet : logique.trajets) {
			int pointX1 = basX - (int) (trajet.getAgence().getLongitude()*50*facteur+(300*facteur));
			int pointY1 = basY - (int) (trajet.getAgence().getLatitude()*50*facteur-(2000*facteur));
			int pointX2 = basX - (int) (trajet.getLieu().getLongitude()*50*facteur+(300*facteur));
			int pointY2 = basY - (int) (trajet.getLieu().getLatitude()*50*facteur-(2000*facteur));
			g.drawLine(pointX1,pointY1,pointX2,pointY2);
		}
		
		g.setColor(Color.GREEN);
		g.fillOval(basX, basY, 10000, 10000);
	}
}
