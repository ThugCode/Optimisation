package Affichage;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import Arcs.Lien;
import Arcs.Trajet;
import Calcul.Logique;
import Commun.Commun;
import Noeuds.Agence;
import Noeuds.Lieu;

/*
 * Classe d'affichage de l'interface
 */
public class InterfaceVisuelle extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;
	
	private final int LARGEUR = 1000;
	private final int HAUTEUR = 600;
	
	private double facteur;
	private boolean afficherTrajet;
	private boolean afficherLieu;
	private boolean afficherAgence;
	private boolean afficherLienAgence;
	
	private Carte carte;
	private Logique logique;
	
	private JPanel pnl_control;
	private JButton btn_hasard;
	private JButton btn_pluspres;
	private JButton btn_barycentre;
	private JCheckBox cb_trajet;
	private JCheckBox cb_lieu;
	private JCheckBox cb_agence;
	private JCheckBox cb_liensAgence;
	private JTextField txt_totalLieu;
	private JTextField txt_totalDistance;
	private JTextField txt_totalPrix;

	public InterfaceVisuelle() {
		setSize(LARGEUR, HAUTEUR);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
		setLayout(null);
		
		facteur = 1000;
		afficherTrajet = true;
		afficherAgence = true;
		afficherLieu = true;
		afficherLienAgence = true;
		
		logique = new Logique(this);
		carte = new Carte(this);
		add(carte);
		
		pnl_control = new JPanel();
		pnl_control.setBorder(BorderFactory.createLineBorder(Color.black));
		pnl_control.setBounds(700, 0, 300, 600);
		pnl_control.setLayout(null);
		add(pnl_control);
		
		afficherPanelDroite();
	}
	
	private void afficherPanelDroite() {
		
		int height = 20;
		
		cb_trajet = new JCheckBox("Afficher les trajets");
	    cb_trajet.setSelected(true);
	    cb_trajet.addItemListener(this);
	    cb_trajet.setBounds(20, height, 240, 30);
		pnl_control.add(cb_trajet);
		
		height += 30;
		
		cb_lieu = new JCheckBox("Afficher les lieux");
		cb_lieu.setSelected(true);
		cb_lieu.addItemListener(this);
		cb_lieu.setBounds(20, height, 240, 30);
		pnl_control.add(cb_lieu);
		
		height += 30;
		
		cb_agence = new JCheckBox("Afficher les agences");
		cb_agence.setSelected(true);
	    cb_agence.addItemListener(this);
	    cb_agence.setBounds(20, height, 240, 30);
		pnl_control.add(cb_agence);
		
		height += 30;
		
		cb_liensAgence = new JCheckBox("Afficher les liens d'agences");
		cb_liensAgence.setSelected(true);
		cb_liensAgence.addItemListener(this);
		cb_liensAgence.setBounds(20, height, 240, 30);
		pnl_control.add(cb_liensAgence);
		
		height += 40;
		
		btn_hasard = new JButton("Hasard");
		btn_hasard.setBounds(20, height, 120, 50);
		btn_hasard.addActionListener(this);
		pnl_control.add(btn_hasard);
		
		btn_pluspres = new JButton("Plus près");
		btn_pluspres.setBounds(160, height, 120, 50);
		btn_pluspres.addActionListener(this);
		pnl_control.add(btn_pluspres);
		
		height += 60;
		
		btn_barycentre = new JButton("Barycentre");
		btn_barycentre.setBounds(20, height, 120, 50);
		btn_barycentre.addActionListener(this);
		pnl_control.add(btn_barycentre);
		
		height += 60;
		
		JLabel lbl_totalLieu = new JLabel("Lieux utilisés :");
		lbl_totalLieu.setBounds(20, height, 120, 30);
		pnl_control.add(lbl_totalLieu);
		
		txt_totalLieu = new JTextField();
		txt_totalLieu.setEditable(false);
		txt_totalLieu.setBounds(160, height, 120, 30);
		pnl_control.add(txt_totalLieu);
		
		height += 40;
		
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

	public void dessinerCarte(Graphics2D g) {
		
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
		
		
		rayon = (int)(3*facteur);
		for (Agence agence : logique.getAgences()) {
			pointX1 = basX - agence.getLongitudeForMap(facteur);
			pointY1 = basY - agence.getLatitudeForMap(facteur);
			g.setColor(Color.black);
			if(afficherAgence)
				g.fillOval(pointX1,pointY1,rayon,rayon);
			
			g.setColor(Color.green);
			for (Lien voisin : agence.getVoisins()) {
				if(agence.equals(voisin.getLieu1())) {
					pointX2 = basX - voisin.getLieu2().getLongitudeForMap(facteur);
					pointY2 = basY - voisin.getLieu2().getLatitudeForMap(facteur);
				} else {
					pointX2 = basX - voisin.getLieu1().getLongitudeForMap(facteur);
					pointY2 = basY - voisin.getLieu1().getLatitudeForMap(facteur);
				}
				if(afficherLienAgence)
					g.drawLine(pointX1,pointY1,pointX2,pointY2);
			}
		}
		
		g.setColor(Color.black);
		
		float dist = 0;
		float distanceTotal = 0;
		float prixTotal = 0;
		int lieuTotal = 0;
		for (Trajet trajet : logique.getTrajets()) {
			pointX1 = basX - trajet.getAgence().getLongitudeForMap(facteur);
			pointY1 = basY - trajet.getAgence().getLatitudeForMap(facteur);
			pointX2 = basX - trajet.getLieu().getLongitudeForMap(facteur);
			pointY2 = basY - trajet.getLieu().getLatitudeForMap(facteur);
			
			if(afficherTrajet)
				g.drawLine(pointX1,pointY1,pointX2,pointY2);
			
			dist = trajet.getDistanceKm();
			distanceTotal += dist;
			prixTotal += dist*trajet.getAgence().getNbpersonnes();
			
			trajet.getLieu().setNbPersonneAssociees(trajet.getLieu().getNbPersonneAssociees()+trajet.getAgence().getNbpersonnes());
			if(trajet.getLieu().getNbPersonneAssociees() > 60)
				System.out.println("Au dela de 60 personnes pour le lieu " + trajet.getLieu().getNom());
			
			if(!trajet.getLieu().isAssocie()) {
				trajet.getLieu().setAssocie(true);
				prixTotal += Commun.prixLieu;
				lieuTotal ++;
			}
		}
		
		txt_totalDistance.setText(distanceTotal+"");
		txt_totalPrix.setText(prixTotal+"");
		txt_totalLieu.setText(lieuTotal+"");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_hasard) {
			logique.trajetAuHasard();
			carte.repaint();
		} else if(e.getSource() == btn_pluspres) {
			logique.trajetAuPlusPres();
			carte.repaint();
		} else if(e.getSource() == btn_barycentre) {
			logique.trajetBarycentre();
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
	    } else if (source == cb_liensAgence) {
	    	afficherLienAgence = ! afficherLienAgence;
	    }

	    carte.repaint();
	    
	    //if (e.getStateChange() == ItemEvent.DESELECTED)
	        //...make a note of it...
	}
}
