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
public class HorizontalPartitioningVector extends VectorSpaceVector {

	private static Logger logger = Logger.getLogger(HorizontalPartitioningVector.class);

	public HorizontalPartitioningVector(ELRChromosome chromosome, ELRPhenotype phenotype, boolean isSource) {
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

		LinkedHashMap<CanonicalModelConstruct[], Double> hpVector = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		Set<SuperLexical> processedSuperLexicals = new HashSet<SuperLexical>();

		List<SuperAbstract> allEntitiesList = new ArrayList<SuperAbstract>(entitySet);
		SuperAbstract[] allEntities = super.generateEntitiesVectorElement(entitySet);
		hpVector.put(allEntities, new Double(0d));

		for (SuperAbstract entity : allEntities) {
			logger.debug("entity: " + entity);
			for (SuperLexical superLexical : entity.getSuperLexicals()) {
				logger.debug("superLexical: " + superLexical);
				if (!processedSuperLexicals.contains(superLexical)) {
					logger.debug("superLexical not yet processed");

					SuperLexical[] equivalentSuperLexicalsWithNullAsPlaceholder = this
							.generateArrayForEquivalentLexicalsElementAndSetAllNull(allEntities.length);
					Set<SuperLexical> equivalentSuperLexicals = this.getChromosome().getEquivalentSuperLexicalsForSuperLexical(superLexical,
							this.isSource());
					if (equivalentSuperLexicals != null && equivalentSuperLexicals.size() > 0) {
						logger.debug("found equivalent superLexicals");
						equivalentSuperLexicalsWithNullAsPlaceholder = this.fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities(
								superLexical, equivalentSuperLexicals, allEntitiesList, equivalentSuperLexicalsWithNullAsPlaceholder,
								processedSuperLexicals);
						for (SuperLexical processedSuperLexical : equivalentSuperLexicalsWithNullAsPlaceholder)
							processedSuperLexicals.add(processedSuperLexical);
						logger.debug("processedSuperLexicals.size(): " + processedSuperLexicals.size());
					} else {
						logger.debug("didn't find equivalent superLexicals for superLexical - place superLexical in vector element at same position as it's parent entity");
						logger.debug("allEntitiesList.indexOf(sa): " + allEntitiesList.indexOf(entity));
						equivalentSuperLexicalsWithNullAsPlaceholder[allEntitiesList.indexOf(entity)] = superLexical;

						int index = 0;
						for (CanonicalModelConstruct parent : allEntitiesList) {
							logger.debug("index: " + index);
							logger.debug("parent: " + parent);
							logger.debug("equivalentSuperLexicalsWithNullAsPlaceholder[index]: "
									+ equivalentSuperLexicalsWithNullAsPlaceholder[index]);
							index++;
						}
						processedSuperLexicals.add(superLexical);
						logger.debug("processedSuperLexicals.size(): " + processedSuperLexicals.size());
					}
					hpVector.put(equivalentSuperLexicalsWithNullAsPlaceholder, new Double(0d));
				} else
					logger.debug("superLexical already processed");
			}
		}

		logger.debug("hpVector: " + hpVector);
		logger.debug("hpVector.size(): " + hpVector.size());
		for (CanonicalModelConstruct[] constructs : hpVector.keySet()) {
			logger.debug("constructs: " + constructs);
			for (CanonicalModelConstruct construct : constructs)
				logger.debug("construct: " + construct);
		}
		logger.debug("this: " + this);
		super.setConstructsWeightsMap(hpVector);
		return hpVector;
	}

	protected SuperLexical[] fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities(SuperLexical superLexical,
			Set<SuperLexical> equivalentSuperLexicals, List<SuperAbstract> allEntitiesList,
			SuperLexical[] equivalentSuperLexicalsWithNullAsPlaceholder, Set<SuperLexical> processedSuperLexicals) {
		//TODO change this kind of logging into aspects
		logger.debug("in fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities");
		logger.debug("superLexical: " + superLexical);
		logger.debug("equivalentSuperLexicals: " + equivalentSuperLexicals);
		logger.debug("allEntitiesList: " + allEntitiesList);
		logger.debug("equivalentSuperLexicalsWithNullAsPlaceholder: " + equivalentSuperLexicalsWithNullAsPlaceholder);
		logger.debug("processedSuperLexicals: " + processedSuperLexicals);

		SuperLexical[] equivalentSuperLexicalsVectorElementArray = equivalentSuperLexicalsWithNullAsPlaceholder;
		for (SuperLexical equivalentSuperLexical : equivalentSuperLexicals) {
			logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
			SuperAbstract firstAncestorSuperAbstractOfEquivalentSuperLexical = equivalentSuperLexical.getFirstAncestorSuperAbstract();
			logger.debug("firstAncestorSuperAbstractOfEquivalentSuperLexical: " + firstAncestorSuperAbstractOfEquivalentSuperLexical);
			if (allEntitiesList.contains(firstAncestorSuperAbstractOfEquivalentSuperLexical)) {
				logger.debug("found firstAncestorSuperAbstractOfEquivalentSuperLexical in allEntitiesList");
				if (!processedSuperLexicals.contains(equivalentSuperLexical)) {
					logger.debug("equivalentSuperLexical not processed yet ... place it in vector element at some position as it's parent firstAncestorSuperAbstract");
					logger.debug("allEntitiesList.indexOf(firstAncestorSuperAbstract): "
							+ allEntitiesList.indexOf(firstAncestorSuperAbstractOfEquivalentSuperLexical));
					equivalentSuperLexicalsVectorElementArray[allEntitiesList.indexOf(firstAncestorSuperAbstractOfEquivalentSuperLexical)] = equivalentSuperLexical;
				} else
					logger.debug("equivalentSuperLexical has already been processed ... do nothing");
			} else
				logger.debug("didn't find firstAncestorSuperAbstractOfEquivalentSuperLexical in allEntitiesList ... do nothing");
		}
		logger.debug("equivalentSuperLexicalsVectorElementArray: " + equivalentSuperLexicalsVectorElementArray);
		int index = 0;
		for (SuperAbstract parent : allEntitiesList) {
			logger.debug("index: " + index);
			logger.debug("parent: " + parent);
			logger.debug("equivalentAttributes[index]: " + equivalentSuperLexicalsVectorElementArray[index]);
			index++;
		}
		return equivalentSuperLexicalsVectorElementArray;
	}

	protected SuperLexical[] generateArrayForEquivalentLexicalsElementAndSetAllNull(int length) {
		SuperLexical[] equivalentAttributes = new SuperLexical[length];
		for (int i = 0; i < length; i++) {
			equivalentAttributes[i] = null;
		}
		return equivalentAttributes;
	}
}
