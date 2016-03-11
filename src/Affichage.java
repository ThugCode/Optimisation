import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

public class Affichage extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	
	public final int LARGEUR = 1000;
	public final int HAUTEUR = 600;
	public double facteur;
	public boolean afficherTrajet;
	public boolean afficherLieu;
	public boolean afficherAgence;
	
	public Carte carte;
	public Logique logique;
	
	private JPanel pnl_control;
	private JButton btn_reset;
	private JCheckBox cb_trajet;
	private JCheckBox cb_lieu;
	private JCheckBox cb_agence;
	private JTextField txt_totalDistance;
	private JTextField txt_totalPrix;

	Affichage() {
		setSize(LARGEUR, HAUTEUR);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
		setLayout(null);
		
		facteur = 1000;
		afficherTrajet = true;
		afficherAgence = true;
		afficherLieu = true;
		
		logique = new Logique(this);
		carte = new Carte(this);
		add(carte);
		
		pnl_control = new JPanel();
		pnl_control.setBorder(BorderFactory.createLineBorder(Color.black));
		pnl_control.setBounds(700, 0, 300, 600);
		pnl_control.setLayout(null);
		add(pnl_control);
		
		int height = 20;
		
		cb_trajet = new JCheckBox("Afficher les trajets");
	    cb_trajet.setSelected(true);
	    cb_trajet.addItemListener(this);
	    cb_trajet.setBounds(20, height, 200, 30);
		pnl_control.add(cb_trajet);
		
		height += 40;
		
		cb_lieu = new JCheckBox("Afficher les lieux");
		cb_lieu.setSelected(true);
		cb_lieu.addItemListener(this);
		cb_lieu.setBounds(20, height, 200, 30);
		pnl_control.add(cb_lieu);
		
		height += 40;
		
		cb_agence = new JCheckBox("Afficher les agences");
		cb_agence.setSelected(true);
	    cb_agence.addItemListener(this);
	    cb_agence.setBounds(20, height, 200, 30);
		pnl_control.add(cb_agence);
		
		height += 40;
		
		btn_reset = new JButton("Reset");
		btn_reset.setBounds(20, height, 120, 50);
		btn_reset.addActionListener(this);
		pnl_control.add(btn_reset);
		
		height += 60;
		
		JLabel lbl_totalDistance = new JLabel("Distance totale :");
		lbl_totalDistance.setBounds(20, height, 120, 30);
		pnl_control.add(lbl_totalDistance);
		
		txt_totalDistance = new JTextField();
		txt_totalDistance.setEditable(false);
		txt_totalDistance.setBounds(160, height, 120, 30);
		pnl_control.add(txt_totalDistance);
		
		height += 40;
		
		JLabel lbl_totalPrix = new JLabel("Prix totale :");
		lbl_totalPrix.setBounds(20, height, 120, 30);
		pnl_control.add(lbl_totalPrix);
		
		txt_totalPrix = new JTextField();
		txt_totalPrix.setEditable(false);
		txt_totalPrix.setBounds(160, height, 120, 30);
		pnl_control.add(txt_totalPrix);
	}

	void dessinerCarte(Graphics2D g) {
		
		int pointX1, pointX2, pointY1, pointY2, rayon;

		int basX = (int)(700*facteur);
		int basY = (int)(570*facteur);
		g.scale(1/facteur, 1/facteur);
		
		g.setColor(Color.red);
		rayon = (int)(1.7*facteur);
		for (Lieu lieu : logique.getLieux()) {
			lieu.reset();
			
			pointX1 = basX - lieu.getLongitudeForMap(facteur);
			pointY1 = basY - lieu.getLatitudeForMap(facteur);
			if(afficherLieu)
				g.fillOval(pointX1,pointY1,rayon,rayon);
		}
		
		g.setColor(Color.black);
		rayon = (int)(3*facteur);
		for (Agence agence : logique.getAgences()) {
			pointX1 = basX - agence.getLongitudeForMap(facteur);
			pointY1 = basY - agence.getLatitudeForMap(facteur);
			if(afficherAgence)
				g.fillOval(pointX1,pointY1,rayon,rayon);
		}
		
		float dist = 0;
		float distanceTotal = 0;
		float prixTotal = 0;
		for (Trajet trajet : logique.getTrajets()) {
			pointX1 = basX - trajet.getAgence().getLongitudeForMap(facteur);
			pointY1 = basY - trajet.getAgence().getLatitudeForMap(facteur);
			pointX2 = basX - trajet.getLieu().getLongitudeForMap(facteur);
			pointY2 = basY - trajet.getLieu().getLatitudeForMap(facteur);
			
			if(afficherTrajet)
				g.drawLine(pointX1,pointY1,pointX2,pointY2);
			
			dist = trajet.getDistance();
			distanceTotal += dist;
			prixTotal += dist*trajet.getAgence().getNbpersonnes1();
			
			trajet.getLieu().setNbPersonneAssociees(trajet.getLieu().getNbPersonneAssociees()+trajet.getAgence().getNbpersonnes1());
			if(trajet.getLieu().getNbPersonneAssociees() > 60)
				System.out.println("Au dela de 60 personne pour le trajet "+trajet.getId());
			
			if(!trajet.getLieu().isAssocie()) {
				trajet.getLieu().setAssocie(true);
				prixTotal += 10000;
			}
		}
		
		txt_totalDistance.setText(distanceTotal+"");
		txt_totalPrix.setText(prixTotal+"");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_reset) {
			logique.resetTrajet();
			carte.repaint();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
	    Object source = e.getItemSelectable();

	    if (source == cb_trajet) {
	    	afficherTrajet = ! afficherTrajet;
	    } else if (source == cb_lieu) {
	    	afficherLieu = ! afficherLieu;
	    } else if (source == cb_agence) {
	    	afficherAgence = ! afficherAgence;
	    }

	    carte.repaint();
	    
	    //if (e.getStateChange() == ItemEvent.DESELECTED)
	        //...make a note of it...
	}
}
