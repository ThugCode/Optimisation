import javax.swing.*;
import java.awt.*;

class Carte extends JPanel {
	private static final long serialVersionUID = 1L;
	
	Affichage affichage;
    
    Carte(Affichage _affichage)
	{
    	affichage = _affichage;

	    //setBackground(Color.lightGray);
	}  
    
    public void paintComponent(Graphics g)
	{
    	super.paintComponent(g); 
    	affichage.dessinerCarte(g);
	}
}