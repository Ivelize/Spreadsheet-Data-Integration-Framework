package uk.ac.manchester.dstoolkit.service.impl.util.mutation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherType;
import uk.ac.manchester.dstoolkit.service.impl.util.mutation.SyntacticMutations.MisspellingMutationsType;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.mutation.MutationService;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/****
 * This service is responsible for introducing mutations to a single Ontology. The mutations are 
 * introduced just at the Syntactic level on local-names. The approach performs the following steps:
 * 
 * Input: A single ontology, perform the mutations to that ontology.
 * (2) Read all the class names from that ontology
 * 	   (2.2) choose the mutation rate: 	
 * 			- Mild mutation: mutate 80% (no), 20% (yes)
 * 			- Severe mutation: mutate 60% (no), 40% (yes)
 * 	   (2.3) choose the mutation mode:
 * 			- Edit-distance: will introduce mutations for edit-distance
 * 			- N-Gram: will introduce mutations for n-Gram
 * 			- Hybrid mode: will introduce mutations for both with a rate of 50% (n-Gram), 50% (edit-distance)
 * 
 * (3) Loop through the set of all Classes
 * 	   (3.1) strip the local-names of each class
 * 	   (3.2) if (mutate) then
 * 			 - tokenize 
 * 
 * Output: - A .csv file that holds the original with the mutated URIs of Classes
 * 		   - The mutated URIs can be used at a later stage to re-introduce mutations generated previously to a given ontology
 * 		   - The mutated version of the ontology.
 *         - The mutated version of the expectation matrix.
 *         - The mutated version of the BLOOMS cross-links alignments 
 * 
 * @author klitos
 *
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class MutationServiceImpl implements MutationService {

	private static Logger logger = Logger.getLogger(MutationServiceImpl.class);
	
	//Temp NamedGraph that will hold tokens with their mutations
	private String tokensGraph = "x-ns://mutation.onto/tokens/";
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	//Hold a List of all the possible mutations that can happen in each mode
	private List<MisspellingMutationsType> possibleMutations = null;
	
	//Hold a List of the string-based matchers, to be used when mutation mode is BOTH
	private List<MatcherType> matchersTypeList = null;
	
	private SyntacticMutations syntacticMutations = null;
	private RandomGeneratorImpl randomGenerator = null;
	private Map<MutationParameterType, MutationParameter> controlParameters;
	private Set<String> stopWords = null;
	
	//Hold the original class URI along with the mutated URI <original, mutated>
	private Map<String, String> mutatedClassesMap = null;
	
	//Hold the original properties URI along with the mutated URI <original, mutated> [not implemented]
	private Map<URI, URI> mutatedPropsMap = null;
	
	//Constructor
	public MutationServiceImpl(Map<MutationParameterType, MutationParameter> controlParam) {
		this.stopWords = this.initStopWordsSet();
		this.randomGenerator = new RandomGeneratorImpl();
		this.syntacticMutations = new SyntacticMutations(randomGenerator);
		this.controlParameters = controlParam;
		this.mutatedClassesMap = new HashMap<String, String>();
	}
	
	
	/***
	 * This is the main method for running the mutation algorithm over a given ontology
	 */
	public void runMutation() {
		
		//Get the location of the Ontology
		String ontologyURI = null;
		if (controlParameters.containsKey(MutationParameterType.ONTOLOGY_FILE)) {
			ontologyURI = controlParameters.get(MutationParameterType.ONTOLOGY_FILE).getValue();
			logger.info("ontologyURI: " + ontologyURI);
		}		
		
		//Use an existing mutation file, from a previous run [ OPTIONAL ]
		String existingMutationFile = null;
		if (controlParameters.containsKey(MutationParameterType.USE_MUTATIONS)) {
			existingMutationFile = controlParameters.get(MutationParameterType.USE_MUTATIONS).getValue();
			logger.info("existingMutationFile: " + existingMutationFile);
		}
		
		//Get the location of the BLOOMS alignment cross-links file
		String bloomsFileAlignment = null;
		if (controlParameters.containsKey(MutationParameterType.BLOOMS_ALIGNMENT)) {
			bloomsFileAlignment = controlParameters.get(MutationParameterType.BLOOMS_ALIGNMENT).getValue();
			logger.info("bloomsFileAlignment: " + bloomsFileAlignment);
		}
		
		//Get the location of the EXPECTATION MATRIX alignment file
		String expMatrixAlignment = null;
		if (controlParameters.containsKey(MutationParameterType.EXPECTATION_ALIGNMENT)) {
			expMatrixAlignment = controlParameters.get(MutationParameterType.EXPECTATION_ALIGNMENT).getValue();
			logger.info("expMatrixAlignment: " + expMatrixAlignment);
		}
		
				
		String mutationRate = null;
		String mutationMode = null;
		if (existingMutationFile == null) {
		
			//Get the mutation rate
			if (controlParameters.containsKey(MutationParameterType.MUTATION_RATE)) {
				mutationRate = controlParameters.get(MutationParameterType.MUTATION_RATE).getValue();
				logger.info("mutationRate: " + mutationRate);
			}
		
			//Get the mutation mode: either for edit-distance, nGram or both
			if (controlParameters.containsKey(MutationParameterType.MUTATION_MODE)) {
				mutationMode = controlParameters.get(MutationParameterType.MUTATION_MODE).getValue();
				logger.info("mutationMode: " + mutationMode);
			}
		}//end if
		
		
		OntModel ontModel = null;
		try {
			
			//Get access to the ontModel	
			ontModel = this.createModelForOntology(ontologyURI);
			
			if (existingMutationFile == null) {	
			
				//Get all Classes from the Ontology not just the Hierarchy root classes
				ExtendedIterator ontClassItr = ontModel.listClasses();
		
				while(ontClassItr.hasNext()) {
					OntClass ontoClass = (OntClass) ontClassItr.next();
			
					if (ontoClass != null) {
						//Filter all classes that are anonymous
						if (!ontoClass.isAnon()) {	
							logger.info("" + ontoClass.getLocalName());
										
							if (mutationRate.equals(MutationParameterType.MILD_MUTATION_RATE.toString())) {
								//MUTATION RATE: MILD MUTATION
								boolean mildMutation = this.randomGenerator.randomWithBias(0.2);
							
								if (mildMutation) {
									this.generateMutatedURI(ontoClass, mutationMode, mutationRate);
								}//end if						
							
							} else if (mutationRate.equals(MutationParameterType.SEVERE_MUTATION_RATE.toString())) {
								//MUTATION RATE: SEVERE MUTATION	
								boolean severeMutation = this.randomGenerator.randomWithBias(0.4);
						
								if (severeMutation) {
									this.generateMutatedURI(ontoClass, mutationMode, mutationRate);
								}//end if
						
							}//end else					
						}//end if
					}//end if			
				}//end while

			} else {
				//Use an existing mutation file, call method to read the mutations 
				logger.info("existingMutationFile: " + existingMutationFile);
				this.readExistingMutationsFromFile(existingMutationFile);
			}//end else		

			/****
			 * END - At the end of the algorithm write the mutated files
			 */
			
			//6. Use the HashMap from above to write a new ontology file, replace original URIs with mutated URI strings						
			this.writeMutatedOntologyFile(mutatedClassesMap, ontologyURI, ontModel);
		
			//6.1 Use the HashMap mutations to replace URIs with the mutated ones from BLOOMS Cross-Links alignment file
			this.writeMutatedAlignmentFile(mutatedClassesMap, bloomsFileAlignment, MutationParameterType.BLOOMS_ALIGNMENT);
			
			//6.2 Use the HashMap mutations to replace URIs with the mutated ones from EXPECTATION alignment file
			this.writeMutatedAlignmentFile(mutatedClassesMap, expMatrixAlignment, MutationParameterType.EXPECTATION_ALIGNMENT);	
			
			//6.3 Save generated mutations in a .csv file <originalURI, mutatedURI>
			this.saveMutationsToCSV(mutatedClassesMap, ontologyURI); 
			
		} catch (Exception exe) {
			logger.info("ERROR - while performing mutation: " + exe);
		}
	}//end runMutation()
	
	
   /***
    * This method generates the new mutated URI 
 * @throws URISyntaxException 
    * 
    */
	public void generateMutatedURI(OntClass ontoClass, String mutationMode, String mutationRate) throws URISyntaxException {
		//1. tokenize the local-names of each ontology class
		String[] tokens = ontoClass.getLocalName().split("_");

		logger.info("tokens array:" + tokens + " | length : " + tokens.length);
		logger.info("has: " + tokens[0]);
		
		
		//2. choose a token at random to mutate
		boolean contain = false;
		int index;
		String token = "";
		String token_lowercase = "";
		do {
			index = this.randomGenerator.randInt(0, tokens.length-1);
			
			token = tokens[index];
			
			//lowercase only used to check if token is an English stop word
			token_lowercase = token.toLowerCase();
				
			contain = this.stopWords.contains(token);
						
		} while (contain);
			
		//3. mutate the token according to the mutation mode
		String mutatedToken = "";
		if (mutationMode.equals(MutationParameterType.EDIT_DISTANCE_MUTATION.toString())) {
			//Get a list of all possible mutations that can happen for EDIT-DISTANCE, the choice is random here
			if (mutationRate.equals(MutationParameterType.MILD_MUTATION_RATE.toString())) {
				this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR,
														MisspellingMutationsType.DOUBLE_CHAR);
			} else if (mutationRate.equals(MutationParameterType.SEVERE_MUTATION_RATE.toString())) {
				this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR,
														MisspellingMutationsType.DOUBLE_CHAR,
														MisspellingMutationsType.TRANSPOSE_ALL_CHARS);
			}
					
			/*With all the above mutations equally likely to be chosen make a choice at random as to
			  which mutation to be made over the string*/
			MisspellingMutationsType synMutationType = this.possibleMutations.get(this.randomGenerator.randInt(0, possibleMutations.size()-1));
			logger.info("synMutationType: " + synMutationType);
			mutatedToken = this.mutateString(token, synMutationType);
			
		} else if (mutationMode.equals(MutationParameterType.NGRAM_MUTATION.toString())) {
			//Get a list of all possible mutations that can happen for NGRAM, the choice is random here
			if (mutationRate.equals(MutationParameterType.MILD_MUTATION_RATE.toString())) {
				this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR_TRI_GRAMS);
			} else if (mutationRate.equals(MutationParameterType.SEVERE_MUTATION_RATE.toString())) { 
				this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
														MisspellingMutationsType.DOUBLE_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR,
														MisspellingMutationsType.TRANSPOSE_CHAR_TRI_GRAMS);
			}
			
			/*With all the above mutations equally likely to be chosen make a choice at random as to
			  which mutation to be made over the string*/
			MisspellingMutationsType synMutationType = this.possibleMutations.get(this.randomGenerator.randInt(0, possibleMutations.size()-1));
			logger.info("synMutationType: " + synMutationType);
			mutatedToken = this.mutateString(token, synMutationType);
		} else if (mutationMode.equals(MutationParameterType.BOTH_MUTATION.toString())) {
			logger.info("mutationMode is: " + mutationMode);
			
			//Get a list with the string based matchers available
			//TODO: Add more matchers here
			this.matchersTypeList = Arrays.asList(MatcherType.LEVENSHTEIN,
												 MatcherType.NGRAM);
			
			//Make a random choice as to which string-based matcher to target the mutation on
			MatcherType selectedMatcherType = this.matchersTypeList.get(this.randomGenerator.randInt(0, matchersTypeList.size()-1));
			logger.info("selectedMatcherType: " + selectedMatcherType);
			
			if (selectedMatcherType.equals(MatcherType.LEVENSHTEIN)) {
				
				if (mutationRate.equals(MutationParameterType.MILD_MUTATION_RATE.toString())) {				
					this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR,
															MisspellingMutationsType.DOUBLE_CHAR);
				} else if (mutationRate.equals(MutationParameterType.SEVERE_MUTATION_RATE.toString())) {
					this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR,
															MisspellingMutationsType.DOUBLE_CHAR,
															MisspellingMutationsType.TRANSPOSE_ALL_CHARS);
				}			
				
			} else if (selectedMatcherType.equals(MatcherType.NGRAM)) {
				
				if (mutationRate.equals(MutationParameterType.MILD_MUTATION_RATE.toString())) {
					this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR_TRI_GRAMS);
				} else if (mutationRate.equals(MutationParameterType.SEVERE_MUTATION_RATE.toString())) { 
					this.possibleMutations = Arrays.asList(MisspellingMutationsType.MISSED_CHAR,
															MisspellingMutationsType.DOUBLE_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR,
															MisspellingMutationsType.TRANSPOSE_CHAR_TRI_GRAMS);
				}				
				
			}		
			
			/*With all the above mutations equally likely to be chosen make a choice at random as to
			  which mutation to be made over the string*/
			MisspellingMutationsType synMutationType = this.possibleMutations.get(this.randomGenerator.randInt(0, possibleMutations.size()-1));
			logger.info("synMutationType: " + synMutationType);
			mutatedToken = this.mutateString(token, synMutationType);			
		}//end if 
			
		
		//4. Reconstruct the original URI with the mutated local-name and then store it in the Map
		URI mutatedURI = null;
		StringBuilder mutatedLocalName = new StringBuilder();
		if (tokens.length > 1) {
			//need to merge tokens back together
			for (int j=0; j<tokens.length; j++) {
				if (j == index) {
					tokens[j] = mutatedToken;
				}
				
				if (j == tokens.length-1) {
					mutatedLocalName.append(tokens[j]);
				} else {
					mutatedLocalName.append(tokens[j]).append("_");	
				}				
			}//end for
			mutatedURI = new URI(""+ontoClass.getNameSpace()+mutatedLocalName.toString());
		} else {
			mutatedURI = new URI(""+ontoClass.getNameSpace()+mutatedToken);
		}//end else
	
		logger.info("original uri: " + ontoClass.getURI() + " | mutated uri: " + mutatedURI.toString());	
		
		//5. Store the original URI with the mutated URI to the hashmap. The hashmap is then used to replace 
		//   string from the ontology actual file.
		mutatedClassesMap.put(ontoClass.getURI(), mutatedURI.toString());	
	}//doMutation()
	
	
	/***
	 * This method will replace the original URIs with the mutated URIs producing a new Ontology 
	 * file 
	 */
	private void writeMutatedOntologyFile(Map<String, String> mutatedClassesMap, String ontologyURI, Model ontModel) {
		try {
			//Build the new file path to store the mutated ontology file
			String fileName = this.getFileName(ontologyURI);		
			StringBuilder outputFilePath = new StringBuilder("./src/test/resources/training/benchmark/mutations/").append(fileName)
																											.append("_mutated")
																											.append(".owl");
		   	//write the model to an output stream 
			String tempFileLoc = "./src/test/resources/training/benchmark/mutations/temp";
			OutputStream outputStream = new FileOutputStream(tempFileLoc);
			RDFDataMgr.write(outputStream, ontModel, Lang.RDFXML) ;			 				
			
			String content = FileUtils.readFileToString(new File(tempFileLoc), "UTF-8");
		     
		    //replace all URIs that are found in the non-mutated file
		    for (Map.Entry<String, String> entry : mutatedClassesMap.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        content = content.replaceAll(key, value);
		    }//end for
	     		     
		    File tempFile = new File(outputFilePath.toString());
		    FileUtils.writeStringToFile(tempFile, content, "UTF-8");
		 } catch (IOException exe) {
		    //Simple exception handling, replace with what's necessary for your use case!
		    throw new RuntimeException("ERROR - while generating ontology mutated file", exe);
		 }//end catch		
	}//end writeMutatedOntologyFile()
	
	
	/***
	 * Method used to replace the original URIs with the mutated ones in the:
	 *   - BLOOMS cross-links file
	 *   - Expectation Matrix alignment file 
	 */
	private void writeMutatedAlignmentFile(Map<String, String> mutatedClassesMap, String alignmentURI, MutationParameterType pType) {
		//Get the fileName
		String fileName = this.getFileName(alignmentURI);
		
		//Create the new filePath location for the mutated file
		StringBuilder outputFilePath = null;
		if (pType.equals(MutationParameterType.BLOOMS_ALIGNMENT)) {
			outputFilePath = new StringBuilder("./src/test/resources/training/benchmark/alignments/blooms/").append(fileName)
																											.append("_mutated")
																											.append(".rdf");
		} else if (pType.equals(MutationParameterType.EXPECTATION_ALIGNMENT)) {
			outputFilePath = new StringBuilder("./src/test/resources/training/benchmark/alignments/expectation/").append(fileName)
																												 .append("_mutated")
																												 .append(".rdf");
		}//end else
		
		
		//Read the original file
		try {
			String content = FileUtils.readFileToString(new File(alignmentURI), "UTF-8");
			
		    //replace all URIs that are found in the non-mutated file
		    for (Map.Entry<String, String> entry : mutatedClassesMap.entrySet()) {
		        String key = entry.getKey();
		        String value = entry.getValue();
		        content = content.replaceAll(key, value);
		    }//end for
		
		    File tempFile = new File(outputFilePath.toString());
		    FileUtils.writeStringToFile(tempFile, content, "UTF-8");
		 } catch (IOException exe) {
		    //Simple exception handling, replace with what's necessary for your use case!
		    throw new RuntimeException("ERROR - while generating mutated file, " + pType.toString() + " | " + exe);
		 }//end catch
		
	}//end writeMutatedAlignmentFile()
	
	
	/***
	 * Method that returns a mutated version of some given string
	 * 
	 * @param word - a string to mutate
	 * @param mutationType - the type of mutation that is happening over the string
	 */
	private String mutateString(String word, MisspellingMutationsType synMutationType) {
		
		if (synMutationType.equals(MisspellingMutationsType.MISSED_CHAR)) {
			return this.syntacticMutations.missedCharTypo(word);
		} else if (synMutationType.equals(MisspellingMutationsType.TRANSPOSE_CHAR)) {
			return this.syntacticMutations.transposeCharsTypo(word);
		} else if (synMutationType.equals(MisspellingMutationsType.TRANSPOSE_ALL_CHARS)) {
			return this.syntacticMutations.transposeAllCharsTypo(word);
		} else if (synMutationType.equals(MisspellingMutationsType.DOUBLE_CHAR)) {
			return this.syntacticMutations.doubleCharTypo(word);
		} else if (synMutationType.equals(MisspellingMutationsType.TRANSPOSE_CHAR_TRI_GRAMS)) {
			return this.syntacticMutations.transposeTriGrams(word);
		}
		
		//no mutation is happening 
		return word;
	}//end mutateString()
	
	
	/***
	 * Get Ontology classes as an ArrayList of type <OntClass>
	 * 
	 * @param model
	 * @return
	 */
    private ArrayList<OntClass> getOntologyClasses(OntModel ontModel)
    {
        ExtendedIterator ontClassItr = ontModel.listClasses();
        ArrayList<OntClass> ontClassList = new ArrayList<OntClass>();
        OntClass c;
        for(; ontClassItr.hasNext(); ontClassList.add(c))
            c = (OntClass)ontClassItr.next();

        return ontClassList;
    }//end getOntologyClasses()
    
	
	/**
	 * Import an ontology into a Jena Model in memory.
	 * 
	 * @return Model - a Jena model with the ontology
	 */
	private OntModel createModelForOntology(String ontologyURI) {
		//Setup an ontology model in memory
		OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
		
		//Setup a custom Document manager, not to retrieve import ontologies
		OntDocumentManager docMgr = new OntDocumentManager();
		docMgr.setProcessImports(false);
		
		//Add the customise document manager 
		modelSpec.setDocumentManager(docMgr);	
		
		//Create a Jena Model in memory, this is the base model of the ontology model
		Model baseModel = ModelFactory.createDefaultModel();
		
		//Read the Ontology to the Jena model
		try {
			FileManager.get().readModel(baseModel, ontologyURI);		
		} catch (Exception exe) {
			logger.debug("ERROR - while reading ontology to Model");
		}
		
		//return an OntModel that holds the given ontology
		return ModelFactory.createOntologyModel(modelSpec, baseModel);
	}//createModelForOntology
	
	/***
	 * There are cases where existing mutations that have been generated by a previous run of the approach
	 * are desired. We can reuse them by reading them into this instance of the approach. The current 
	 * mutation file only holds mutations for Classes. The same method can be used for properties.
	 */
	private void readExistingMutationsFromFile(String filePath) {
		logger.info("in readExistingMutationsFromFile()");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			
			String line = br.readLine();			
			while ((line != null) && !(line.equals("\n"))) {
				String[] array = line.split(",");
				URI originalURI = new URI(array[0]);
				URI mutatedURI  = new URI(array[1]);		
				
				//insert them into the mutated classes map
				mutatedClassesMap.put(originalURI.toString(), mutatedURI.toString());
				
				line = br.readLine();
			}//end while			
			
			br.close();
		} catch (IOException exe) {
			logger.error("ERROR - while reading existing mutations from file: " + exe.getLocalizedMessage());
		} catch (URISyntaxException urie) {
			logger.error("ERROR - while creating URIs from mutations file: " + urie.getLocalizedMessage());
		}//end catch
	}//end readExistingMutationsFromFile()	
			
	/**
	 * This approach generates various random mutations according to the control parameters specified, this method
	 * is responsible for storing the generated mutations to a .csv file. This file can be used in cases we would 
	 * like to generate all the mutated files with a pevious set of mutations.
	 */
	private void saveMutationsToCSV(Map<String, String> mutatedClassesMap, String ontologyURI) {
		//Open up a file
		File loc = null;		
		BufferedWriter out = null;
		
		try {
			
			String fileName = this.getFileName(ontologyURI);
			StringBuilder outputFilePath = new StringBuilder("./src/test/resources/training/benchmark/mutations/").append(fileName)
																												  .append(".mutations");													
			
			//Create the file name for mutations file, this file holds <originalURI, mutatedURI>
			loc = new File(outputFilePath.toString());
			out = new BufferedWriter(new FileWriter(loc, false));
			
		    //replace all URIs that are found in the non-mutated file
		    for (Map.Entry<String, String> entry : mutatedClassesMap.entrySet()) {
		        out.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");	        
		    }//end for
			
			//close the file, release buffer
			if (out != null) { out.close(); }	
		} catch (IOException exe) {
			logger.error("ERROR - while CSV file that holds mutated URIs: " + exe.getLocalizedMessage());
		}		
	}//end saveMutationsToCSV()
	
	/**
	 * Method that returns the fileName given a file location. For example:
	 *   ./src/file/path/Conference.owl this method will return the filename "Conference"
	 */
	private String getFileName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/")+1, filePath.length()-4);
	}//end getFileName()
	
	/**
	 * List of common English stop-words
	 */
	private Set<String> initStopWordsSet() {
		return new HashSet<String>(Arrays.asList("a","able","about","across","after","all","almost","also","am","among",
				 "an","and","any","are","as","at","be","because","been","but","by","can",
				 "cannot","could","dear","did","do","does","either","else","ever","every",
				 "for","from","get","got","had","has","have","he","her","hers","him","his",
				 "how","however","i","if","in","into","is","it","its","just","least","let",
				 "like","likely","may","me","might","most","must","my","neither","no","nor",
				 "not","of","off","often","on","only","or","other","our","own","rather","said",
				 "say","says","she","should","since","so","some","than","that","the","their",
				 "them","then","there","these","they","this","tis","to","too","twas","us","wants",
				 "was","we","were","what","when","where","which","while","who","whom","why","will",
				 "with","would","yet","you","your"));
	}//end initStopWordsSet()	
}//end class
