package Affichage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Arcs.Lien;
import Arcs.Trajet;
import Calcul.Logique;
import Commun.LireFichiers;
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
	private JButton btn_recuit;
	private JButton btn_algogene;
	private JButton btn_choixAgence;
	private JCheckBox cb_trajet;
	private JCheckBox cb_lieu;
	private JCheckBox cb_agence;
	private JCheckBox cb_liensAgence;
	private JTextField txt_totalLieu;
	private JTextField txt_totalDistance;
	private JTextField txt_totalPrix;
	private JTextField txt_nombreVoisinsAgences;

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
		pnl_control.setBounds(680, 0, 320, 600);
		pnl_control.setLayout(null);
		add(pnl_control);
		
		afficherPanelDroite();
	}
	
	private void afficherPanelDroite() {
		
		int height = 10;
		
		JLabel lbl_line_noeuds = new JLabel("Noeuds");
		lbl_line_noeuds.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_noeuds);
		
		height += 20;
		
		cb_lieu = new JCheckBox("<html>Afficher les lieux <font color=red>・</font></html>");
		cb_lieu.setSelected(true);
		cb_lieu.addItemListener(this);
		cb_lieu.setBounds(20, height, 240, 30);
		pnl_control.add(cb_lieu);
		
		height += 30;
		
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
		
		JLabel lbl_line_arcs = new JLabel("Arcs");
		lbl_line_arcs.setBounds(20, height, 280, 30);
		pnl_control.add(lbl_line_arcs);
		
		height += 20;
		
		cb_liensAgence = new JCheckBox("<html>Afficher les liens d'agences <font color=blue>---</font></html>");
		cb_liensAgence.setSelected(true);
		cb_liensAgence.addItemListener(this);
		cb_liensAgence.setBounds(20, height, 240, 30);
		pnl_control.add(cb_liensAgence);
		
		height += 30;
		
		JLabel lbl_nombreVoisinsAgences = new JLabel("Nombre de voisins d'une agence :");
		lbl_nombreVoisinsAgences.setBounds(30, height, 250, 30);
		pnl_control.add(lbl_nombreVoisinsAgences);
		
		txt_nombreVoisinsAgences = new JTextField("10");
		txt_nombreVoisinsAgences.setBounds(260, height, 40, 30);
		pnl_control.add(txt_nombreVoisinsAgences);
		
		height += 30;
		
		cb_trajet = new JCheckBox("Afficher les trajets ---");
	    cb_trajet.setSelected(true);
	    cb_trajet.addItemListener(this);
	    cb_trajet.setBounds(20, height, 240, 30);
		pnl_control.add(cb_trajet);
		
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
		
		btn_recuit = new JButton("Test recuit");
		btn_recuit.setBounds(160, height, 120, 50);
		btn_recuit.addActionListener(this);
		pnl_control.add(btn_recuit);
		
		height += 60;
		
		btn_algogene = new JButton("Algogene");
		btn_algogene.setBounds(20, height, 120, 50);
		btn_algogene.addActionListener(this);
		pnl_control.add(btn_algogene);
		
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
		rayon = (int)(4*facteur);
		g.setColor(Color.orange);
		for (Agence barycentre : logique.getBarycentres()) {
			pointX1 = barycentre.getLongitudeForMap(facteur);
			pointY1 = basY - barycentre.getLatitudeForMap(facteur);
			g.fillOval(pointX1,pointY1,rayon,rayon);
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
		if(e.getSource() == btn_hasard) {
			logique.trajetAuHasard();
			carte.repaint();
		} else if(e.getSource() == btn_pluspres) {
			logique.trajetAuPlusPres();
			carte.repaint();
		} else if(e.getSource() == btn_barycentre) {
			//logique.trajetBarycentre();
			
			Thread t1 = new Thread(new Runnable() {
			     public void run() {
			    	 //logique.recuitBarycentre();
			    	 logique.temperatureBarycentre();
			     }
			});  
			t1.start();
			
			//carte.repaint();
			
		} else if(e.getSource() == btn_recuit) {
			logique.recuitSimule();
			carte.repaint();
			System.out.println("Prix meilleure solution: " + logique.getPrixTotal());
			
		} else if(e.getSource() == btn_algogene) {
			logique.algogene();
		} else if(e.getSource() == btn_choixAgence) {
			
			File file = new File("Fichiers");
			file = new File(file.getAbsolutePath());
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(file);
		    chooser.setFileFilter(new FileNameExtensionFilter("txt", "txt"));
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	logique.setAgences(LireFichiers.LireAgence(chooser.getSelectedFile().getPath(), Integer.parseInt(txt_nombreVoisinsAgences.getText())));
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
	    }

	    carte.repaint();
	    
	    //if (e.getStateChange() == ItemEvent.DESELECTED)
	        //...make a note of it...
	}

	public void update() {
		carte.repaint();
		this.repaint();
	}
}
