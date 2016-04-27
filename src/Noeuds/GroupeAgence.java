package Noeuds;

import java.util.ArrayList;

public class GroupeAgence extends ArrayList<Agence> {
	private static final long serialVersionUID = 1L;

	private int numero;
	private int nombrePersonne;
	private Agence barycentre;
	
	public GroupeAgence(GroupeAgence groupeAgence) {
		super(groupeAgence);
	}
	
	public GroupeAgence() {
		super();
		setNumero(0);
	}
	
	public int nombreLieuxMinimum() {
		return (int) Math.ceil((double)nombrePersonne/Commun.Commun.MAX_PERSONNE);
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getNombrePersonne() {
		
		int nombrePersonne = 0;
		for(Agence agence : this) {
			nombrePersonne += agence.getNbpersonnes();
		}
		return nombrePersonne;
	}

	public Agence getBarycentre() {
		return barycentre;
	}

	public void setBarycentre(Agence barycentre) {
		this.barycentre = barycentre;
	}
}
