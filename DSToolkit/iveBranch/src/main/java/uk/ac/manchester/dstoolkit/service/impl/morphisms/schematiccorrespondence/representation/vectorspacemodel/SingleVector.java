/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;

/**
 * @author chedeler
 *
 */
public class SingleVector extends VectorSpaceVector {

	private static Logger logger = Logger.getLogger(SingleVector.class);

	public SingleVector(ELRChromosome chromosome, ELRPhenotype phenotype, boolean isSource) {
		super(chromosome, phenotype, isSource);
	}

	@Override
	public LinkedHashMap<CanonicalModelConstruct[], Double> generateVector(LinkedHashSet<SuperAbstract> entitySet) {
		logger.debug("in generateVector");
		logger.debug("this: " + this);
		logger.debug("isSource: " + this.isSource());
		logger.debug("chromosome: " + this.getChromosome());
		logger.debug("entitySet: " + entitySet);
		logger.debug("entitySet.size(): " + entitySet.size());

		if (entitySet.size() > 1)
			logger.debug("more than one superAbstract ... something wrong");
		else {
			LinkedHashMap<CanonicalModelConstruct[], Double> singleVector = new LinkedHashMap<CanonicalModelConstruct[], Double>();
			for (SuperAbstract sa : entitySet) {
				logger.debug("sa: " + sa);
				CanonicalModelConstruct[] saArray = { sa };
				singleVector.put(saArray, new Double(0d));
				logger.debug("singleVector: " + singleVector);
				logger.debug("singleVector.size(): " + singleVector.size());
				logger.debug("sa.getSuperLexicals().size(): " + sa.getSuperLexicals().size());
				for (SuperLexical sl : sa.getSuperLexicals()) {
					logger.debug("sl: " + sl);
					CanonicalModelConstruct[] slArray = { sl };
					singleVector.put(slArray, new Double(0d));
				}
				logger.debug("singleVector: " + singleVector);
				logger.debug("singleVector.size(): " + singleVector.size());
				for (CanonicalModelConstruct[] constructs : singleVector.keySet()) {
					logger.debug("constructs: " + constructs);
					for (CanonicalModelConstruct construct : constructs) {
						logger.debug("construct: " + construct);
					}
				}
			}
			logger.debug("singleVector: " + singleVector);
			logger.debug("singleVector.size(): " + singleVector.size());
			for (CanonicalModelConstruct[] constructs : singleVector.keySet()) {
				logger.debug("constructs: " + constructs);
				for (CanonicalModelConstruct construct : constructs)
					logger.debug("construct: " + construct);
			}
			logger.debug("this: " + this);
			super.setConstructsWeightsMap(singleVector);
			return singleVector;
		}
		return null;
	}

}
