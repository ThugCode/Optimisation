import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lieu {
	private int id;
	private String nom;
	private String codepostal;
	private float longitude;
	private float latitude;
	
	public Lieu() {
		super();
	}
	
	public Lieu(int id, String nom, String codepostal, float longitude, float latitude) {
		super();
		this.id = id;
		this.nom = nom;
		this.codepostal = codepostal;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public static List<Lieu> LireLieuxPossible() {
		
		List<Lieu> liste = new ArrayList<Lieu>();
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
	
	public String toString() {
		return "Lieu "+this.nom+" ("+this.id+") cp : "+this.codepostal;
	}
}
