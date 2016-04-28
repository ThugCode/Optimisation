package Calcul;

import java.util.BitSet;

public class Solution {

	private BitSet lieux;
	private Float prix;
	private Float proba;
	
	public Solution() {
		this.lieux = new BitSet();
		this.prix = Float.MAX_VALUE;
	}
	
	public Solution(BitSet lieux, Float prix, Float propa) {
		this.lieux = lieux;
		this.prix = prix;
		this.proba = propa;
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

	public Float getProba() {
		return proba;
	}

	public void setProba(float proba) {
		this.proba = proba;
	}
	
	@Override
	public String toString() {
		return this.prix + "";
	}
	
	@Override
	public Solution clone() {
		return new Solution((BitSet) this.lieux.clone(),this.prix, new Float(0));
	}
}
