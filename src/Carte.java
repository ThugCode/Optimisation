import javax.swing.*;
import java.awt.*;

class Carte extends JPanel {
	private static final long serialVersionUID = 1L;
	
	Affichage affichage;
    
    Carte(Affichage pAffichage)
	{
    	affichage = pAffichage;

	    //setBackground(Color.lightGray);
	}  
    
    public void paintComponent(Graphics g)
	{
    	Graphics2D g2d = (Graphics2D)g;
    	
    	super.paintComponent(g2d); 
    	affichage.dessinerCarte(g2d);
	}
}