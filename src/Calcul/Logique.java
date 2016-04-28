package Calcul;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import Affichage.InterfaceVisuelle;
import Arcs.Lien;
import Arcs.Trajet;
import Commun.Commun;
import Commun.LireFichiers;
import Noeuds.Agence;
import Noeuds.GroupeAgence;
import Noeuds.Lieu;

/*
 * Classe d'application des algorithmes
 */
public class Logique extends Thread {

	private InterfaceVisuelle affichage;
	private String pathFichier;
	private ArrayList<Lieu> lieux;
	private GroupeAgence agences;
	private ArrayList<Agence> barycentres;
	private ArrayList<Trajet> trajets;
	private int nbLieuxMin;
	private Solution meilleureSolutionAlgogene;
	
	private Random rand;
	private boolean rafraichirCarte;
	private int temperature;
	private int iterations;
	private int tailleGeneration;
	private float tauxMutation;
	private float distanceTotale;
	private float prixTotal;
	private int lieuTotal;
	
	private ArrayList<GroupeAgence> listeGroupes;
	private int nombrePersonneAuTotal;
	private boolean enCalcul;
	
	/**
	 * Constructeur
	 * @param pAffichage
	 */
	public Logique(InterfaceVisuelle pAffichage)
	{
		affichage = pAffichage;
		
		//Lecture du fichier 100 agences par défaut
		String filePath = new File("").getAbsolutePath();
		filePath += "/Fichiers/ListeAgences_100.txt";
		pathFichier = filePath;
		
		//Enregistrer les agences et les lieux
		lireAgences();
    	lieux = LireFichiers.LireLieuxPossible();
    	
    	//Initialisation des variables pour le recuit simulé
    	rand = new Random();
    	setRafraichirCarte(true);
    	setTemperature(Commun.TEMPERATURE_MAX);
    	setIterations(500);
    	setTailleGeneration(100);
    	
    	//Initialisation des trajets
    	resetTrajets();
    	
    	setEnCalcul(false);
	}
	
	/**
	 * Enregistrer les agences et leurs voisins
	 */
	public void lireAgences() {
		agences = LireFichiers.LireAgence(pathFichier);
		nombrePersonneAuTotal = agences.getNombrePersonne();
	}
	
	/**
	 * Réinitialisation des variables et listes
	 */
	public void resetTrajets() {
		trajets = new ArrayList<Trajet>();
		barycentres = new ArrayList<Agence>();
		
		distanceTotale = 0;
    	lieuTotal = 0;
    	prixTotal = 0;
	}
	
	/**
	 * Vérifie si le lieu n'a pas plus de 60 personnes associées
	 * @param lieu
	 */
	private void checkLieuPersonne(Lieu lieu) {
		if(lieu.getNbPersonneAssociees() > Commun.MAX_PERSONNE)
			System.out.println("Au dela de " + Commun.MAX_PERSONNE + " personnes pour le lieu " 
					+ lieu.getNom() + " (" + lieu.getNbPersonneAssociees() + ")");
	}
	
	/**
	 * Fonction de test des trajets au hasard
	 * @deprecated
	 */
	public void trajetAuHasard() {
		
		int random;
		resetTrajets();
		
		for (Agence agence : agences) {
			random = (int)(Math.random()*lieux.size());
			Lieu lieu = lieux.get(random);
			
			lieu.setAssocie(true);
			
			Trajet trajet = new Trajet(agence, lieu);
			lieu.getTrajets().add(trajet);
			agence.setTrajet(trajet);
			trajets.add(trajet);
		}
	}

	/**
	 * Fonction de test des trajets au plus près
	 * @deprecated
	 */
	public void trajetAuPlusPres() {
		
		resetTrajets();
		Lieu best;
		float min;
		Trajet temp = new Trajet();
		
		for (Agence agence : agences) {
			temp.setAgence(agence);
			best = null;
			min = Float.MAX_VALUE;
			for (Lieu lieu : lieux) {
				temp.setLieu(lieu);
				if(best == null || temp.getDistanceKm() < min) {
					best = lieu;
					min = temp.getDistanceKm();
				}
			}
			
			if(!best.isAssocie()) {
				best.setAssocie(true);
				prixTotal += Commun.PRIX_LIEU;
				lieuTotal ++;
			}
			
			Trajet trajet = new Trajet(agence, best);
			best.getTrajets().add(trajet);
			agence.setTrajet(trajet);
			trajets.add(trajet);
			
			distanceTotale += trajet.getDistanceKm();
			prixTotal += trajet.getDistanceKm()*agence.getNbpersonnes()*Commun.PRIX_TRAJET;
			best.setNbPersonneAssociees(best.getNbPersonneAssociees()+agence.getNbpersonnes());
		}
	}	
	
	
	
	/********************************************************************
	 * 
	 * 
	 * 							RECUIT SIMULE
	 * 
	 * 
	 * 
	 ********************************************************************/
	
	
	/**
	 * Lancement du recuit simulé sur les barycentres.
	 */
	public void recuitSimuleBarycentre() {
		
		//Initialisation des variables de sauvegarde 
		//de la meilleure solution rencontrée
		int bestLieu = 0;
		float bestDistance = 0;
		float bestPrix = Float.MAX_VALUE;
		ArrayList<Trajet> bestSolution = null;
		ArrayList<Agence> bestBary = null;
		
		//Mélange de la liste des agences
		Collections.shuffle(agences);
		
		for(int i = 1; i <= iterations; i++) {
			
			//Construction des trajets par barycentre
			//Retour de l'index de l'agence à partir de laquelle
			//le récursif a effectué les groupes d'agence
			int indexSwap = trajetBarycentre();
			
			prixTotal = calculPrix();
			
			//Affichage du prix
			System.out.println("Prix solution "+i+" : " + prixTotal);
			
			//Si le prix est inférieur, on adopte la solution en temps que
			//meilleure solution et nouvelle solution de départ du recursif
			//et on enregistre les variables utiles
			if(prixTotal < bestPrix) {
				bestSolution = trajets;
				bestPrix = prixTotal;
				bestBary = barycentres;
				bestDistance = distanceTotale;
				bestLieu = lieuTotal;
				System.out.println("Solution adoptée (Meilleure)");
			} 
			else //Sinon, on effectue le calcul avec la température
			{
				//Si le random est supérieur on rejete la solution et on reprend la solution précédente
				if(rand.nextFloat() > Math.exp( -( (prixTotal - bestPrix) / temperature ) ) ) {
					Collections.swap(agences, 0, indexSwap);
					System.out.println("Solution rejetée");
				} 
				else //Sinon on adopte la solution comme nouvelle solution de départ du recursif
				{
					System.out.println("Solution adoptée (Moins bonne)");
				}
			}
			
			if(rafraichirCarte) {
				//Rafraichissement de la carte
				affichage.update();
				//Attente avant d'effectuer une itération
				try { Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		
		//Affichage du meilleur résultat trouvé
		//Mise à jour des champs avec le meilleur résultat trouvé
		JOptionPane.showMessageDialog(null, "La meilleure solution donne un prix de : "+bestPrix+" €");
		trajets = bestSolution;
		prixTotal = bestPrix;
		barycentres = bestBary;
		distanceTotale = bestDistance;
		lieuTotal = bestLieu;
		affichage.update();
	}
	
	/**
	 * Fonction de calcul du prix
	 * @return Prix float
	 */
	private float calculPrix() {
		
		float prix = 0;
		
		for(Lieu lieu : lieux) {
			if(lieu.isAssocie()) {
				prix += Commun.PRIX_LIEU;
			}
		}
		
		for(Trajet trajet : trajets) {
			prix += trajet.getAgence().getNbpersonnes()
					*Commun.PRIX_TRAJET
					*trajet.getDistanceKm();
		}
		
		return prix;
	}

	/**
	 * Recherche de trajet par barycentre
	 * -> Création de groupes d'agences par voisinage
	 * -> Recherche du barycentre de chaque groupe
	 * -> Recherche du lieu le plus proche du barycentre
	 * -> Association de chaque agence avec le lieu
	 * -> Suppression de lieux inutiles
	 */
	public int trajetBarycentre() {
		
		//Initialisation compteur de lieux ou groupes d'agences
		int courant = 0;
		//Initialisation de la liste des groupes d'agences
		listeGroupes = new ArrayList<GroupeAgence>();
		//Réinitialisation des trajets
		resetTrajets();
		//Suppression des groupes pour toutes les agences
		for (Agence agence : agences) {
			agence.setGroupe(-1);
		}
		//Désassociation des lieux
		for (Lieu lieu : lieux) {
			lieu.reset();
		}
		
		//Changement d'agence de départ pour le récursif
		//pour donne des résutlats toujours différents.
		//On inverse la première agence avec une agence au hasard dans la liste
		//C'est ici que l'on prend un voisin de la solution précédente.
		int indexSwap = rand.nextInt(agences.size());
		Collections.swap(agences, 0, indexSwap);
		
		//Parcours de toutes les agences pour leur trouver un groupe
		//en fonction de leurs voisins en récursif et ainsi avoir des "cercles" d'agences.
		for (Agence agence : agences) {
			if(agence.getGroupe() == -1) {
				listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				courant++;
			}
		}
		
		float[] coord;						//Coordonnees du barycentre
		float min;							//Plus petite distance
		Lieu lieuLePlusPres = null;			//Lieu le plus près de l'agence barycentre
		ArrayList<Lieu> lieuUtilises = new ArrayList<Lieu>();
		Agence barycentre = new Agence();	//Barycentre de toutes les agences du groupe
		Trajet temp = new Trajet();			//Trajet entre lieux/barycentre pour calculer les distances
		
		//Recherche du lieu le plus proche du barycentre 
		//des agences dans chaque groupe
		for (GroupeAgence groupe : listeGroupes) {
			
			//Calcul du barycentre du groupe
			coord = getBarycentre(groupe);
			barycentre = new Agence();
			barycentre.setLatitude(coord[0]);
			barycentre.setLongitude(coord[1]);
			temp.setAgence(barycentre);
			
			barycentres.add(barycentre);
			groupe.setBarycentre(barycentre);
			
			//Recherche du lieu le plus proche
			min = Float.MAX_VALUE;
			for (Lieu lieu : lieux) {
				if(!lieu.isAssocie()) {
					temp.setLieu(lieu);
					if(temp.getDistanceKm() < min) {
						lieuLePlusPres = lieu;
						min = temp.getDistanceKm();
					}
				}
			}
			//Ajout du lieu à la solution et au prix
			lieuLePlusPres.setAssocie(true);
			lieuUtilises.add(lieuLePlusPres);
			prixTotal += Commun.PRIX_LIEU;
			lieuTotal ++;
			
			int agencePersonne = 0;
			int lieuPersonne = 0;
			float distance = 0;
			
			//Pour toutes les agences du groupe,
			//on créer un trajet avec le lieu le plus proche
			for (Agence agence : groupe) {
				Trajet trajet = new Trajet(agence, lieuLePlusPres);
				lieuLePlusPres.getTrajets().add(trajet);
				agence.setTrajet(trajet);
				trajets.add(trajet);
				
				//Calcul de la distance entre chaque agence et le lieu
				//et le nombre de personnes associées.
				distance = trajet.getDistanceKm();
				agencePersonne = trajet.getAgence().getNbpersonnes();
				lieuPersonne = trajet.getLieu().getNbPersonneAssociees();
				
				//Modification de la distance totale, du prix total et du nombre de personne dans le lieu
				distanceTotale += distance*agencePersonne;
				prixTotal += distance*agencePersonne*Commun.PRIX_TRAJET;
				trajet.getLieu().setNbPersonneAssociees(lieuPersonne+agencePersonne);
				
				checkLieuPersonne(trajet.getLieu());
			}
		}
		
		//Enfin, on essai de vider des lieux que ne sont pas utile
		//en répartissant les agences sur d'autres lieux.
		Collections.sort(lieuUtilises);
		while(lieuUtilises != null) {
			lieuUtilises = viderLieuMoinsRempli(lieuUtilises);
		}
		
		return indexSwap;
	}
	
	/**
	 * Vider le lieu le moins rempli en réorientant les agences sur d'autres lieux
	 * Si la solution est meilleure, elle est adoptée, sinon elle est rejetée.
	 * Lorsqu'un solution est impossible ou qu'elle est rejetée, on arrête d'enlever des lieux
	 * @param utilises Liste des lieux utilisés
	 * @return
	 */
	private ArrayList<Lieu> viderLieuMoinsRempli(ArrayList<Lieu> utilises) {
		
		ArrayList<Trajet> trajetsCopy = new ArrayList<Trajet>(trajets);
		float distanceTotaleCopy = distanceTotale;
		int lieuTotalCopy = lieuTotal;
		
		//Si le nombre de lieu minimum est atteint, on ne peut plus en enlever
		if(utilises.size() == Math.ceil(this.agences.getNombrePersonne()/Commun.MAX_PERSONNE)) {
			//System.out.println("NOMBRE DE LIEU MINIMUM");
			return null;
		}
		
		//Recherche des lieux non pleins et du lieu le plus vide
		ArrayList<Lieu> lieuNonPlein = new ArrayList<Lieu>();
		Lieu plusVide = utilises.get(0);
		for (Lieu lieu : utilises) {
			if(lieu.getNbPersonneAssociees() != 60)
				lieuNonPlein.add(lieu);
			
			if(plusVide.getNbPersonneAssociees() > lieu.getNbPersonneAssociees())
				plusVide = lieu;
		}
		Collections.sort(lieuNonPlein);
		
		//Sélection des trajets à déplacer sur d'autre lieu
		ArrayList<Trajet> trajetEnMoins = new ArrayList<Trajet>();
		for (Trajet trajet : plusVide.getTrajets()) {
			trajetEnMoins.add(trajet);
		}
		for (Trajet trajet : trajetEnMoins) {
			
			Agence agenceDuTrajet = trajet.getAgence();
			Lieu lieuDuTrajet = trajet.getLieu();
			Lieu lieuOptimum = lieuAssociePlusProche(lieuNonPlein, plusVide, agenceDuTrajet);
			
			if(lieuOptimum == null) {
				//System.out.println("AUCUN LIEU OPTIMUM");
				return null;
			}
			
			//Supprimer le trajet actuel
			trajetsCopy.remove(trajet);
			distanceTotaleCopy -= trajet.getDistanceKm();
			lieuDuTrajet.setNbPersonneAssociees(lieuDuTrajet.getNbPersonneAssociees() - agenceDuTrajet.getNbpersonnes());
			lieuDuTrajet.getTrajets().remove(trajet);
			
			//Ajouter le nouveau trajet
			Trajet nouveauTrajet = new Trajet(agenceDuTrajet, lieuOptimum);
			distanceTotaleCopy += nouveauTrajet.getDistanceKm();
			lieuOptimum.setNbPersonneAssociees(nouveauTrajet.getLieu().getNbPersonneAssociees()+agenceDuTrajet.getNbpersonnes());
			
			lieuOptimum.getTrajets().add(nouveauTrajet);
			agenceDuTrajet.setTrajet(nouveauTrajet);
			trajetsCopy.add(nouveauTrajet);
		}
		
		//Vérification de la nouvelle solution
		float prix = calculPrix();
		if(prix < prixTotal) {
			
			utilises.remove(0);
			plusVide.setAssocie(false);
			lieuTotalCopy--;
			
			trajets = trajetsCopy;
			distanceTotale = distanceTotaleCopy;
			prixTotal = prix;
			lieuTotal = lieuTotalCopy;
			
		} else {
			return null;
		}
		
		return utilises;
	}
	
	/**
	 * Trouver le lieu utilisée le plus proche et capable d'accueillir une agence
	 * @param lieuAssociesNonPlein
	 * @param plusVide
	 * @param agence
	 * @return
	 */
	private Lieu lieuAssociePlusProche(ArrayList<Lieu> lieuAssociesNonPlein, Lieu plusVide, Agence agence) {
		Trajet temp = new Trajet();
		temp.setAgence(agence);
		Lieu best = null;
		float min = Float.MAX_VALUE;
		
		for (Lieu lieu : lieuAssociesNonPlein) {
			temp.setLieu(lieu);
			if(lieu == plusVide)
				continue;
			
			if(temp.getDistanceKm() < min 
			&& lieu.getNbPersonneAssociees()+agence.getNbpersonnes() <= Commun.MAX_PERSONNE) {
				best = lieu;
				min = temp.getDistanceKm();
			}
		}
		
		return best;
	}
	
	/**
	 * Recherche récursive de voisin pour contruire les groupes d'agences
	 * Construction de "cercle" de voisin
	 * Maximisation des 60 personnes dans chaque groupe
	 * @param listeGroupes
	 * @param agence
	 * @param courant
	 * @return listeGroupes
	 */
	private ArrayList<GroupeAgence> recursifVoisin(ArrayList<GroupeAgence> listeGroupes, Agence agence, int courant) {
		
		Agence agenceVoisine;
		GroupeAgence groupeTemp;
		
		//Récupération du groupe à l'index courant
		if(listeGroupes.size() > courant)
			groupeTemp = listeGroupes.get(courant);
		else {
			groupeTemp = new GroupeAgence();
			listeGroupes.add(courant, groupeTemp);
		}
		
		//Ajout de l'agence dans le groupe si l'agence n'est pas déjà dans un groupe 
		//et que le nombre de personne du groupe ne dépasse pas 60
		if(groupeTemp.getNombrePersonne()+agence.getNbpersonnes() <= Commun.MAX_PERSONNE && agence.getGroupe()==-1) {
			
			agence.setGroupe(courant);
			groupeTemp.add(agence);
			
			//Pour chaque agence voisine, on regarde les voisins
			for (Lien liens : agence.getVoisins()) {
				agenceVoisine = liens.getVoisin(agence);
				
				//Si l'agence voisine est conforme, on l'ajoute au groupe
				if(groupeTemp.getNombrePersonne()+agenceVoisine.getNbpersonnes() <= Commun.MAX_PERSONNE 
						&& agenceVoisine.getGroupe()==-1) {
					
					agenceVoisine.setGroupe(courant);
					groupeTemp.add(agenceVoisine);
					
					//Appel récursif des voisins
					listeGroupes = recursifVoisin(listeGroupes, agence, courant);
				}
			}
		}
		
		return listeGroupes;
	}
	
	
	
	
	
	/********************************************************************
	 * 
	 * 
	 * 							ALGOGENE
	 * 
	 * 
	 * 
	 ********************************************************************/
	
	/**
	 * Algorithme genetique
	 * Dans cette fonction on crée les solutions de la première génération
	 * aléatoirement, puis on appelle la fonction récursif qui va executer l'algorithme
	 */
	public void algogene() {
		
		meilleureSolutionAlgogene = new Solution();
		Random r = new Random();
		Solution solution;
		
		//Liste correspondant aux solutions d'un génération
		List<Solution> generation = new ArrayList<Solution>();
		
		//Calcul du nombre de lieux minimun pour qu'une solution soit viable
		calculNbLieuxMin();
		
		//Génération des solutions aléatoire
		while(generation.size() < tailleGeneration) { 
			BitSet bitSet = new BitSet(lieux.size()); 
			int nbIteration = nbLieuxMin + nbLieuxMin*5 +r.nextInt(nbLieuxMin*5); 
			int nbBitSet = 0; 
			
			while(nbBitSet < nbIteration) {
				int a = r.nextInt(lieux.size());
				if(!bitSet.get(a)){ 
					bitSet.set(r.nextInt(lieux.size()));
					nbBitSet ++; 
				}
			} 
			solution = new Solution(bitSet,new Float(0), new Float(0));
			generation.add(solution);
		} 
		recursifAlgogene(generation, 1);
	}
	
	/**
	 * Fonction recursive qui applique les trois étapes de l'algorithme génétique
	 * qui sont la reproduction, le croissement, et mutation 
	 * 
	 * @param generation, La génération en cours
	 * @param iteration, L'itération actuelle
	 */
	private void recursifAlgogene(List<Solution> generation, int iteration) {
		List<Solution> solutions = new ArrayList<Solution>();
		Random r = new Random();
		
		solutions = Reproduction(generation);
		
		Solution solution;
		Solution copie;
		Solution temp;
		ArrayList<Solution> copies = new ArrayList<Solution>();	
		int indexCroissement;
		
		//Croisements et mutations en fonction d'une probabilité
		while(solutions.size() > 0) {
			solution = solutions.get(0);
			solutions.remove(solution);
			
			if(solutions.size() > 0){ 
				//Croisement
				indexCroissement = r.nextInt(solution.getLieux().size());
				temp = solutions.get(r.nextInt(solutions.size()));
				solutions.remove(temp);
				copie = solution.clone();
				
				//Parcours des bits à partir de l'index de croissement
				//Echange de ces bits entre les deux solutions (solution et temp)
				for(int l = indexCroissement + 1; l < lieux.size(); l++) {
					solution.getLieux().set(l, temp.getLieux().get(l));
					temp.getLieux().set(l,copie.getLieux().get(l));
				}
				
				copies.add(temp);
			}
				
				//Mutation
				if(r.nextFloat() < tauxMutation){
					int index = solution.getLieux().nextSetBit((r.nextInt(solution.getLieux().size())));
					if(index != -1) {
						solution.getLieux().flip(index);
					}
				}
				
				copies.add(solution);
		}
		
		//LireFichiers.ecrireFichier(copies);
		
		//Rappel de la fonction avec la nouvelle generation
		//On stop à l'itération voulu
		if(iteration < iterations) {
			recursifAlgogene(copies, iteration + 1);		
		} else {
			JOptionPane.showMessageDialog(null, "La meilleure solution donne un prix de : "+meilleureSolutionAlgogene.getPrix()+" €");
			affichage.update();
		}
	}	
	
	/**
	 * Reproduit les solutions en fonction de la probabilité de la solution
	 * Pour calculer la probabilité on inverse le prix puis on le divise par la
	 * somme des inverses, afin d'obtenir des poids qui favorise les solutions avec
	 * un prix faible
	 * @param generation, La génération à reproduire
	 * @return La liste des solutions reproduitent
	 */
	private List<Solution> Reproduction(List<Solution> generation) {
		
		float prix = 0;
		float sommeInverse = 0;
		List<Solution> solutions = new ArrayList<Solution>();
		int tailleSelection = (generation.size()/3)*2;
		
		for(Solution solution : generation) {
			//Calcul du prix de la solution
			prix = calculPrixSolution(solution.getLieux());
			solution.setPrix(prix);

			if(prix < meilleureSolutionAlgogene.getPrix() && solution.getLieux().cardinality() >= nbLieuxMin)
				meilleureSolutionAlgogene = solution;			
		}
		
		//Tri dans l'ordre croissant du prix des solutions
		Collections.sort(generation,new Comparator<Solution>() {
			public int compare(Solution b1, Solution b2){
				return b1.getPrix().compareTo(b2.getPrix());
			}
		});
		
		//Inversion des prix de la moitié des meilleures solutions
		for(int i = 0; i < tailleSelection; i++) {
			Solution solution = generation.get(i);
			float valeur = 1/solution.getPrix();
			solution.setProba(valeur);
			sommeInverse += valeur;	
		}

		//Calcul de la probabilité de choisir une solution
		for(int i = 0; i < tailleSelection; i++) {
			Solution solution = generation.get(i);
			float valeur = solution.getProba();
			valeur = valeur/sommeInverse;
			solution.setProba(valeur);
		}
		
		float proba;
		float probCumul;
		int index;
		Random r = new Random();
		
		//Reproduction avec selection aléatoire en fonction des probabilités
		for(int i = 0; i < tailleGeneration; i++) {
			proba = r.nextFloat();
			probCumul = 0;
			index = 0;
			
			for(int j = 0; j < tailleSelection; j++) {
				probCumul += generation.get(j).getProba();
				if(proba <= probCumul){
					break;
				} else {
					index++;
				}
			}
						
			Solution solution = generation.get(index).clone();
			
			solutions.add(solution);
		}
		return solutions;
	}
	
	
	private static float[] getBarycentre(ArrayList<Agence> agences) {
		
		float numerateurX = 0;
		float denominateurX = 0;
		float numerateurY = 0;
		float denominateurY = 0;
		
		for(Agence agence : agences) {
			numerateurX += agence.getNbpersonnes() * agence.getLatitude();
			denominateurX += agence.getNbpersonnes();
			numerateurY += agence.getNbpersonnes() * agence.getLongitude();
			denominateurY += agence.getNbpersonnes();
		}
		
		float x = numerateurX / denominateurX;
		float y = numerateurY / denominateurY;
		return new float[] { x, y };
	}
	/**
	 * Calcul du prix d'une solution et enregistrement des trajets si c'est une meilleure solution
	 * @param solution, La solution à calculer
	 * @return Le prix de la solution
	 */
	private float calculPrixSolution(BitSet solution) {

		Lieu best;
		float min;
		Lieu courant;
		Trajet temp = new Trajet();
		float prix = 0;
		float distance = 0;
		List<Trajet> trajetsTmp = new ArrayList<Trajet>();
		prix = Commun.PRIX_LIEU*solution.cardinality();

		//Parcours des agences pour leur attribuer le lieu le plus proche 
		for (Agence agence : agences) {
			temp.setAgence(agence);
			best = null;
			min = Float.MAX_VALUE;
			
			//Parcours des bits et récupération du lieu associé
			for (int i = solution.nextSetBit(0); i >= 0; i = solution.nextSetBit(i+1)) {
				if (i == Integer.MAX_VALUE || i >= lieux.size()) {
					break; // or (i+1) would overflow
				}
				courant = lieux.get(i);
				courant.setNbPersonneAssociees(0);

				temp.setLieu(courant);
				//On vérifie que le lieu possède assez de places pour accepter l'agence
				if(best == null || temp.getDistanceKm() < min && courant.getNbPersonneAssociees()+agence.getNbpersonnes() < 60) {
					best = courant;
					min = temp.getDistanceKm();
				}
			}

			//Enregistrement du trajet
			Trajet trajet = new Trajet(agence, best);
			trajetsTmp.add(trajet);
			best.getTrajets().add(trajet);
			agence.setTrajet(trajet);

			//Calcul du prix et de la distance puis enregistrement des personnes dans le lieu
			prix += trajet.getDistanceKm()*agence.getNbpersonnes()*Commun.PRIX_TRAJET;
			distance += trajet.getDistanceKm()*agence.getNbpersonnes();
			best.setNbPersonneAssociees(best.getNbPersonneAssociees()+agence.getNbpersonnes());
		}
		
		//Si le prix est meilleur que la meilleure solution courant et que la solution est valable on l'enregistre
		if(prix < meilleureSolutionAlgogene.getPrix() && solution.cardinality() >= nbLieuxMin) {
			resetTrajets();
			prixTotal = prix;
			distanceTotale = distance;
			lieuTotal = solution.cardinality();
			trajets = new ArrayList<Trajet>(trajetsTmp);
			
			if(rafraichirCarte){
				affichage.update();
			
				try { Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		return prix;
	}
	
	private void calculNbLieuxMin() {
		int nbPersonnes = 0;
    	for(Agence agence : agences) {
    		nbPersonnes += agence.getNbpersonnes();
    	}
    	
    	nbLieuxMin = nbPersonnes / 60;
    	if(nbPersonnes % 60 != 0)
    		nbLieuxMin += 1;
    	
    	System.out.println("Nb Personnes : " + nbPersonnes + " Lieux Min : "+ nbLieuxMin);
	}
	
	public InterfaceVisuelle getAffichage() {
		return affichage;
	}
	public ArrayList<Lieu> getLieux() {
		return lieux;
	}
	public GroupeAgence getAgences() {
		return agences;
	}
	public ArrayList<Agence> getBarycentres() {
		return barycentres;
	}

	public ArrayList<Trajet> getTrajets() {
		return trajets;
	}
	
	public float getDistanceTotale() {
		return distanceTotale;
	}

	public float getPrixTotal() {
		return prixTotal;
	}

	public int getLieuTotal() {
		return lieuTotal;
	}

	public int getNbLieuxMin() {
		return nbLieuxMin;
	}

	public boolean isRafraichirCarte() {
		return rafraichirCarte;
	}

	public int getTemperature() {
		return temperature;
	}

	public int getIterations() {
		return iterations;
	}

	public String getPathFichier() {
		return pathFichier;
	}
	
	public int getTailleGeneration() {
		return tailleGeneration;
	}
	
	public float getTauxMutation() {
		return tauxMutation;
	}

	public void setPathFichier(String pathFichier) {
		this.pathFichier = pathFichier;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	public void setNbLieuxMin(int nbLieuxMin) {
		this.nbLieuxMin = nbLieuxMin;
	}

	public void setAgences(GroupeAgence listes) {
		resetTrajets();
		this.agences = listes;
	}
	
	public void setRafraichirCarte(boolean rafraichirCarte) {
		this.rafraichirCarte = rafraichirCarte;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	
	public void setTailleGeneration(int tailleGeneration) {
		this.tailleGeneration = tailleGeneration;
	}

	public void setTauxMutation(float tauxMutation) {
		this.tauxMutation = tauxMutation;
	}

	public boolean isEnCalcul() {
		return enCalcul;
	}

	public void setEnCalcul(boolean enCalcul) {
		this.enCalcul = enCalcul;
	}
}
