package Affichage;
import javax.swing.*;

import java.awt.*;

/*
 * Classe d'affichage de la carte
 */
class Carte extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final int LARGEUR = 700;
	private final int HAUTEUR = 600;
	private InterfaceVisuelle affichage;
    
    Carte(InterfaceVisuelle pAffichage)
	{
    	affichage = pAffichage;
    	
    	setBounds(0, 0, LARGEUR, HAUTEUR);
    	setBorder(BorderFactory.createLineBorder(Color.black));
	    setBackground(Color.lightGray);
	}  
    
    public void paintComponent(Graphics g)
	{
    	Graphics2D g2d = (Graphics2D)g;
    	
    	super.paintComponent(g2d); 
    	affichage.dessinerCarte(g2d);
	}
}