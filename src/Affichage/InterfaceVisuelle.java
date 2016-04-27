package Affichage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import Arcs.Lien;
import Arcs.Trajet;
import Calcul.Logique;
import Noeuds.Agence;
import Noeuds.Lieu;
import Commun.*;

/*
 * Classe d'affichage de l'interface
 */
public class InterfaceVisuelle extends JFrame 
			implements ActionListener, ItemListener, ChangeListener, KeyListener {
	private static final long serialVersionUID = 1L;
	
	private final int LARGEUR = 1000;
	private final int HAUTEUR = 610;
	
	private double facteur;
	private boolean afficherTrajet;
	private boolean afficherLieu;
	private boolean afficherAgence;
	private boolean afficherLienAgence;
	private boolean afficherBarycentre;
	
	private Carte carte;
	private Logique logique;
	
	private JPanel pnl_control;
	private JButton btn_barycentre;
	private JButton btn_algogene;
	private JButton btn_choixAgence;
	private JCheckBox cb_trajet;
	private JCheckBox cb_lieu;
	private JCheckBox cb_barycentre;
	private JCheckBox cb_agence;
	private JCheckBox cb_liensAgence;
	private JCheckBox cb_rafraichirCarte;
	private JTextField txt_minimumLieu;
	private JTextField txt_totalPersonne;
	private JTextField txt_totalLieu;
	private JTextField txt_totalDistance;
	private JTextField txt_totalPrix;
	private JTextField txt_iterations;
	private JSlider slider_temperature;

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
		afficherLienAgence = false;
		afficherBarycentre = true;
		
		logique = new Logique(this);
		carte = new Carte(this);
		add(carte);
		
		pnl_control = new JPanel();
		pnl_control.setBorder(BorderFactory.createLineBorder(Color.black));
		pnl_control.setBounds(680, 0, 320, 600);
		pnl_control.setLayout(null);
		add(pnl_control);
		
		afficherPanelDroite();
	}
	
	private void afficherPanelDroite() {
		
		int height = 10;
		
		cb_rafraichirCarte = new JCheckBox("Maj de la carte en temps réel");
		cb_rafraichirCarte.setSelected(true);
		cb_rafraichirCarte.addItemListener(this);
		cb_rafraichirCarte.setBounds(20, height, 280, 30);
		pnl_control.add(cb_rafraichirCarte);
		
		height += 25;
		
		JLabel lbl_line_noeuds = new JLabel("<html><font color=orange>Noeuds -------------------</font></html>");
		lbl_line_noeuds.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_noeuds);
		
		height += 20;
		
		cb_lieu = new JCheckBox("<html>Afficher les lieux <font color=red>・</font></html>");
		cb_lieu.setSelected(true);
		cb_lieu.addItemListener(this);
		cb_lieu.setBounds(20, height, 240, 30);
		pnl_control.add(cb_lieu);
		
		height += 20;
		
		cb_agence = new JCheckBox("Afficher les agences ●");
		cb_agence.setSelected(true);
	    cb_agence.addItemListener(this);
	    cb_agence.setBounds(20, height, 180, 30);
		pnl_control.add(cb_agence);
		
		btn_choixAgence = new JButton("Choix agences");
		btn_choixAgence.setBounds(190, height, 120, 30);
		btn_choixAgence.addActionListener(this);
		pnl_control.add(btn_choixAgence);
		
		height += 30;
		
		JLabel lbl_line_arcs = new JLabel("<html><font color=orange>Arcs -------------------</font></html>");
		lbl_line_arcs.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_arcs);
		
		height += 20;
		
		cb_trajet = new JCheckBox("Afficher les trajets ---");
	    cb_trajet.setSelected(true);
	    cb_trajet.addItemListener(this);
	    cb_trajet.setBounds(20, height, 240, 30);
		pnl_control.add(cb_trajet);
		
		height += 20;
		
		cb_liensAgence = new JCheckBox("<html>Afficher les liens d'agences <font color=blue>---</font></html>");
		cb_liensAgence.setSelected(false);
		cb_liensAgence.addItemListener(this);
		cb_liensAgence.setBounds(20, height, 240, 30);
		pnl_control.add(cb_liensAgence);
		
		height += 30;
		
		JLabel lbl_line_recuit = new JLabel("<html><font color=orange>Recuit simulé ------------</font></html>");
		lbl_line_recuit.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_recuit);
		
		height += 30;
		
		btn_barycentre = new JButton("Barycentre");
		btn_barycentre.setBounds(20, height, 120, 40);
		btn_barycentre.addActionListener(this);
		pnl_control.add(btn_barycentre);
		
		JLabel lbl_iterations = new JLabel("Itérations :");
		lbl_iterations.setBounds(150, height, 70, 30);
		pnl_control.add(lbl_iterations);
		
		txt_iterations = new JTextField("500");
		txt_iterations.setBounds(240, height, 60, 30);
		txt_iterations.addKeyListener(this);
		pnl_control.add(txt_iterations);
		
		height += 40;
		
		JLabel lbl_temperature = new JLabel("Température :");
		lbl_temperature.setBounds(20, height, 90, 30);
		pnl_control.add(lbl_temperature);
		
		slider_temperature = new JSlider(JSlider.HORIZONTAL, 1, Commun.TEMPERATURE_MAX, 5000);
		slider_temperature.setValue(Commun.TEMPERATURE_MAX);
		slider_temperature.setBounds(120, height-8, 180, 50);
		slider_temperature.addChangeListener(this);
		pnl_control.add(slider_temperature);

		height += 30;
		
		cb_barycentre = new JCheckBox("<html>Afficher les barycentres <font color=#1bee14>●</font></html>");
		cb_barycentre.setSelected(true);
		cb_barycentre.addItemListener(this);
		cb_barycentre.setBounds(20, height, 240, 30);
		pnl_control.add(cb_barycentre);
		
		height += 30;
		
		JLabel lbl_line_algogene = new JLabel("<html><font color=orange>Algogène ------------</font></html>");
		lbl_line_algogene.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_algogene);
		
		height += 30;
		
		btn_algogene = new JButton("Algogene");
		btn_algogene.setBounds(100, height, 120, 40);
		btn_algogene.addActionListener(this);
		pnl_control.add(btn_algogene);
		
		height += 40;
		
		JLabel lbl_line_resultats = new JLabel("<html><font color=orange>Résultats ------------");
		lbl_line_resultats.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_resultats);
		
		height += 20;
		
		JLabel lbl_totalPersonne = new JLabel("Nombre de personnes :");
		lbl_totalPersonne.setBounds(20, height, 160, 30);
		pnl_control.add(lbl_totalPersonne);
		
		txt_totalPersonne = new JTextField();
		txt_totalPersonne.setText(logique.getAgences().getNombrePersonne()+"");
		txt_totalPersonne.setEditable(false);
		txt_totalPersonne.setBounds(180, height, 120, 30);
		pnl_control.add(txt_totalPersonne);
		
		height += 30;
		
		JLabel lbl_lieuMinimum = new JLabel("Lieux minimum :");
		lbl_lieuMinimum.setBounds(20, height, 160, 30);
		pnl_control.add(lbl_lieuMinimum);
		
		txt_minimumLieu = new JTextField();
		txt_minimumLieu.setText(Math.ceil(logique.getAgences().getNombrePersonne()/Commun.MAX_PERSONNE)+"");
		txt_minimumLieu.setEditable(false);
		txt_minimumLieu.setBounds(180, height, 120, 30);
		pnl_control.add(txt_minimumLieu);
		
		height += 30;
		
		JLabel lbl_totalLieu = new JLabel("Lieux utilisés :");
		lbl_totalLieu.setBounds(20, height, 160, 30);
		pnl_control.add(lbl_totalLieu);
		
		txt_totalLieu = new JTextField();
		txt_totalLieu.setEditable(false);
		txt_totalLieu.setBounds(180, height, 120, 30);
		pnl_control.add(txt_totalLieu);
		
		height += 30;
		
		JLabel lbl_totalDistance = new JLabel("Distance totale :");
		lbl_totalDistance.setBounds(20, height, 160, 30);
		pnl_control.add(lbl_totalDistance);
		
		txt_totalDistance = new JTextField();
		txt_totalDistance.setEditable(false);
		txt_totalDistance.setBounds(180, height, 120, 30);
		pnl_control.add(txt_totalDistance);
		
		height += 30;
		
		JLabel lbl_totalPrix = new JLabel("Prix totale :");
		lbl_totalPrix.setBounds(20, height, 160, 30);
		pnl_control.add(lbl_totalPrix);
		
		txt_totalPrix = new JTextField();
		txt_totalPrix.setEditable(false);
		txt_totalPrix.setBounds(180, height, 120, 30);
		pnl_control.add(txt_totalPrix);
	}

	public void dessinerCarte(Graphics2D g) {
		
		Image image = new ImageIcon("Fichiers/maFrance.png").getImage();
		g.drawImage(image, 0, 0, this);
		
		int pointX1, pointX2, pointY1, pointY2, rayon;

		//int basX = (int)(700*facteur);
		int basY = (int)(570*facteur);
		g.scale(1/facteur, 1/facteur);
		
		//Affichage des lieux
		if(afficherLieu) {
			g.setColor(Color.red);
			rayon = (int)(1.7*facteur);
			for (Lieu lieu : logique.getLieux()) {
				pointX1 = lieu.getLongitudeForMap(facteur);
				pointY1 = basY - lieu.getLatitudeForMap(facteur);
				g.fillOval(pointX1,pointY1,rayon,rayon);
			}
		}
		
		//Affichage des barycentres
		if(afficherBarycentre) {
			rayon = (int)(4*facteur);
			g.setColor(Color.green);
			for (Agence barycentre : logique.getBarycentres()) {
				pointX1 = barycentre.getLongitudeForMap(facteur);
				pointY1 = basY - barycentre.getLatitudeForMap(facteur);
				g.fillOval(pointX1,pointY1,rayon,rayon);
			}
		}
		
		//Affichage des agences
		rayon = (int)(3*facteur);
		for (Agence agence : logique.getAgences()) {
			pointX1 = agence.getLongitudeForMap(facteur);
			pointY1 = basY - agence.getLatitudeForMap(facteur);
			if(afficherAgence) {
				g.setColor(Color.black);
				g.fillOval(pointX1,pointY1,rayon,rayon);
			}
			
			//Affichage des liens d'agences
			if(afficherLienAgence) {
				g.setColor(Color.blue);
				for (Lien voisin : agence.getVoisins()) {
					if(agence.equals(voisin.getLieu1())) {
						pointX2 = voisin.getLieu2().getLongitudeForMap(facteur);
						pointY2 = basY - voisin.getLieu2().getLatitudeForMap(facteur);
					} else {
						pointX2 = voisin.getLieu1().getLongitudeForMap(facteur);
						pointY2 = basY - voisin.getLieu1().getLatitudeForMap(facteur);
					}
					g.drawLine(pointX1,pointY1,pointX2,pointY2);
				}
			}
		}
		
		//Affichage des trajets
		g.setColor(Color.black);
		for(Trajet trajet : logique.getTrajets()) {
			pointX1 = trajet.getAgence().getLongitudeForMap(facteur);
			pointY1 = basY - trajet.getAgence().getLatitudeForMap(facteur);
			pointX2 = trajet.getLieu().getLongitudeForMap(facteur);
			pointY2 = basY - trajet.getLieu().getLatitudeForMap(facteur);

			if(afficherTrajet)
				g.drawLine(pointX1,pointY1,pointX2,pointY2);
		}
		
		txt_totalDistance.setText(logique.getDistanceTotale()+"");
		txt_totalPrix.setText(logique.getPrixTotal()+"");
		txt_totalLieu.setText(logique.getLieuTotal()+"");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btn_barycentre) {
			
			Thread t1 = new Thread(new Runnable() {
			     public void run() {
			    	 logique.recuitSimuleBarycentre();
			     }
			});  
			t1.start();
			
		} else if(e.getSource() == btn_algogene) {
			Thread t2 = new Thread(new Runnable() {
				public void run() {
					logique.algogene();
				}
			});
			t2.start();
		} else if(e.getSource() == btn_choixAgence) {
			
			File file = new File("Fichiers");
			file = new File(file.getAbsolutePath());
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(file);
		    chooser.setFileFilter(new FileNameExtensionFilter("txt", "txt"));
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	logique.setPathFichier(chooser.getSelectedFile().getPath());
		    	logique.lireAgences();
		    	txt_totalPersonne.setText(logique.getAgences().getNombrePersonne()+"");
		    	txt_minimumLieu.setText(Math.ceil(logique.getAgences().getNombrePersonne()/Commun.MAX_PERSONNE)+"");
		    	carte.repaint();
		    }
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
	    } else if (source == cb_barycentre) {
	    	afficherBarycentre = ! afficherBarycentre;
	    } else if (source == cb_rafraichirCarte) {
	    	logique.setRafraichirCarte(!logique.isRafraichirCarte());
	    }

	    carte.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        logique.setTemperature((int)source.getValue());
	    }
	}
	
	public static boolean estEntier(String str)  
	{  
		try { Integer.parseInt(str); }  
		catch(NumberFormatException nfe)  
		{ return false; }  
		return true;  
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource() == txt_iterations) {
			if(estEntier(txt_iterations.getText())) {
				logique.setIterations(Integer.parseInt(txt_iterations.getText()));
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
	
	public void update() {
		carte.repaint();
		this.repaint();
	}	
}
