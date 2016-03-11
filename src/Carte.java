import javax.swing.*;
import java.awt.*;

class Carte extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final int LARGEUR = 800;
	public final int HAUTEUR = 600;
	private Affichage affichage;
    
    Carte(Affichage pAffichage)
	{
    	affichage = pAffichage;
    	
    	setBounds(0, 0, 700, HAUTEUR);
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