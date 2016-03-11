import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StaticMethods {
	
	public static ArrayList<Trajet> trajetAuHasard(ArrayList<Agence> agences, ArrayList<Lieu> lieux) {
		
		int random;
		ArrayList<Trajet> trajets = new ArrayList<Trajet>();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			trajets.add(new Trajet(agence, lieux.get(random)));
		}
		
		return trajets;
	}

	public static ArrayList<Agence> LireAgence() {
		
		ArrayList<Agence> liste = new ArrayList<Agence>();
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		
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
						nouvelleAgence.setNbpersonnes1(Integer.parseInt(parts[5]));
						//System.out.println(nouvelleAgence);
						liste.add(nouvelleAgence);
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
}
