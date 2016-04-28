package Commun;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Arcs.Lien;
import Calcul.Solution;
import Noeuds.Agence;
import Noeuds.GroupeAgence;
import Noeuds.Lieu;

public class LireFichiers {

	public static GroupeAgence LireAgence(String filePath) {
		
		GroupeAgence liste = new GroupeAgence();
		
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
						liste.add(nouvelleAgence);
						
						for(Agence voisin : liste) {
							
							temp = new Lien(nouvelleAgence, voisin);
							
							if(!nouvelleAgence.getVoisins().contains(temp)) {
								nouvelleAgence.getVoisins().add(temp);
								Collections.sort(nouvelleAgence.getVoisins());
								if(nouvelleAgence.getVoisins().size()>Commun.MAX_VOISINS_AGENCES)
									nouvelleAgence.getVoisins().remove(Commun.MAX_VOISINS_AGENCES);
							}
							
							if(!voisin.getVoisins().contains(temp)) {
								voisin.getVoisins().add(temp);
								Collections.sort(voisin.getVoisins());
								if(voisin.getVoisins().size()>Commun.MAX_VOISINS_AGENCES)
									voisin.getVoisins().remove(Commun.MAX_VOISINS_AGENCES);
							}
						}
					}
					i++;
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) { System.out.println("Erreur IO --" + ioe.toString());}
		
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
	
	public static void ecrireFichier(List<Solution> solutions) {
		
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/report.txt";

		try {
			FileWriter fw = new FileWriter(filePath,true);
			for(Solution solution : solutions) {
			fw.write(solution.toString());
			fw.write(";");
			}
			fw.write("\n");
		    fw.close();
		} catch (FileNotFoundException fnfe) { System.out.println("Fichier de messages introuvable");
		} catch (IOException e) { System.out.println("Erreur IO --" + e.toString()); }
	}
}
