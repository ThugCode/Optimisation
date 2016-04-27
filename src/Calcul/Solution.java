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
	
	public Solution(BitSet lieux, Float prix, Float propa) {
		this.lieux = lieux;
		this.prix = prix;
		this.propa = propa;
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
	
	@Override
	public String toString() {
		return "Lieux : " + this.lieux + "\n" + "Prix : " + this.prix + " Prop : " + this.propa ;
	}
	
	@Override
	public Solution clone() {
		return new Solution((BitSet) this.lieux.clone(),this.prix, this.propa);
	}
}
