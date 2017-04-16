/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;

/**
 * @author chedeler
 *
 */
@Service(value = "pairsOfEntitySetsGeneratorService")
public class PairsOfEntitySetsGeneratorServiceImpl implements PairsOfEntitySetsGeneratorService {

	private static Logger logger = Logger.getLogger(PairsOfEntitySetsGeneratorServiceImpl.class);

	private Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap;
	private Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap;

	public Set<PairOfEntitySets> generatePairsOfSourceAndTargetEntitySetsForELRGenotypeBitVectorIndividual(
			ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual, List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts) {
		logger.debug("in generateSourceAndTargetEntitySetsForELRGenotypeBitVectorIndividual");
		logger.debug("this: " + this);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual);
		if (elrGenotypeBitVectorIndividual != null)
			logger.debug("elrGenotypeBitVectorIndividual.genotypeToStringForHumans(): " + elrGenotypeBitVectorIndividual.genotypeToStringForHumans());
		logger.debug("chromosomeOfPairsOfSuperAbstracts: " + chromosomeOfPairsOfSuperAbstracts);
		for (SuperAbstract[] pairOfSuperAbstracts : chromosomeOfPairsOfSuperAbstracts) {
			logger.debug("pairOfSuperAbstracts: " + pairOfSuperAbstracts);
			logger.debug("pairOfSuperAbstracts[0]: " + pairOfSuperAbstracts[0]);
			logger.debug("pairOfSuperAbstracts[1]: " + pairOfSuperAbstracts[1]);
		}
		this.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividual, chromosomeOfPairsOfSuperAbstracts);
		return this.getPairsOfEntitySets();
	}

	protected void generatePairsOfSourceAndTargetEntitySets(ELRGenotypeBitVectorIndividual elrGenotypeBitVectorIndividual,
			List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts) {
		logger.debug("in generatePairsOfSourceAndTargetEntitySets");
		logger.debug("this: " + this);
		logger.debug("elrGenotypeBitVectorIndividual: " + elrGenotypeBitVectorIndividual.toString());
		logger.debug("elrGenotypeBitVectorIndividual.genome: " + elrGenotypeBitVectorIndividual.genome);
		logger.debug("chromosomeOfPairsOfSuperAbstracts: " + chromosomeOfPairsOfSuperAbstracts);
		sourceSuperAbstractPairOfEntitySetsMap = new HashMap<SuperAbstract, PairOfEntitySets>();
		targetSuperAbstractPairOfEntitySetsMap = new HashMap<SuperAbstract, PairOfEntitySets>();
		int index = 0;
		for (Boolean selected : elrGenotypeBitVectorIndividual.genome) {
			logger.debug("index: " + index);
			logger.debug("selected: " + selected);
			if (selected) {
				logger.debug("selected = true");
				SuperAbstract superAbstract1 = chromosomeOfPairsOfSuperAbstracts.get(index)[0];
				logger.debug("superAbstract1: " + superAbstract1);
				SuperAbstract superAbstract2 = chromosomeOfPairsOfSuperAbstracts.get(index)[1];
				logger.debug("superAbstract2: " + superAbstract2);
				if (!sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1)
						&& !targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2)) {
					logger.debug("sourceSuperAbstractPairOfEntitySetsMap doesn't contain superAbstract1 and targetSuperAbstractPairOfEntitySetsMap doesn't  contain superAbstract2");
					logger.debug("create new pairOfEntitySets with superAbstract1 and superAbstract2 and add to maps");
					PairOfEntitySets pairOfEntitySets = this.createNewPairOfEntitySetsWithSuperAbstract1AndSuperAbstract2(superAbstract1,
							superAbstract2);
					this.placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets(pairOfEntitySets);
				} else if (sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1)
						&& targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2)) {
					logger.debug("sourceSuperAbstractPairOfEntitySetsMap contains superAbstract1 and targetSuperAbstractPairOfEntitySetsMap contains superAbstract2");
					logger.debug("check whether they're in the same pairOfEntitySets");
					if (sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1).equals(targetSuperAbstractPairOfEntitySetsMap.get(superAbstract2))) {
						logger.debug("both are in the same pairOfEntitySets - do nothing");
						//shouldn't have to do anything here as superAbstract1 and superAbstract2 should already be in the entitySets of the pair
						//TODO test for it
					} else {
						logger.debug("both are in different pairOfEntitySets - create new pairOfEntitySets containing all superAbstracts from the two pairs");
						logger.debug("place new pairOfEntitySets into map replacing the previous two pairs");
						PairOfEntitySets pairOfEntitySets1 = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);
						PairOfEntitySets pairOfEntitySets2 = targetSuperAbstractPairOfEntitySetsMap.get(superAbstract2);
						PairOfEntitySets newPairOfEntitySets = this.createNewPairOfEntitySetsFromTwoOldPairsOfEntitySets(pairOfEntitySets1,
								pairOfEntitySets2);
						this.placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets(newPairOfEntitySets);
						//superAbstract1 and superAbstract2 shouldn't have to be added explicitly, as they should be in the entitySets of the two pairs and, therefore, in the new one too
						//TODO test for it
					}
				} else if (sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1)
						&& !targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2)) {
					logger.debug("sourceSuperAbstractPairOfEntitySetsMap contains superAbstract1 but targetSuperAbstractPairOfEntitySetsMap doesn't contain superAbstract2");
					logger.debug("get pairOfEntitySets for superAbstract1 from sourceSuperAbstractPairOfEntitySetsMap");
					PairOfEntitySets pairOfEntitySets = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);
					logger.debug("add superAbstract2 to targetEntitySet of pairOfEntitySets");
					pairOfEntitySets.addTargetEntity(superAbstract2);
					logger.debug("place pairOfEntitySets in targetSuperAbstractPairOfEntitySetsMap for superAbstract2");
					this.targetSuperAbstractPairOfEntitySetsMap.put(superAbstract2, pairOfEntitySets);
					//using placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets would be overkill here as only the above needs to be added
				} else if (!sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1)
						&& targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2)) {
					logger.debug("sourceSuperAbstractPairOfEntitySetsMap doesn't contain superAbstract1 but targetSuperAbstractPairOfEntitySetsMap contains superAbstract2");
					logger.debug("get pairOfEntitySets for superAbstract2 from targetSuperAbstractPairOfEntitySetsMap");
					PairOfEntitySets pairOfEntitySets = targetSuperAbstractPairOfEntitySetsMap.get(superAbstract2);
					logger.debug("add superAbstract1 to sourceEntitySet of pairOfEntitySets");
					pairOfEntitySets.addSourceEntity(superAbstract1);
					logger.debug("place pairOfEntitySets in sourceSuperAbstractPairOfEntitySetsMap for superAbstract1");
					this.sourceSuperAbstractPairOfEntitySetsMap.put(superAbstract1, pairOfEntitySets);
					//using placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets would be overkill here as only the above needs to be added
				}
			} else
				logger.debug("selected = false - do nothing");
			index++;
		}
	}

	protected PairOfEntitySets createNewPairOfEntitySetsWithSuperAbstract1AndSuperAbstract2(SuperAbstract superAbstract1, SuperAbstract superAbstract2) {
		logger.debug("in createNewPairOfEntitySetsWithSuperAbstract1AndSuperAbstract2");
		logger.debug("this: " + this);
		logger.debug("superAbstract1: " + superAbstract1);
		logger.debug("superAbstract2: " + superAbstract2);
		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(superAbstract1, superAbstract2);
		logger.debug("pairOfEntitySets: " + pairOfEntitySets);
		logger.debug("pairOfEntitySets.getSourceEntitySet().size(): " + pairOfEntitySets.getSourceEntitySet().size());
		logger.debug("pairOfEntitySets.getTargetEntitySet().size(): " + pairOfEntitySets.getTargetEntitySet().size());
		return pairOfEntitySets;
	}

	protected PairOfEntitySets createNewPairOfEntitySetsFromTwoOldPairsOfEntitySets(PairOfEntitySets pairOfEntitySets1,
			PairOfEntitySets pairOfEntitySets2) {
		logger.debug("in createNewPairOfEntitySetsFromTwoOldPairOfEntitySets");
		logger.debug("this: " + this);
		logger.debug("pairOfEntitySets1: " + pairOfEntitySets1);
		logger.debug("pairOfEntitySets1.getSourceEntitySet().size(): " + pairOfEntitySets1.getSourceEntitySet().size());
		logger.debug("pairOfEntitySets1.getTargetEntitySet().size(): " + pairOfEntitySets1.getTargetEntitySet().size());
		logger.debug("pairOfEntitySets2: " + pairOfEntitySets2);
		logger.debug("pairOfEntitySets2.getSourceEntitySet().size(): " + pairOfEntitySets2.getSourceEntitySet().size());
		logger.debug("pairOfEntitySets2.getTargetEntitySet().size(): " + pairOfEntitySets2.getTargetEntitySet().size());
		PairOfEntitySets newPairOfEntitySets = new PairOfEntitySets(pairOfEntitySets1.getSourceEntitySet(), pairOfEntitySets1.getTargetEntitySet());
		newPairOfEntitySets.addSourceEntities(pairOfEntitySets2.getSourceEntitySet());
		newPairOfEntitySets.addTargetEntities(pairOfEntitySets2.getTargetEntitySet());
		logger.debug("newPairOfEntitySets: " + newPairOfEntitySets);
		logger.debug("newPairOfEntitySets.getSourceEntitySet().size(): " + newPairOfEntitySets.getSourceEntitySet().size());
		logger.debug("newPairOfEntitySets.getTargetEntitySet().size(): " + newPairOfEntitySets.getTargetEntitySet().size());
		return newPairOfEntitySets;
	}

	//TODO sideeffect, changes sourceSuperAbstractPairOfEntitySetsMap and targetSuperAbstractPairOfEntitySetsMap and doesn't return them
	protected void placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets(
			PairOfEntitySets pairOfEntitySets) {
		logger.debug("in placePairOfEntitySetsInSourceAndTargetSuperAbstractPairOfEntitySetsMapsForAllSuperAbstractsInPairOfEntitySets");
		logger.debug("this: " + this);
		logger.debug("pairOfEntitySets: " + pairOfEntitySets);
		Set<SuperAbstract> sourceSuperAbstracts = pairOfEntitySets.getSourceEntitySet();
		logger.debug("sourceSuperAbstracts: " + sourceSuperAbstracts);
		this.sourceSuperAbstractPairOfEntitySetsMap = this.placePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet(
				pairOfEntitySets, sourceSuperAbstracts, this.sourceSuperAbstractPairOfEntitySetsMap);
		Set<SuperAbstract> targetSuperAbstracts = pairOfEntitySets.getTargetEntitySet();
		logger.debug("targetSuperAbstracts: " + targetSuperAbstracts);
		this.targetSuperAbstractPairOfEntitySetsMap = this.placePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet(
				pairOfEntitySets, targetSuperAbstracts, this.targetSuperAbstractPairOfEntitySetsMap);
	}

	protected Map<SuperAbstract, PairOfEntitySets> placePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet(
			PairOfEntitySets pairOfEntitySets, Set<SuperAbstract> superAbstracts,
			Map<SuperAbstract, PairOfEntitySets> superAbstractPairOfEntitySetsMap) {
		logger.debug("in placePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet");
		logger.debug("this: " + this);
		logger.debug("pairOfEntitySets: " + pairOfEntitySets);
		logger.debug("superAbstracts: " + superAbstracts);
		logger.debug("superAbstractPairOfEntitySetsMap: " + superAbstractPairOfEntitySetsMap);
		for (SuperAbstract superAbstract : superAbstracts) {
			logger.debug("superAbstract: " + superAbstract);
			if (superAbstractPairOfEntitySetsMap.containsKey(superAbstract))
				logger.debug("superAbstractPairOfEntitySetsMap contains superAbstract");
			superAbstractPairOfEntitySetsMap.put(superAbstract, pairOfEntitySets);
			//I'm not checking whether and what I'm replacing ... doesn't really matter as long as the end result is correct ...
		}
		return superAbstractPairOfEntitySetsMap;
	}

	protected Set<PairOfEntitySets> getPairsOfEntitySets() {
		logger.debug("in getPairsOfEntitySets");
		logger.debug("this: " + this);
		Set<PairOfEntitySets> pairsOfEntitySets = new HashSet<PairOfEntitySets>();
		for (PairOfEntitySets pairOfEntitySets : sourceSuperAbstractPairOfEntitySetsMap.values()) {
			logger.debug("source - pairOfEntitySets: " + pairOfEntitySets);
			if (!pairsOfEntitySets.contains(pairOfEntitySets)) {
				logger.debug("pairsOfEntitySets doesn't contain pairOfEntitySets - add it");
				pairsOfEntitySets.add(pairOfEntitySets);
			} else
				logger.debug("pairsOfEntitySets contains pairOfEntitySets");
		}
		for (PairOfEntitySets pairOfEntitySets : targetSuperAbstractPairOfEntitySetsMap.values()) {
			logger.debug("target - pairOfEntitySets: " + pairOfEntitySets);
			if (!pairsOfEntitySets.contains(pairOfEntitySets)) {
				logger.debug("pairsOfEntitySets doesn't contain pairOfEntitySets - add it");
				pairsOfEntitySets.add(pairOfEntitySets);
			} else
				logger.debug("pairsOfEntitySets contains pairOfEntitySets");
		}
		return pairsOfEntitySets;
	}

	/**
	 * @return the sourceSuperAbstractPairOfEntitySetsMap
	 */
	protected Map<SuperAbstract, PairOfEntitySets> getSourceSuperAbstractPairOfEntitySetsMap() {
		return sourceSuperAbstractPairOfEntitySetsMap;
	}

	/**
	 * @return the targetSuperAbstractPairOfEntitySetsMap
	 */
	protected Map<SuperAbstract, PairOfEntitySets> getTargetSuperAbstractPairOfEntitySetsMap() {
		return targetSuperAbstractPairOfEntitySetsMap;
	}

}
