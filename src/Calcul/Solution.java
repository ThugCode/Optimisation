package Calcul;

import java.util.BitSet;

public class Solution {

	private BitSet lieux;
	private Float prix;
	private Float propa;
	
	public Solution() {
		this.lieux = new BitSet();
		this.prix = Float.MAX_VALUE;
	}
	
	public Solution(BitSet lieux, Float prix) {
		this.lieux = lieux;
		this.prix = prix;
	}

	public BitSet getLieux() {
		return lieux;
	}

	public void setLieux(BitSet lieux) {
		this.lieux = lieux;
	}

	public Float getPrix() {
		return prix;
	}

	public void setPrix(Float prix) {
		this.prix = prix;
	}

	public Float getPropa() {
		return propa;
	}

	public void setPropa(float propa) {
		this.propa = propa;
	}
	
	public String toString() {
		return "Lieux " + this.lieux + " Prix : " + this.prix + " Prop : " + this.propa ;
	}
}
