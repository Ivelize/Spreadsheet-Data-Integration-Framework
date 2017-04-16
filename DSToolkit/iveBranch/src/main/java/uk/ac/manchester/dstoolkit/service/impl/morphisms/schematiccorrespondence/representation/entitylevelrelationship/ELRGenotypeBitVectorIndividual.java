/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import org.apache.log4j.Logger;

import ec.vector.BitVectorIndividual;

/**
 * @author chedeler
 *
 */
public class ELRGenotypeBitVectorIndividual extends BitVectorIndividual {

	private static Logger logger = Logger.getLogger(ELRGenotypeBitVectorIndividual.class);

	private ELRPhenotype elrPhenotype;

	/**
	 * @return the elrPhenotype
	 */
	public ELRPhenotype getElrPhenotype() {
		logger.debug("in getElrPhenotype");
		logger.debug("elrPhenotype: " + elrPhenotype);
		return elrPhenotype;
	}

	/**
	 * @param elrPhenotype the elrPhenotype to set
	 */
	public void setElrPhenotype(ELRPhenotype elrPhenotype) {
		logger.debug("in setElrPhenotype");
		logger.debug("elrPhenotype: " + elrPhenotype);
		this.elrPhenotype = elrPhenotype;
	}

	@Override
	public Object clone() {
		logger.debug("in clone");
		logger.debug("this: " + this);
		logger.debug("elrPhenotype: " + elrPhenotype);
		if (this != null)
			logger.debug("this.genotypeToStringForHumans(): " + this.genotypeToStringForHumans());
		logger.debug("this.getElrPhenotype(): " + this.getElrPhenotype());
		ELRGenotypeBitVectorIndividual clonedObj = (ELRGenotypeBitVectorIndividual) super.clone();
		logger.debug("clonedObj: " + clonedObj);
		if (clonedObj != null)
			logger.debug("clonedObj.genotypeToStringForHumans(): " + clonedObj.genotypeToStringForHumans());
		clonedObj.setElrPhenotype(this.elrPhenotype);
		logger.debug("clonedObj.getElrPhenotype(): " + clonedObj.getElrPhenotype());
		if (this.fitness != null)
			logger.debug("this.fitness.fitness(): " + this.fitness.fitness());
		logger.debug("this.evaluated: " + this.evaluated);
		return clonedObj;
	}

}
