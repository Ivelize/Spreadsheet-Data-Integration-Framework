/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import scala.actors.threadpool.Arrays;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.CardinalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.DirectionalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.PartitioningType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.GenerateSchematicCorrespondencesForPhenotypeService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SPARQLService;

/**
 * @author chedeler
 *
 */
@Service(value = "generateSchematicCorrespondenceForPhenotypeService")
public class GenerateSchematicCorrespondencesForPhenotypeServiceImpl implements GenerateSchematicCorrespondencesForPhenotypeService {
	
	@Autowired
	@Qualifier("schematicCorrespondenceService")
	private SchematicCorrespondenceService schematicCorrespondenceService;
	
	@Autowired
	@Qualifier("localQueryTranslator2SPARQLService")
	private LocalQueryTranslator2SPARQLService localQueryTranslator2SPARQLService;

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;

	private static Logger logger = Logger.getLogger(GenerateSchematicCorrespondencesForPhenotypeServiceImpl.class);

	public Set<SchematicCorrespondence> generateSchematicCorrespondencesForBestPhenotype(ELRPhenotype bestPhenotype) {
		logger.debug("in generateSchematicCorrespondencesForBestPhenotype");
		
		Set<SchematicCorrespondence> correspondencesToReturn = new HashSet<SchematicCorrespondence>();
		for (ELREntityLevelRelationship elr : bestPhenotype.getElrs()) {
			correspondencesToReturn.addAll(this.generateSchematicCorrespondencesForELREntityLevelRelationship(elr));
		}
		
		return correspondencesToReturn;
	}

	protected Set<SchematicCorrespondence> generateSchematicCorrespondencesForELREntityLevelRelationship(ELREntityLevelRelationship elr) {
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();
		logger.debug("elr: " + elr);
		logger.debug("elr.getNumberOfSourceEntities(): " + elr.getNumberOfSourceEntities());
		logger.debug("elr.getNumberOfTargetEntities(): " + elr.getNumberOfTargetEntities());
		logger.debug("elr.getSourceVectors().size(): " + elr.getSourceVectors().size());
		logger.debug("elr.getTargetVectors().size(): " + elr.getTargetVectors().size());
		logger.debug("elr.getPartitioningType(): " + elr.getPartitioningType());
		logger.debug("elr.getSimilarityScore(): " + elr.getSimilarityScore());
		for (SuperAbstract sourceEntity : elr.getSourceEntitySet())
			logger.debug("sourceEntity: " + sourceEntity);
		for (SuperAbstract targetEntity : elr.getTargetEntitySet())
			logger.debug("targetEntity: " + targetEntity);
		if (elr.getPartitioningType() == null) {
			logger.debug("partitioningType == null - should only be single vectors");
			if (elr.getSourceVectors().size() > 1 || elr.getTargetVectors().size() > 1)
				logger.error("no partitioningType but more than one sourceVector or targetVector - something wrong - TODO"); //TODO
			else {
				VectorSpaceVector sourceVector = elr.getSourceVectors().get(0);
				logger.debug("sourceVector: " + sourceVector);
				VectorSpaceVector targetVector = elr.getTargetVectors().get(0);
				logger.debug("targetVector: " + targetVector);
				if (!(sourceVector instanceof SingleVector) || !(targetVector instanceof SingleVector))
					logger.error("sourceVector and/or targetVector isn't SingleVector - something wrong - TODO"); //TODO
				else {
					SingleVector singleSourceVector = (SingleVector) sourceVector;
					logger.debug("singleSourceVector: " + singleSourceVector);
					//Klitos: I have change that to targetVector
					SingleVector singleTargetVector = (SingleVector) targetVector;
					logger.debug("singleTargetVector: " + singleTargetVector);
					schematicCorrespondences.addAll(this.generateOneToOneSchematicCorrespondencesBetweenConstructsInTwoSingleVectors(
							singleSourceVector, singleTargetVector, elr.getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap(),
							elr.getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap()));
				}
			}
		} else if (elr.getPartitioningType().equals(PartitioningType.HP_VS_HP)) {
			logger.debug("partitioningType is HP_VS_HP, check the vectors for HP and SingleVector as one of them could be a singleVector");
			VectorSpaceVector sourceHorizontalPartitioningOrSingleVector = this.getSingleOrHorizontalPartitioningVectorFromListOfVectors(elr
					.getSourceVectors());
			VectorSpaceVector targetHorizontalPartitioningOrSingleVector = this.getSingleOrHorizontalPartitioningVectorFromListOfVectors(elr
					.getTargetVectors());
			logger.debug("sourceHorizontalPartitioningOrSingleVector: " + sourceHorizontalPartitioningOrSingleVector);
			logger.debug("targetHorizontalPartitioningOrSingleVector: " + targetHorizontalPartitioningOrSingleVector);
			if (sourceHorizontalPartitioningOrSingleVector instanceof SingleVector
					&& targetHorizontalPartitioningOrSingleVector instanceof SingleVector) {
				logger.error("both singleVector - shouldn't be here - TODO"); //TODO
			} else {
				schematicCorrespondences.addAll(this.generateHorizontalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
						sourceHorizontalPartitioningOrSingleVector, targetHorizontalPartitioningOrSingleVector,
						elr.getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap(),
						elr.getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap()));
			}
		} else if (elr.getPartitioningType().equals(PartitioningType.HP_VS_VP)) {
			logger.debug("partitioningType is HP_VS_VP, check vectors for HP and SingleVectors in source and, VP and SingleVectors in target as one of them could be a singleVector");
			VectorSpaceVector sourceHorizontalPartitioningOrSingleVector = this.getSingleOrHorizontalPartitioningVectorFromListOfVectors(elr
					.getSourceVectors());
			VectorSpaceVector targetVerticalPartitioningOrSingleVector = this.getSingleOrVerticalPartitioningVectorFromListOfVectors(elr
					.getTargetVectors());
			logger.debug("sourceHorizontalPartitioningOrSingleVector: " + sourceHorizontalPartitioningOrSingleVector);
			logger.debug("targetVerticalPartitioningOrSingleVector: " + targetVerticalPartitioningOrSingleVector);
			if (sourceHorizontalPartitioningOrSingleVector instanceof SingleVector
					&& targetVerticalPartitioningOrSingleVector instanceof SingleVector) {
				logger.error("both singleVector - shouldn't be here - TODO"); //TODO
			} else {
				schematicCorrespondences.addAll(this.generateHorizontalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
						sourceHorizontalPartitioningOrSingleVector, targetVerticalPartitioningOrSingleVector,
						elr.getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap(),
						elr.getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap()));
			}
		} else if (elr.getPartitioningType().equals(PartitioningType.VP_VS_VP)) {
			logger.debug("partitioningType is VP_VS_VP, check vectors for VP and SingleVectors as one of them could be a singleVector");
			VectorSpaceVector sourceVerticalPartitioningOrSingleVector = this.getSingleOrVerticalPartitioningVectorFromListOfVectors(elr
					.getSourceVectors());
			VectorSpaceVector targetVerticalPartitioningOrSingleVector = this.getSingleOrVerticalPartitioningVectorFromListOfVectors(elr
					.getTargetVectors());
			logger.debug("sourceVerticalPartitioningOrSingleVector: " + sourceVerticalPartitioningOrSingleVector);
			logger.debug("targetVerticalPartitioningOrSingleVector: " + targetVerticalPartitioningOrSingleVector);
			if (sourceVerticalPartitioningOrSingleVector instanceof SingleVector && targetVerticalPartitioningOrSingleVector instanceof SingleVector) {
				logger.error("both singleVector - shouldn't be here - TODO"); //TODO
			} else {
				schematicCorrespondences.addAll(this.generateVerticalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
						sourceVerticalPartitioningOrSingleVector, targetVerticalPartitioningOrSingleVector,
						elr.getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap(),
						elr.getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap()));
			}
		} else if (elr.getPartitioningType().equals(PartitioningType.VP_VS_HP)) {
			logger.debug("partitioningType is VP_VS_HP, check vectors for VP and SingleVectors in source and, HP and SingleVectors in target as one of them could be a singleVector");
			VectorSpaceVector sourceVerticalPartitioningOrSingleVector = this.getSingleOrVerticalPartitioningVectorFromListOfVectors(elr
					.getSourceVectors());
			VectorSpaceVector targetHorizontalPartitioningOrSingleVector = this.getSingleOrHorizontalPartitioningVectorFromListOfVectors(elr
					.getTargetVectors());
			logger.debug("sourceVerticalPartitioningOrSingleVector: " + sourceVerticalPartitioningOrSingleVector);
			logger.debug("targetHorizontalPartitioningOrSingleVector: " + targetHorizontalPartitioningOrSingleVector);
			if (sourceVerticalPartitioningOrSingleVector instanceof SingleVector
					&& targetHorizontalPartitioningOrSingleVector instanceof SingleVector) {
				logger.error("both singleVector - shouldn't be here - TODO"); //TODO
			} else {
				schematicCorrespondences.addAll(this.generateVerticalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
						sourceVerticalPartitioningOrSingleVector, targetHorizontalPartitioningOrSingleVector,
						elr.getEquivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap(),
						elr.getEquivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap()));
			}
		}
		return schematicCorrespondences;
	}

	protected Set<SchematicCorrespondence> generateOneToOneSchematicCorrespondencesBetweenConstructsInTwoSingleVectors(
			SingleVector singleSourceVector, SingleVector singleTargetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in generateOneToOneSchematicCorrespondenceForTwoSingleVectors");
		logger.debug("singleSourceVector: " + singleSourceVector);
		logger.debug("singleTargetVector: " + singleTargetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);

		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

		SuperAbstract[] sourceSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(singleSourceVector);
		logger.debug("sourceSuperAbstractsArray: " + sourceSuperAbstractsArray);
		SuperAbstract[] targetSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(singleTargetVector);
		logger.debug("targetSuperAbstractsArray: " + targetSuperAbstractsArray);
		SuperAbstract sourceSuperAbstract = sourceSuperAbstractsArray[0]; //singleVector, only one element in array
		logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
		SuperAbstract targetSuperAbstract = targetSuperAbstractsArray[0];
		logger.debug("targetSuperAbstract: " + targetSuperAbstract);

		Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> equivalentConstructsInSourceAndTargetVectors = this
				.identifyEquivalentConstructsInSourceAndTargetVectors(singleSourceVector, singleTargetVector,
						equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
						equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentConstructsInSourceAndTargetVectors: " + equivalentConstructsInSourceAndTargetVectors);
		logger.debug("equivalentConstructsInSourceAndTargetVectors.keySet().size(): " + equivalentConstructsInSourceAndTargetVectors.keySet().size());
		logger.debug("equivalentConstructsInSourceAndTargetVectors.values().size(): " + equivalentConstructsInSourceAndTargetVectors.values().size());

		Set<CanonicalModelConstruct[]> sourceConstructArraysWithoutEquivalentTargetConstructArrays = new HashSet<CanonicalModelConstruct[]>(
				singleSourceVector.getConstructsWeightsMap().keySet());
		sourceConstructArraysWithoutEquivalentTargetConstructArrays.removeAll(equivalentConstructsInSourceAndTargetVectors.keySet());
		sourceConstructArraysWithoutEquivalentTargetConstructArrays.remove(sourceSuperAbstractsArray);
		logger.debug("sourceConstructArraysWithoutEquivalentTargetConstructArrays: " + sourceConstructArraysWithoutEquivalentTargetConstructArrays);
		logger.debug("sourceConstructArraysWithoutEquivalentTargetConstructArrays.size(): "
				+ sourceConstructArraysWithoutEquivalentTargetConstructArrays.size());

		Set<CanonicalModelConstruct[]> targetConstructArraysWithoutEquivalentSourceConstructArrays = new HashSet<CanonicalModelConstruct[]>(
				singleTargetVector.getConstructsWeightsMap().keySet());
		targetConstructArraysWithoutEquivalentSourceConstructArrays.removeAll(equivalentConstructsInSourceAndTargetVectors.values());
		targetConstructArraysWithoutEquivalentSourceConstructArrays.remove(targetSuperAbstractsArray);
		logger.debug("targetConstructArraysWithoutEquivalentSourceConstructArrays: " + targetConstructArraysWithoutEquivalentSourceConstructArrays);
		logger.debug("targetConstructArraysWithoutEquivalentSourceConstructArrays: "
				+ targetConstructArraysWithoutEquivalentSourceConstructArrays.size());

		SchematicCorrespondence parentSchematicCorrespondenceBetweenSuperAbstracts = this
				.generateOneToOneSchematicCorrespondenceBetweenTwoSuperAbstracts(sourceSuperAbstract, targetSuperAbstract);
		schematicCorrespondences.add(parentSchematicCorrespondenceBetweenSuperAbstracts);
		logger.debug("parentSchematicCorrespondenceBetweenSuperAbstracts: " + parentSchematicCorrespondenceBetweenSuperAbstracts);

		Set<SchematicCorrespondence> allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals = this
				.generateAllOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals(equivalentConstructsInSourceAndTargetVectors);
		logger.debug("allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals: "
				+ allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals);
		logger.debug("allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals.size(): "
				+ allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals.size());
		parentSchematicCorrespondenceBetweenSuperAbstracts
				.addAllChildSchematicCorrespondence(allOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals);

		Set<SchematicCorrespondence> allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical = this
				.generateAllMissingSuperLexicalSchematicCorrespondenceForSuperLexicalsWithoutEquivalentSuperLexical(sourceSuperAbstract,
						targetSuperAbstract, sourceConstructArraysWithoutEquivalentTargetConstructArrays, true); //Ive
		logger.debug("allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical: "
				+ allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical);
		logger.debug("allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical.size(): "
				+ allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical.size());
		parentSchematicCorrespondenceBetweenSuperAbstracts
				.addAllChildSchematicCorrespondence(allMissingSuperLexicalSchematicCorrespondencesForSourceSuperLexicalsWithoutEquivalentTargetSuperLexical);

		Set<SchematicCorrespondence> allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical = this
				.generateAllMissingSuperLexicalSchematicCorrespondenceForSuperLexicalsWithoutEquivalentSuperLexical(sourceSuperAbstract,
						targetSuperAbstract, targetConstructArraysWithoutEquivalentSourceConstructArrays, false); //Ive
		logger.debug("allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical: "
				+ allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical);
		logger.debug("allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical.size(): "
				+ allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical.size());
		parentSchematicCorrespondenceBetweenSuperAbstracts
				.addAllChildSchematicCorrespondence(allMissingSuperLexicalSchematicCorrespondencesForTargetSuperLexicalsWithoutEquivalentSourceSuperLexical);
		
		
		
		
		
		
		
		
		
		
		//TODO check what viewGen and others of Lu's operators require
		return schematicCorrespondences;
	}

	//TODO refactor
	protected Set<SchematicCorrespondence> generateHorizontalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in generateHorizontalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

		SuperAbstract[] sourceSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(sourceVector);
		logger.debug("sourceSuperAbstractsArray: " + sourceSuperAbstractsArray);
		SuperAbstract[] targetSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(targetVector);
		logger.debug("targetSuperAbstractsArray: " + targetSuperAbstractsArray);

		SchematicCorrespondence parentSchematicCorrespondenceBetweenSuperAbstracts = this
				.generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts(sourceSuperAbstractsArray,
						targetSuperAbstractsArray, PartitioningType.HP_VS_HP);
		schematicCorrespondences.add(parentSchematicCorrespondenceBetweenSuperAbstracts);

		Set<CanonicalModelConstruct[]> sourceConstructArraysWithoutSuperAbstractsArray = new HashSet<CanonicalModelConstruct[]>(sourceVector
				.getConstructsWeightsMap().keySet());
		sourceConstructArraysWithoutSuperAbstractsArray.remove(sourceSuperAbstractsArray);
		logger.debug("sourceConstructArraysWithoutSuperAbstractsArray: " + sourceConstructArraysWithoutSuperAbstractsArray);
		logger.debug("sourceConstructArraysWithoutSuperAbstractsArray.size(): " + sourceConstructArraysWithoutSuperAbstractsArray.size());
		Set<CanonicalModelConstruct[]> targetConstructArraysWithoutSuperAbstractsArray = new HashSet<CanonicalModelConstruct[]>(targetVector
				.getConstructsWeightsMap().keySet());
		targetConstructArraysWithoutSuperAbstractsArray.remove(targetSuperAbstractsArray);
		logger.debug("targetConstructArraysWithoutSuperAbstractsArray: " + targetConstructArraysWithoutSuperAbstractsArray);
		logger.debug("targetConstructArraysWithoutSuperAbstractsArray.size(): " + targetConstructArraysWithoutSuperAbstractsArray.size());

		//Set<SchematicCorrespondence> equivalentOrMissingSuperLexicalSchematicCorrespondenceWithinVectorArray = 

		Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> equivalentConstructsInSourceAndTargetVectors = this
				.identifyEquivalentConstructsInSourceAndTargetVectors(sourceVector, targetVector,
						equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
						equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentConstructsInSourceAndTargetVectors: " + equivalentConstructsInSourceAndTargetVectors);
		logger.debug("equivalentConstructsInSourceAndTargetVectors.keySet().size(): " + equivalentConstructsInSourceAndTargetVectors.keySet().size());
		logger.debug("equivalentConstructsInSourceAndTargetVectors.values().size(): " + equivalentConstructsInSourceAndTargetVectors.values().size());

		Set<CanonicalModelConstruct[]> sourceConstructArraysWithoutEquivalentTargetConstructArrays = new HashSet<CanonicalModelConstruct[]>(
				sourceVector.getConstructsWeightsMap().keySet());
		sourceConstructArraysWithoutEquivalentTargetConstructArrays.removeAll(equivalentConstructsInSourceAndTargetVectors.keySet());
		sourceConstructArraysWithoutEquivalentTargetConstructArrays.remove(sourceSuperAbstractsArray);
		logger.debug("sourceConstructArraysWithoutEquivalentTargetConstructArrays: " + sourceConstructArraysWithoutEquivalentTargetConstructArrays);
		logger.debug("sourceConstructArraysWithoutEquivalentTargetConstructArrays.size(): "
				+ sourceConstructArraysWithoutEquivalentTargetConstructArrays.size());

		Set<CanonicalModelConstruct[]> targetConstructArraysWithoutEquivalentSourceConstructArrays = new HashSet<CanonicalModelConstruct[]>(
				targetVector.getConstructsWeightsMap().keySet());
		targetConstructArraysWithoutEquivalentSourceConstructArrays.removeAll(equivalentConstructsInSourceAndTargetVectors.values());
		targetConstructArraysWithoutEquivalentSourceConstructArrays.remove(targetSuperAbstractsArray);
		logger.debug("targetConstructArraysWithoutEquivalentSourceConstructArrays: " + targetConstructArraysWithoutEquivalentSourceConstructArrays);
		logger.debug("targetConstructArraysWithoutEquivalentSourceConstructArrays: "
				+ targetConstructArraysWithoutEquivalentSourceConstructArrays.size());

	/*	Set<SchematicCorrespondence> schematicCorrespondencesForEquivalentConstructsWithinAndBetweenSourceAndTargetVectors = this
				.generateSchematicCorrespondencesForEquivalentConstructsWithinAndBetweenSourceAndTargetVectors(equivalentConstructsInSourceAndTargetVectors);
		schematicCorrespondences.addAll(schematicCorrespondencesForEquivalentConstructsWithinAndBetweenSourceAndTargetVectors);
		parentSchematicCorrespondenceBetweenSuperAbstracts
				.addAllChildSchematicCorrespondence(schematicCorrespondencesForEquivalentConstructsWithinAndBetweenSourceAndTargetVectors);*/
		return schematicCorrespondences;
	}

	//TODO I need the superAbstract from which the superLexical is missing ....
	protected Set<SchematicCorrespondence> generateEquivalentOrMissingSuperLexicalSchematicCorrespondenceWithinVectorArray(
			Set<CanonicalModelConstruct[]> constructsArraysWithoutSuperAbstracts, SuperAbstract[] superAbstractsArray) {
		logger.debug("generateEquivalentOrMissingSuperLexicalSchematicCorrespondenceWithinVectorArray");
		logger.debug("constructsArraysWithoutSuperAbstracts: " + constructsArraysWithoutSuperAbstracts);
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();
		for (CanonicalModelConstruct[] constructsArray : constructsArraysWithoutSuperAbstracts) {
			logger.debug("constructsArray: " + constructsArray);

		}
		return schematicCorrespondences;
	}

	/*
	protected List<CanonicalModelConstruct> getEquivalentConstructsFromArrayWithoutNull(CanonicalModelConstruct[] constructsArray) {
		logger.debug("in getEquivalentConstructsFromArrayWithoutNull");
		logger.debug("constructsArray: " + constructsArray);
		List<CanonicalModelConstruct> constructsWithoutNull = new ArrayList<CanonicalModelConstruct>();
		for (CanonicalModelConstruct construct : constructsArray) {
			logger.debug("construct: " + construct);
			if (construct != null) {
				logger.debug("construct != null");
				constructsWithoutNull.add(construct);
			}
		}
		return constructsWithoutNull;
	}
	*/

	/*
	protected Set<SchematicCorrespondence> generateSchematicCorrespondencesForEquivalentOrMissingConstructsWithinAndBetweenSourceAndTargetVectors(
			Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> equivalentConstructsInSourceAndTargetVectors) {
		logger.debug("in generateSchematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors");
		logger.debug("equivalentConstructsInSourceAndTargetVectors: " + equivalentConstructsInSourceAndTargetVectors);
		Set<SchematicCorrespondence> schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors = new HashSet<SchematicCorrespondence>();

		for (CanonicalModelConstruct[] sourceConstructsArrayWithEquivalentTargetConstructsArray : equivalentConstructsInSourceAndTargetVectors
				.keySet()) {
			logger.debug("sourceConstructsArrayWithEquivalentTargetConstructsArray: " + sourceConstructsArrayWithEquivalentTargetConstructsArray);
			CanonicalModelConstruct[] equivalentTargetConstructsArray = equivalentConstructsInSourceAndTargetVectors
					.get(sourceConstructsArrayWithEquivalentTargetConstructsArray);
			logger.debug("equivalentTargetConstructsArray: " + equivalentTargetConstructsArray);
			schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors.addAll(this
					.generateEquivalentSuperLexicalsSchematicCorrespondencesForUnion(sourceConstructsWithoutNull));
			logger.debug("schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors: "
					+ schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors);
			schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors.addAll(this
					.generateEquivalentSuperLexicalsSchematicCorrespondencesForUnion(targetConstructsWithoutNull));
			logger.debug("schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors.size(): "
					+ schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors.size());

		}

		return schematicCorrespondencesForEquivalentConstructsInSourceAndTargetVectors;
	}
	*/

	protected Set<SchematicCorrespondence> generateEquivalentSuperLexicalsSchematicCorrespondencesBetweenSourceAndTargetConstructArray() {
		return null;
	}

	protected Set<SchematicCorrespondence> generateEquivalentSuperLexicalsOrMissingSuperLexicalsSchematicCorrespondencesForUnion(
			List<CanonicalModelConstruct> eqivalentConstructs) {
		logger.debug("in generateEquivalentSuperLexicalsSchematicCorrespondencesForUnion");
		logger.debug("eqivalentConstructs: " + eqivalentConstructs);
		Set<SchematicCorrespondence> schematicCorrespondencesForEquivalentSuperLexicals = new HashSet<SchematicCorrespondence>();
		for (CanonicalModelConstruct construct1 : eqivalentConstructs) {
			for (int i = eqivalentConstructs.indexOf(construct1) + 1; i < eqivalentConstructs.size(); i++) {
				logger.debug("i: " + eqivalentConstructs);
				CanonicalModelConstruct construct2 = eqivalentConstructs.get(i);
				logger.debug("construct1: " + construct1);
				logger.debug("construct2: " + construct2);
				if (construct1 instanceof SuperLexical && construct2 instanceof SuperLexical) {
					SchematicCorrespondence equivalentSuperLexicalsForUnionSchematicCorrespondence = this
							.generateSameNameOrDifferentNameSameConstructSchematicCorrespondenceBetweenTwoCanonicalModelConstructs(construct1,
									construct2);
					logger.debug("equivalentSuperLexicalsForUnionSchematicCorrespondence: " + equivalentSuperLexicalsForUnionSchematicCorrespondence);
					schematicCorrespondencesForEquivalentSuperLexicals.add(equivalentSuperLexicalsForUnionSchematicCorrespondence);
				} else
					logger.error("construct1 and/or construct2 not SuperLexical - ignored for now - TODO");
			}
		}
		return schematicCorrespondencesForEquivalentSuperLexicals;
	}

	protected Set<SchematicCorrespondence> generateHorizontalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in generateHorizontalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap1: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
	
		/** PRINT **/
		
		Set<CanonicalModelConstruct[]> entry = equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry) {

			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name1: " + construct.getName() +  " | " + e);
			}
		
		}
		
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap2: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

		Set<CanonicalModelConstruct[]> entry2 = equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry2) {
		
			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name2: " + construct.getName() +  " | " + e);
			}
		
		}		


		SuperAbstract[] sourceSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(sourceVector);
		logger.debug("sourceSuperAbstractsArray: " + sourceSuperAbstractsArray);
		SuperAbstract[] targetSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(targetVector);
		logger.debug("targetSuperAbstractsArray: " + targetSuperAbstractsArray);

		SchematicCorrespondence parentSchematicCorrespondenceBetweenSuperAbstracts = this
				.generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts(sourceSuperAbstractsArray,
						targetSuperAbstractsArray, PartitioningType.HP_VS_VP);
		schematicCorrespondences.add(parentSchematicCorrespondenceBetweenSuperAbstracts);

		//TODO rest
		return schematicCorrespondences;
	}

	protected Set<SchematicCorrespondence> generateVerticalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in generateVerticalVsHorizontalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap1: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);

		/** PRINT **/
		
		Set<CanonicalModelConstruct[]> entry = equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry) {

			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name1: " + construct.getName() +  " | " + e);
			}
		
		}
		
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap2: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

		Set<CanonicalModelConstruct[]> entry2 = equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry2) {
		
			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name2: " + construct.getName() +  " | " + e);
			}
		
		}
		
		

		SuperAbstract[] sourceSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(sourceVector);
		logger.debug("sourceSuperAbstractsArray: " + sourceSuperAbstractsArray);
		SuperAbstract[] targetSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(targetVector);
		logger.debug("targetSuperAbstractsArray: " + targetSuperAbstractsArray);

		SchematicCorrespondence parentSchematicCorrespondenceBetweenSuperAbstracts = this
				.generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts(sourceSuperAbstractsArray,
						targetSuperAbstractsArray, PartitioningType.VP_VS_HP);
		schematicCorrespondences.add(parentSchematicCorrespondenceBetweenSuperAbstracts);

		//TODO rest
		return schematicCorrespondences;
	}

	protected Set<SchematicCorrespondence> generateVerticalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in generateVerticalVsVerticalPartitioningSchematicCorrespondenceBetweenConstructsInTwoVectors");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap1: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		
		/** PRINT **/
		
		Set<CanonicalModelConstruct[]> entry = equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry) {
		
			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name1: " + construct.getName() +  " | " + e);
			}
		
		}
		
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap2: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		Set<SchematicCorrespondence> schematicCorrespondences = new HashSet<SchematicCorrespondence>();

		Set<CanonicalModelConstruct[]> entry2 = equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (CanonicalModelConstruct[] e : entry2) {
		
			for (CanonicalModelConstruct construct : e) {
				logger.debug("Name2: " + construct.getName() +  " | " + e);
			}
		
		}
		
		
		
		SuperAbstract[] sourceSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(sourceVector);
		logger.debug("sourceSuperAbstractsArray: " + sourceSuperAbstractsArray);
		SuperAbstract[] targetSuperAbstractsArray = this.getConstructsArrayContainingSuperAbstractsFromVector(targetVector);
		logger.debug("targetSuperAbstractsArray: " + targetSuperAbstractsArray);

		SchematicCorrespondence parentSchematicCorrespondenceBetweenSuperAbstracts = this
				.generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts(sourceSuperAbstractsArray,
						targetSuperAbstractsArray, PartitioningType.VP_VS_VP);
		schematicCorrespondences.add(parentSchematicCorrespondenceBetweenSuperAbstracts);

		//TODO rest
		return schematicCorrespondences;
	}

	protected SchematicCorrespondence generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts(
			SuperAbstract[] sourceSuperAbstracts, SuperAbstract[] targetSuperAbstracts, PartitioningType partitioningType) {
		logger.debug("in generateManyToManyOrOneToManyOrManyToOneSchematicCorrespondenceBetweenTwoArraysOfSuperAbstracts");
		logger.debug("sourceSuperAbstracts: " + sourceSuperAbstracts);
		logger.debug("targetSuperAbstracts: " + targetSuperAbstracts);
		logger.debug("partitioningType: " + partitioningType);

		StringBuilder nameSb = new StringBuilder();
		StringBuilder shortNameSb = new StringBuilder();
		SchematicCorrespondenceType schematicCorrespondenceType = null;
		DirectionalityType direction = null;

		if (partitioningType.equals(PartitioningType.HP_VS_HP)) {
			logger.debug("is HP_VS_HP");
			nameSb.append("HorizontalVsHorizontalPartitioning_");
			shortNameSb.append("HPvsHP_");
			if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_VS_HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			} else if (sourceSuperAbstracts.length == 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.SECOND_TO_FIRST;
			} else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length == 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			}
		} else if (partitioningType.equals(PartitioningType.HP_VS_VP)) {
			logger.debug("is HP_VS_VP"); //DAME
			nameSb.append("HorizontalVsVerticalPartitioning_");
			shortNameSb.append("HPvsVP_");
			if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			} else if (sourceSuperAbstracts.length == 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_PARTITIONING;
				direction = DirectionalityType.SECOND_TO_FIRST;
			} else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length == 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			}
		} else if (partitioningType.equals(PartitioningType.VP_VS_HP)) {
			logger.debug("is VP_VS_HP");
			nameSb.append("VerticalVsHorizontalPartitioning_");
			shortNameSb.append("VPvsHP_");
			if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_VS_HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			} else if (sourceSuperAbstracts.length == 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.HORIZONTAL_PARTITIONING;
				direction = DirectionalityType.SECOND_TO_FIRST;
			} else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length == 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			}
		} else if (partitioningType.equals(PartitioningType.VP_VS_VP)) {
			logger.debug("is VP_VS_VP");
			nameSb.append("VerticalVsVerticalPartitioning_");
			shortNameSb.append("VPvsVP_");
			if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_VS_VERTICAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			} else if (sourceSuperAbstracts.length == 1 && targetSuperAbstracts.length > 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_PARTITIONING;
				direction = DirectionalityType.SECOND_TO_FIRST;
			} else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length == 1) {
				schematicCorrespondenceType = SchematicCorrespondenceType.VERTICAL_PARTITIONING;
				direction = DirectionalityType.FIRST_TO_SECOND;
			}
		} else
			logger.error("unidentified PartitioningType: " + partitioningType);

		int index = 0;
		for (SuperAbstract sourceSuperAbstract : sourceSuperAbstracts) {
			logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
			if (index > 0) {
				nameSb.append(",");
				shortNameSb.append(",");
			}
			nameSb.append(sourceSuperAbstract.getName());
			shortNameSb.append(sourceSuperAbstract.getName());
		}
		nameSb.append("_vs_");
		shortNameSb.append("_vs_");
		index = 0;

		for (SuperAbstract targetSuperAbstract : targetSuperAbstracts) {
			logger.debug("targetSuperAbstract: " + targetSuperAbstract);
			if (index > 0) {
				nameSb.append(",");
				shortNameSb.append(",");
			}
			nameSb.append(targetSuperAbstract.getName());
			shortNameSb.append(targetSuperAbstract.getName());
		}
		logger.debug("name: " + nameSb.toString());
		logger.debug("shortName: " + shortNameSb.toString());
		logger.debug("schematicCorrespondenceType: " + schematicCorrespondenceType);

		SchematicCorrespondence manyToManySchematicCorrespondence = new SchematicCorrespondence(nameSb.toString(), shortNameSb.toString(),
				schematicCorrespondenceType);
		manyToManySchematicCorrespondence.addAllConstructs1(Arrays.asList(sourceSuperAbstracts));
		manyToManySchematicCorrespondence.addAllConstructs2(Arrays.asList(targetSuperAbstracts));
		//TODO the following two should be inferred within SchematicCorrespondence
		if (sourceSuperAbstracts.length == 1 && targetSuperAbstracts.length > 1)
			manyToManySchematicCorrespondence.setCardinalityType(CardinalityType.ONE_TO_MANY);
		else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length == 1)
			manyToManySchematicCorrespondence.setCardinalityType(CardinalityType.MANY_TO_ONE);
		else if (sourceSuperAbstracts.length > 1 && targetSuperAbstracts.length > 1)
			manyToManySchematicCorrespondence.setCardinalityType(CardinalityType.MANY_TO_MANY);
		manyToManySchematicCorrespondence
				.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		manyToManySchematicCorrespondence.setDirection(direction);
		return manyToManySchematicCorrespondence;
	}

	protected SchematicCorrespondence generateOneToOneSchematicCorrespondenceBetweenTwoSuperAbstracts(SuperAbstract sourceSuperAbstract,
			SuperAbstract targetSuperAbstract) {
		logger.debug("in generateOneToOneSchematicCorrespondenceBetweenTwoSuperAbstracts");
		logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
		logger.debug("targetSuperAbstract: " + targetSuperAbstract);
		return this.generateSameNameOrDifferentNameSameConstructSchematicCorrespondenceBetweenTwoCanonicalModelConstructs(sourceSuperAbstract,
				targetSuperAbstract);
	}

	protected Set<SchematicCorrespondence> generateAllOneToOneSchematicCorrespondencesBetweenEquivalentSuperLexicals(
			Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> equivalentConstructsInSourceAndTargetVectors) {
		Set<SchematicCorrespondence> schematicCorrespondencesBetweenEquivalentSuperLexicals = new HashSet<SchematicCorrespondence>();
		for (CanonicalModelConstruct[] sourceConstructArrayWithEquivalentTargetConstructArray : equivalentConstructsInSourceAndTargetVectors.keySet()) {
			logger.debug("sourceConstructArrayWithEquivalentTargetConstructArray: " + sourceConstructArrayWithEquivalentTargetConstructArray);

			CanonicalModelConstruct[] equivalentTargetConstructs = equivalentConstructsInSourceAndTargetVectors
					.get(sourceConstructArrayWithEquivalentTargetConstructArray);
			logger.debug("equivalentTargetConstructs: " + equivalentTargetConstructs);

			CanonicalModelConstruct sourceConstruct = sourceConstructArrayWithEquivalentTargetConstructArray[0];
			CanonicalModelConstruct targetConstruct = equivalentTargetConstructs[0];
			logger.debug("sourceConstruct: " + sourceConstruct);
			logger.debug("targetConstruct: " + targetConstruct);
			if (sourceConstruct instanceof SuperLexical && targetConstruct instanceof SuperLexical) {
				logger.debug("both constructs are SuperLexicals - generate schematicCorrespondence between them");
				SchematicCorrespondence childSchematicCorrespondenceBetweenSuperLexicals = this
						.generateSameNameOrDifferentNameSameConstructSchematicCorrespondenceBetweenTwoCanonicalModelConstructs(sourceConstruct,
								targetConstruct);
				logger.debug("childSchematicCorrespondenceBetweenSuperLexicals: " + childSchematicCorrespondenceBetweenSuperLexicals);
				schematicCorrespondencesBetweenEquivalentSuperLexicals.add(childSchematicCorrespondenceBetweenSuperLexicals);
			}
		}
		return schematicCorrespondencesBetweenEquivalentSuperLexicals;
	}

	protected Set<SchematicCorrespondence> generateAllMissingSuperLexicalSchematicCorrespondenceForSuperLexicalsWithoutEquivalentSuperLexical(
			SuperAbstract sourceSuperAbstract, SuperAbstract targetSuperAbstract,
			Set<CanonicalModelConstruct[]> constructArraysWithoutEquivalentConstructArray, boolean areMissingInSource) {
		logger.debug("in generateAllMissingSuperLexicalSchematicCorrespondenceForSuperLexicalsWithoutEquivalentSuperLexical");
		logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
		logger.debug("targetSuperAbstract: " + targetSuperAbstract);
		logger.debug("constructArraysWithoutEquivalentConstructArray: " + constructArraysWithoutEquivalentConstructArray);
		logger.debug("areMissingInSource: " + areMissingInSource);
		Set<SchematicCorrespondence> missingSuperLexicalSchematicCorrespondences = new HashSet<SchematicCorrespondence>();
		for (CanonicalModelConstruct[] constructArrayWithoutEquivalentConstructArray : constructArraysWithoutEquivalentConstructArray) {
			logger.debug("constructArrayWithoutEquivalentConstructArray: " + constructArrayWithoutEquivalentConstructArray);
			for (CanonicalModelConstruct constructInArray : constructArrayWithoutEquivalentConstructArray) {
				logger.debug("constructInArray: " + constructInArray);
				if (constructInArray instanceof SuperLexical) {
					logger.debug("constructInArray is superLexical - add missingSuperLexical schematicCorrespondence");
					SchematicCorrespondence childMissingSuperLexicalSchematicCorrespondence = this
							.generateMissingSuperLexicalSchematicCorrespondenceBetweenSuperLexicalAndSuperAbstractWithoutEquivalentSuperLexical(
									sourceSuperAbstract, targetSuperAbstract, (SuperLexical) constructInArray, areMissingInSource);
					logger.debug("childMissingSuperLexicalSchematicCorrespondence: " + childMissingSuperLexicalSchematicCorrespondence);
					missingSuperLexicalSchematicCorrespondences.add(childMissingSuperLexicalSchematicCorrespondence);
				} else {
					logger.error("missing construct isn't SuperLexical - ignored for now - TODO"); //TODO
				}
			}
		}
		return missingSuperLexicalSchematicCorrespondences;
	}

	protected SchematicCorrespondence generateMissingSuperLexicalSchematicCorrespondenceBetweenSuperLexicalAndSuperAbstractWithoutEquivalentSuperLexical(
			SuperAbstract sourceSuperAbstract, SuperAbstract targetSuperAbstract, SuperLexical missingSuperLexical, boolean isMissingInSource) {
		logger.debug("in generateMissingSuperLexicalSchematicCorrespondenceBetweenTwoSuperAbstracts");
		logger.debug("sourceSuperAbstract: " + sourceSuperAbstract);
		logger.debug("targetSuperAbstract: " + sourceSuperAbstract);
		logger.debug("missingSuperLexical: " + missingSuperLexical);
		logger.debug("isMissingInSource: " + isMissingInSource);
		String shortName = "MSL_" + missingSuperLexical.getName();
		StringBuilder nameSb = new StringBuilder();
		nameSb.append("MissingSuperLexical_in_");
		if (isMissingInSource)
			nameSb.append("source_");
		else
			nameSb.append("target_");
		nameSb.append(targetSuperAbstract.getSchema().getDataSource().getName() + "." + targetSuperAbstract.getName());
		SchematicCorrespondenceType schematicCorrespondenceType = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
		logger.debug("name: " + nameSb.toString());
		logger.debug("shortName: " + shortName);
		logger.debug("schematicCorrespondenceType: " + schematicCorrespondenceType);
		SchematicCorrespondence missingSuperLexicalSchematicCorrespondence = new SchematicCorrespondence(nameSb.toString(), shortName,
				schematicCorrespondenceType);
		if (isMissingInSource) {
			missingSuperLexicalSchematicCorrespondence.addConstruct1(sourceSuperAbstract);
			missingSuperLexicalSchematicCorrespondence.addConstruct2(missingSuperLexical);
			missingSuperLexicalSchematicCorrespondence.setDirection(DirectionalityType.FIRST_TO_SECOND); //TODO not sure that this direction makes the most sense
			missingSuperLexicalSchematicCorrespondence
					.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
		} else {
			missingSuperLexicalSchematicCorrespondence.addConstruct1(missingSuperLexical);
			missingSuperLexicalSchematicCorrespondence.addConstruct2(targetSuperAbstract);
			missingSuperLexicalSchematicCorrespondence.setDirection(DirectionalityType.SECOND_TO_FIRST); //TODO not sure that this direction makes the most sense
			missingSuperLexicalSchematicCorrespondence
					.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
			missingSuperLexicalSchematicCorrespondence.setCardinalityType(CardinalityType.ONE_TO_ONE);
		}
		return missingSuperLexicalSchematicCorrespondence;
	}

	protected SchematicCorrespondence generateSameNameOrDifferentNameSameConstructSchematicCorrespondenceBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct sourceConstruct, CanonicalModelConstruct targetConstruct) {
		logger.debug("in generateSchematicCorrespondenceBetweenTwoCanonicalModelConstructs");
		logger.debug("sourceConstruct: " + sourceConstruct);
		logger.debug("targetConstruct: " + targetConstruct);
		String shortName = null;
		String name = null;
		SchematicCorrespondenceType schematicCorrespondenceType = null;
		if (sourceConstruct.getName().equals(targetConstruct.getName())) {
			logger.debug("both constructs have same name - create SNSC correspondence");
			name = "SameNameSameConstruct_" + sourceConstruct.getSchema().getDataSource().getName() + "." + sourceConstruct.getName() + "_"
					+ targetConstruct.getSchema().getDataSource().getName() + "." + targetConstruct.getName();
			shortName = "SNSC" + "_" + sourceConstruct.getName() + "_" + targetConstruct.getName();
			schematicCorrespondenceType = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
		} else {
			logger.debug("different name for the two constructs - create DNSC correspondence");
			name = "DifferentNameSameConstruct_" + sourceConstruct.getSchema().getDataSource().getName() + "." + sourceConstruct.getName() + "_"
					+ targetConstruct.getSchema().getDataSource().getName() + "." + targetConstruct.getName();
			shortName = "DNSC_" + sourceConstruct.getName() + "_" + targetConstruct.getName();
			schematicCorrespondenceType = SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT;
		}
		logger.debug("name: " + name);
		logger.debug("shortName: " + shortName);
		logger.debug("schematicCorrespondenceType: " + schematicCorrespondenceType);
		SchematicCorrespondence schematicCorrespondence = new SchematicCorrespondence(name, shortName, schematicCorrespondenceType);
		schematicCorrespondence.addConstruct1(sourceConstruct);
		schematicCorrespondence.addConstruct2(targetConstruct);
		schematicCorrespondence.setDirection(DirectionalityType.BIDIRECTIONAL);
		
		//Ive: insert the instance of verification because before was save wrong
		if(sourceConstruct instanceof SuperLexical && targetConstruct instanceof SuperLexical){
			schematicCorrespondence
				.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
		}else if (sourceConstruct instanceof SuperAbstract && targetConstruct instanceof SuperAbstract){
			schematicCorrespondence
			.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		}else if (sourceConstruct instanceof SuperAbstract && targetConstruct instanceof SuperLexical){
			schematicCorrespondence
			.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_LEXICAL);
		}else if (sourceConstruct instanceof SuperLexical && targetConstruct instanceof SuperAbstract){
			schematicCorrespondence
			.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_ABSTRACT);
		}
		
		schematicCorrespondence.setCardinalityType(CardinalityType.ONE_TO_ONE);
		logger.debug("schematicCorrespondence: " + schematicCorrespondence);
		return schematicCorrespondence;
	}

	protected Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> identifyEquivalentConstructsInSourceAndTargetVectors(
			VectorSpaceVector sourceVector, VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in identifyEquivalentConstructsInSingleSourceAndTargetVectors");
		logger.debug("sourceVector: " + sourceVector);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		logger.debug("equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap: "
				+ equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
		Map<CanonicalModelConstruct[], CanonicalModelConstruct[]> constructsArrayInSourceWithEquivalentConstructsArrayInTarget = new HashMap<CanonicalModelConstruct[], CanonicalModelConstruct[]>();
		for (CanonicalModelConstruct[] sourceConstructsArray : sourceVector.getConstructsWeightsMap().keySet()) {
			logger.debug("sourceConstructsArray: " + sourceConstructsArray);
			CanonicalModelConstruct[] equivalentConstructsArrayInTarget = this.identifyEquivalentConstructsToSourceConstructsInTargetVector(
					sourceConstructsArray, targetVector, equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
			logger.debug("equivalentConstructsArrayInTarget: " + equivalentConstructsArrayInTarget);
			if (equivalentConstructsArrayInTarget != null) {
				logger.debug("equivalentConstructsArrayInTarget != null - check it's equivalent constructs in source and compare with sourceConstructs");
				CanonicalModelConstruct[] equivalentConstructsArrayInSource = this.identifyEquivalentConstructsToSourceConstructsInTargetVector(
						equivalentConstructsArrayInTarget, sourceVector, equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);
				logger.debug("equivalentConstructsArrayInSource: " + equivalentConstructsArrayInSource);
				if (equivalentConstructsArrayInSource != null) {
					logger.debug("equivalentConstructsArrayInSource != null");
					if (!equivalentConstructsArrayInSource.equals(sourceConstructsArray))
						logger.debug("different equivalentConstructs in the different directions - pick source to target direction anyway"); //TODO think about this
					else
						logger.debug("same equivalentConstructs in both directions - all ok");
				} else
					logger.debug("didn't find equivalent constructsArrayInSource for equivalentConstructsArrayInSource, but have equivalent constructs for sourceConstructs");
				constructsArrayInSourceWithEquivalentConstructsArrayInTarget.put(sourceConstructsArray, equivalentConstructsArrayInTarget);
			}
		}
		return constructsArrayInSourceWithEquivalentConstructsArrayInTarget;
	}

	protected CanonicalModelConstruct[] identifyEquivalentConstructsToSourceConstructsInTargetVector(CanonicalModelConstruct[] sourceConstructs,
			VectorSpaceVector targetVector,
			Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap) {
		logger.debug("in identifyEquivalentConstructsToSourceConstructsInTargetVector");
		logger.debug("sourceConstructs: " + sourceConstructs);
		logger.debug("targetVector: " + targetVector);
		logger.debug("equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap: "
				+ equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap);
		Set<CanonicalModelConstruct[]> constructsInTargetVector = targetVector.getConstructsWeightsMap().keySet();
		logger.debug("constructsInTargetVector: " + constructsInTargetVector);
		if (equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.containsKey(sourceConstructs)) {
			logger.debug("found equivalent targetConstructs to sourceConstructs");
			Set<CanonicalModelConstruct[]> equivalentConstructs = equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.get(
					sourceConstructs).keySet();
			logger.debug("equivalentConstructs: " + equivalentConstructs);
			for (CanonicalModelConstruct[] targetConstructs : constructsInTargetVector) {
				logger.debug("targetConstructs: " + targetConstructs);
				if (equivalentConstructs.contains(targetConstructs)) {
					logger.debug("found targetConstructs in eqivalentConstructs ... there should only be one element of the vector in there, not tested though, ignore the rest");
					return targetConstructs;
				}
			}
		}
		return null;
	}

	protected SuperAbstract[] getConstructsArrayContainingSuperAbstractsFromVector(VectorSpaceVector vector) {
		logger.debug("in getConstructsArrayContainingSuperAbstractsFromVector");
		logger.debug("vector: " + vector);
		for (CanonicalModelConstruct[] constructsArray : vector.getConstructsWeightsMap().keySet()) {
			logger.debug("constructsArray: " + constructsArray);
			CanonicalModelConstruct construct = constructsArray[0];
			logger.debug("construct: " + construct);
			if (construct != null && construct instanceof SuperAbstract) {
				logger.debug("construct is SuperAbstract - assume others in same array are too and return constructsArray"); // should only be one element with SuperAbstracts in the vector - not checked though
				SuperAbstract[] superAbstractsArray = new SuperAbstract[constructsArray.length];
				for (int i = 0; i < constructsArray.length; i++)
					superAbstractsArray[i] = (SuperAbstract) constructsArray[i];
				return superAbstractsArray;
			}
		}
		return null;
	}

	protected VectorSpaceVector getSingleOrHorizontalPartitioningVectorFromListOfVectors(List<VectorSpaceVector> listOfVectors) {
		if (listOfVectors.size() == 1) {
			if (!(listOfVectors.get(0) instanceof SingleVector)) {
				logger.error("only one vector, but not SingleVector - something wrong - TODO");//TODO
			} else {
				logger.debug("vector is singleVector - return vector");
				return listOfVectors.get(0);
			}
		} else {
			logger.debug("more than one vector - should be two and one of them should be the HP vector");
			if (listOfVectors.size() > 2) {
				logger.error("more than two vectors ... something wrong - TODO"); //TODO
			}
			for (VectorSpaceVector vector : listOfVectors) {
				logger.debug("vector: " + vector);
				if (vector instanceof HorizontalPartitioningVector) {
					logger.debug("vector is HP vector - return ");
					return vector;
				}
			}
		}
		return null;
	}

	protected VectorSpaceVector getSingleOrVerticalPartitioningVectorFromListOfVectors(List<VectorSpaceVector> listOfVectors) {
		if (listOfVectors.size() == 1) {
			if (!(listOfVectors.get(0) instanceof SingleVector)) {
				logger.error("only one vector, but not SingleVector - something wrong - TODO");//TODO
			} else {
				logger.debug("vector is singleVector - return vector");
				return listOfVectors.get(0);
			}
		} else {
			logger.debug("more than one vector - should be two and one of them should be the HP vector");
			if (listOfVectors.size() > 2) {
				logger.error("more than two vectors ... something wrong - TODO"); //TODO
			}
			for (VectorSpaceVector vector : listOfVectors) {
				logger.debug("vector: " + vector);
				if (vector instanceof VerticalPartitioningVector) {
					logger.debug("vector is VP vector - return ");
					return vector;
				}
			}
		}
		return null;
	}
}
