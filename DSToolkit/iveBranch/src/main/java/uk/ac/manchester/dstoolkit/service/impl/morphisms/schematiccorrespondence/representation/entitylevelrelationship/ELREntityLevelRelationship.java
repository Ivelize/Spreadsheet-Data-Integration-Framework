/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.PartitioningType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryService;

/**
 * @author chedeler
 *
 */
@Configurable(autowire = Autowire.BY_NAME)
public class ELREntityLevelRelationship {

	private static Logger logger = Logger.getLogger(ELREntityLevelRelationship.class);

	private ELRChromosome chromosome;
	private ELRPhenotype phenotype;

	//calling it entity because that's what's it called in Chenjuan's paper

	private final LinkedHashSet<SuperAbstract> sourceEntitySet;
	private final LinkedHashSet<SuperAbstract> targetEntitySet;

	@Autowired
	@Qualifier("vectorSpaceVectorFactoryService")
	private VectorSpaceVectorFactoryService vectorSpaceVectorFactoryService;

	private List<VectorSpaceVector> sourceVectors;
	private List<VectorSpaceVector> targetVectors;
	private VectorSpaceVector sourceVectorWithSimilarityScore;
	private VectorSpaceVector targetVectorWithSimilarityScore;

	private Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap;
	private Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap;

	private double similarityScore;
	private PartitioningType partitioningType;

	public ELREntityLevelRelationship(ELRChromosome chromosome, ELRPhenotype phenotype, SuperAbstract entity1, SuperAbstract entity2) {
		logger.debug("in ELR");
		logger.debug("this: " + this);
		logger.debug("entity1: " + entity1);
		logger.debug("entity2: " + entity2);
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		this.chromosome = chromosome;
		this.phenotype = phenotype;
		sourceEntitySet = new LinkedHashSet<SuperAbstract>();
		targetEntitySet = new LinkedHashSet<SuperAbstract>();
		sourceEntitySet.add(entity1);
		targetEntitySet.add(entity2);
	}

	public ELREntityLevelRelationship(ELRChromosome chromosome, ELRPhenotype phenotype, LinkedHashSet<SuperAbstract> entitySet1,
			LinkedHashSet<SuperAbstract> entitySet2) {
		logger.debug("in ELR");
		logger.debug("this: " + this);
		logger.debug("entitySet1: " + entitySet1);
		logger.debug("entitySet2: " + entitySet2);
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		this.chromosome = chromosome;
		this.phenotype = phenotype;
		this.sourceEntitySet = entitySet1;
		this.targetEntitySet = entitySet2;
	}

	public List<VectorSpaceVector> generateSourceVectorSpaceVectors() {
		logger.debug("in generateSourceVectorSpaceVectors");
		logger.debug("this: " + this);
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		logger.debug("sourceEntitySet.size(): " + sourceEntitySet.size());

		for (SuperAbstract sourceEntity : sourceEntitySet) {
			logger.debug("sourceEntity: " + sourceEntity);
		}

		if (this.vectorSpaceVectorFactoryService == null)
			this.vectorSpaceVectorFactoryService = new VectorSpaceVectorFactoryServiceImpl(); //TODO hack for testing purposes ... should run all tests with spring to use autowire

		this.sourceVectors = this.vectorSpaceVectorFactoryService
				.generateVectorSpaceVectorsForEntitySet(chromosome, phenotype, true, sourceEntitySet);
		return this.sourceVectors;
	}

	public List<VectorSpaceVector> generateTargetVectorSpaceVectors() {
		logger.debug("in generateTargetVectorSpaceVectors");
		logger.debug("this: " + this);
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		logger.debug("targetEntitySet.size(): " + targetEntitySet.size());

		for (SuperAbstract targetEntity : targetEntitySet) {
			logger.debug("targetEntity: " + targetEntity);
		}

		if (this.vectorSpaceVectorFactoryService == null)
			this.vectorSpaceVectorFactoryService = new VectorSpaceVectorFactoryServiceImpl();
		this.targetVectors = this.vectorSpaceVectorFactoryService.generateVectorSpaceVectorsForEntitySet(chromosome, phenotype, false,
				targetEntitySet);
		return this.targetVectors;
	}

	public double calculateCosineSimilarityOfSourceAndTargetVectorCombinationsAndIdentifyCombinationWithHighestSimilarity() {
		logger.debug("in calculateCosineSimilarityOfSourceAndTargetVectorCombinationsAndIdentifyCombinationWithHighestSimilarity");
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		double currentMaxSimilarity = -1;
		VectorSpaceVector currentSourceVectorWithMaxSimilarity = null;
		VectorSpaceVector currentTargetVectorWithMaxSimilarity = null;

		logger.debug("this.sourceVectors.size(): " + this.sourceVectors);
		logger.debug("this.targetVectors.size(): " + this.targetVectors);

		for (VectorSpaceVector sourceVector : this.sourceVectors) {
			for (VectorSpaceVector targetVector : this.targetVectors) {
				logger.debug("sourceVector: " + sourceVector);
				logger.debug("targetVector: " + targetVector);

				//TODO this is only checking for equivalent superLexicals, not superAbstracts, at least I think so - check ... should perhaps be renamed to make that clear
				equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap = this
						.identifyEquivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores(sourceVector, targetVector, true);// this method isn't tested, but methods called from within are
				logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
						+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
				equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap = this
						.identifyEquivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores(targetVector, sourceVector, false);// this method isn't tested, but methods called from within are
				logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap: "
						+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);

				double similarity = this.calculateSimilaritiesOfVectors(sourceVector.getConstructsWeightsMap(),
						targetVector.getConstructsWeightsMap(), equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
						equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap); //this method is tested
				logger.debug("similarity: " + similarity);
				logger.debug("currentMaxSimilarity: " + currentMaxSimilarity);

				if (currentMaxSimilarity < similarity) {
					logger.debug("got new max similarity");
					currentMaxSimilarity = similarity;
					currentSourceVectorWithMaxSimilarity = sourceVector;
					currentTargetVectorWithMaxSimilarity = targetVector;
					logger.debug("currentMaxSimilarity: " + currentMaxSimilarity);
					logger.debug("currentSourceVectorWithMaxSimilarity: " + currentSourceVectorWithMaxSimilarity);
					logger.debug("currentTargetVectorWithMaxSimilarity: " + currentTargetVectorWithMaxSimilarity);
				} else if (currentMaxSimilarity == similarity) {
					//TODO might have to check the type of vectors and give priority to HP over VP vectors
					logger.debug("currentMaxSimilarity == similarity");
					logger.debug("TODO might have to check the type of vectors and give priority to HP over VP vectors");
				}
			}
		}

		logger.debug("currentMaxSimilarity: " + currentMaxSimilarity);
		logger.debug("currentSourceVectorWithMaxSimilarity: " + currentSourceVectorWithMaxSimilarity);
		logger.debug("currentTargetVectorWithMaxSimilarity: " + currentTargetVectorWithMaxSimilarity);
		this.sourceVectorWithSimilarityScore = currentSourceVectorWithMaxSimilarity;
		this.targetVectorWithSimilarityScore = currentTargetVectorWithMaxSimilarity;
		this.similarityScore = currentMaxSimilarity;
		logger.debug("sourceVectorWithSimilarityScore: " + sourceVectorWithSimilarityScore);
		logger.debug("targetVectorWithSimilarityScore: " + targetVectorWithSimilarityScore);
		logger.debug("similarityScore: " + similarityScore);
		this.partitioningType = this.determinePartitioningType(currentSourceVectorWithMaxSimilarity, currentTargetVectorWithMaxSimilarity); // this method is tested
		logger.debug("partitioningType: " + partitioningType);
		return this.similarityScore;
	}

	protected PartitioningType determinePartitioningType(VectorSpaceVector sourceVector, VectorSpaceVector targetVector) {
		logger.debug("in determinePartitioningType");
		logger.debug("vectorSpaceVectorFactoryService: " + vectorSpaceVectorFactoryService);
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		if (sourceVector instanceof SingleVector && targetVector instanceof SingleVector) {
			logger.debug("both vectors are singleVectors - no partitioningType to determine - return null");
			return null;
		} else if (sourceVector instanceof SingleVector && targetVector instanceof HorizontalPartitioningVector) {
			logger.debug("sourceVector is SingleVector and targetVector is HorizontalPartitioningVector - return HP_VS_HP as partitioningType");
			return PartitioningType.HP_VS_HP;
		} else if (sourceVector instanceof SingleVector && targetVector instanceof VerticalPartitioningVector) {
			logger.debug("sourceVector is SingleVector and targetVector is VerticalPartitioningVector - return HP_VS_VP as partitioningType");
			return PartitioningType.HP_VS_VP;
		} else if (sourceVector instanceof HorizontalPartitioningVector && targetVector instanceof SingleVector) {
			logger.debug("sourceVector is HorizontalPartitioningVector and targetVector is SingleVector - return HP_VS_HP as partitioningType");
			return PartitioningType.HP_VS_HP;
		} else if (sourceVector instanceof HorizontalPartitioningVector && targetVector instanceof HorizontalPartitioningVector) {
			logger.debug("sourceVector is HorizontalPartitioningVector and targetVector is HorizontalPartitioningVector - return HP_VS_HP as partitioningType");
			return PartitioningType.HP_VS_HP;
		} else if (sourceVector instanceof HorizontalPartitioningVector && targetVector instanceof VerticalPartitioningVector) {
			logger.debug("sourceVector is HorizontalPartitioningVector and targetVector is VerticalPartitioningVector - return HP_VS_VP as partitioningType");
			return PartitioningType.HP_VS_VP;
		} else if (sourceVector instanceof VerticalPartitioningVector && targetVector instanceof SingleVector) {
			logger.debug("sourceVector is VerticalPartitioningVector and targetVector is SingleVector - return VP_VS_HP as partitioningType");
			return PartitioningType.VP_VS_HP;
		} else if (sourceVector instanceof VerticalPartitioningVector && targetVector instanceof HorizontalPartitioningVector) {
			logger.debug("sourceVector is VerticalPartitioningVector and targetVector is HorizontalPartitioningVector - return VP_VS_HP as partitioningType");
			return PartitioningType.VP_VS_HP;
		} else if (sourceVector instanceof VerticalPartitioningVector && targetVector instanceof VerticalPartitioningVector) {
			logger.debug("sourceVector is VerticalPartitioningVector and targetVector is VerticalPartitioningVector - return VP_VS_VP as partitioningType");
			return PartitioningType.VP_VS_VP;
		}
		logger.debug("unspecified partitioningType - returning null");
		return null;
	}

	protected double calculateSumOfSquaredTfIdfWeightsInConstructsWeightMap(Map<CanonicalModelConstruct[], Double> constructsWeightMap) {
		logger.debug("in calculateSumOfSquaredTfIdfWeightsInConstructsWeightMap");
		logger.debug("constructsWeightMap: " + constructsWeightMap);
		double sumOfSquaredTfIdfWeightsInConstructsWeightMap = 0d;
		for (CanonicalModelConstruct[] constructs : constructsWeightMap.keySet()) {
			logger.debug("constructs: " + constructs);
			for (CanonicalModelConstruct construct : constructs)
				logger.debug("construct: " + construct);
			double weight = constructsWeightMap.get(constructs);
			logger.debug("weight: " + weight);
			sumOfSquaredTfIdfWeightsInConstructsWeightMap += (weight * weight);
			logger.debug("sumOfSquaredTfIdfWeightsInConstructsWeightMap: " + sumOfSquaredTfIdfWeightsInConstructsWeightMap);
		}
		logger.debug("sumOfSquaredTfIdfWeightsInConstructsWeightMap: " + sumOfSquaredTfIdfWeightsInConstructsWeightMap);
		return sumOfSquaredTfIdfWeightsInConstructsWeightMap;
	}

	protected double calculateDemoninatorOfSimilarityFunction(LinkedHashMap<CanonicalModelConstruct[], Double> sourceVectorConstructsWeightMap,
			LinkedHashMap<CanonicalModelConstruct[], Double> targetVectorConstructsWeightMap) {
		logger.debug("in calculateDemoninatorOfSimilarityFunction");
		logger.debug("sourceVectorConstructsWeightMap: " + sourceVectorConstructsWeightMap);
		logger.debug("sourceVectorConstructsWeightMap: " + targetVectorConstructsWeightMap);
		logger.debug("sourceVectorConstructsWeightMap.size(): " + sourceVectorConstructsWeightMap.size());
		logger.debug("sourceVectorConstructsWeightMap.size(): " + targetVectorConstructsWeightMap.size());

		double sumOfSourceWeightsSquared = this.calculateSumOfSquaredTfIdfWeightsInConstructsWeightMap(sourceVectorConstructsWeightMap);
		logger.debug("sumOfSourceWeightsSquared: " + sumOfSourceWeightsSquared);
		double sumOfTargetWeightsSquared = this.calculateSumOfSquaredTfIdfWeightsInConstructsWeightMap(targetVectorConstructsWeightMap);
		logger.debug("sumOfTargetWeightsSquared: " + sumOfTargetWeightsSquared);
		double denominator = Math.sqrt(sumOfSourceWeightsSquared) * Math.sqrt(sumOfTargetWeightsSquared);
		logger.debug("denominator: " + denominator);
		return denominator;
	}

	protected double calculateNumeratorInOneDirection(LinkedHashMap<CanonicalModelConstruct[], Double> sourceVectorConstructsWeightMap,
			LinkedHashMap<CanonicalModelConstruct[], Double> targetVectorConstructsWeightMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap) {
		double numerator = 0d;

		for (CanonicalModelConstruct[] sourceConstructs : sourceVectorConstructsWeightMap.keySet()) {
			logger.debug("sourceConstructs: " + sourceConstructs);
			for (CanonicalModelConstruct construct : sourceConstructs)
				logger.debug("construct: " + construct);

			for (CanonicalModelConstruct[] targetConstructs : targetVectorConstructsWeightMap.keySet()) {
				logger.debug("targetConstructs: " + targetConstructs);
				for (CanonicalModelConstruct construct : targetConstructs)
					logger.debug("construct: " + construct);

				logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.size(): "
						+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.size());

				logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.containsKey(sourceConstructs): "
						+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.containsKey(sourceConstructs));

				if (equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.containsKey(sourceConstructs))
					logger.debug("sourceConstructsTargetConstructsDerivedMatchingsMap.get(sourceConstructs).containsKey(targetConstructs): "
							+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.get(sourceConstructs).containsKey(targetConstructs));

				if (equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.containsKey(sourceConstructs)
						&& equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.get(sourceConstructs).containsKey(targetConstructs)) {
					logger.debug("found matching between source and target construct");
					double sourceWeight = sourceVectorConstructsWeightMap.get(sourceConstructs);
					double targetWeight = targetVectorConstructsWeightMap.get(targetConstructs);
					logger.debug("sourceWeight: " + sourceWeight);
					logger.debug("targetWeight: " + targetWeight);
					double avgMatchSimilarity = equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.get(sourceConstructs).get(
							targetConstructs);
					logger.debug("avgMatchSimilarity: " + avgMatchSimilarity);

					numerator += avgMatchSimilarity * sourceWeight * targetWeight;
					logger.debug("numerator: " + numerator);
				} else
					logger.debug("sourceConstructsSourceConstructsDerivedMatchingsMap doesn't contain sourceConstructs or sourceConstructsSourceConstructsDerivedMatchingsMap doesn't contain targetConstructs for sourceConstructs");
			}
		}
		logger.debug("numerator: " + numerator);
		return numerator;
	}

	protected double calculateSimilaritiesOfVectors(LinkedHashMap<CanonicalModelConstruct[], Double> sourceVectorConstructsWeightMap,
			LinkedHashMap<CanonicalModelConstruct[], Double> targetVectorConstructsWeightMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in calculateSimilaritiesOfVectors");
		logger.debug("this: " + this);
		logger.debug("sourceVectorConstructsWeightMap: " + sourceVectorConstructsWeightMap);
		logger.debug("sourceVectorConstructsWeightMap: " + targetVectorConstructsWeightMap);
		logger.debug("sourceVectorConstructsWeightMap.size(): " + sourceVectorConstructsWeightMap.size());
		logger.debug("sourceVectorConstructsWeightMap.size(): " + targetVectorConstructsWeightMap.size());
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.size(): "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.size());
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.size(): "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.size());

		//denominator
		double denominator = this.calculateDemoninatorOfSimilarityFunction(sourceVectorConstructsWeightMap, targetVectorConstructsWeightMap);
		logger.debug("denominator: " + denominator);

		if (denominator == 0) {
			logger.debug("denominator == 0, return similarityScore = 0");
			this.similarityScore = 0d;
			return similarityScore;
		}

		//source to target numerator
		double sourceToTargetNumerator = this.calculateNumeratorInOneDirection(sourceVectorConstructsWeightMap, targetVectorConstructsWeightMap,
				equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		//target to source numerator
		double targetToSourceNumerator = this.calculateNumeratorInOneDirection(targetVectorConstructsWeightMap, sourceVectorConstructsWeightMap,
				equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		logger.debug("sourceToTargetNumerator: " + sourceToTargetNumerator);
		logger.debug("targetToSourceNumerator: " + targetToSourceNumerator);
		logger.debug("denominator: " + denominator);
		double sourceToTarget = sourceToTargetNumerator / denominator;
		double targetToSource = targetToSourceNumerator / denominator;
		logger.debug("sourceToTarget: " + sourceToTarget);
		logger.debug("targetToSource: " + targetToSource);

		double similarityScore = (sourceToTarget + targetToSource) / 2;
		logger.debug("similarityScore: " + similarityScore);
		logger.debug("this: " + this);
		logger.debug("leaving calculateSimilaritiesOfVectors");
		return similarityScore;
	}

	protected Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> identifyEquivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector, boolean isSourceToTarget) {
		logger.debug("in identifyEquivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);

		Set<CanonicalModelConstruct[]> sourceConstructsArraysSet = sourceVector.getConstructsWeightsMap().keySet();
		logger.debug("sourceConstructsArraysSet: " + sourceConstructsArraysSet);
		Set<CanonicalModelConstruct[]> targetConstructsArraysSet = targetVector.getConstructsWeightsMap().keySet();
		logger.debug("targetConstructsArraysSet: " + targetConstructsArraysSet);

		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> targetConstructsArrayWithMatchedSourceConstructsArraysWithMaxAvgMatchingScoresFromSourceToTarget = this
				.identifyTargetConstructsArraysMatchedToSourceConstructsArraysWithMaxAvgMatchingScoreFromSourceToTarget(sourceConstructsArraysSet,
						targetConstructsArraysSet, isSourceToTarget); // methods called within this method are tested

		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores = this
				.identifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource(targetConstructsArrayWithMatchedSourceConstructsArraysWithMaxAvgMatchingScoresFromSourceToTarget); // this method is tested

		return equivalentTermsBetweenSourceAndTargetVectorsWithMaxAvgMatchingScores;
	}

	protected Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> identifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource(
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget) {
		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource = new HashMap<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>>();
		logger.debug("in identifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource");
		logger.debug("targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget: "
				+ targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget);
		for (CanonicalModelConstruct[] targetConstructsArray : targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget
				.keySet()) {
			logger.debug("targetConstructsArray: " + targetConstructsArray);
			for (CanonicalModelConstruct targetConstruct : targetConstructsArray)
				logger.debug("targetConstruct: " + targetConstruct);

			Map<CanonicalModelConstruct[], Double> matchedTargetConstructsArrayWithMaxAvgMatchingScores = new HashMap<CanonicalModelConstruct[], Double>();
			CanonicalModelConstruct[] sourceConstructsArray = null;
			if (targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.get(targetConstructsArray).size() > 1) {
				logger.debug("targetConstructsArray matched with more than one sourceConstructsArray - identify sourceConstructsArrayWithMaxAvgMatchingScore among those matched");
				logger.debug("chromosome: " + chromosome);
				Map<CanonicalModelConstruct[], Double> matchedSourceConstructsArrayWithMaxAvgMatchingScore = chromosome
						.identifyConstructsArrayWithMaxAvgMatchingScores(targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget
								.get(targetConstructsArray)); // method called in there is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
				logger.debug("matchedSourceConstructsArrayWithMaxAvgMatchingScore: " + matchedSourceConstructsArrayWithMaxAvgMatchingScore);
				logger.debug("matchedSourceConstructsArrayWithMaxAvgMatchingScore.size(): "
						+ matchedSourceConstructsArrayWithMaxAvgMatchingScore.size()); //TODO assumed to be 1, not checked
				sourceConstructsArray = matchedSourceConstructsArrayWithMaxAvgMatchingScore.keySet().iterator().next();
				logger.debug("sourceConstructsArray: " + sourceConstructsArray);
				for (CanonicalModelConstruct sourceConstruct : sourceConstructsArray)
					logger.debug("sourceConstruct: " + sourceConstruct);
				double maxAvgMatchingScore = matchedSourceConstructsArrayWithMaxAvgMatchingScore.get(sourceConstructsArray);
				logger.debug("maxAvgMatchingScores: " + maxAvgMatchingScore);
				matchedTargetConstructsArrayWithMaxAvgMatchingScores.put(targetConstructsArray, maxAvgMatchingScore);
			} else {
				logger.debug("only one sourceConstructsArray matched with targetConstructsArray, place in matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource");
				sourceConstructsArray = targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget
						.get(targetConstructsArray).keySet().iterator().next();
				logger.debug("sourceConstructsArray: " + sourceConstructsArray);
				for (CanonicalModelConstruct sourceConstruct : sourceConstructsArray)
					logger.debug("sourceConstruct: " + sourceConstruct);
				double maxAvgMatchingScore = targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.get(
						targetConstructsArray).get(sourceConstructsArray);
				logger.debug("maxAvgMatchingScores: " + maxAvgMatchingScore);
				matchedTargetConstructsArrayWithMaxAvgMatchingScores.put(targetConstructsArray, maxAvgMatchingScore);
			}
			logger.debug("sourceConstructsArray: " + sourceConstructsArray);
			for (CanonicalModelConstruct sourceConstruct : sourceConstructsArray)
				logger.debug("sourceConstruct: " + sourceConstruct);
			matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.put(sourceConstructsArray,
					matchedTargetConstructsArrayWithMaxAvgMatchingScores);
		}
		return matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource;
	}

	//needs chromosome, which in turn calls methods in DerivedOneToOneMatchingsGeneratorServiceImpl
	protected Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> identifyTargetConstructsArraysMatchedToSourceConstructsArraysWithMaxAvgMatchingScoreFromSourceToTarget(
			Set<CanonicalModelConstruct[]> sourceConstructsArraysSet, Set<CanonicalModelConstruct[]> targetConstructsArraysSet,
			boolean isSourceToTarget) {
		logger.debug("in identifyTargetConstructsArraysMatchedToSourceConstructsArraysWithMaxAvgMatchingScoreFromSourceToTarget");
		logger.debug("sourceConstructsArraysSet: " + sourceConstructsArraysSet);
		logger.debug("targetConstructsArraysSet: " + targetConstructsArraysSet);
		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget = new HashMap<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>>();

		for (CanonicalModelConstruct[] sourceConstructsArray : sourceConstructsArraysSet) {
			logger.debug("sourceConstructsArray: " + sourceConstructsArray);
			List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList = this
					.getConstructsArraysMatchedWithConstructsInSourceConstructsArray(sourceConstructsArray, targetConstructsArraysSet,
							isSourceToTarget); // method called in there is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
			logger.debug("constructMatchedWithConstructArraysAndDerivedMatchingsList: " + constructMatchedWithConstructArraysAndDerivedMatchingsList);
			Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> groupedMatchedTargetConstructsArraysWithCollatedDerivedOneToOneMatchings = chromosome
					.groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(constructMatchedWithConstructArraysAndDerivedMatchingsList); // method called in there is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
			logger.debug("groupedMatchedTargetConstructsArraysWithCollatedDerivedOneToOneMatchings: "
					+ groupedMatchedTargetConstructsArraysWithCollatedDerivedOneToOneMatchings);
			Map<CanonicalModelConstruct[], Double> matchedTargetConstructsArraysWithAvgMatchingScore = chromosome
					.calculateAvgOfMatchingScoresForMatchedConstructsArrays(sourceConstructsArray,
							groupedMatchedTargetConstructsArraysWithCollatedDerivedOneToOneMatchings); // method called in there is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
			logger.debug("matchedTargetConstructsArraysWithAvgMatchingScore: " + matchedTargetConstructsArraysWithAvgMatchingScore);
			Map<CanonicalModelConstruct[], Double> matchedTargetConstructsArraysWithMaxAvgMatchingScore = chromosome
					.identifyConstructsArrayWithMaxAvgMatchingScores(matchedTargetConstructsArraysWithAvgMatchingScore); // method called in there is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
			logger.debug("matchedTargetConstructsArraysWithMaxAvgMatchingScore: " + matchedTargetConstructsArraysWithMaxAvgMatchingScore);
			if (matchedTargetConstructsArraysWithMaxAvgMatchingScore.size() > 0) {
				targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget = this
						.placeMatchedTargetConstructsArrayAndSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTargetInMap(
								targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget, sourceConstructsArray,
								matchedTargetConstructsArraysWithMaxAvgMatchingScore);// this method is tested
			}
		}
		return targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget;
	}

	protected Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> placeMatchedTargetConstructsArrayAndSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTargetInMap(
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget,
			CanonicalModelConstruct[] sourceConstructsArray,
			Map<CanonicalModelConstruct[], Double> matchedTargetConstructsArraysWithMaxAvgMatchingScore) {
		logger.debug("in placeMatchedTargetConstructsArrayAndSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTargetInMap");
		logger.debug("targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget: "
				+ targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget);
		logger.debug("targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.size(): "
				+ targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.size());
		logger.debug("sourceConstructsArray: " + sourceConstructsArray);
		for (CanonicalModelConstruct sourceConstruct : sourceConstructsArray)
			logger.debug("sourceConstruct: " + sourceConstruct);
		logger.debug("matchedTargetConstructsArraysWithMaxAvgMatchingScore: " + matchedTargetConstructsArraysWithMaxAvgMatchingScore);
		logger.debug("matchedTargetConstructsArraysWithMaxAvgMatchingScore.size(): " + matchedTargetConstructsArraysWithMaxAvgMatchingScore.size()); //TODO assumed to be 1, not checked

		if (matchedTargetConstructsArraysWithMaxAvgMatchingScore.size() > 0) {
			CanonicalModelConstruct[] matchedTargetConstructs = matchedTargetConstructsArraysWithMaxAvgMatchingScore.keySet().iterator().next();
			logger.debug("matchedTargetConstructs: " + matchedTargetConstructs);
			for (CanonicalModelConstruct matchedTargetConstruct : matchedTargetConstructs)
				logger.debug("matchedTargetConstructs: " + matchedTargetConstructs);
			double avgMatchingScoreForMatchedTargetConstructsArray = matchedTargetConstructsArraysWithMaxAvgMatchingScore
					.get(matchedTargetConstructs);
			logger.debug("avgMatchingScoreForMatchedTargetConstructsArray: " + avgMatchingScoreForMatchedTargetConstructsArray);

			Map<CanonicalModelConstruct[], Double> matchedSourceConstructsArraysWithAvgMatchingScore = null;
			if (targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.containsKey(matchedTargetConstructs)) {
				logger.debug("found matchedTargetConstructs in targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget");
				matchedSourceConstructsArraysWithAvgMatchingScore = targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget
						.get(matchedTargetConstructs);
				logger.debug("matchedSourceConstructsArraysWithAvgMatchingScore: " + matchedSourceConstructsArraysWithAvgMatchingScore);
				if (!matchedSourceConstructsArraysWithAvgMatchingScore.containsKey(sourceConstructsArray)) {
					logger.debug("add sourceConstructsArray to its map of matchedSourceConstructsArraysWithAvgMatchingScore");
					matchedSourceConstructsArraysWithAvgMatchingScore.put(sourceConstructsArray, avgMatchingScoreForMatchedTargetConstructsArray);
				} else
					logger.error("sourceConstructsArray already in map of matchedSourceConstructsArraysWithAvgMatchingScore"); //TODO this shouldn't occur, but it's not tested that it doesn't
			} else {
				logger.debug("targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget doesn't contain matchedTargetConstructsArraysWithMaxAvgMatchingScore");
				matchedSourceConstructsArraysWithAvgMatchingScore = new HashMap<CanonicalModelConstruct[], Double>();
				matchedSourceConstructsArraysWithAvgMatchingScore.put(sourceConstructsArray, avgMatchingScoreForMatchedTargetConstructsArray);
				logger.debug("matchedSourceConstructsArraysWithAvgMatchingScore: " + matchedSourceConstructsArraysWithAvgMatchingScore);
			}
			targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.put(matchedTargetConstructs,
					matchedSourceConstructsArraysWithAvgMatchingScore);
		}
		return targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget;
	}

	//needs chromosome, which in turn calls method in DerivedOneToOneMatchingsGeneratorServiceImpl
	protected List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> getConstructsArraysMatchedWithConstructsInSourceConstructsArray(
			CanonicalModelConstruct[] sourceConstructsArray, Set<CanonicalModelConstruct[]> targetConstructsArraysSet, boolean isSourceToTarget) {
		logger.debug("in getConstructsArraysMatchedWithConstructsInSourceConstructsArray");
		List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList = new ArrayList<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>>();
		for (CanonicalModelConstruct constructInSourceConstructsArray : sourceConstructsArray) {
			logger.debug("constructInSourceConstructsArray: " + constructInSourceConstructsArray);
			Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedTargetConstructsWithDerivedMatchings = this.chromosome
					.getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(constructInSourceConstructsArray,
							targetConstructsArraysSet, isSourceToTarget); //this method is tested in DerivedOneToOneMatchingsGeneratorServiceImpl
			logger.debug("matchedTargetConstructsWithDerivedMatchings: " + matchedTargetConstructsWithDerivedMatchings);
			constructMatchedWithConstructArraysAndDerivedMatchingsList.add(matchedTargetConstructsWithDerivedMatchings);
		}
		return constructMatchedWithConstructArraysAndDerivedMatchingsList;
	}

	/*
	//TODO need to do this between all combinations of source and target vectors
	protected LinkedHashMap<CanonicalModelConstruct[], LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>>> determinePairsOfSourceAndTargetVectorElementArrayWithDerivedMatchingsWithMaxScoreBetweenConstructsInVectors(
			LinkedHashSet<CanonicalModelConstruct[]> sourceVector, LinkedHashSet<CanonicalModelConstruct[]> targetVector, boolean isSourceToTarget) {
		//a source construct can only be matched to a single target construct (the one with the maximum score) and vice versa

		logger.debug("in determinePairsOfSourceAndTargetVectorElementArrayWithDerivedMatchingsWithMaxScoreBetweenConstructsInVectors");
		logger.debug("this: " + this);
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("sourceVector.size(): " + sourceVector.size());
		logger.debug("targetVector.size(): " + targetVector.size());
		logger.debug("isSourceToTarget: " + isSourceToTarget);

		LinkedHashMap<CanonicalModelConstruct[], LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>>> sourceConstructsTargetConstructsDerivedMatchingsMap = new LinkedHashMap<CanonicalModelConstruct[], LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>>>();

		LinkedHashMap<CanonicalModelConstruct[], LinkedHashMap<CanonicalModelConstruct[], Double>> matchedTargetConstructsSourceConstructsWithMaxAvgScoreMap = new LinkedHashMap<CanonicalModelConstruct[], LinkedHashMap<CanonicalModelConstruct[], Double>>();
		//I think I need the sourceConstructs too to be able to remove the matches with the lower avg score and the matching score ...

		for (CanonicalModelConstruct[] sourceConstructs : sourceVector) {
			logger.debug("sourceConstructs: " + sourceConstructs);
			logger.debug("sourceConstructs.length: " + sourceConstructs.length);

			for (CanonicalModelConstruct construct : sourceConstructs) {
				logger.debug("construct: " + construct);
			}

			LinkedHashSet<DerivedOneToOneMatching> derivedMatchingsWithMaxAvgScore = null;
			double maxAvgMatchingScore = 0; //avg of all derivedMatching between source and target constructs
			CanonicalModelConstruct[] targetConstructsOfDerivedMatchingsWithMaxAvgScore = null; //the corresponding element in target vector (consisting of all constructs of that element)

			//TODO can have null as constructs, in particular for VPs ... might have to deal with those separately

			for (CanonicalModelConstruct[] targetConstructs : targetVector) {
				logger.debug("targetConstructs: " + targetConstructs);
				logger.debug("targetConstructs.length: " + targetConstructs.length);

				double sumMatchingScore = 0;
				LinkedHashSet<DerivedOneToOneMatching> derivedMatchings = new LinkedHashSet<DerivedOneToOneMatching>();

				logger.debug("sumMatchingScore: " + sumMatchingScore);

				for (CanonicalModelConstruct construct : targetConstructs) {
					logger.debug("construct: " + construct);
				}

				logger.debug("sumMatchingScore: " + sumMatchingScore);

				for (CanonicalModelConstruct sourceConstruct : sourceConstructs) {
					logger.debug("sourceConstruct: " + sourceConstruct);

					for (CanonicalModelConstruct targetConstruct : targetConstructs) {
						logger.debug("targetConstruct: " + targetConstruct);
						logger.debug("sumMatchingScore: " + sumMatchingScore);

						if (sourceConstruct instanceof SuperAbstract && targetConstruct instanceof SuperAbstract) {
							logger.debug("source and target constructs are SuperAbstracts");

							logger.debug("isSourceToTarget: " + isSourceToTarget);
							logger.debug("chromosome.getSourceSAtargetSAsDerivedMatchingMap().containsKey(sourceConstruct): "
									+ chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getSourceSAtargetSAsDerivedMatchingMap()
											.containsKey(sourceConstruct));
							logger.debug("chromosome.getTargetSAsourceSAsDerivedMatchingMap().containsKey(sourceConstruct): "
									+ chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getTargetSAsourceSAsDerivedMatchingMap()
											.containsKey(sourceConstruct));

							if ((isSourceToTarget
									&& chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getSourceSAtargetSAsDerivedMatchingMap()
											.containsKey(sourceConstruct) && chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
									.getSourceSAtargetSAsDerivedMatchingMap().get(sourceConstruct).containsKey(targetConstruct))
									|| (!isSourceToTarget
											&& chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
													.getTargetSAsourceSAsDerivedMatchingMap().containsKey(sourceConstruct) && chromosome
											.getMatchedConstructsWithDerivedMatchingsInformation().getTargetSAsourceSAsDerivedMatchingMap()
											.get(sourceConstruct).containsKey(targetConstruct))) {
								logger.debug("isSourceToTarget: " + isSourceToTarget);
								logger.debug("found derived matching between source and target sa");
								DerivedOneToOneMatching derivedMatching = null;
								if (isSourceToTarget)
									derivedMatching = chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
											.getSourceSAtargetSAsDerivedMatchingMap().get(sourceConstruct).get(targetConstruct);
								else
									derivedMatching = chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
											.getTargetSAsourceSAsDerivedMatchingMap().get(sourceConstruct).get(targetConstruct);
								logger.debug("derivedMatching: " + derivedMatching);

								if (!derivedMatchings.contains(derivedMatching)) {
									logger.debug("derivedMatching not in derivedMatchings, add score to sum");
									logger.debug("sumMatchingScore: " + sumMatchingScore);
									logger.debug("derivedMatching.getSumOfMatchingScores(): " + derivedMatching.getSumOfMatchingScores());
									sumMatchingScore += derivedMatching.getSumOfMatchingScores();
									logger.debug("sumMatchingScore: " + sumMatchingScore);
									logger.debug("derivedMatchings.size(): " + derivedMatchings.size());
									derivedMatchings.add(derivedMatching);
									logger.debug("derivedMatchings.size(): " + derivedMatchings.size());
								} else {
									logger.debug("derivedMatching in derivedMatchings - do nothing");
								}
							} else {
								logger.debug("not found matching between source and target construct");
							}
						} else if (sourceConstruct instanceof SuperLexical && targetConstruct instanceof SuperLexical) {
							logger.debug("source and target constructs are SuperLexicals");
							logger.debug("isSourceToTarget: " + isSourceToTarget);
							logger.debug("chromosome.getSourceSLtargetSLsDerivedMatchingMap().containsKey(sourceConstruct): "
									+ chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getSourceSLtargetSLsDerivedMatchingMap()
											.containsKey(sourceConstruct));
							logger.debug("chromosome.getTargetSLsourceSLsDerivedMatchingMap().containsKey(sourceConstruct): "
									+ chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getTargetSLsourceSLsDerivedMatchingMap()
											.containsKey(sourceConstruct));
							if ((isSourceToTarget
									&& chromosome.getMatchedConstructsWithDerivedMatchingsInformation().getSourceSLtargetSLsDerivedMatchingMap()
											.containsKey(sourceConstruct) && chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
									.getSourceSLtargetSLsDerivedMatchingMap().get(sourceConstruct).containsKey(targetConstruct))
									|| (!isSourceToTarget
											&& chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
													.getTargetSLsourceSLsDerivedMatchingMap().containsKey(sourceConstruct) && chromosome
											.getMatchedConstructsWithDerivedMatchingsInformation().getTargetSLsourceSLsDerivedMatchingMap()
											.get(sourceConstruct).containsKey(targetConstruct))) {
								logger.debug("isSourceToTarget: " + isSourceToTarget);
								logger.debug("found derived matching between source and target sl");
								DerivedOneToOneMatching derivedMatching = null;
								if (isSourceToTarget)
									derivedMatching = chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
											.getSourceSLtargetSLsDerivedMatchingMap().get(sourceConstruct).get(targetConstruct);
								else
									derivedMatching = chromosome.getMatchedConstructsWithDerivedMatchingsInformation()
											.getTargetSLsourceSLsDerivedMatchingMap().get(sourceConstruct).get(targetConstruct);
								logger.debug("derivedMatching: " + derivedMatching);

								if (!derivedMatchings.contains(derivedMatching)) {
									logger.debug("derivedMatching not in derivedMatchings, add score to sum");
									logger.debug("sumMatchingScore: " + sumMatchingScore);
									logger.debug("derivedMatching.getSumOfMatchingScores(): " + derivedMatching.getSumOfMatchingScores());
									sumMatchingScore += derivedMatching.getSumOfMatchingScores();
									logger.debug("sumMatchingScore: " + sumMatchingScore);
									logger.debug("derivedMatchings.size(): " + derivedMatchings.size());
									derivedMatchings.add(derivedMatching);
									logger.debug("derivedMatchings.size(): " + derivedMatchings.size());
								} else {
									logger.debug("derivedMatching in derivedMatchings - do nothing");
								}
							} else {
								logger.debug("not found matching between source and target construct");
							}
						} else
							logger.debug("sourceConstruct and targetConstruct are of different type, i.e., SuperLexical and SuperAbstract, or one/both of them is/are null - ignore");
					}
				}

				logger.debug("sumMatchingScore: " + sumMatchingScore);
				logger.debug("derivedMatchings.size(): " + derivedMatchings.size());
				double avgMatchingScore = 0d;
				//if (derivedMatchings.size() > 0) avgMatchingScore = sumMatchingScore / derivedMatchings.size();
				logger.debug("sourceConstructs.length: " + sourceConstructs.length);
				logger.debug("targetConstructs.length: " + targetConstructs.length);
				avgMatchingScore = sumMatchingScore / (sourceConstructs.length + targetConstructs.length); //to dilute when there aren't matches between all the constructs and when there are null constructs in vector element of HP vector
				logger.debug("avgMatchingScore: " + avgMatchingScore);
				logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);

				if (maxAvgMatchingScore < avgMatchingScore) {
					logger.debug("found new maxAvgMatchingScore - keep corresponding derived matchings");
					maxAvgMatchingScore = avgMatchingScore;
					derivedMatchingsWithMaxAvgScore = derivedMatchings;
					targetConstructsOfDerivedMatchingsWithMaxAvgScore = targetConstructs;
					logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);
					logger.debug("derivedMatchingsWithMaxAvgScore: " + derivedMatchingsWithMaxAvgScore);
					logger.debug("targetConstructsOfDerivedMatchingsWithMaxAvgScore: " + targetConstructsOfDerivedMatchingsWithMaxAvgScore);
				} else
					logger.debug("didn't find new maxAvgMatchingScore - do nothing");
			}

			if (derivedMatchingsWithMaxAvgScore != null && targetConstructsOfDerivedMatchingsWithMaxAvgScore != null) {
				logger.debug("found derivedMatchings with maxAvgScore for sourceConstructs");
				logger.debug("sourceConstructs: " + sourceConstructs);
				logger.debug("derivedMatchingsWithMaxAvgScore: " + derivedMatchingsWithMaxAvgScore);
				logger.debug("targetConstructsOfDerivedMatchingsWithMaxAvgScore: " + targetConstructsOfDerivedMatchingsWithMaxAvgScore);
				for (CanonicalModelConstruct targetConstructWithMaxAvgScore : targetConstructsOfDerivedMatchingsWithMaxAvgScore) {
					logger.debug("targetConstructWithMaxAvgScore: " + targetConstructWithMaxAvgScore);
				}
				logger.debug("maxAvgMatchingScore: " + maxAvgMatchingScore);

				//keep only the one with the max score ... 
				//might loose some matches though ... Chenjuan seems to be doing the same ... so just do that too

				if (matchedTargetConstructsSourceConstructsWithMaxAvgScoreMap.containsKey(targetConstructsOfDerivedMatchingsWithMaxAvgScore)) {
					logger.debug("targetConstructs have already been matched to other sourceConstructs");
					LinkedHashMap<CanonicalModelConstruct[], Double> matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore = matchedTargetConstructsSourceConstructsWithMaxAvgScoreMap
							.get(targetConstructsOfDerivedMatchingsWithMaxAvgScore);
					logger.debug("matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore: "
							+ matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore);
					logger.debug("matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore.size(): "
							+ matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore.size()); //should be only 1 
					if (matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore.size() > 1)
						logger.debug("more than one set of matches ... something wrong");
					CanonicalModelConstruct[] correspondingSourceConstructs = matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore
							.keySet().iterator().next();
					logger.debug("correspondingSourceConstructs: " + correspondingSourceConstructs);
					for (CanonicalModelConstruct sourceConstruct : correspondingSourceConstructs) {
						logger.debug("sourceConstruct: " + sourceConstruct);
					}
					double maxAvgMatchingScoreForTargetConstructs = matchedSourceConstructsForTargetConstructsWithMaxAvgMatchingScore
							.get(correspondingSourceConstructs);
					logger.debug("maxAvgMatchingScoreForTargetConstructs: " + maxAvgMatchingScoreForTargetConstructs);
					if (maxAvgMatchingScoreForTargetConstructs < maxAvgMatchingScore) {
						logger.debug("new maxAvgMatchingScore > maxAvgMatchingScoreForTargetConstructs of current matches for targetConstructs - replace");
						for (CanonicalModelConstruct targetConstruct : targetConstructsOfDerivedMatchingsWithMaxAvgScore) {
							logger.debug("targetConstruct: " + targetConstruct);
						}
						logger.debug("derivedMatchingsWithMaxAvgScore: " + derivedMatchingsWithMaxAvgScore);
						LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>> newTargetConstructsAndMatching = new LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>>();
						newTargetConstructsAndMatching.put(targetConstructsOfDerivedMatchingsWithMaxAvgScore, derivedMatchingsWithMaxAvgScore);

						//this.sourceConstructsTargetConstructsDerivedMatchingsMap.put(sourceConstructs, newTargetConstructsAndMatching);

						sourceConstructsTargetConstructsDerivedMatchingsMap.put(sourceConstructs, newTargetConstructsAndMatching);

						logger.debug("sourceConstructsTargetConstructsDerivedMatchingsMap.size(): "
								+ sourceConstructsTargetConstructsDerivedMatchingsMap.size());
						for (CanonicalModelConstruct[] constructs : sourceConstructsTargetConstructsDerivedMatchingsMap.keySet()) {
							logger.debug("constructs: " + constructs);
							for (CanonicalModelConstruct construct : constructs) {
								logger.debug("construct: " + construct);
							}
							LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>> targetConstructsAndMatching = sourceConstructsTargetConstructsDerivedMatchingsMap
									.get(constructs);
							for (CanonicalModelConstruct[] targetConstructs : targetConstructsAndMatching.keySet()) {
								logger.debug("targetConstructs: " + targetConstructs);
								for (CanonicalModelConstruct targetConstruct : targetConstructs) {
									logger.debug("targetConstruct: " + targetConstruct);
								}
							}
						}
					} else {
						logger.debug("new maxAvgMatchingScore < maxAvgMatchingScoreForTargetConstructs of current matches for targetConstructs - do nothing");
					}
				} else {
					logger.debug("targetConstructs haven't been matched to other sourceConstructs, add currentMatching with maxScore");

					for (CanonicalModelConstruct targetConstruct : targetConstructsOfDerivedMatchingsWithMaxAvgScore) {
						logger.debug("targetConstruct: " + targetConstruct);
					}

					LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>> newTargetConstructsAndMatching = new LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>>();
					newTargetConstructsAndMatching.put(targetConstructsOfDerivedMatchingsWithMaxAvgScore, derivedMatchingsWithMaxAvgScore);
					sourceConstructsTargetConstructsDerivedMatchingsMap.put(sourceConstructs, newTargetConstructsAndMatching);

					logger.debug("sourceConstructsTargetConstructsDerivedMatchingsMap.size(): "
							+ sourceConstructsTargetConstructsDerivedMatchingsMap.size());
					for (CanonicalModelConstruct[] constructs : sourceConstructsTargetConstructsDerivedMatchingsMap.keySet()) {
						logger.debug("constructs: " + constructs);
						for (CanonicalModelConstruct construct : constructs) {
							logger.debug("construct: " + construct);
						}
						LinkedHashMap<CanonicalModelConstruct[], LinkedHashSet<DerivedOneToOneMatching>> targetConstructsAndMatching = sourceConstructsTargetConstructsDerivedMatchingsMap
								.get(constructs);
						for (CanonicalModelConstruct[] targetConstructs : targetConstructsAndMatching.keySet()) {
							logger.debug("targetConstructs: " + targetConstructs);
							for (CanonicalModelConstruct targetConstruct : targetConstructs) {
								logger.debug("targetConstruct: " + targetConstruct);
							}
						}
					}
				}
			}
		}

		logger.debug("this: " + this);
		logger.debug("leaving determineDerivedMatchingWithMaxScoreBetweenConstructsInVectors");

		return sourceConstructsTargetConstructsDerivedMatchingsMap;
	}
	*/

	/**
	 * @return the chromosome
	 */
	public ELRChromosome getChromosome() {
		return chromosome;
	}

	/**
	 * @return the phenotype
	 */
	public ELRPhenotype getPhenotype() {
		return phenotype;
	}

	public int getNumberOfSourceEntities() {
		return this.sourceEntitySet.size();
	}

	/**
	 * @return the sourceEntitySet
	 */
	public LinkedHashSet<SuperAbstract> getSourceEntitySet() {
		return sourceEntitySet;
	}

	public boolean hasSourceEntity(SuperAbstract sourceEntity) {
		return this.sourceEntitySet.contains(sourceEntity);
	}

	/**
	 * @return the targetEntitySet
	 */
	public LinkedHashSet<SuperAbstract> getTargetEntitySet() {
		return targetEntitySet;
	}

	public int getNumberOfTargetEntities() {
		return this.targetEntitySet.size();
	}

	public boolean hasTargetEntity(SuperAbstract targetEntity) {
		return this.targetEntitySet.contains(targetEntity);
	}

	/**
	 * @return the similarityScore
	 */
	public double getSimilarityScore() {
		return similarityScore;
	}

	/**
	 * @return the partitioningType
	 */
	public PartitioningType getPartitioningType() {
		return partitioningType;
	}

	/**
	 * @return the sourceVectors
	 */
	public List<VectorSpaceVector> getSourceVectors() {
		return sourceVectors;
	}

	/**
	 * @return the targetVectors
	 */
	public List<VectorSpaceVector> getTargetVectors() {
		return targetVectors;
	}

	/**
	 * @param vectorSpaceVectorFactoryService the vectorSpaceVectorFactoryService to set
	 */
	protected void setVectorSpaceVectorFactoryService(VectorSpaceVectorFactoryService vectorSpaceVectorFactoryService) {
		this.vectorSpaceVectorFactoryService = vectorSpaceVectorFactoryService;
	}

	/**
	 * @param chromosome the chromosome to set
	 */
	protected void setChromosome(ELRChromosome chromosome) {
		this.chromosome = chromosome;
	}

	/**
	 * @param phenotype the phenotype to set
	 */
	protected void setPhenotype(ELRPhenotype phenotype) {
		this.phenotype = phenotype;
	}

	/**
	 * @param similarityScore the similarityScore to set
	 */
	protected void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	/**
	 * @param partitioningType the partitioningType to set
	 */
	protected void setPartitioningType(PartitioningType partitioningType) {
		this.partitioningType = partitioningType;
	}

	/**
	 * @param sourceVectors the sourceVectors to set
	 */
	public void setSourceVectors(List<VectorSpaceVector> sourceVectors) {
		this.sourceVectors = sourceVectors;
	}

	/**
	 * @param targetVectors the targetVectors to set
	 */
	public void setTargetVectors(List<VectorSpaceVector> targetVectors) {
		this.targetVectors = targetVectors;
	}

	/**
	 * @return the sourceVectorWithSimilarityScore
	 */
	public VectorSpaceVector getSourceVectorWithSimilarityScore() {
		return sourceVectorWithSimilarityScore;
	}

	/**
	 * @return the targetVectorWithSimilarityScore
	 */
	public VectorSpaceVector getTargetVectorWithSimilarityScore() {
		return targetVectorWithSimilarityScore;
	}

	/**
	 * @return the equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap
	 */
	public Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap() {
		return equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap;
	}

	/**
	 * @return the equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap
	 */
	public Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap() {
		return equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap;
	}
}
