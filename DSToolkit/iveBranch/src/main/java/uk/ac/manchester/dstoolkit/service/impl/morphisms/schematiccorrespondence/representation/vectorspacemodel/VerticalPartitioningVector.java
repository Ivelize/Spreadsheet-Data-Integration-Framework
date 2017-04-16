/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
public class VerticalPartitioningVector extends VectorSpaceVector {

	private static Logger logger = Logger.getLogger(VerticalPartitioningVector.class);

	public VerticalPartitioningVector(ELRChromosome chromosome, ELRPhenotype phenotype, boolean isSource) {
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

		LinkedHashMap<CanonicalModelConstruct[], Double> vpVector = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		Set<SuperLexical> processedSuperLexicals = new HashSet<SuperLexical>();

		List<SuperAbstract> allEntitiesList = new ArrayList<SuperAbstract>(entitySet);
		SuperAbstract[] allEntities = super.generateEntitiesVectorElement(entitySet);
		vpVector.put(allEntities, new Double(0d));

		for (SuperAbstract entity : allEntities) {
			logger.debug("entity: " + entity);
			for (SuperLexical superLexical : entity.getSuperLexicals()) {
				logger.debug("superLexical: " + superLexical);
				if (!processedSuperLexicals.contains(superLexical)) {
					logger.debug("superLexical not yet processed");

					Set<SuperLexical> equivalentSuperLexicals = this.getChromosome().getEquivalentSuperLexicalsForSuperLexical(superLexical,
							this.isSource());
					Set<SuperLexical> equivalentSuperLexicalsForEntitiesInAllEntities = new HashSet<SuperLexical>();
					if (equivalentSuperLexicals != null && equivalentSuperLexicals.size() > 0) {
						logger.debug("found equivalent superLexicals");
						equivalentSuperLexicalsForEntitiesInAllEntities.addAll(this.identifyEquivalentSuperLexicalsForEntitiesInAllEntities(
								superLexical, equivalentSuperLexicals, allEntitiesList, processedSuperLexicals));
					} else {
						logger.debug("didn't find equivalent superLexicals for superLexical - add superLexical to equivalentSuperLexicalsForEntitiesInAllEntities");
						equivalentSuperLexicalsForEntitiesInAllEntities.add(superLexical);
					}
					List<SuperLexical[]> vectorElementArrays = this.generateVectorElementArrays(allEntitiesList,
							equivalentSuperLexicalsForEntitiesInAllEntities);
					for (SuperLexical[] superLexicalVectorElement : vectorElementArrays)
						vpVector.put(superLexicalVectorElement, new Double(0d));
					for (SuperLexical processedSuperLexical : equivalentSuperLexicalsForEntitiesInAllEntities)
						processedSuperLexicals.add(processedSuperLexical);
					logger.debug("processedSuperLexicals.size(): " + processedSuperLexicals.size());
				} else
					logger.debug("superLexical already processed");
			}
		}

		logger.debug("vpVector: " + vpVector);
		logger.debug("vpVector.size(): " + vpVector.size());
		for (CanonicalModelConstruct[] constructs : vpVector.keySet()) {
			logger.debug("constructs: " + constructs);
			for (CanonicalModelConstruct construct : constructs)
				logger.debug("construct: " + construct);
		}
		logger.debug("this: " + this);
		super.setConstructsWeightsMap(vpVector);
		return vpVector;
	}

	protected List<SuperLexical[]> generateVectorElementArrays(List<SuperAbstract> allEntitiesList,
			Set<SuperLexical> equivalentSuperLexicalsForEntitiesInAllEntities) {
		logger.debug("in generateVectorElementArrays");
		logger.debug("allEntitiesList: " + allEntitiesList);
		logger.debug("allEntitiesList.size(): " + allEntitiesList.size());
		logger.debug("equivalentSuperLexicalsForEntitiesInAllEntities: " + equivalentSuperLexicalsForEntitiesInAllEntities);
		logger.debug("equivalentSuperLexicalsForEntitiesInAllEntities.size(): " + equivalentSuperLexicalsForEntitiesInAllEntities.size());

		List<SuperLexical[]> vectorElementArrays = new ArrayList<SuperLexical[]>();

		if (equivalentSuperLexicalsForEntitiesInAllEntities.size() == allEntitiesList.size()) {
			logger.debug("same number of equivalent superLexicals as entities - add them all together to single vector element in order corresponding to order of entities");
			SuperLexical[] equivalentSuperLexicalsVectorElementArray = new SuperLexical[allEntitiesList.size()];
			for (SuperLexical equivalentSuperLexical : equivalentSuperLexicalsForEntitiesInAllEntities) {
				logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
				SuperAbstract firstAncestorSuperAbstractOfEquivalentSuperLexical = equivalentSuperLexical.getFirstAncestorSuperAbstract();
				logger.debug("firstAncestorSuperAbstractOfEquivalentSuperLexical: " + firstAncestorSuperAbstractOfEquivalentSuperLexical);
				equivalentSuperLexicalsVectorElementArray[allEntitiesList.indexOf(firstAncestorSuperAbstractOfEquivalentSuperLexical)] = equivalentSuperLexical;
			}
			vectorElementArrays.add(equivalentSuperLexicalsVectorElementArray);
		} else {
			logger.debug("not same number of equivalent superLexicals as entities, should be less, create separate vector element for each superLexical");
			for (SuperLexical equivalentSuperLexical : equivalentSuperLexicalsForEntitiesInAllEntities) {
				logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
				SuperLexical[] singleSuperLexicalVectorElementArray = { equivalentSuperLexical };
				logger.debug("singleSuperLexicalVectorElementArray: " + singleSuperLexicalVectorElementArray);
				vectorElementArrays.add(singleSuperLexicalVectorElementArray);
			}
		}
		logger.debug("vectorElementArrays: " + vectorElementArrays);
		logger.debug("vectorElementArrays.size(): " + vectorElementArrays.size());
		for (SuperLexical[] superLexical : vectorElementArrays) {
			logger.debug("superLexical in vectorElementArrays: " + superLexical);
		}
		return vectorElementArrays;
	}

	protected Set<SuperLexical> identifyEquivalentSuperLexicalsForEntitiesInAllEntities(SuperLexical superLexical,
			Set<SuperLexical> equivalentSuperLexicals, List<SuperAbstract> allEntitiesList, Set<SuperLexical> processedSuperLexicals) {
		//TODO change this kind of logging into aspects
		logger.debug("in identifyEquivalentSuperLexicalsForEntitiesInAllEntities");
		logger.debug("superLexical: " + superLexical);
		logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
		logger.debug("equivalentSuperLexicals.size(): " + equivalentSuperLexicals.size());
		logger.debug("allEntitiesList: " + allEntitiesList);
		logger.debug("allEntitiesList.size(): " + allEntitiesList.size());
		logger.debug("processedSuperLexicals: " + processedSuperLexicals);
		logger.debug("processedSuperLexicals.size(): " + processedSuperLexicals.size());

		Set<SuperLexical> equivalentSuperLexicalsForEntitiesInAllEntities = new HashSet<SuperLexical>();

		for (SuperLexical equivalentSuperLexical : equivalentSuperLexicals) {
			logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
			SuperAbstract firstAncestorSuperAbstractOfEquivalentSuperLexical = equivalentSuperLexical.getFirstAncestorSuperAbstract();
			logger.debug("firstAncestorSuperAbstractOfEquivalentSuperLexical: " + firstAncestorSuperAbstractOfEquivalentSuperLexical);
			if (allEntitiesList.contains(firstAncestorSuperAbstractOfEquivalentSuperLexical)) {
				logger.debug("found firstAncestorSuperAbstractOfEquivalentSuperLexical in allEntitiesList");
				if (!processedSuperLexicals.contains(equivalentSuperLexical)) {
					logger.debug("equivalentSuperLexical not processed yet ... add it to equivalentSuperLexicalsForEntitiesInAllEntities");
					equivalentSuperLexicalsForEntitiesInAllEntities.add(equivalentSuperLexical);
				} else
					logger.debug("equivalentSuperLexical has already been processed ... do nothing");
			} else
				logger.debug("didn't find firstAncestorSuperAbstractOfEquivalentSuperLexical in allEntitiesList ... do nothing");
		}
		logger.debug("equivalentSuperLexicalsForEntitiesInAllEntities: " + equivalentSuperLexicalsForEntitiesInAllEntities);
		for (SuperLexical equivalentSuperLexicalForEntityInAllEntities : equivalentSuperLexicalsForEntitiesInAllEntities)
			logger.debug("equivalentSuperLexicalForEntityInAllEntities: " + equivalentSuperLexicalForEntityInAllEntities);
		return equivalentSuperLexicalsForEntitiesInAllEntities;
	}
}
