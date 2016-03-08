import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Agence {
	private int id;
	private String nom;
	private String codepostal;
	private float longitude;
	private float latitude;
	private int nbpersonnes1;
	
	public Agence() {
		super();
	}
	
	public Agence(int id, String nom, String codepostal, float longitude, float latitude, int nbpersonnes1) {
		super();
		this.id = id;
		this.nom = nom;
		this.codepostal = codepostal;
		this.longitude = longitude;
		this.latitude = latitude;
		this.nbpersonnes1 = nbpersonnes1;
	}
	
	public static List<Agence> LireAgence() {
		
		List<Agence> liste = new ArrayList<Agence>();
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
						System.out.println(nouvelleAgence);
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

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodepostal() {
		return codepostal;
	}
	public void setCodepostal(String codepostal) {
		this.codepostal = codepostal;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public int getNbpersonnes1() {
		return nbpersonnes1;
	}
	public void setNbpersonnes1(int nbpersonnes1) {
		this.nbpersonnes1 = nbpersonnes1;
	}
	
	public String toString() {
		return "Agence "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}
}
