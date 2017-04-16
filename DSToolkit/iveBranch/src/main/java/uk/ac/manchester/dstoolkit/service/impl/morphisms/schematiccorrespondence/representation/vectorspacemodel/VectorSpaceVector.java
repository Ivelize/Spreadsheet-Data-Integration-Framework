/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import java.util.Arrays;
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
public abstract class VectorSpaceVector {

	private static Logger logger = Logger.getLogger(VectorSpaceVector.class);

	private boolean isSource;
	private ELRChromosome chromosome;
	private final ELRPhenotype phenotype;

	private LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap;

	public abstract LinkedHashMap<CanonicalModelConstruct[], Double> generateVector(LinkedHashSet<SuperAbstract> entitySet);

	public VectorSpaceVector(ELRChromosome chromosome, ELRPhenotype phenotype, boolean isSource) {
		logger.debug("in VectorSpaceVector");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("isSource: " + isSource);
		this.chromosome = chromosome;
		this.phenotype = phenotype;
		this.isSource = isSource;
	}

	public LinkedHashMap<CanonicalModelConstruct[], Double> calculateTfIdfWeightsForVectorElementArrays() {
		logger.debug("in calculateTfIdfWeights");
		logger.debug("constructsWeightsMap: " + constructsWeightsMap);
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);

		logger.debug("this: " + this);
		logger.debug("this.isSource: " + this.isSource);

		//T = total number of constructs in current vector
		int T = this.calculate_T_determineTotalNumberOfConstructsInThisVector(this.constructsWeightsMap);
		//N = total number of vectors in vector space to which this ELR belongs
		int N = this.calculate_N_determineSumOfNumberOfELRsAndNumberOfUnassociatedEntitiesInPhenotype();

		for (CanonicalModelConstruct[] constructs : constructsWeightsMap.keySet()) {
			logger.debug("constructs: " + constructs);

			for (CanonicalModelConstruct construct : constructs) {
				logger.debug("construct: " + construct);
			}

			double tfIdfWeight = this.calculateTfIdfWeightForVectorElementArrayWithTandN(constructs, T, N);
			logger.debug("tfIdfWeight: " + tfIdfWeight);
			double weight = this.constructsWeightsMap.get(constructs);
			logger.debug("weight: " + weight);
			double newWeight = tfIdfWeight;
			this.constructsWeightsMap.put(constructs, new Double(newWeight));
			logger.debug("constructsWeightsMap.size(): " + constructsWeightsMap.size());
			logger.debug("constructsWeightsMap.get(construct) after assigning tfIdfWeight: " + constructsWeightsMap.get(constructs));
		}
		return this.constructsWeightsMap;
	}

	public boolean containsAnEquivalentSuperLexical(Set<SuperLexical> equivalentSuperLexicals) {
		logger.debug("in containsAnEquivalentSuperLexical");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		for (SuperLexical equivalentSuperLexical : equivalentSuperLexicals) {
			logger.debug("equivalentSuperLexical: " + equivalentSuperLexical);
			Set<CanonicalModelConstruct[]> constructsInVector = this.constructsWeightsMap.keySet();
			for (CanonicalModelConstruct[] constructs : constructsInVector) {
				logger.debug("constructs: " + constructs);
				for (CanonicalModelConstruct construct : constructs)
					logger.debug("construct: " + construct);
				List<CanonicalModelConstruct> constructsList = Arrays.asList(constructs);
				if (constructsList.contains(equivalentSuperLexical)) {
					logger.debug("found sl in constructsList: return true");
					return true;
				}
			}
		}
		return false;
	}

	protected double calculateTfIdfWeightForVectorElementArrayWithTandN(CanonicalModelConstruct[] constructs, int T, int N) {
		logger.debug("in calculateTfIdfWeightForVectorElementArrayWithTandN");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("T: " + T);
		logger.debug("N: " + N);
		double tfIdfWeight = 0d;
		logger.debug("constructs: " + constructs);

		for (CanonicalModelConstruct construct : constructs) {
			logger.debug("construct: " + construct);
		}

		//t = number of equivalent constructs in vector element
		int t = this.calculate_t_determineNumberOfConstructsIncludingNullInVectorElementArray(constructs);
		logger.debug("t: " + t);

		//n = number of vectors in vector space with equivalent constructs to specific term
		int n = this.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);

		double tf = (double) t / (double) T; // = t / T;
		double idf = Math.log10((double) N / (double) n); // = Math.log10(N / n);

		//tf = new Double(t / T);
		logger.debug("tf: " + tf);
		//idf = Math.log10(N / n);
		logger.debug("idf: " + idf);
		tfIdfWeight = tf * idf; //new Double(tf * idf);
		logger.debug("tfIdfWeight: " + tfIdfWeight);
		return tfIdfWeight;
	}

	//TODO this might be better in phenotype, as all information comes from there, but result is needed here ...
	protected int calculate_N_determineSumOfNumberOfELRsAndNumberOfUnassociatedEntitiesInPhenotype() {
		logger.debug("in calculate_N_determineSumOfNumberOfELRsAndNumberOfUnassociatedEntitiesInPhenotype");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		int N = this.phenotype.getElrs().size();
		logger.debug("this.phenotype.getElrs().size(): " + this.phenotype.getElrs().size());
		logger.debug("this.phenotype.getUnassociatedSourceEntities().size(): " + this.phenotype.getMatchedButUnassociatedSourceEntities().size());
		logger.debug("this.phenotype.getUnassociatedTargetEntities().size(): " + this.phenotype.getMatchedButUnassociatedTargetEntities().size());
		logger.debug("isSource");
		if (isSource)
			N += this.phenotype.getMatchedButUnassociatedSourceEntities().size();
		else
			N += this.phenotype.getMatchedButUnassociatedTargetEntities().size();

		logger.debug("N: " + N);
		return N;
	}

	//TODO refactor and/or move most of the code into phenotype ...
	//TODO needs phenotype and chromosome ... 
	protected int calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(CanonicalModelConstruct[] constructs) {
		logger.debug("in calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("constructs: " + constructs);
		if (constructs[0] instanceof SuperAbstract) {
			logger.debug("superAbstract - n = 1");
			return 1;
		} else if (constructs[0] == null || constructs[0] instanceof SuperLexical) {
			logger.debug("superLexical - n = number of vectors with equivalent constructs");

			LinkedHashSet<SuperLexical> equivalentSuperLexicals = new LinkedHashSet<SuperLexical>();

			for (CanonicalModelConstruct construct : constructs) {
				logger.debug("construct: " + construct);
				logger.debug("isSource: " + isSource);
				if (construct != null) {
					equivalentSuperLexicals.addAll(chromosome.getEquivalentSuperLexicalsForSuperLexical((SuperLexical) construct, isSource));
				} else
					logger.debug("construct is null");
			}

			if (equivalentSuperLexicals.size() > 0) {
				logger.debug("found equivalent equivalentSuperLexicals, size: " + equivalentSuperLexicals.size());
				int n = this.phenotype.getNumberOfVectorsInVectorSpaceWithEquivalentSuperLexicals(equivalentSuperLexicals, isSource);
				logger.debug("n: " + n);
				return n;
			}
		}
		return 1; //constructs are in this vector, so should be 1
	}

	//TODO this just returns the sum of the length of the arrays in the map, it doesn't check how many constructs are in it ... the way the arrays are generated, this is fine, as
	//TODO they won't be empty (under normal circumstances) but it may be better to rename the method accordingly to make that behaviour more obvious
	protected int calculate_T_determineTotalNumberOfConstructsInThisVector(LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap) {
		logger.debug("in calculate_T_determineTotalNumberOfConstructsInThisVector");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("constructsWeightsMap: " + constructsWeightsMap);
		int T = 0;
		for (CanonicalModelConstruct[] constructs : constructsWeightsMap.keySet()) {
			logger.debug("constructs: " + constructs);
			logger.debug("constructs.length: " + constructs.length);
			T += constructs.length;
			logger.debug("T: " + T);
		}
		logger.debug("T to return: " + T);
		return T;
	}

	//TODO this just returns the length of the array, it doesn't check how many constructs are in it ... the way the array is generated, this is fine, as
	//TODO it won't be empty (under normal circumstances) but it may be better to rename the method accordingly to make that behaviour more obvious
	protected int calculate_t_determineNumberOfConstructsIncludingNullInVectorElementArray(CanonicalModelConstruct[] constructs) {
		logger.debug("in calculate_t_determineNumberOfConstructsIncludingNullInVectorElementArray");
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		logger.debug("constructs: " + constructs);
		if (constructs == null)
			return 0;
		return constructs.length;
	}

	protected SuperAbstract[] generateEntitiesVectorElement(LinkedHashSet<SuperAbstract> entitySet) {
		logger.debug("in generateEntitiesVectorElement");
		logger.debug("entitySet: " + entitySet);
		logger.debug("chromosome: " + chromosome);
		logger.debug("phenotype: " + phenotype);
		SuperAbstract[] allEntities = new SuperAbstract[entitySet.size()];
		int i = 0;
		for (SuperAbstract sa : entitySet) {
			logger.debug("sa: " + sa);
			allEntities[i] = sa;
			logger.debug("i: " + i);
			logger.debug("allEntities[i]: " + allEntities[i]);
			i++;
		}
		logger.debug("allEntities: " + allEntities);
		return allEntities;
	}

	/**
	 * @return the constructWeightMap
	 */
	public LinkedHashMap<CanonicalModelConstruct[], Double> getConstructsWeightsMap() {
		return constructsWeightsMap;
	}

	/**
	 * @param constructWeightMap the constructWeightMap to set
	 */
	public void setConstructsWeightsMap(LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap) {
		this.constructsWeightsMap = constructsWeightsMap;
	}

	/**
	 * @return the isSource
	 */
	public boolean isSource() {
		return isSource;
	}

	/**
	 * @param isSource the isSource to set
	 */
	public void setSource(boolean isSource) {
		this.isSource = isSource;
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
