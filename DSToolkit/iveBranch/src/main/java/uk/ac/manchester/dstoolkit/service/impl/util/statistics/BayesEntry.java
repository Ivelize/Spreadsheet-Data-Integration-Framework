package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;

/***
 * This is the entry used for the SemanticMatrix of bayes
 * 
 * @author klitos
 */
public class BayesEntry extends SemanticMatrixEntry {
		
	static Logger logger = Logger.getLogger(BayesEntry.class);
	
	/***
	 * Bayes formula variables
	 */
	private double likelihood = 0.0;
	private double negLikelihood = 0.0;	
	
	/** Keep the priors and posteriors for each round, 
	 * the lists keep track of the values as they change
	 * */
	ArrayList<Double> priorsHistory = null; //Holds a history of the evolution of priors in this entry
	ArrayList<Double> posteriorsHistory = null; //Holds a history of the evolution of posteriors in this entry
	
	private double lastPrior	 = 0.0; //Holds the most recent prior
	private double lastPosterior = 0.0;	//Holds the most recent posterior
	
	/**
	 * Keep a history of the evidence been accumulated
	 */
	Set<BooleanVariables> accumulatedEvidenceSet = null;
				
	/**
	 * Empty constructor
	 */
	public BayesEntry(double prior) {
		if (posteriorsHistory == null) { 
			posteriorsHistory = new ArrayList<Double>();
		}

		if (priorsHistory == null) { 
			priorsHistory = new ArrayList<Double>();
		}

		this.setLikelihood(0.0);
		this.setNegLikelihood(0.0);			
		this.updatePrior(prior);	
	}	
	
	/***
	 * 
	 * simScore - the similarity score of the matcher
	 * prior - for bayes, before seeing any evidence
	 * posterior - updated probability after seeing the evidence
	 */
	public BayesEntry(double prior, double likelihood, double negLikelihood) {
		if (posteriorsHistory == null) { 
			posteriorsHistory = new ArrayList<Double>(); 
		}

		if (priorsHistory == null) { 
			priorsHistory = new ArrayList<Double>();
		}
		
		this.setLikelihood(likelihood);
		this.setNegLikelihood(negLikelihood);		
		this.updatePrior(prior);		
	}//end constructor	
	
	public void updatePrior( double prior ) {
		if (priorsHistory == null) { 
			priorsHistory = new ArrayList<Double>();
		}
		
		//update the latest prior
		this.setLastPrior(prior);
	
		//add it to the list so we can keep track of the history, top of the list is the oldest, bottom the latest
		priorsHistory.add(prior);
	}//end addPrior()
	
	/**
	 * Calculate posterior from previous prior using likelihoods and Bayes formula.
	 */
	public double updatePosterior() {
		
		double result = 0.0;
		
		//calculate posterior using Bayes
		result = (likelihood * lastPrior) / ((likelihood * lastPrior) + (negLikelihood * (1 - lastPrior)));
				
		//update posterior variable
		this.setLastPosterior(result);
				
		//add posterior to the history list
		posteriorsHistory.add(result);		
				
		return result;		
	}//end updatePosterior()	

	/*** 
	 * Method that keeps a history of the semantic evidence being accumulated by this cell.
	 **/
	public void addSemEvidenceInHistory( BooleanVariables bVar ) {
		if (accumulatedEvidenceSet == null) { 
			accumulatedEvidenceSet = new HashSet<BooleanVariables>();			
		}
		
		this.accumulatedEvidenceSet.add(bVar);		
	}//end addEvidenceInHistory()	

	public Set<BooleanVariables> getHistoryOfSemEvidence() {
		return this.accumulatedEvidenceSet;
	}//end getHistoryOfSemEvidence()
	
	/***
	 * Supportive methods
	 */	
	public double getLastPrior() {
		return lastPrior;
	}

	public double getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
	}

	public double getNegLikelihood() {
		return negLikelihood;
	}

	public void setNegLikelihood(double negLikelihood) {
		this.negLikelihood = negLikelihood;
	}

	private void setLastPrior(double lastPrior) {
		this.lastPrior = lastPrior;
	}

	public double getLastPosterior() {
		return lastPosterior;
	}
	
	public String getLastPosteriorAsPerc() {		
		double perc = this.lastPosterior * 100;
        DecimalFormat df = new DecimalFormat("#.##");		
		return df.format(perc);
	}

	public void setLastPosterior(double lastPosterior) {
		this.lastPosterior = lastPosterior;
	}

	@Override
	public String toString() {
		return "BayesEntry [priors=" + priorsHistory + ",\n posteriors=" + posteriorsHistory
				+ "]";
	}
}//end class