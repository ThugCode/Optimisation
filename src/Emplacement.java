
public class Emplacement {
	
	protected int id;
	protected String nom;
	protected String codepostal;
	protected float longitude;
	protected float latitude;
	
	public Emplacement() {
		super();
	}
	
	public Emplacement(int id, String nom, String codepostal, float longitude, float latitude) {
		super();
		this.id = id;
		this.nom = nom;
		this.codepostal = codepostal;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public int getLongitudeForMap(double facteur) {
		return (int) (longitude*50*facteur+(260*facteur));
	}
	
	public int getLatitudeForMap(double facteur) {
		return (int) (latitude*50*facteur-(2050*facteur));
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
}
