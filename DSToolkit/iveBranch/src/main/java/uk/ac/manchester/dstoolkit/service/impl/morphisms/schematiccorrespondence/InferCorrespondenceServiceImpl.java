/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.GenerateSchematicCorrespondencesForPhenotypeService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.SchematicCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.DecoderService;
import ec.BreedingSource;
import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.Population;
import ec.Statistics;
import ec.simple.SimpleStatistics;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add findTopIndividuals().
 */

//TODO think about whether this should be singleton or prototype
//@Scope("prototype")
@Service(value = "inferCorrespondenceService")
public class InferCorrespondenceServiceImpl implements InferCorrespondenceService {
	
	@Autowired
	@Qualifier("schematicCorrespondenceService")
	private SchematicCorrespondenceService schematicCorrespondenceService;

	@Autowired
	@Qualifier("generateSchematicCorrespondenceForPhenotypeService")
	private GenerateSchematicCorrespondencesForPhenotypeService generateSchematicCorrespondenceForPhenotypeService;

	private static Logger logger = Logger.getLogger(InferCorrespondenceServiceImpl.class);

	/**/
	private ArrayList<Individual> subPopulationList; 
	private final int TOP_X_INDIVIDUALS = 5;

	//TODO this might not work while this is a prototype ... 
	@Autowired
	@Qualifier("decoderService")
	private DecoderService decoderService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;

	//used to try and enforce a predictable order of constructs in the vectors as I'm suspecting the different order is the reason that I got different fitness values for the same genotype
	private LinkedHashSet<Schema> placeSchemasInLinkedHashSetToEnforcePredictableOrdering(Set<Schema> schemas) {
		LinkedHashSet<Schema> schemasLinkedHashSet = new LinkedHashSet<Schema>();
		for (Schema schema : schemas) {
			logger.debug("schema: " + schema);
			schemasLinkedHashSet.add(schema);
		}
		return schemasLinkedHashSet;
	}

	//TODO maxMatchingScore ... either infer somehow or pass in as one of the controlParameters ... should be inferrable from matchings though or rather the matchers that produced the matchings
	public Set<SchematicCorrespondence> inferCorrespondences(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			double maxMatchingScore, Map<ControlParameterType, ControlParameter> controlParameters) {

		logger.debug("in inferCorrespondences");
		logger.debug("sourceSchemas.size(): " + sourceSchemas.size());
		logger.debug("sourceSchemas: " + sourceSchemas);
		logger.debug("targetSchemas.size(): " + targetSchemas.size());
		logger.debug("targetSchemas: " + targetSchemas);
		logger.debug("matchings.size(): " + matchings.size());
		logger.debug("matchings: " + matchings);
		logger.debug("controlParameters: " + controlParameters);
		logger.debug("maxMatchingScore: " + maxMatchingScore); //this depends on number and type of matchers used, in particular, what the max score is that each matcher produces
		logger.debug("decoderService: " + decoderService);

		Set<SchematicCorrespondence> correspondencesToReturn = new HashSet<SchematicCorrespondence>();

		//TODO: think about return type and parameters ... should both (sets of) schemas be provided ... probably, 
		//otherwise could have some issues if the order in the matchings is different, which could happen

		//TODO: think about whether I'll need a Finalizer

		//used to try and enforce a predictable order of constructs in the vectors as I'm suspecting the different order is the reason that I got different fitness values for the same genotype
		LinkedHashSet<Schema> sourceSchemaLinkedHashSet = this.placeSchemasInLinkedHashSetToEnforcePredictableOrdering(sourceSchemas);
		LinkedHashSet<Schema> targetSchemaLinkedHashSet = this.placeSchemasInLinkedHashSetToEnforcePredictableOrdering(targetSchemas);

		logger.debug("before generating elrChromosome");
		ELRChromosome elrChromosome = new ELRChromosome(sourceSchemaLinkedHashSet, targetSchemaLinkedHashSet, matchings);
		List<SuperAbstract[]> chromosome = elrChromosome.getChromosomeOfPairsOfSuperAbstracts();
		logger.debug("finished generating elrChromosome");
		logger.debug("chromosome: " + chromosome);
		elrChromosome.generateDerivedOneToOneMatchings();
		elrChromosome.identifyEquivalentSuperLexicals();
		decoderService.setChromosome(elrChromosome);

		int chromosomeLength = chromosome.size(); //elrChromosome.getChromosomeOfDerivedSuperAbstractMatchings().size();
		logger.debug("chromosomeLength: " + chromosomeLength);

		Output output = new Output(false);
		logger.debug("output: " + output);
		File ecLogFile = new File("ecInferCorrespondenceLogFile.log");
		logger.debug("ecLogFile: " + ecLogFile);
		int logFileNumber = -1;
		try {
			logFileNumber = output.addLog(ecLogFile, true);
		} catch (IOException e) {
			logger.error("unable to create ecInferCorrespondenceLogFile.log");
			e.printStackTrace();
		}

		String[] args = new String[] { "-file", "src/main/resources/ecjInferCorrespondences.params" }; //TODO: this should probably go somewhere else in case the location changes
		logger.debug("args: " + args);
		ParameterDatabase parameterDatabase = Evolve.loadParameterDatabase(args);
		logger.debug("parameterDatabase: " + parameterDatabase);

		//the following assumes that there is only one subpopulation, i.e., that it's not using a coevolutionary algorithm

		if (controlParameters.containsKey("INFERCORR_POPULATION_SIZE")) {
			Parameter param = new Parameter("pop.subpop.0.size");
			logger.debug("param: " + param);
			parameterDatabase.set(param, controlParameters.get("INFERCORR_POPULATION_SIZE").getValue());
		}
		if (controlParameters.containsKey("INFERCORR_CROSSOVER_RATE")) {
			Parameter param = new Parameter("pop.subpop.0.species.crossover-prob");
			logger.debug("param: " + param);
			parameterDatabase.set(param, controlParameters.get("INFERCORR_CROSSOVER_RATE").getValue());
		}
		if (controlParameters.containsKey("INFERCORR_NUMBER_OF_GENERATIONS")) {
			Parameter param = new Parameter("generations");
			logger.debug("param: " + param);
			parameterDatabase.set(param, controlParameters.get("INFERCORR_NUMBER_OF_GENERATIONS").getValue());
		}

		Parameter genomeSizeParam = new Parameter("pop.subpop.0.species.genome-size");
		logger.debug("genomeSizeParam: " + genomeSizeParam);
		parameterDatabase.set(genomeSizeParam, String.valueOf(chromosomeLength));

		Parameter mutationProbParam = new Parameter("pop.subpop.0.species.mutation-prob");
		logger.debug("mutationProbParam: " + mutationProbParam);
		double mutationProb = 1.0d / chromosomeLength;
		logger.debug("mutationProb: " + mutationProb);
		parameterDatabase.set(mutationProbParam, String.valueOf(mutationProb));

		
		//mutation rate depends on length of chromosome / genome-size in ec but could also be configured in config-file, use 1/n with n = length of chromosome as default if none provided
		logger.debug("parameterDatabase: " + parameterDatabase);

		EvolutionState state = Evolve.initialize(parameterDatabase, 0);
		logger.debug("state: " + state);

		logger.debug("state.breeder: " + state.breeder);
		logger.debug("state.initializer: " + state.initializer);
		logger.debug("state.evaluator: " + state.evaluator);

		state.startFresh();

		logger.debug("state.population.subpops[0]: " + state.population.subpops[0]);
		logger.debug("state.population.subpops[0].species: " + state.population.subpops[0].species);
		logger.debug("state.population.subpops[0].species.pipe_prototype: " + state.population.subpops[0].species.pipe_prototype);
		for (BreedingSource pipeline : state.population.subpops[0].species.pipe_prototype.sources) {
			logger.debug("pipeline: " + pipeline);
		}

		for (Individual individual : state.population.subpops[0].individuals) {
			logger.debug("individual: " + individual);
			if (individual != null) {
				//individual.printIndividualForHumans(state, logFileNumber);
				logger.debug("individual.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
			}
		}

		int result = EvolutionState.R_NOTDONE;
		logger.debug("result: " + result);
		while (result == EvolutionState.R_NOTDONE) {
			Population p = state.population;

			for (Individual individual : p.subpops[0].individuals) {
				logger.debug("individual: " + individual);
				if (individual != null) {
					//individual.printIndividualForHumans(state, logFileNumber);
					logger.debug("individual.genotypeToStringForHumans(): " + individual.genotypeToStringForHumans());
				}
			}
			logger.debug("state: " + state);
			logger.debug("before calling evolve");
			result = state.evolve();
			logger.debug("after evolve");
			logger.debug("result: " + result);
			logger.debug("individuals.length: " + state.population.subpops[0].individuals.length);
			
			/*Create a new array of Individuals*/
			subPopulationList = new ArrayList<Individual>();
			
			/*Iterate through the individuals of the final iteration*/
			for (Individual individual : state.population.subpops[0].individuals) {
				logger.debug("individual: " + individual);
				if (individual != null) {
					logger.debug("Genotype: ");
					logger.debug("individual: " + individual.genotypeToStringForHumans());
					logger.debug("individual.fitness: " + individual.fitness.fitness());
					logger.debug("individial.birthday: " + individual.birthday);
					logger.debug("individual.count: " + individual.count);
					logger.debug("individual.evaluated: " + individual.evaluated);

						
					subPopulationList.add(individual);
					
					//individual.printIndividualForHumans(state, logFileNumber);
				}
			}
		}
		state.finish(result);
		//state.run(EvolutionState.C_STARTED_FRESH);

		
		Statistics statistics = state.statistics;
		if (statistics instanceof SimpleStatistics) {
			logger.debug("statistics is SimpleStatistics::");
			SimpleStatistics simpleStatistics = (SimpleStatistics) statistics;

			logger.debug("subPopulationList: " + subPopulationList);
			Individual[] bestIndividualsOfRun = findTopIndividuals(subPopulationList);
			
			logger.debug("bestIndividualsOfRun.length: " + bestIndividualsOfRun.length);
			for (Individual bestIndividualOfRun : bestIndividualsOfRun) {
				if (bestIndividualOfRun != null) {
		
					logger.debug("bestIndividualOfRun: " + bestIndividualOfRun.genotypeToStringForHumans());
					logger.debug("bestIndividualOfRun.fitness: " + bestIndividualOfRun.fitness.fitness());
					logger.debug("bestIndividualOfRun.birthday: " + bestIndividualOfRun.birthday);
					logger.debug("bestIndividualOfRun.count: " + bestIndividualOfRun.count);
					logger.debug("bestIndividualOfRun.evaluated: " + bestIndividualOfRun.evaluated);
					if (bestIndividualOfRun instanceof ELRGenotypeBitVectorIndividual) {
						ELRGenotypeBitVectorIndividual bestGenotypeBitVectorIndividual = (ELRGenotypeBitVectorIndividual) bestIndividualOfRun;
						ELRPhenotype bestPhenotype = bestGenotypeBitVectorIndividual.getElrPhenotype();
						logger.debug(">>>> bestPhenotype: " + bestPhenotype);
						if (bestPhenotype != null)
							correspondencesToReturn.addAll(generateSchematicCorrespondenceForPhenotypeService.generateSchematicCorrespondencesForBestPhenotype(bestPhenotype));
					}
				}
			}
		}
		
		//save schematic correspondence in the database
		for (SchematicCorrespondence sc : correspondencesToReturn) {
			if(schematicCorrespondenceService.findSchematicCorrespondenceByName(sc.getName()) == null)
				schematicCorrespondenceService.addSchematicCorrespondence(sc);
		}
		

		Evolve.cleanup(state);

		return correspondencesToReturn;
	}
	
	
	/***
	 * Discover X top Individuals based on their fitness score.
	 * 
	 * @param newList
	 * @param inputList
	 * @return Individual[] of top X individuals.
	 */
	private Individual[] findTopIndividuals(ArrayList<Individual> inputList) {

		Individual[] topIndividuals = new Individual[this.TOP_X_INDIVIDUALS];		
		
		//Initialisation: Fill the topIndividuals[] with first individuals
		if (topIndividuals.length <= inputList.size()) {
			for (int i=0; i<topIndividuals.length; i++) {
				topIndividuals[i] = inputList.get(i);
				inputList.remove(i);
		    }//end for
		} else
			return null; 

		while (!inputList.isEmpty()) {
			
			topIndividuals = bubbleSort(topIndividuals);
			
			//This is just for debug
		    for (int w=0; w<topIndividuals.length; w++) {
		    	logger.debug(">>>> Individual: " + topIndividuals[w].fitness.fitness());
		       }		    
	    	logger.debug("---");
		    
		    //Get the first element of the newList
		    Individual  worstElement  = topIndividuals[0];
		    Individual  firstOnList   = inputList.get(0);
		    
		    if (worstElement.fitness.fitness() <= firstOnList.fitness.fitness()) {
		      //replace worst individual with the best individual on the list
		      topIndividuals[0] = firstOnList;
		    }//end if

		    inputList.remove(0);
		   }//end while

		   return topIndividuals;
	}//end findTopIndividuals()	
	
	/**
	 * Sort the array of Individuals.
	 * @param array
	 * @param n
	 * @return Individual[] of sorted Individuals.
	 */
	public Individual[] bubbleSort(Individual[] array) {
		Individual temp = null;
		
		for (int i = 0; i < array.length; i++) {
	 		for (int j = 1; j < array.length-i; j++) {
		 		if (array[j-1].fitness.fitness() > array[j].fitness.fitness()) {
		 			temp = array[j-1];
		 			array[j-1] = array[j];
		 			array[j] = temp;
	 			}//end if
	 		}//end for
	 	}//end for		
		return array;
	}//bubbleSort	
}//end class
