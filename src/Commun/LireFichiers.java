package Commun;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import Arcs.Lien;
import Noeuds.Agence;
import Noeuds.GroupeAgence;
import Noeuds.Lieu;

public class LireFichiers {

	public static GroupeAgence LireAgence(String filePath, int nombreDeVoisins) {
		
		GroupeAgence liste = new GroupeAgence();
		
		int nbPersonneTotal = 0;
		Lien temp;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(filePath));
		 
			try {
				String line;
				String[] parts;
				int i = 0;
				while ((line = buff.readLine()) != null) {
					if(i != 0) {
						parts = line.split(";");
						Agence nouvelleAgence = new Agence();
						nouvelleAgence.setId(i);
						nouvelleAgence.setNom(parts[1].replace("\"", ""));
						nouvelleAgence.setCodepostal(parts[2].replace("\"", ""));
						nouvelleAgence.setLongitude(Float.parseFloat(parts[3]));
						nouvelleAgence.setLatitude(Float.parseFloat(parts[4]));
						nouvelleAgence.setNbpersonnes(Integer.parseInt(parts[5]));
						nbPersonneTotal += nouvelleAgence.getNbpersonnes();
						liste.add(nouvelleAgence);
						
						for(Agence voisin : liste) {
							
							temp = new Lien(nouvelleAgence, voisin);
							
							if(!nouvelleAgence.getVoisins().contains(temp)) {
								nouvelleAgence.getVoisins().add(temp);
								Collections.sort(nouvelleAgence.getVoisins());
								if(nouvelleAgence.getVoisins().size()>nombreDeVoisins)
									nouvelleAgence.getVoisins().remove(nombreDeVoisins);
							}
							
							if(!voisin.getVoisins().contains(temp)) {
								voisin.getVoisins().add(temp);
								Collections.sort(voisin.getVoisins());
								if(voisin.getVoisins().size()>nombreDeVoisins)
									voisin.getVoisins().remove(nombreDeVoisins);
							}
						}
					}
					i++;
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) { System.out.println("Erreur IO --" + ioe.toString());}
		
		liste.setNombrePersonne(nbPersonneTotal);
		
		return liste;
	}

	public static ArrayList<Lieu> LireLieuxPossible() {
		
		ArrayList<Lieu> liste = new ArrayList<Lieu>();
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/LieuxPossibles.txt";
		
		
		try {
			BufferedReader buff = new BufferedReader(new FileReader(filePath));
		 
			try {
				String line;
				String[] parts;
				int i = 0;
				while ((line = buff.readLine()) != null) {
					if(i != 0) {
						parts = line.split(";");
						Lieu nouveauLieu = new Lieu();
						nouveauLieu.setId(i);
						nouveauLieu.setNom(parts[1].replace("\"", ""));
						nouveauLieu.setCodepostal(parts[2].replace("\"", ""));
						nouveauLieu.setLongitude(Float.parseFloat(parts[3]));
						nouveauLieu.setLatitude(Float.parseFloat(parts[4]));
						//System.out.println(nouveauLieu);
						liste.add(nouveauLieu);
					}
					i++;
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) { System.out.println("Erreur IO --" + ioe.toString());}
		
		return liste;
	}
}
