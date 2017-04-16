/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.DecoderService;

/**
 * @author chedeler
 *
 */
@Service(value = "decoderService")
public class DecoderServiceImpl implements DecoderService {

	private static Logger logger = Logger.getLogger(DecoderServiceImpl.class);

	//TODO this isn't ideal ... but I need a handle to the chromosome somewhere for the initializer and everything else to work
	//TODO needs to be set manually at the moment ... 
	private ELRChromosome chromosome;

	public ELRPhenotype decodeAndGenerateVectorSpaceVectorsAndCalculateWeights(Set<PairOfEntitySets> pairsOfEntitySets, ELRChromosome chromosome) {
		logger.debug("in decodeAndGenerateVectorSpaceVectors");
		logger.debug("this: " + this);
		logger.debug("chromosome: " + chromosome);
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		for (PairOfEntitySets pairOfEntitySets : pairsOfEntitySets) {
			logger.debug("pairOfEntitySets: " + pairOfEntitySets);
			logger.debug("pairOfEntitySets.getSourceEntitySet(): " + pairOfEntitySets.getSourceEntitySet());
			logger.debug("pairOfEntitySets.getTargetEntitySet(): " + pairOfEntitySets.getTargetEntitySet());
		}
		ELRPhenotype phenotype = this.decode(pairsOfEntitySets, chromosome);
		logger.debug("phenotype: " + phenotype);

		for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
			logger.debug("elr: " + elr);
			List<VectorSpaceVector> sourceVectorSpaceVectors = elr.generateSourceVectorSpaceVectors();
			logger.debug("sourceVectorSpaceVectors: " + sourceVectorSpaceVectors);
			List<VectorSpaceVector> targetVectorSpaceVectors = elr.generateTargetVectorSpaceVectors();
			logger.debug("targetVectorSpaceVectors: " + targetVectorSpaceVectors);
		}

		for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
			logger.debug("elr: " + elr);
			for (VectorSpaceVector sourceVectorSpaceVector : elr.getSourceVectors()) {
				logger.debug("sourceVectorSpaceVector: " + sourceVectorSpaceVector);
				LinkedHashMap<CanonicalModelConstruct[], Double> sourceConstructsWeightMapWithTfIdfWeights = sourceVectorSpaceVector
						.calculateTfIdfWeightsForVectorElementArrays();
				logger.debug("sourceConstructsWeightMapWithTfIdfWeights: " + sourceConstructsWeightMapWithTfIdfWeights);
			}
			for (VectorSpaceVector targetVectorSpaceVector : elr.getTargetVectors()) {
				logger.debug("targetVectorSpaceVector: " + targetVectorSpaceVector);
				LinkedHashMap<CanonicalModelConstruct[], Double> targetConstructsWeightMapWithTfIdfWeights = targetVectorSpaceVector
						.calculateTfIdfWeightsForVectorElementArrays();
				logger.debug("targetConstructsWeightMapWithTfIdfWeights: " + targetConstructsWeightMapWithTfIdfWeights);
			}

		}
		return phenotype;
	}

	protected ELRPhenotype decode(Set<PairOfEntitySets> pairsOfEntitySets, ELRChromosome chromosome) {
		logger.debug("in decode");
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		for (PairOfEntitySets pairOfEntitySets : pairsOfEntitySets) {
			logger.debug("pairOfEntitySets: " + pairOfEntitySets);
			logger.debug("pairOfEntitySets.getSourceEntitySet(): " + pairOfEntitySets.getSourceEntitySet());
			logger.debug("pairOfEntitySets.getTargetEntitySet(): " + pairOfEntitySets.getTargetEntitySet());
		}
		logger.debug("chromosome: " + chromosome);
		logger.debug("chromosome.getMatchedSuperAbstractsInSourceSchemas(): " + chromosome.getMatchedSuperAbstractsInSourceSchemas());
		logger.debug("chromosome.getMatchedSuperAbstractsInSourceSchemas().size(): " + chromosome.getMatchedSuperAbstractsInSourceSchemas().size());
		logger.debug("chromosome.getMatchedSuperAbstractsInTargetSchemas(): " + chromosome.getMatchedSuperAbstractsInTargetSchemas());
		logger.debug("chromosome.getMatchedSuperAbstractsInTargetSchemas().size(): " + chromosome.getMatchedSuperAbstractsInTargetSchemas().size());
		logger.debug("chromosome.getSuperAbstractsInSourceSchemas(): " + chromosome.getSuperAbstractsInSourceSchemas());
		logger.debug("chromosome.getSuperAbstractsInSourceSchemas().size(): " + chromosome.getSuperAbstractsInSourceSchemas().size());
		logger.debug("chromosome.getSuperAbstractsInTargetSchemas(): " + chromosome.getSuperAbstractsInTargetSchemas());
		logger.debug("chromosome.getSuperAbstractsInTargetSchemas().size(): " + chromosome.getSuperAbstractsInTargetSchemas().size());
		ELRPhenotype phenotype = new ELRPhenotype(chromosome);
		logger.debug("phenotype: " + phenotype);

		LinkedHashSet<ELREntityLevelRelationship> elrs = new LinkedHashSet<ELREntityLevelRelationship>();

		for (PairOfEntitySets pairOfEntitySets : pairsOfEntitySets) {
			logger.debug("pairOfEntitySets: " + pairOfEntitySets);
			//TODO think about the following ... I had introduced LinkedHashSets to ensure the same order of the constructs in the vectors, not sure it's working though
			//TODO think about whether it's necessary and it if is, try to test for it
			LinkedHashSet<SuperAbstract> sourceEntitySet = new LinkedHashSet<SuperAbstract>(pairOfEntitySets.getSourceEntitySet());
			LinkedHashSet<SuperAbstract> targetEntitySet = new LinkedHashSet<SuperAbstract>(pairOfEntitySets.getTargetEntitySet());
			logger.debug("sourceEntitySet: " + sourceEntitySet);
			logger.debug("targetEntitySet: " + targetEntitySet);
			ELREntityLevelRelationship elr = new ELREntityLevelRelationship(chromosome, phenotype, sourceEntitySet, targetEntitySet);
			logger.debug("elr: " + elr);
			elrs.add(elr);
		}
		logger.debug("elrs: " + elrs);
		logger.debug("elrs.size(): " + elrs.size());
		phenotype.setElrs(elrs);
		logger.debug("before calling identifyUnmatchedEntities for superAbstracts in sourceSchemas");
		Set<SuperAbstract> unmatchedSourceSuperAbstracts = this.identifyUnmatchedEntities(chromosome.getSuperAbstractsInSourceSchemas(),
				chromosome.getMatchedSuperAbstractsInSourceSchemas());
		logger.debug("unmatchedSourceSuperAbstracts: " + unmatchedSourceSuperAbstracts);
		logger.debug("before calling identifyMatchedButUnassociatedEntities for superAbstracts in sourceSchemas");
		phenotype.setMatchedButUnassociatedSourceEntities(this.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				chromosome.getSuperAbstractsInSourceSchemas(), unmatchedSourceSuperAbstracts, true));
		logger.debug("before calling identifyUnmatchedEntities for superAbstracts in targetSchemas");
		Set<SuperAbstract> unmatchedTargetSuperAbstracts = this.identifyUnmatchedEntities(chromosome.getSuperAbstractsInTargetSchemas(),
				chromosome.getMatchedSuperAbstractsInTargetSchemas());
		logger.debug("unmatchedTargetSuperAbstracts: " + unmatchedTargetSuperAbstracts);
		logger.debug("before calling identifyMatchedButUnassociatedEntities for superAbstracts in targetSchemas");
		phenotype.setMatchedButUnassociatedTargetEntities(this.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				chromosome.getSuperAbstractsInTargetSchemas(), unmatchedTargetSuperAbstracts, false));
		return phenotype;
	}

	protected Set<SuperAbstract> identifyUnmatchedEntities(Set<SuperAbstract> superAbstractsInSchemas,
			Set<SuperAbstract> matchedSuperAbstractsInSchemas) {
		logger.debug("in identifyUnmatchedEntities");
		logger.debug("superAbstractsInSchemas: " + superAbstractsInSchemas);
		logger.debug("superAbstractsInSchemas.size(): " + superAbstractsInSchemas.size());
		Set<SuperAbstract> unmatchedSourceSuperAbstracts = new HashSet<SuperAbstract>(superAbstractsInSchemas);
		logger.debug("unmatchedSourceSuperAbstracts: " + unmatchedSourceSuperAbstracts);
		logger.debug("unmatchedSourceSuperAbstracts.size(): " + unmatchedSourceSuperAbstracts.size());
		unmatchedSourceSuperAbstracts.removeAll(matchedSuperAbstractsInSchemas);
		logger.debug("unmatchedSourceSuperAbstracts after removing matchedSuperAbstracts: " + unmatchedSourceSuperAbstracts);
		logger.debug("unmatchedSourceSuperAbstracts.size(): " + unmatchedSourceSuperAbstracts.size());
		return unmatchedSourceSuperAbstracts;
	}

	protected Set<SuperAbstract> identifyMatchedButUnassociatedEntities(Set<PairOfEntitySets> pairsOfEntitySets,
			Set<SuperAbstract> superAbstractsInSchemas, Set<SuperAbstract> unmatchedSuperAbstracts, boolean isSource) {
		logger.debug("in identifyUnassociatedEntities");
		logger.debug("pairsOfEntitySets: " + pairsOfEntitySets);
		logger.debug("pairsOfEntitySets.size(): " + pairsOfEntitySets.size());
		logger.debug("superAbstractsInSchemas: " + superAbstractsInSchemas);
		logger.debug("superAbstractsInSchemas.size(): " + superAbstractsInSchemas.size());
		logger.debug("unmatchedSuperAbstracts: " + unmatchedSuperAbstracts);
		logger.debug("unmatchedSuperAbstracts.size(): " + unmatchedSuperAbstracts.size());
		logger.debug("isSource: " + isSource);
		Set<SuperAbstract> matchedButUnassociatedEntities = new HashSet<SuperAbstract>(superAbstractsInSchemas);
		logger.debug("matchedButUnassociatedEntities: " + matchedButUnassociatedEntities);
		logger.debug("matchedButUnassociatedEntities.size(): " + matchedButUnassociatedEntities.size());
		matchedButUnassociatedEntities.removeAll(unmatchedSuperAbstracts);
		logger.debug("matchedButUnassociatedEntities after removing all unmatchedSuperAbstracts: " + matchedButUnassociatedEntities);
		logger.debug("matchedButUnassociatedEntities.size(): " + matchedButUnassociatedEntities.size());
		for (PairOfEntitySets pairOfEntitySets : pairsOfEntitySets) {
			logger.debug("pairOfEntitySets: " + pairOfEntitySets);
			Set<SuperAbstract> associatedEntities;
			if (isSource)
				associatedEntities = pairOfEntitySets.getSourceEntitySet();
			else
				associatedEntities = pairOfEntitySets.getTargetEntitySet();
			logger.debug("associatedEntities: " + associatedEntities);
			logger.debug("associatedEntities.size(): " + associatedEntities.size());
			matchedButUnassociatedEntities.removeAll(associatedEntities);
			logger.debug("matchedButUnassociatedEntities after removing associatedEntities: " + matchedButUnassociatedEntities);
			logger.debug("matchedButUnassociatedEntities.size(): " + matchedButUnassociatedEntities.size());
		}
		logger.debug("matchedButUnassociatedEntities: " + matchedButUnassociatedEntities);
		logger.debug("matchedButUnassociatedEntities.size(): " + matchedButUnassociatedEntities.size());
		return matchedButUnassociatedEntities;
	}

	/**
	 * @return the chromosome
	 */
	public ELRChromosome getChromosome() {
		return chromosome;
	}

	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(ELRChromosome chromosome) {
		this.chromosome = chromosome;
	}
}
