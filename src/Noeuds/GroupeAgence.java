package Noeuds;

import java.util.ArrayList;

public class GroupeAgence extends ArrayList<Agence> {
	private static final long serialVersionUID = 1L;

	private int numero;
	private int nombrePersonne;
	
	public GroupeAgence(GroupeAgence groupeAgence) {
		super(groupeAgence);
	}
	
	public GroupeAgence() {
		super();
		setNumero(0);
		setNombrePersonne(0);
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
		return nombrePersonne;
	}

	public void setNombrePersonne(int nombrePersonne) {
		this.nombrePersonne = nombrePersonne;
	}
	
	public void getSetNombrePersonne(int nombrePersonne) {
		this.nombrePersonne += nombrePersonne;
	}
}
