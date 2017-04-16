/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;

/**
 * @author chedeler
 *
 */
public class ELRPhenotype {

	private static Logger logger = Logger.getLogger(ELRPhenotype.class);

	private LinkedHashSet<ELREntityLevelRelationship> elrs = new LinkedHashSet<ELREntityLevelRelationship>();
	private Set<SuperAbstract> matchedButUnassociatedSourceEntities = new HashSet<SuperAbstract>();
	private Set<SuperAbstract> matchedButUnassociatedTargetEntities = new HashSet<SuperAbstract>();

	private final ELRChromosome chromosome;

	private double fitness;

	public ELRPhenotype(ELRChromosome chromosome) {
		logger.debug("in ELRPhenotype");
		logger.debug("chromosome: " + chromosome);
		this.chromosome = chromosome;
	}

	public double calculateFitnessOfPhenotype() {
		logger.debug("in calculateFitnessOfPhenotype");
		logger.debug("chromosome: " + chromosome);

		double sumSimilarityScoreTimesCoverage = 0d;
		double sumCoverage = 0d;

		for (ELREntityLevelRelationship elr : elrs) {
			logger.debug("elr: " + elr);
			double elrSimilarityScore = elr.getSimilarityScore();
			logger.debug("elrSimilarityScore: " + elrSimilarityScore);
			int elrEntitySet1Size = elr.getNumberOfSourceEntities();
			int elrEntitySet2Size = elr.getNumberOfTargetEntities();
			logger.debug("elrEntitySet1Size: " + elrEntitySet1Size);
			logger.debug("elrEntitySet2Size: " + elrEntitySet2Size);
			double coverage = (elrEntitySet1Size + elrEntitySet2Size) / 2;
			logger.debug("coverage: " + coverage);
			sumSimilarityScoreTimesCoverage += elrSimilarityScore * coverage;
			logger.debug("sumSimilarityScoreTimesCoverage: " + sumSimilarityScoreTimesCoverage);
			sumCoverage += coverage;
			logger.debug("sumCoverage: " + sumCoverage);
		}
		logger.debug("sumSimilarityScoreTimesCoverage: " + sumSimilarityScoreTimesCoverage);
		logger.debug("sumCoverage: " + sumCoverage);

		fitness = (sumSimilarityScoreTimesCoverage * sumSimilarityScoreTimesCoverage) / sumCoverage;
		logger.debug("fitness: " + fitness);
		if (sumCoverage == 0)
			fitness = 0d;
		logger.debug("fitness: " + fitness);
		logger.debug("chromosome: " + chromosome);
		return fitness;
	}

	public int getNumberOfVectorsInVectorSpaceWithEquivalentSuperLexicals(LinkedHashSet<SuperLexical> equivalentSuperLexicals, boolean isSource) {
		logger.debug("in getNumberOfVectorsInVectorSpaceWithEquivalentSuperLexicals");
		logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
		logger.debug("chromosome: " + chromosome);
		logger.debug("isSource: " + isSource);
		int numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals = 0;
		numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals += this.getNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals(
				equivalentSuperLexicals, isSource);
		logger.debug("numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals: " + numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals);
		numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals += this.getNumberOfELRsWithEquivalentSuperLexicals(equivalentSuperLexicals, isSource);
		logger.debug("numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals: " + numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals);
		return numberOfVectorsInVectorSpaceWithEquivalentSuperLexicals;
	}

	protected int getNumberOfELRsWithEquivalentSuperLexicals(LinkedHashSet<SuperLexical> equivalentSuperLexicals, boolean isSource) {
		logger.debug("in getNumberOfELRsWithEquivalentSuperLexicals");
		logger.debug("chromosome: " + chromosome);
		logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
		logger.debug("isSource: " + isSource);
		Set<ELREntityLevelRelationship> elrsWithEquivalentSuperLexicals = new HashSet<ELREntityLevelRelationship>();
		for (ELREntityLevelRelationship elr : elrs) {
			logger.debug("elr: " + elr);
			List<VectorSpaceVector> vectorSpaceVectors;
			if (isSource)
				vectorSpaceVectors = elr.getSourceVectors();
			else
				vectorSpaceVectors = elr.getTargetVectors();
			logger.debug("vectorSpaceVectors: " + vectorSpaceVectors);
			logger.debug("vectorSpaceVectors.size(): " + vectorSpaceVectors.size());
			for (VectorSpaceVector vectorSpaceVector : vectorSpaceVectors) {
				logger.debug("vectorSpaceVector: " + vectorSpaceVector);
				if (vectorSpaceVector.containsAnEquivalentSuperLexical(equivalentSuperLexicals)) {
					logger.debug("vectorSpaceVector contains equivalentSuperLexicals - add elr to elrsWithEquivalentSuperLexicals");
					elrsWithEquivalentSuperLexicals.add(elr);
				}
				logger.debug("elrsWithEquivalentSuperLexicals.size(): " + elrsWithEquivalentSuperLexicals.size());
			}
		}
		return elrsWithEquivalentSuperLexicals.size();
	}

	protected int getNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals(Set<SuperLexical> equivalentSuperLexicals, boolean isSource) {
		logger.debug("in getNumberOfUnassociatedEntitiesWithEquivalentSuperLexicals");
		logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
		logger.debug("chromosome: " + chromosome);
		logger.debug("isSource: " + isSource);
		Set<SuperAbstract> unassociatedEntitiesWithEquivalentSuperLexicals = new HashSet<SuperAbstract>();
		Set<SuperAbstract> unassociatedEntities;
		if (isSource)
			unassociatedEntities = this.matchedButUnassociatedSourceEntities;
		else
			unassociatedEntities = this.matchedButUnassociatedTargetEntities;
		logger.debug("unassociatedEntities: " + unassociatedEntities);
		for (SuperAbstract unassociatedEntity : unassociatedEntities) {
			logger.debug("unassociatedEntity: " + unassociatedEntity);
			for (SuperLexical equivalentSuperLexical : equivalentSuperLexicals) {
				logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
				if (unassociatedEntity.getSuperLexicals().contains(equivalentSuperLexical)) {
					logger.debug("unassociatedEntity contains equivalentSuperLexical - add to unassociatedEntitiesWithEquivalentSuperLexicals");
					unassociatedEntitiesWithEquivalentSuperLexicals.add(unassociatedEntity);
				}
			}
		}
		logger.debug("unassociatedEntitiesWithEquivalentSuperLexicals.size(): " + unassociatedEntitiesWithEquivalentSuperLexicals.size());
		return unassociatedEntitiesWithEquivalentSuperLexicals.size();
	}

	/**
	 * @return the elrs
	 */
	public LinkedHashSet<ELREntityLevelRelationship> getElrs() {
		return elrs;
	}

	public void addELR(ELREntityLevelRelationship elr) {
		this.elrs.add(elr);
	}

	/**
	 * @param elrs the elrs to set
	 */
	public void setElrs(LinkedHashSet<ELREntityLevelRelationship> elrs) {
		this.elrs = elrs;
	}

	/**
	 * @return the matchedButUnassociatedSourceEntities
	 */
	public Set<SuperAbstract> getMatchedButUnassociatedSourceEntities() {
		return matchedButUnassociatedSourceEntities;
	}

	public void addMatchedButUnassociatedSourceEntity(SuperAbstract matchedButUnassociatedSourceEntity) {
		this.matchedButUnassociatedSourceEntities.add(matchedButUnassociatedSourceEntity);
	}

	/**
	 * @param matchedButUnassociatedSourceEntities the matchedButUnassociatedSourceEntities to set
	 */
	public void setMatchedButUnassociatedSourceEntities(Set<SuperAbstract> matchedButUnassociatedSourceEntities) {
		this.matchedButUnassociatedSourceEntities = matchedButUnassociatedSourceEntities;
	}

	/**
	 * @return the matchedButUnassociatedTargetEntities
	 */
	public Set<SuperAbstract> getMatchedButUnassociatedTargetEntities() {
		return matchedButUnassociatedTargetEntities;
	}

	public void addMatchedButUnassociatedTargetEntity(SuperAbstract matchedButUnassociatedTargetEntity) {
		this.matchedButUnassociatedTargetEntities.add(matchedButUnassociatedTargetEntity);
	}

	/**
	 * @param matchedButUnassociatedTargetEntities the matchedButUnassociatedTargetEntities to set
	 */
	public void setMatchedButUnassociatedTargetEntities(Set<SuperAbstract> matchedButUnassociatedTargetEntities) {
		this.matchedButUnassociatedTargetEntities = matchedButUnassociatedTargetEntities;
	}

	/**
	 * @return the chromosome
	 */
	public ELRChromosome getChromosome() {
		return chromosome;
	}

	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

}
