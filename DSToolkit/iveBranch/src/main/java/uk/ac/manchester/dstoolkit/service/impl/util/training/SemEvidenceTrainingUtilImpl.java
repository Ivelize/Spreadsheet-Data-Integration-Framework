package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ContingencyTable;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.SmoothingMethod;
import uk.ac.manchester.dstoolkit.service.util.training.SemEvidenceTrainingUtil;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/*********************************************************************************************************
 * 									TrainingServiceUtilImpl (Abstract)
 * 													|
 * 					________________________________|___________________________________
 * 					|								|									|
 * 					|								|									|
 * 		SynEvidenceTrainingUtilImpl		SemEvidenceDataAnalysisUtilImpl		SemEvidenceTrainingUtilImpl
 * 			 																	  (this class)
 *
 * 
 * NOTE: the results are made persistent as an RDF graph
 *  
 * 
 * @author klitos
 */
public class SemEvidenceTrainingUtilImpl extends TrainingServiceUtilImpl implements SemEvidenceTrainingUtil {
	
	static Logger logger = Logger.getLogger(SemEvidenceTrainingUtilImpl.class);

	/*Hold a reference to the TDBStore that holds RDF to be used for training*/
	private TDBStoreServiceImpl tdbStore = null;
	
	/*Hold the URIs of the named graphs*/
	private Map<String, String> namedGraphsURIMap = new HashMap<String, String>();

	PrefixMapping pmap = null;
	
	/*Classes, CSN*/
	private int pairClassesSameNSequi = 0;	
	private int pairClassesSameNSnonEqui = 0;	
	
	/*Classes, CSR, CBSR*/
	private int	pairClassesCSRequi = 0; 
	private int	pairClassesCSRnonEqui = 0;
	
	private int	pairClassesCBSRequi = 0; 
	private int pairClassesCBSRnonEqui = 0;
	
	/*Classes, CSP*/
	private int	pairClassesCSPequi = 0; 
	private int	pairClassesCSPnonEqui = 0;

	/*Classes, CEC*/
	private int	pairClassesCECequi = 0; 
	private int	pairClassesCECnonEqui = 0;

	/*Classes, CSA*/
	private int	pairClassesCSAequi = 0; 
	private int	pairClassesCSAnonEqui = 0;
	
	/*Classes, CEM*/
	private int	pairClassesCEMequi = 0; 
	private int	pairClassesCEMnonEqui = 0;
	
	/****/
	
	/*Properties, PSN*/
	private int pairPropsSameNSequi = 0;	
	private int pairPropsSameNSnonEqui = 0;	
	
	/*Properties, PSR*/
	private int	pairPropsPSRequi = 0; 
	private int	pairPropsPSRnonEqui = 0;
	
	/*Properties, PSP*/
	private int	pairPropsPSPequi = 0; 
	private int	pairPropsPSPnonEqui = 0;
	
	/*Properties, PSD*/
	private int	pairPropsPSDequi = 0; 
	private int	pairPropsPSDnonEqui = 0;
	
	/*Properties, PSRA*/
	private int	pairPropsPSRAequi = 0; 
	private int	pairPropsPSRAnonEqui = 0;
	
	/*Properties, PEP*/
	private int	pairPropsPEPequi = 0; 
	private int	pairPropsPEPnonEqui = 0;
	
	/*Properties, DCSR*/
	private int	pairPropsDCSRequi = 0; 
	private int	pairPropsDCSRnonEqui = 0;
	
	/*Properties, DCCPR*/
	private int	pairPropsDCCPRequi = 0; 
	private int	pairPropsDCCPRnonEqui = 0;
	
	
	/*Constructor 1*/
	public SemEvidenceTrainingUtilImpl(TDBStoreServiceImpl store, String data_analysis, String evid_classes,
																		String evid_props, String endpoint_data) {
		logger.debug("in TrainingDataServiceImpl");
		
		if (tdbStore == null) {
			tdbStore = store;
		}
		
		/*Add the URIs of the named graphs to the Map*/
		namedGraphsURIMap.put("DATA_ANALYSIS", data_analysis);
		namedGraphsURIMap.put("EVID_CLASSES", evid_classes); 
		namedGraphsURIMap.put("EVID_PROPS", evid_props); 
		namedGraphsURIMap.put("ENDPOINT_DATA", endpoint_data);
	}//end constructor
	
	
	/**
	 * For Properties
	 *
	 *
	 */
	public void createTrainingSetProps(boolean reset) {
		/**Do some initialisation**/
		
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
		
		/*Iterate over the following list of axioms to discover pairs of equivalent props*/
		ArrayList<Property> equiPropsAxioms = new ArrayList<Property>();
		equiPropsAxioms.add( OWL.sameAs );
		equiPropsAxioms.add( OWL.equivalentProperty );
		equiPropsAxioms.add( VOCAB.skosExactMatch );
		
		/*Iterate over the following list of axioms to discover pairs of non-equivalent props*/
		/*Set of pairs of Non-Equivalent Classes*/		
		ArrayList<Property> nonEquiPropsAxioms = new ArrayList<Property>();
		nonEquiPropsAxioms.add( OWL.differentFrom ); 
		nonEquiPropsAxioms.add( VOCAB.onRule1 );
		
		/****/	

		Model modelQuery = null; //this named graph holds the data from the SPARQL endpoint		
	
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {

			/* Model created from Endpoint, stored as a NamedModel: http://train.metadata/endpoint/data */
			modelQuery = tdbStore.getModel(namedGraphsURIMap.get("ENDPOINT_DATA"));
						
			/* Get/Create the Model that will store the results for each evidence, x-ns://train.metadata/evidence/props */
			Model modelEvidenceCounts = tdbStore.getModel(namedGraphsURIMap.get("EVID_PROPS"));
			
			/*Reset the model by removing all statements from it*/
			if (reset) {				
				logger.debug("Evidence model cleanup... ");				
				String graphURI = namedGraphsURIMap.get("EVID_PROPS");
				tdbStore.removeNamedModel(graphURI);					
				modelEvidenceCounts = tdbStore.getModel(namedGraphsURIMap.get("EVID_PROPS"));
			}//end if (reset)	
			
			if (modelEvidenceCounts.isEmpty()) {
			
				/*Iterate over pairs of constructs that are *equivalent* Properties to search for specific evidence
				 * for building the likelihoods*/
				for (Property axiom : equiPropsAxioms) {
					if ( axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch) ) {
						this.doQueryExecProps1(modelQuery, axiom);
					
						//discover DCSR, DCCPR
						this.doQueryExecProps3(modelQuery, axiom, BooleanVariables.DCSR);
						this.doQueryExecProps3(modelQuery, axiom, BooleanVariables.DCCPR);
					
					} else if (axiom.equals( OWL.equivalentProperty )) {
						//do the count for explicit pair of Properties
						this.doQueryExecProps1(modelQuery, axiom);				
					
						//do the count for implicit pair of Properties
						this.doQueryExecProps2(modelQuery, axiom);					
					
						//discover DCSR, DCCPR
						this.doQueryExecProps3(modelQuery, axiom, BooleanVariables.DCSR);
						this.doQueryExecProps3(modelQuery, axiom, BooleanVariables.DCCPR);					
					}		
				}//end for
			
				/*Iterate over pairs of constructs that are *non-equivalent* Properties to search for specific evidence
				 * for building the likelihoods*/
				for (Property axiom : nonEquiPropsAxioms) {
					if (axiom.equals(OWL.differentFrom)) {
						logger.debug("axiom: owl:differentFrom");	
						this.doQueryExecProps1(modelQuery, axiom);					
				
					} else if (axiom.equals( VOCAB.onRule1 )) {
						logger.debug("axiom: custom:onRule1");
						this.doQueryExecProps1(modelQuery, axiom);					
					}
				}//end for			

			
				/***
				 * Output the results
				 */
				logger.info("");
				logger.info("For Properties:");
				logger.info(" Pairs of Equivalent Props that have the same NS: " + pairPropsSameNSequi);
				logger.info(" Pairs of Non-equivalent Props that have the same NS: " + pairPropsSameNSnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PSN, pairPropsSameNSequi, pairPropsSameNSnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have PSR: " + pairPropsPSRequi);
				logger.info(" Pairs of Non-equivalent Props that have PSR: " + pairPropsPSRnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PSR, pairPropsPSRequi, pairPropsPSRnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have PSP: " + pairPropsPSPequi);
				logger.info(" Pairs of Non-equivalent Props that have PSP: " + pairPropsPSPnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PSP, pairPropsPSPequi, pairPropsPSPnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have PSD: " + pairPropsPSDequi);
				logger.info(" Pairs of Non-equivalent Props that have PSD: " + pairPropsPSDnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PSD, pairPropsPSDequi, pairPropsPSDnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have PSRA: " + pairPropsPSRAequi);
				logger.info(" Pairs of Non-equivalent Props that have PSRA: " + pairPropsPSRAnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PSRA, pairPropsPSRAequi, pairPropsPSRAnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have PEP: " + pairPropsPEPequi);
				logger.info(" Pairs of Non-equivalent Props that have PEP: " + pairPropsPEPnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.PEP, pairPropsPEPequi, pairPropsPEPnonEqui);		
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have DCSR: " + pairPropsDCSRequi);
				logger.info(" Pairs of Non-equivalent Props that have DCSR: " + pairPropsDCSRnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.DCSR, pairPropsDCSRequi, pairPropsDCSRnonEqui);	
			
				logger.info("");
				logger.info(" Pairs of Equivalent Props that have DCCPR: " + pairPropsDCSRequi);
				logger.info(" Pairs of Non-equivalent Props that have DCCPR: " + pairPropsDCSRnonEqui);
		
				this.makePersistent(modelEvidenceCounts, BooleanVariables.DCCPR, pairPropsDCCPRequi, pairPropsDCCPRnonEqui);		
	
			
				/*Commit the dataset*/
				dataset.commit();
			
				//Print it to test
				RDFDataMgr.write(System.out, modelEvidenceCounts, Lang.RDFXML);			
			}//end if			

		} finally {
			dataset.end();
		}		
	}//end createTrainingSetProps
		
	
    /**
     * In this class I have a method that searches for evidences within pairs of Classes and one that
     * searches for evidences within pairs of Properties
     * 
     * For Classes: 
     * 
     * This method will call the other methods to create the sets for each evidence.
     * Also it will make several counts to where evidence is found that will be used to
     * create the likelihoods.
     */
	public void createTrainingSetClasses(boolean reset) {
		/**Do some initialisation**/
		
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}
		
		/*Set of pairs of Equivalent Classes, as a result of the following axioms*/
		ArrayList<Property> equiClassesAxioms = new ArrayList<Property>();
		equiClassesAxioms.add( OWL.sameAs );
		equiClassesAxioms.add( OWL.equivalentClass );
		equiClassesAxioms.add( VOCAB.skosExactMatch );

		/*Set of pairs of Non-Equivalent Classes*/		
		ArrayList<Property> nonEquiClassesAxioms = new ArrayList<Property>();
		nonEquiClassesAxioms.add( OWL.differentFrom ); 
		nonEquiClassesAxioms.add( OWL.disjointWith ); //Used on Classes only
		
		/****/	
		
		Model modelQuery = null; //this named graph holds the data from the SPARQL endpoint
		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {
			
			/* Model created from Endpoint, stored as a NamedModel: http://train.metadata/endpoint/data */
			modelQuery = tdbStore.getModel(namedGraphsURIMap.get("ENDPOINT_DATA"));
						
			/* Get/Create the Model that will store the results for each evidence, x-ns://train.metadata/evidence/classes */
			Model modelEvidenceCounts = tdbStore.getModel(namedGraphsURIMap.get("EVID_CLASSES"));
			
			/*Reset the model by removing all statements from it*/
			if (reset) {				
				logger.debug("Evidence model cleanup... ");				
				String graphURI = namedGraphsURIMap.get("EVID_CLASSES");
				tdbStore.removeNamedModel(graphURI);					
				modelEvidenceCounts = tdbStore.getModel(namedGraphsURIMap.get("EVID_CLASSES"));
			}//end if (reset)	
			
			if (modelEvidenceCounts.isEmpty()) {					
			
				/*Iterate over pair of Classes that are Equivalent to find evidence for*/
				for (Property axiom : equiClassesAxioms) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch)) {
						this.doQueryExec1(modelQuery, axiom);
					} else if (axiom.equals(OWL.equivalentClass)) {
						//do the count for explicit pair of Classes
						this.doQueryExec1(modelQuery, axiom);				
					
						//do the count for implicit pair of Classes
						this.doQueryExec2(modelQuery, axiom);
					}
				}//end for	
			
				/*Iterate over pair of Classes that are Non-Equivalent to find evidence for*/
				for (Property axiom : nonEquiClassesAxioms) {
					if (axiom.equals(OWL.differentFrom)) {
						logger.debug("axiom: owl:differentFrom");	
						this.doQueryExec1(modelQuery, axiom);
					} else if (axiom.equals(OWL.disjointWith)) {
						logger.debug("axiom: owl:disjointWith");
						//do the count for explicit pair of Classes
						this.doQueryExec1(modelQuery, axiom);				
					
						//do the count for implicit pair of Classes
						this.doQueryExec2(modelQuery, axiom);
					}
				}//end for			
			
				/***
				 * Output the results
				 */
				logger.info("For Classes:");
				logger.info(" Pairs of Equivalent Classes that have the same NS: " + pairClassesSameNSequi);
				logger.info(" Pairs of Non-equivalent Classes that have the same NS: " + pairClassesSameNSnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CSN, pairClassesSameNSequi, pairClassesSameNSnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CSR: " + pairClassesCSRequi);
				logger.info(" Pairs of Non-equivalent Classes that have CSR: " + pairClassesCSRnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CSR, pairClassesCSRequi, pairClassesCSRnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CBSR: " + pairClassesCBSRequi);	
				logger.info(" Pairs of Non-equivalent Classes that have CBSR: " + pairClassesCBSRnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CBSR, pairClassesCBSRequi, pairClassesCBSRnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CSP: " + pairClassesCSPequi);	
				logger.info(" Pairs of Non-equivalent Classes that have CSP: " + pairClassesCSPnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CSP, pairClassesCSPequi, pairClassesCSPnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CEC: " + pairClassesCECequi);	
				logger.info(" Pairs of Non-equivalent Classes that have CEC: " + pairClassesCECnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CEC, pairClassesCECequi, pairClassesCECnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CSA: " + pairClassesCSAequi);	
				logger.info(" Pairs of Non-equivalent Classes that have CSA: " + pairClassesCSAnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CSA, pairClassesCSAequi, pairClassesCSAnonEqui);
			
				logger.info("");
				logger.info(" Pairs of Equivalent Classes that have CEM: " + pairClassesCEMequi);	
				logger.info(" Pairs of Non-equivalent Classes that have CEM: " + pairClassesCEMnonEqui);
			
				this.makePersistent(modelEvidenceCounts, BooleanVariables.CEM, pairClassesCEMequi, pairClassesCEMnonEqui);			
				
				/*Commit the dataset*/
				dataset.commit();	
				
				//Print it to test
				RDFDataMgr.write(System.out, modelEvidenceCounts, Lang.RDFXML);
			
			}//end if
			
		
			
	
		} finally {
			dataset.end(); 
		}//end finally		
	}//end createTrainingSets()
	
	
	
	/***
	 * Method responsible for constructing Probability Mass Functions (PMF) for
	 * each BooleanVariable (semantic evidence).
	 * 
	 * @return - A Map that contains the PMF calculated for each semantic evidence
	 */
	public Map<BooleanVariables, ProbabilityMassFunction> constructPMF(SmoothingMethod smoothingMethod) {
		logger.debug("in constructPMF");
		
		Map<BooleanVariables, ProbabilityMassFunction> pmfMap = new HashMap<BooleanVariables, ProbabilityMassFunction>();
				
		Model resultsModel 		  = null; //data analysis named graph
		Model classesEvidCounts   = null; //class evidence graph
		Model propsEvidCounts     = null; //class evidence graph
		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.READ);		
		try {			
			resultsModel = tdbStore.getModel(namedGraphsURIMap.get("DATA_ANALYSIS"));	
	
			//Get the models for querying purposes
			classesEvidCounts = tdbStore.getModel(namedGraphsURIMap.get("EVID_CLASSES"));
			propsEvidCounts   =	tdbStore.getModel(namedGraphsURIMap.get("EVID_PROPS"));
			
			if (!resultsModel.isEmpty() && !classesEvidCounts.isEmpty() && !propsEvidCounts.isEmpty()) {
				//Variables
				Literal totalPairsOfEquiClasses	   = null;
				Literal totalPairsOfNonEquiClasses = null;
				
				Literal totalPairsOfEquiProps	   = null;
				Literal totalPairsOfNonEquiProps   = null;
				
				//Get counts for Classes
				Query query = totalPairsOfCountsForClassesProps();			
				QueryExecution qexec = QueryExecutionFactory.create(query, resultsModel);
				try {
					ResultSet results = qexec.execSelect() ;
					if (results.hasNext()) {
						QuerySolution soln = results.nextSolution() ;
						totalPairsOfEquiClasses	 	= soln.getLiteral("totalPairsEquiClasses") ;
						totalPairsOfNonEquiClasses 	= soln.getLiteral("totalPairsNonEquiClasses") ;
						
						totalPairsOfEquiProps       = soln.getLiteral("totalPairsEquiProps") ;
						totalPairsOfNonEquiProps    = soln.getLiteral("totalPairsNonEquiProps") ;
					}//end if
				} finally {
					qexec.close();
				}
						
				for (BooleanVariables var: BooleanVariables.values()) {
					if (var.toString().startsWith("C") && !var.equals(BooleanVariables.CSE) 
													   && !var.equals(BooleanVariables.CCS)
													   && !var.equals(BooleanVariables.CHL)
													   && !var.equals(BooleanVariables.CSURI)) {
						
						logger.debug("Boolean variable is: " + var.toString());
						
						double[][] table = new double[2][2];
				
						query = evidenceCountsForClassesProps(var);			
						qexec = QueryExecutionFactory.create(query, classesEvidCounts);
						try {
							ResultSet results = qexec.execSelect() ;
							if (results.hasNext()) {
								QuerySolution soln = results.nextSolution() ;
								Literal evidenceAndEqui	 	= soln.getLiteral("evidenceAndEqui") ;
								Literal evidenceAndNonEqui 	= soln.getLiteral("evidenceAndNonEqui") ;
								
								table[0][0] = evidenceAndEqui.getDouble();
								table[0][1] = totalPairsOfEquiClasses.getDouble() - table[0][0];
								
								table[1][0] = evidenceAndNonEqui.getDouble();
								table[1][1] = totalPairsOfNonEquiClasses.getDouble() -  table[1][0];		
				
							}//end if
						} finally {
							qexec.close();
						}						
						
						//Create row and column labels for the contingency table
						//Specify row labels
						String[] rname = {"Equiv", "NoEquiv"};
						
						//Specify column labels
						String[] cname = {var.toString(), "No"+var.toString()};						
										
						//Build a Contingency Table for each semantic evidence
						ContingencyTable contingencyTable = new ContingencyTable(table, rname, cname);
						
						//Add a smoothing method if exists
						if (smoothingMethod != null) {
							contingencyTable.useSmoothing(smoothingMethod);
						}
						
						/** If from the sample data we are unable to find any occurrences for a 
						 * specific evidence, we then consider them as insufficient evidence and
						 * therefore we do not add it to the list of PMFs **/
						
						if ( !((table[0][0] == 0) && (table[1][0] == 0)) ) {
							pmfMap.put(var, new ProbabilityMassFunction(var, contingencyTable));							
						}						
					
					} else if ( ( var.toString().startsWith("P") || (var.toString().startsWith("D")) ) 
																 && !var.equals(BooleanVariables.PSE) 
							   								  	 && !var.equals(BooleanVariables.PCS)
							   								  	 && !var.equals(BooleanVariables.PHL)
							   								  	 && !var.equals(BooleanVariables.PSA)
							   								  	 //&& !var.equals(BooleanVariables.PSRA) //remove this
							   								  	 && !var.equals(BooleanVariables.PSURI)) {
						
						double[][] table = new double[2][2];
						
						query = evidenceCountsForClassesProps(var);			
						qexec = QueryExecutionFactory.create(query, propsEvidCounts);
						try {
							ResultSet results = qexec.execSelect() ;
							if (results.hasNext()) {
								QuerySolution soln = results.nextSolution() ;
								Literal evidenceAndEqui	 	= soln.getLiteral("evidenceAndEqui") ;
								Literal evidenceAndNonEqui 	= soln.getLiteral("evidenceAndNonEqui") ;
	
								table[0][0] = evidenceAndEqui.getDouble();
								table[0][1] = totalPairsOfEquiProps.getDouble() - table[0][0];
								
								table[1][0] = evidenceAndNonEqui.getDouble();
								table[1][1] = totalPairsOfNonEquiProps.getDouble() -  table[1][0];		
				
							}//end if
						} finally {
							qexec.close();
						}						
						
						//Create row and column labels for the contingency table
						//Specify row labels
						String[] rname = {"Equiv", "NoEquiv"};
						
						//Specify column labels
						String[] cname = {var.toString(), "No"+var.toString()};						
										
						//Build a Contingency Table for each semantic evidence
						ContingencyTable contingencyTable = new ContingencyTable(table, rname, cname);
				
						
						/** If from the sample data we are unable to find any occurrences for a 
						 * specific evidence, we then consider them as insufficient evidence and
						 * therefore we do not add it to the list of PMFs **/
						if (smoothingMethod != null) {
							//attach the smoothing method if exists and then add the Contingency table to the map
							contingencyTable.useSmoothing(smoothingMethod);							
							pmfMap.put(var, new ProbabilityMassFunction(var, contingencyTable));
						} else {						
							//if not smoothing ignore 0/0 
							if ( !( (table[0][0] == 0) && (table[1][0] == 0) ) ) {
								pmfMap.put(var, new ProbabilityMassFunction(var, contingencyTable));							
							}						
						}//end else
					}//end if
				}//end for		
			}//end if
		
		} finally {
			dataset.end(); 
		}//end finally	
				
		return pmfMap;		
	}//end constructPMF()
	
	
	/***
	 * Private method that will make evidence counts persistent to an RDF-Graph
	 */
	private void makePersistent(Model modelEvidenceCounts, BooleanVariables evidence,
							    int evidenceAndEqui, int evidenceAndNonEqui) {
		
		logger.info("onEvidence: " + evidence.toString());
		
		Resource bNode = modelEvidenceCounts.createResource();
		Statement stmt = modelEvidenceCounts.createStatement(bNode, VOCAB.onEvidence,
																modelEvidenceCounts.createLiteral(evidence.toString()));
		modelEvidenceCounts.add(stmt);		
		
		Statement stmtEqui = modelEvidenceCounts.createStatement(
				bNode, VOCAB.evidenceAndEqui, modelEvidenceCounts.createTypedLiteral(new Long(evidenceAndEqui)) );
		modelEvidenceCounts.add(stmtEqui);
		
		Statement stmtNonEqui = modelEvidenceCounts.createStatement(
				bNode, VOCAB.evidenceAndNonEqui, modelEvidenceCounts.createTypedLiteral(new Long(evidenceAndNonEqui)) );	
		
		modelEvidenceCounts.add(stmtNonEqui);		
	}//end method
	
	
	
	private com.hp.hpl.jena.query.Query totalPairsOfCountsForClassesProps() {
		 String queryString =  getNSPrefixes() +	
				 "SELECT DISTINCT ?totalPairsEquiClasses ?totalPairsNonEquiClasses ?totalPairsEquiProps ?totalPairsNonEquiProps" +
				 " WHERE {" + 
				 "    ?s rdf:type <http://vocab.deri.ie/void#Dataset> ; " + 
				 "		 j.0:totalPairsEquiClasses ?totalPairsEquiClasses ; " + 
				 "	     j.0:totalPairsNonEquiClasses ?totalPairsNonEquiClasses ; " + 
				 "	     j.0:totalPairsEquiProps ?totalPairsEquiProps ; " + 
				 "	     j.0:totalPairsNonEquiProps ?totalPairsNonEquiProps . " +
				 " }"; 
		
		return QueryFactory.create(queryString);		
	}//end totalPairsOfCountsForClasses()
	
	private com.hp.hpl.jena.query.Query evidenceCountsForClassesProps(BooleanVariables var) {
		 String queryString =  getNSPrefixes() +	
				 "SELECT DISTINCT ?evidenceAndEqui ?evidenceAndNonEqui " +
				 " WHERE {" + 
				 "    ?s  j.0:onEvidence \"" + var.toString()+ "\" ; " +
				 "		  j.0:evidenceAndEqui ?evidenceAndEqui ; " + 
				 "	      j.0:evidenceAndNonEqui ?evidenceAndNonEqui . " + 
				 " }"; 
		
		return QueryFactory.create(queryString);		
	}//end totalPairsOfCountsForClasses()
	

	
	
	
	
	
	/****************
	 * Properties
	 ****************/
	
	
	/**
	 * For Positive case of DCSR, DCCPR
	 * 
	 * Search for specific evidences using SPARQL queries 
	 */
	private void doQueryExecProps3(Model modelQuery, Property axiom, BooleanVariables bVar) {
		logger.info("in doQueryExecProps1");
		String qName = pmap.qnameFor(axiom.getURI());
		Query query = null; 
		
		if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
			//This is for the equivalent case
			if (bVar.equals(BooleanVariables.DCSR)) {
				query = this.discoverDCSR(qName);
			} else if (bVar.equals(BooleanVariables.DCCPR)) {
				query = this.discoverDCCPR(qName);
			}			
		} else if (axiom.equals(OWL.differentFrom)) {
			//This is for the non-equivalent case
			if (bVar.equals(BooleanVariables.DCSR)) {
				query = this.discoverDCSR(qName);
			} else if (bVar.equals(BooleanVariables.DCCPR)) {
				query = this.discoverDCCPR(qName);
			}			
		}//end if
		
		QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		try {
			ResultSet results = qexec.execSelect();
			
			for ( ; results.hasNext() ; ) {
				QuerySolution soln = results.nextSolution() ;
				Literal total_pairs = soln.getLiteral("total_pairs") ;

				if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
					//This is for the equivalent case
					if (bVar.equals(BooleanVariables.DCSR)) {
						pairPropsDCSRequi = pairPropsDCSRequi + total_pairs.getInt();
					} else if (bVar.equals(BooleanVariables.DCCPR)) {
						pairPropsDCCPRequi = pairPropsDCCPRequi + total_pairs.getInt();
					}
				} else if (axiom.equals(OWL.differentFrom)) {
					//This is for the non-equivalent case
					if (bVar.equals(BooleanVariables.DCSR)) {
						pairPropsDCSRnonEqui = pairPropsDCSRnonEqui + total_pairs.getInt();
					} else if (bVar.equals(BooleanVariables.DCCPR)) {
						pairPropsDCCPRnonEqui = pairPropsDCCPRnonEqui + total_pairs.getInt();
					}
				}
			}//end for
			
		} finally {
			qexec.close();
		}
		
	}//end doQueryExecProps3()
	
	
	/**
	 * Used by: owl:sameAs
	 */
	private void doQueryExecProps1(Model modelQuery, Property axiom) {
		logger.info("in doQueryExecProps1");
		String qName = pmap.qnameFor(axiom.getURI());
		Query query = null; 
		
		if (axiom.equals(VOCAB.onRule1)) {
			query = getSumOfPairsOfNonEquivalentPropsRule1();
		} else {
			query = getQueryProps1(qName);
		}
				
		QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		try {
			ResultSet results_set = qexec.execSelect();
			//Iterate the result set to find evidences
			for ( ; results_set.hasNext() ; ) {
				QuerySolution soln  = results_set.nextSolution() ;
				RDFNode elem1 = soln.get("elem1");
				RDFNode elem2 = soln.get("elem2");
				
				//Evidence 1: Search pair of properties for PSN
				boolean sameNSuri = findSameNSuri(elem1.toString(), elem2.toString());
				if (sameNSuri) {
					//Increase the count for the set of equivalent pairs of constructs that are props
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsSameNSequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsSameNSnonEqui++;
					}						
				}//end if				
				
				//Evidence 2: PSR
				BooleanVariables subsumption = findSubsumption(modelQuery, elem1.toString(), elem2.toString(), RDFS.subPropertyOf);
				if ((subsumption != null) && subsumption.equals(BooleanVariables.PSR)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsPSRequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSRnonEqui++;
					}
				}//end if
				
				//Evidence 3: PSP (same immediate parent)
				BooleanVariables sameParent = findShareParent(modelQuery, elem1.toString(), elem2.toString(), RDFS.subPropertyOf);
				if ((sameParent != null) && sameParent.equals(BooleanVariables.PSP)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsPSPequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSPnonEqui++;
					}
				}
				
				//Evidence 4: PSD
				BooleanVariables sameDomain = findDomainRange(modelQuery, elem1.toString(), elem2.toString(), RDFS.domain);
				if ((sameDomain != null) && sameDomain.equals(BooleanVariables.PSD)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsPSDequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSDnonEqui++;
					}
				}
				
				//Evidence 5: PSRA
				BooleanVariables sameRange = findDomainRange(modelQuery, elem1.toString(), elem2.toString(), RDFS.range);
				if ((sameRange != null) && sameRange.equals(BooleanVariables.PSRA)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsPSRAequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSRAnonEqui++;
					}
				}
				
				//Evidence 6: owl:equivalentProperty axiom
				//This strategyB
				boolean axiomExistsPEP = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.equivalentProperty);
				if (axiomExistsPEP) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentProperty) || axiom.equals(VOCAB.skosExactMatch)) {
						pairPropsPEPequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPEPnonEqui++;
					}
				}	
				
				//Evidence 7: DCSR - negative case only
				if (axiom.equals(VOCAB.onRule1)) {
				boolean negDCSR = hasDCSR(modelQuery, elem1.toString(), elem2.toString());
				if (negDCSR) {
					pairPropsDCSRnonEqui++;								
				}
				
				//Evidence 8: DCCPR - negative case only
				boolean negDCCPR = hasDCCPR(modelQuery, elem1.toString(), elem2.toString());
				if (negDCCPR) {
					pairPropsDCCPRnonEqui++;										
				 }
				}//end if
				

			}//end for			
		} finally {
			qexec.close();
		}	
	}//end doQueryExecProps1()	
	
	
	/***
	 * Used by: owl:equivalentProperty (implicit) 
	 */
	private void doQueryExecProps2(Model modelQuery, Property axiom) {
		String qName = pmap.qnameFor(axiom.getURI());
		Query query = getQueryProps2(qName);
		QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		try {
			ResultSet results_set = qexec.execSelect();
			//Iterate the result set to find evidences
			for ( ; results_set.hasNext() ; ) {
				QuerySolution soln  = results_set.nextSolution() ;
				RDFNode elem1 = soln.get("elem1");
				RDFNode elem2 = soln.get("elem2");
				
				//Evidence 1: Search pair of properties for CSN
				boolean sameNSuri = findSameNSuri(elem1.toString(), elem2.toString());
				if (sameNSuri) {
					//Increase the count for the set of equivalent pairs of constructs that are classes
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsSameNSequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsSameNSnonEqui++;
					}					
				}//end if
				
				//Evidence 2: PSR
				BooleanVariables subsumption = findSubsumption(modelQuery, elem1.toString(), elem2.toString(), RDFS.subPropertyOf);
				if ((subsumption != null) && subsumption.equals(BooleanVariables.PSR)) {
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsPSRequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSRnonEqui++;
					}
				}//end if
				
				//Evidence 3: PSP (same immediate parent)
				BooleanVariables sameParent = findShareParent(modelQuery, elem1.toString(), elem2.toString(), RDFS.subPropertyOf);
				if ((sameParent != null) && sameParent.equals(BooleanVariables.PSP)) {
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsPSPequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSPnonEqui++;
					}
				}
				
				//Evidence 4: PSD
				BooleanVariables sameDomain = findDomainRange(modelQuery, elem1.toString(), elem2.toString(), RDFS.domain);
				if ((sameDomain != null) && sameDomain.equals(BooleanVariables.PSD)) {
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsPSDequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSDnonEqui++;
					}
				}
				
				//Evidence 5: PSRA
				BooleanVariables sameRange = findDomainRange(modelQuery, elem1.toString(), elem2.toString(), RDFS.range);
				if ((sameRange != null) && sameRange.equals(BooleanVariables.PSRA)) {
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsPSRAequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPSRAnonEqui++;
					}
				}
				
				//Evidence 6: owl:equivalentProperty axiom
				//This strategyB
				boolean axiomExistsPEP = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.equivalentProperty);
				if (axiomExistsPEP) {
					if (axiom.equals(OWL.equivalentProperty)) {
						pairPropsPEPequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(VOCAB.onRule1)) {
						pairPropsPEPnonEqui++;
					}
				}
				
				
				
			}//end for		
		} finally {
			qexec.close();
		}		
	}//end doQueryExecProps2
	
	
	/****************
	 * Classes
	 ****************/
	
	/**
	 * Used by: owl:sameAs, owl:equivalentClass (explicit), skos:exactMatch, owl:differentFrom
	 */
	private void doQueryExec1(Model modelQuery, Property axiom) {
		String qName = pmap.qnameFor(axiom.getURI());
		Query query = getQuery1(qName);
		QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		try {
			ResultSet results_set = qexec.execSelect();
			//Iterate the result set to find evidences
			for ( ; results_set.hasNext() ; ) {
				QuerySolution soln  = results_set.nextSolution() ;
				RDFNode elem1 = soln.get("elem1");
				RDFNode elem2 = soln.get("elem2");
						
				//Evidence 1: CSN
				boolean sameNSuri = findSameNSuri(elem1.toString(), elem2.toString());				
				if (sameNSuri) {
					//Increase the count for the set of equivalent pairs of constructs that are classes
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesSameNSequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesSameNSnonEqui++;
					}	
				}//end if	
				
				//Evidence 2: CSR and CBSR
				BooleanVariables subsumption = findSubsumption(modelQuery, elem1.toString(), elem2.toString(), RDFS.subClassOf);
				if ((subsumption != null) && subsumption.equals(BooleanVariables.CSR)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCSRequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCSRnonEqui++;
					}
				} else if ((subsumption != null) && subsumption.equals(BooleanVariables.CBSR)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCBSRequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCBSRnonEqui++;	
					}
				}
				
				//Evidence 3: CSP (same immediate parent)
				BooleanVariables sameParent = findShareParent(modelQuery, elem1.toString(), elem2.toString(), RDFS.subClassOf);
				if ((sameParent != null) && sameParent.equals(BooleanVariables.CSP)) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCSPequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCSPnonEqui++;
					}
				}
				
				//Evidence 4: owl:equivalentClass axiom
				//This is strategyB
				boolean axiomExistsCEC = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.equivalentClass);
				if (axiomExistsCEC) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCECequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCECnonEqui++;
					}
				}	
				
				//Evidence 5: owl:sameAs axiom
				//This is strategyB
				boolean axiomExistsCSA = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.sameAs);
				if (axiomExistsCSA) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCSAequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCSAnonEqui++;
					}
				}
				
				//Evidence 6: skos:exactMatch axiom
				//TODO: the count is 22 because some URIs are not actually URIs, fix this
				//This strategyB
				
				//logger.info("elem1 uri: " + elem1.toString()); //FIX THIS LATER AFTER CREATING THE CONTNGENCY TABLES
				//logger.info("elem2 uri: " + elem2.toString());				
				boolean axiomExistsCEM = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), VOCAB.skosExactMatch);
				if (axiomExistsCEM) {
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch)) {
						pairClassesCEMequi++;
					} else if (axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						pairClassesCEMnonEqui++;
					}
				}
				
			}//end for
		} finally {
			qexec.close();
		}
	}//end doQueryExec1()
	
	/**
	 * Used by: owl:equivalentClass (implicit), owl:disjointWith (implicit)
	 */
	private void doQueryExec2(Model modelQuery, Property axiom) {
		String qName = pmap.qnameFor(axiom.getURI());
		Query query = getQuery2(qName);
		QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		try {
			ResultSet results_set = qexec.execSelect();
			//Iterate the result set to find evidences
			for ( ; results_set.hasNext() ; ) {
				QuerySolution soln  = results_set.nextSolution() ;
				RDFNode elem1 = soln.get("elem1");
				RDFNode elem2 = soln.get("elem2");
						
				//Evidence 1: CSN
				boolean sameNSuri = findSameNSuri(elem1.toString(), elem2.toString());
				if (sameNSuri) {
					//Increase the count
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesSameNSequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesSameNSnonEqui++;
					}
					
					//Store the pair in the named model: http://train.metadata/equi/classes
					//store them as bnode, their uris, and a predicate to indicate the type of evidence
					//_:b1 evidence "CSN"
					//TODO							
				}//end if	
				
				//Evidence 2: CSR, CBSR
				BooleanVariables subsumption = findSubsumption(modelQuery, elem1.toString(), elem2.toString(), RDFS.subClassOf);
				if ((subsumption != null) && subsumption.equals(BooleanVariables.CSR)) {
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesCSRequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesCSRnonEqui++;
					}
				} else if ((subsumption != null) && subsumption.equals(BooleanVariables.CBSR)) {
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesCBSRequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesCBSRnonEqui++;	
					}
				}				
				
				//Evidence 3: CSP (same immediate parent)
				BooleanVariables sameParent = findShareParent(modelQuery, elem1.toString(), elem2.toString(), RDFS.subClassOf);
				if ((sameParent != null) && sameParent.equals(BooleanVariables.CSP)) {
					if ( axiom.equals(OWL.equivalentClass) ) {
						pairClassesCSPequi++;
					} else if ( axiom.equals(OWL.disjointWith) ) {
						pairClassesCSPnonEqui++;
					}
				}				
				
				//Evidence 4: owl:equivalentClass axiom
				//This strategyB
				boolean axiomExistsCEC = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.equivalentClass);
				if (axiomExistsCEC) {
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesCECequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesCECnonEqui++;
					}
				}
				
				//Evidence 5: owl:sameAs axiom
				//TODO: the count is 22 because some URIs are not actually URIs, fix this
				//This strategyB
				boolean axiomExistsCSA = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), OWL.sameAs);
				if (axiomExistsCSA) {
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesCSAequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesCSAnonEqui++;
					}
				}
				
				//Evidence 6: skos:exactMatch axiom
				//This strategyB
				boolean axiomExistsCME = hasAxiom(modelQuery, elem1.toString(), elem2.toString(), VOCAB.skosExactMatch);
				if (axiomExistsCME) {
					if (axiom.equals(OWL.equivalentClass)) {
						pairClassesCEMequi++;
					} else if (axiom.equals(OWL.disjointWith)) {
						pairClassesCEMnonEqui++;
					}
				}
				
				
			}//end for
		} finally {
			qexec.close();
		}
	}//end doQueryExec1()
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Supporting Methods
	 */
	private Model queryDescribe(Model sourceModel, String queryString, Model targetModel) {
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, sourceModel);
        Model result = null;
        try {
        	targetModel.begin();
            result = qexec.execDescribe(targetModel);
            targetModel.commit();
        } finally {
            qexec.close();
            targetModel.close();
        }        
        return result;
    }//end queryDescribe()
	
	private Model queryDescribe2 (Model sourceModel, String queryString) {
	
	Query query = QueryFactory.create(queryString) ;


	QueryExecution qexec = QueryExecutionFactory.create(query, sourceModel) ;

	Model m = qexec.execDescribe();
	
	
	qexec.close() ;
	
	return m;
	
	}	
	
	

	
	
	/***
	 * Equivalent Constructs: This is the set of constructs that are Classes and share some notion of equivalence
	 * that is explicitly defined in the set of vocabularies (i.e., we are not doing any inferencing). We are 
	 * searching for the following axioms: owl:sameAs, owl:equivalentClass, owl:equivalentPropery, skos:exactMatch.
	 * 
	 *   owl:sameAs / skos:exactMatch - used in both schema and instance level.
	 *   owl:equivalentClass / owl:equivalentProperty - used for schema level only.
	 *   
	 * The method, will start by creating a new Named Graph that will hold all the constructs that are Classes and
	 * they share some notion of equivalence as above. Then it will issue several SPARQL queries to determine which
	 * constructs share this kind of similarity and add their definitions in the newly created Graph. 
	 * 
	 * @param sparqlURI - this is the URI of the SPARQL service
	 * @param dumpPath  - this is the path of the SPARQL service dump file 
	 * @param isService - this parameter is set to True if we are planning to ask the queries to a remote SPARQL 
	 * 					  endpoint rather to our local copy.
	 *   
	 */

	


	/***
	 * Axiom: owl:sameAs
	 * @return - A query that returns a pair of Resources which are Classes
	 * 
	 * 		     Note: I have noticed at some cases that the Object of owl:sameAs is a Qname, this is what
	 * 			 I describe as selective crawling.
	 *
	private com.hp.hpl.jena.query.Query querySelectOWLsameAsClasses() {		
		 String queryString =  getNamespacePrefixes() +	                		
	        		"SELECT DISTINCT ?elem1 ?elem2 WHERE { " +
	        		"    {?elem1 a rdfs:Class .} " +
	        		"    UNION {?elem1 a owl:Class .} " +
	        		"    ?elem1 ?elem1_predicates ?elem2 . " +
	        		"    FILTER (?elem1_predicates = owl:sameAs && !isBlank(?elem2)) " +		        		
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end queryCountOWLsameAsClasses()	*/
	
	/******************
	 * Evidences
	 *****************/
	
	
	/**
	 * For DCSR
	 * Negative case: Used to find whether non-equivalent pair of evidences have DCSR
	 */
	private boolean hasDCSR(Model model, String elem1URI, String elem2URI) {
		
		boolean result = false;

		String queryString = getNSPrefixes() +
				"ASK " +
				"WHERE {" +
				"  BIND (<" + elem1URI + "> AS ?p1 )" +
				"  BIND (<" + elem2URI + "> AS ?p2 )" +
				
				"  ?p1 rdfs:domain ?d1 . " +
				"  ?d1 rdfs:subClassOf ?sp1 . " +
				
				"  ?p2 rdfs:domain ?d2 . " +
				"  ?d2 rdfs:subClassOf ?sp2 . " +
				
				"  FILTER EXISTS { {?sp1 rdfs:subClassOf ?sp2} UNION {?sp2 rdfs:subClassOf ?sp1} }" +				
				" }";
			
		//Create the SPARQL ASK query
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		result = qe.execAsk();
		qe.close();
			
		if (result) { 
			return true;
		}	
			
		return result;
	}//end hasDCSR()
	
	/**
	 * For DCCPR
	 * Negative case: Used to find whether non-equivalent pair of evidences have DCCPR
	 */
	private boolean hasDCCPR(Model model, String elem1URI, String elem2URI) {
		
		boolean result = false;

		String queryString = getNSPrefixes() +
				"ASK " +
				"WHERE {" +
				"  BIND (<" + elem1URI + "> AS ?p1 )" +
				"  BIND (<" + elem2URI + "> AS ?p2 )" +
				
				"  ?p1 rdfs:domain ?d1 . " +
				"  ?d1 rdfs:subClassOf ?sp1 . " +
				
				"  ?p2 rdfs:domain ?d2 . " +
				"  ?d2 rdfs:subClassOf ?sp2 . " +
				
				"  FILTER (?sp1 = ?sp2)" +				
				" }";
			
		//Create the SPARQL ASK query
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		result = qe.execAsk();
		qe.close();
			
		if (result) { 
			return true;
		}	
			
		return result;
	}//end hasDCSR()
	
	
	/**
	 * return true - if the pair of constructs share the axiom specified (one direction).
	 */
	private boolean hasAxiom(Model model, String elem1URI, String elem2URI, Property axiom) {
		
		boolean result = false;

		String queryString =
				"ASK " +
				"WHERE {" +
				" <" + elem1URI + "> <" + axiom.getURI() + "> <" + elem2URI + "> ." +
				" }";
			
		//Create the SPARQL ASK query
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		result = qe.execAsk();
		qe.close();
			
		if (result) { 
			return true;
		}	
			
		return result;
	}//end hasAxiom()	
	
	/**
	 * @return true - if the pair of constructs share the same namespace, false otherwise
	 */
	private boolean findSameNSuri(String elem1, String elem2) {
		boolean result = false;
	
		if (!elem1.startsWith("http://") && elem1.contains(":")) {
			//expand the prefix using prefix mappings
			elem1 = pmap.expandPrefix(elem1);
		}
		
		if (!elem2.startsWith("http://") && elem2.contains(":")) {
			//expand the prefix using prefix mappings
			elem2 = pmap.expandPrefix(elem2);	
		}		
		
		String ns_1 = this.getNameSpace(elem1);
		String ns_2 = this.getNameSpace(elem2);		
		
		//Resource res_1 = ResourceFactory.createResource(elem1);
		//String ns_1 = res_1.getNameSpace();
		
		if (ns_1.equals(ns_2)) {
			//logger.debug("NS_1: " + ns_1 + " | " + "NS_2: " + ns_2);
			result = true;
		}
		
		return result;
	}//end findSameNSuri()	
	
	/**
     * @return 
     *   CSP/PSP - if constructs share an immediate parent
	 */
	private BooleanVariables findShareParent(Model model, String elem1, String elem2, com.hp.hpl.jena.rdf.model.Property axiom) {
		BooleanVariables result = null;
		
		/*Expand URIs if any issue*/
		if (!elem1.startsWith("http://") && elem1.contains(":")) {
			//expand the prefix using prefix mappings
			elem1 = pmap.expandPrefix(elem1);
		}
		
		if (!elem2.startsWith("http://") && elem2.contains(":")) {
			//expand the prefix using prefix mappings
			elem2 = pmap.expandPrefix(elem2);	
		}
		
		/*Check if URIs exist in the Model, if not dereference URI and retrieve it*/
	    if (!this.resourceExists(model, elem1)) {
			logger.debug("Resource URI - Not Exist: " + elem1);
	    	this.doSelectiveCrawling(model, elem1);
	    }
	    
	    if (!this.resourceExists(model, elem2)) {
			logger.debug("Resource URI - Not Exist: " + elem2);
	    	this.doSelectiveCrawling(model, elem2);
	    } 
	    	    
	    /*If constructs have the same URI do not search for same super-parent Class*/
		if (!elem1.equals(elem2)) {							
			ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(model, elem1, axiom);
			ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(model, elem2, axiom);	
			
			if ( (set_1 != null) && (set_2 != null) ) {
			
				boolean shareSuperClass = this.shareSuperClass(set_1, set_2);
			
				if (shareSuperClass) {
					//logger.debug(">>>> elem1: " + elem1);
					//logger.debug(">>>> elem2: " + elem2);				
					if (axiom.equals(RDFS.subClassOf)) {
						result = BooleanVariables.CSP;
					} else if (axiom.equals(RDFS.subPropertyOf)) {
						result = BooleanVariables.PSP;					
					}					
				}//end if
			}//end inner if	
			
		}//end if			
	    
	   return result; 
	}//end findShareParent()	
	
	/**
     * @return 
     *   CSR/PSR - if constructs share a subsumption relationship
     *   CBSR - for bi-derectional subsumption relationship 
	 */
	private BooleanVariables findSubsumption(Model model, String elem1, String elem2, com.hp.hpl.jena.rdf.model.Property axiom) {
		BooleanVariables result = null;
		
		//logger.debug("elem1: " + elem1);
		//logger.debug("elem2: " + elem2);		
		
		/*Expand URIs if any issue*/
		if (!elem1.startsWith("http://") && elem1.contains(":")) {
			//expand the prefix using prefix mappings
			elem1 = pmap.expandPrefix(elem1);
		}
		
		if (!elem2.startsWith("http://") && elem2.contains(":")) {
			//expand the prefix using prefix mappings
			elem2 = pmap.expandPrefix(elem2);	
		}
		
		/*Check if URIs exist in the Model, if not dereference URI and retrieve it*/
	    if (!this.resourceExists(model, elem1)) {
			logger.debug("Resource URI - Not Exist: " + elem1);
	    	this.doSelectiveCrawling(model, elem1);
	    }
	    
	    if (!this.resourceExists(model, elem2)) {
			logger.debug("Resource URI - Not Exist: " + elem2);
	    	this.doSelectiveCrawling(model, elem2);
	    }    
		
	    //Axiom is either rdfs:subClassOf or rdfs:subPropertyOf
		ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(model, elem1, axiom);
		ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(model, elem2, axiom);
		
		boolean cons1SubClassOfconst2 = false;
		if (set_1 != null) {
		 //Resource do not exists		
			cons1SubClassOfconst2 = this.isSubsumed(set_1, elem2);
		}
		
		boolean cons2SubClassOfconst1 = false;
		if (set_2 != null) {
			cons2SubClassOfconst1 = this.isSubsumed(set_2, elem1);
		}
		
		if (cons1SubClassOfconst2 != cons2SubClassOfconst1) {
			
			//logger.debug(">>>> elem1: " + elem1);
			//logger.debug(">>>> elem2: " + elem2);
			
			if (cons1SubClassOfconst2) {
				if (axiom.equals(RDFS.subClassOf)) {
					result = BooleanVariables.CSR;
				} else if (axiom.equals(RDFS.subPropertyOf)) {
					result = BooleanVariables.PSR;					
				}
			} else if (cons2SubClassOfconst1) {
				if (axiom.equals(RDFS.subClassOf)) {
					result = BooleanVariables.CSR;
				} else if (axiom.equals(RDFS.subPropertyOf)) {
					result = BooleanVariables.PSR;				
				}				
			}				
		} else if (cons1SubClassOfconst2 && cons2SubClassOfconst1) {
			result = BooleanVariables.CBSR;	
		}
		
		//logger.debug("result: " + result);
		
		return result;
	}//end findSubsumption()
	
	/**
     * @return 
     *   PSD/PSRA - if pairs of constructs have the same domain
	 */
	
	private BooleanVariables findDomainRange(Model model, String elem1, String elem2, com.hp.hpl.jena.rdf.model.Property axiom) {
		BooleanVariables result = null;
		
		/*Expand URIs if any issue*/
		if (!elem1.startsWith("http://") && elem1.contains(":")) {
			//expand the prefix using prefix mappings
			elem1 = pmap.expandPrefix(elem1);
		}
		
		if (!elem2.startsWith("http://") && elem2.contains(":")) {
			//expand the prefix using prefix mappings
			elem2 = pmap.expandPrefix(elem2);	
		}
		
		/*Check if URIs exist in the Model, if not dereference URI and retrieve it*/
	    if (!this.resourceExists(model, elem1)) {
			logger.debug("Resource URI - Not Exist: " + elem1);
	    	this.doSelectiveCrawling(model, elem1);
	    }
	    
	    if (!this.resourceExists(model, elem2)) {
			logger.debug("Resource URI - Not Exist: " + elem2);
	    	this.doSelectiveCrawling(model, elem2);
	    } 
	    	    
	    /*If constructs have the same URI do not search for same same domain or range classes*/
		if (!elem1.equals(elem2)) {							
			ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(model, elem1, axiom);
			ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(model, elem2, axiom);	
			
			if ( (set_1 != null) && (set_2 != null) ) {
			
				boolean shareSameDomRan = this.shareSameDomainRange(set_1, set_2);
			
				if (shareSameDomRan) {
					//logger.debug(">>>> elem1: " + elem1);
					//logger.debug(">>>> elem2: " + elem2);				
					if (axiom.equals(RDFS.domain)) {
						result = BooleanVariables.PSD;
					} else if (axiom.equals(RDFS.range)) {
						result = BooleanVariables.PSRA;					
					}					
				}//end if
			}//end inner if	
			
		}//end if			
	    
	   return result; 
	}//end findDomainRange()	
	
	
	public ResultSet getResultSetForSubjectURIandPredicate(Model model, String constructURI, com.hp.hpl.jena.rdf.model.Property pred) {
		//logger.debug("in getResultSetForSubjectURI()");
		ResultSet results = null;
		/*Firstly check whether this construct URI exists in the model*/
		boolean exists = resourceExists(model, constructURI);
		
		if (exists) {			
			String queryString = getNSPrefixes() +	
					"SELECT DISTINCT ?o " +
					"WHERE {" +
					"  <" + constructURI + "> <" + pred + "> ?o ." +
					" }";
			
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			results = qe.execSelect();	
			/*Do not close the set here because any operations on the ResultSet afterwards are not allowed*/
			//qe.close();
		}//end if		
		return results;
	}//end getResultSetForSubjectURI()
	
	
	
		
	/******************
	 * SPARQL Queries
	 *****************/	
	
	/**
	 * For finding equivalent pair of Properties (Equivalent)
	 * Used by: owl:sameAs
	 * @return - pairs of Properties that have *explicitly* stated the axiom
	 */
	private com.hp.hpl.jena.query.Query getQueryProps1(String qName) {
		 String queryString =  getNSPrefixes() +	
	        		" SELECT DISTINCT ?elem1 ?elem2 " +
	        		" WHERE { " +
	        		" 			{?elem1 a rdf:Property .} " +
	        		" 			UNION {?elem1 a owl:ObjectProperty .} " +
	        		" 			UNION {?elem1 a owl:DatatypeProperty .} " +
	        		" 			UNION {?elem1 a owl:AnnotationProperty .} " +
	        		" 			UNION {?elem1 a owl:FunctionalProperty .} " +
	        		" 			UNION {?elem1 a owl:OntologyProperty .} " +
	        		" 			UNION {?elem1 a owl:AsymmetricProperty. } " +
	        		" 			UNION {?elem1 a owl:InverseFunctionalProperty .} " +
	        		" 			UNION {?elem1 a owl:IrreflexiveProperty .} " +
	        		" 			UNION {?elem1 a owl:ReflexiveProperty .} " +
	        		" 			UNION {?elem1 a owl:SymmetricProperty .} " +
	        		" 			UNION{?elem1 a owl:TransitiveProperty .} " +
	        		
	        		"   		?elem1 ?elem1_predicates ?elem2 . " +
	        		"			FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem2))" +
	        		"  }";	        		
		
		return QueryFactory.create(queryString);
	}//end getQueryProps1()	
	
	
	/**
	 * For Classes (Equivalent)
	 * Used by: owl:sameAs, skos:exactMatch
	 * 
	 * 
	 * 
	 * @return - pairs of *explicit* Classes that have the axiom
	 */
	private com.hp.hpl.jena.query.Query getQuery1(String qName) {
		 String queryString = getNSPrefixes() +
	        		"SELECT DISTINCT ?elem1 ?elem2 WHERE { " +
	        		"    {?elem1 a rdfs:Class .} " +
	        		"    UNION {?elem1 a owl:Class .} " +
	        		"    ?elem1 ?elem1_predicates ?elem2 . " +
	        		"    FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem2)) " +		        		
	        		"}";
		
		return QueryFactory.create(queryString);
	}//end getQuery1	
	
	/**
	 * For Classes (Equivalent)
	 * Used by: owl:equivalentClass
	 * @return - pairs of *implicit* Classes that have the axiom
	 */
	private com.hp.hpl.jena.query.Query getQuery2(String qName) {
		 String queryString = getNSPrefixes() +
	        		"SELECT DISTINCT ?elem1 ?elem2 " +
	        		"WHERE " +
	        		"{ " +
	        		" { " +
	        		"   SELECT DISTINCT ?elem1 ?elem2 " +
	        		" 	WHERE { " +
	        		" 		?elem1 ?elem1_predicates ?elem2 . " +
	        		"		FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem1) && !isBlank(?elem2)) " +
	        		" 	} " +
	        		" } " +
	        		" MINUS " +
	        		" { " +
	        		"	SELECT DISTINCT ?elem1 ?elem2 " +
	        		"	WHERE { " +
	        		"		{?elem1 a rdfs:Class .} " +
	        		" 		UNION {?elem1 a owl:Class .} " +
	        		" 		?elem1 ?elem1_predicates ?elem2 . " +
	        		"		FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem2)) " +
	        		" 	}" +
	        		" }" +
	        		"}";
		
		return QueryFactory.create(queryString);
	}//end getQuery2
	
	
	/**
	 * For Properties (Equivalent)
	 * @param qName
	 * @return - pairs of *implicit* Properties that have the axiom
	 */
	private com.hp.hpl.jena.query.Query getQueryProps2(String qName) {
		 String queryString = getNSPrefixes() +
	        		"SELECT DISTINCT ?elem1 ?elem2 " +
	        		"WHERE " +
	        		"{ " +
	        		" { " +
	        		"   SELECT DISTINCT ?elem1 ?elem2 " +
	        		" 	WHERE { " +
	        		" 		?elem1 ?elem1_predicates ?elem2 . " +
	        		"		FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem1) && !isBlank(?elem2)) " +
	        		" 	} " +
	        		" } " +
	        		" MINUS " +
	        		" { " +	        		
					"  SELECT DISTINCT ?elem1 ?elem2 " +
					"  WHERE { " +
					" 			{?elem1 a rdf:Property .} " +
					" 			UNION {?elem1 a owl:ObjectProperty .} " +
					" 			UNION {?elem1 a owl:DatatypeProperty .} " +
					" 			UNION {?elem1 a owl:AnnotationProperty .} " +
					" 			UNION {?elem1 a owl:FunctionalProperty .} " +
					" 			UNION {?elem1 a owl:OntologyProperty .} " +
					" 			UNION {?elem1 a owl:AsymmetricProperty. } " +
					" 			UNION {?elem1 a owl:InverseFunctionalProperty .} " +
					" 			UNION {?elem1 a owl:IrreflexiveProperty .} " +
					" 			UNION {?elem1 a owl:ReflexiveProperty .} " +
					" 			UNION {?elem1 a owl:SymmetricProperty .} " +
					" 			UNION{?elem1 a owl:TransitiveProperty .} " +

					"   		?elem1 ?elem1_predicates ?elem2 . " +
					"			FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem2))" +
					"  }" +	        		
	        		" }" +
	        		"}";
		
		return QueryFactory.create(queryString);
	}//end getQuery2
	
	
	
	
	/***
	 * For Properties (Properties)
	 * Used by: vocab:onRule1
	 * 
	 * @return - Will return the total count (SUM) of non-equivalent pair of properties
	 * 			 according to Rule a from the proposal, described by the following 
	 * 			 SPARQL query
	 */
	private com.hp.hpl.jena.query.Query getSumOfPairsOfNonEquivalentPropsRule1() {
		 String queryString =  getNSPrefixes() +				    
	        		" SELECT DISTINCT ?elem1 ?elem2 " +
	        		" WHERE " +
	        		" { " +
	        		"  { "+
	        		"   SELECT ?elem1 ?range1 " +
	        		"   WHERE { ?elem1 a rdf:Property . " +
	        		"		    ?elem1 rdfs:range ?range1 . " +
	        		"	 } " +
	        		"  } " +
	        		"  { " +
	        		"   SELECT ?elem2 ?range2 " +
	        		"	WHERE { ?elem2 a rdf:Property . " +
	        		"			?elem2 rdfs:range ?range2 . " +
	        		" 	 } " +
	        		"  } " +
	        		"  FILTER (?range1 != ?range2)" +
	        		"  FILTER EXISTS { ?range1 owl:disjointWith ?range2 } " +
	        		" }"; 
		 
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentClasses()
	
	
	
	
	
	
	
	
	
	
	
	/***********************
	 * Selective Crawling
	 **********************/
	private void doSelectiveCrawling(Model model, String resourceURI) {
		logger.debug("  >>>>> Selective crawling needed for: " + resourceURI);
		//FileManager.get().readModel(model, resourceURI);		
	}	
	
	
	/*******************************************
	 * Search for evidences methods && queries
	 ******************************************/
	
	/**
	 * This method searches whether two constructs share the same direct super-class by looking their subClassOf predicates.
	 * It does not use any reasoning.
	 * 
	 * @param set_1 - all rdfs:subClassOf/ rdfs:subPropertyOf statements of this construct
	 * @param set_2 - all rdfs:subClassOf/ rdfs:subPropertyOf statements of this construct
	 * @return true - if constructs share the same super-call, false otherwise.
	 */
	public boolean shareSuperClass(ResultSet set_1, ResultSet set_2) {
		ResultSetMem rs = (ResultSetMem) ResultSetFactory.makeRewindable(set_2);		
	    for ( ; set_1.hasNext() ; ) {
	        QuerySolution soln = set_1.next() ;
		    RDFNode res1 = soln.get("o");
		    for ( ; rs.hasNext() ; ) {
		        QuerySolution soln2 = rs.next() ;
			    RDFNode res2 = soln2.get("o");
				if (res1.equals(res2)) {
					logger.debug("Res 1: " + res1 + " share super-class with, Res 2: " + res2);
					return true;
				}//end if				
		    }//end for
		    /*reset iterator*/
		    rs.reset();		    
	    }//end for		
	  return false;
	}//end shareSuperClass()	
	
	/**
	 * Check for subclass/subproperty without a reasoner.
	 * 
	 * @param set_1 - the result set of the construct to search whether is a subclass of the resource
	 * @param res1 - the resource to check
	 * @return true - if the construct is a subClassOf/ subPropertyOf the resource, false otherwise 
	 */
	public boolean isSubsumed(ResultSet set, String constructURI) {
		while (set.hasNext()) {
		    QuerySolution soln = set.nextSolution() ;
		    RDFNode res2 = soln.get("o");
		    if (constructURI.equals(res2.toString())) {		    	
		    	return true;
		    }//end if
		}//end while
		return false;	
	}//end isSubsumed()	
	
	/***
	 * Check if two properties share the same domain/range
	 */
	public boolean shareSameDomainRange(ResultSet set_1, ResultSet set_2) {
		ResultSetMem rs = (ResultSetMem) ResultSetFactory.makeRewindable(set_2);		
	    for ( ; set_1.hasNext() ; ) {
	        QuerySolution soln = set_1.next() ;
		    RDFNode res1 = soln.get("o");
		    for ( ; rs.hasNext() ; ) {
		        QuerySolution soln2 = rs.next() ;
			    RDFNode res2 = soln2.get("o");
				if (res1.equals(res2)) {
					logger.debug("Res 1: " + res1 + " share same domain/range with, Res 2: " + res2);
					return true;
				}//end if				
		    }//end for
		    /*reset iterator*/
		    rs.reset();		    
	    }//end for		
	  return false;
	}//end shareSameDomainRange()	
	
	
	/**
	 * @param qName - is the URI of the predicate that produces equivalent constructs that are
	 * properties, e.g., owl:equivalentProperties, owl:sameAs, skos:exactMatch
	 * 
	 * @return - count pairs of constructs that are properties and their domain classes share a
	 * subsumption relationship between them
	 */
	private com.hp.hpl.jena.query.Query discoverDCSR(String qName) {
		 String queryString =  this.getNSPrefixes() +	
					" SELECT DISTINCT (SUM(?pairs) AS ?total_pairs) " +
			 		" WHERE { " +
			 			" { SELECT DISTINCT ?p1(COUNT(DISTINCT ?p2) AS ?pairs) " +
			 			" WHERE { " +
			 			" 	{?p1 a rdf:Property .} " +
			 			"	UNION {?p1 a owl:ObjectProperty .} " +
			 			"	UNION {?p1 a owl:DatatypeProperty .} " +
			 			"	UNION {?p1 a owl:AnnotationProperty .} " +
			 			"	UNION {?p1 a owl:FunctionalProperty .} " + 
			 			"	UNION {?p1 a owl:OntologyProperty .} " +
			 			"	UNION {?p1 a owl:AsymmetricProperty. } " +
	 		 			"	UNION {?p1 a owl:InverseFunctionalProperty .} " +
			 			"	UNION {?p1 a owl:IrreflexiveProperty .} " +
			 			"	UNION {?p1 a owl:ReflexiveProperty .} " +
			 			"	UNION {?p1 a owl:SymmetricProperty .} " +
			 			"	UNION{?p1 a owl:TransitiveProperty .} " +
			   
			    		"   ?p1 " + qName + " ?p2 . " +
			    		"   ?p1 rdfs:domain ?d1 . " +
			    		"   ?d1 rdfs:subClassOf ?sp1 . " +

			     		"   ?p2 rdfs:domain ?d2 . " +
			     		"   ?d2 rdfs:subClassOf ?sp2 . " +

			     		" FILTER EXISTS { {?sp1 rdfs:subClassOf ?sp2} UNION {?sp2 rdfs:subClassOf ?sp1} } " +
			     		"} GROUP BY ?p1 " +
			     	"}" +
			     "}";	        		
			
			return QueryFactory.create(queryString);	
	}//end discoverDCSR()
	
	
	/**
	 * @param qName - is the URI of the predicate that produces equivalent constructs that are
	 * properties, e.g., owl:equivalentProperties, owl:sameAs, skos:exactMatch
	 * 
	 * @return - count pairs of constructs that are properties and their domain classes have a
	 * common parent
	 */
	private com.hp.hpl.jena.query.Query discoverDCCPR(String qName) {
		 String queryString =  this.getNSPrefixes() +	
				" SELECT DISTINCT (SUM(?pairs) AS ?total_pairs) " +
		 		" WHERE { " +
		 			" { SELECT DISTINCT ?p1(COUNT(DISTINCT ?p2) AS ?pairs) " +
		 			" WHERE { " +
		 			" 	{?p1 a rdf:Property .} " +
		 			"	UNION {?p1 a owl:ObjectProperty .} " +
		 			"	UNION {?p1 a owl:DatatypeProperty .} " +
		 			"	UNION {?p1 a owl:AnnotationProperty .} " +
		 			"	UNION {?p1 a owl:FunctionalProperty .} " + 
		 			"	UNION {?p1 a owl:OntologyProperty .} " +
		 			"	UNION {?p1 a owl:AsymmetricProperty. } " +
 		 			"	UNION {?p1 a owl:InverseFunctionalProperty .} " +
		 			"	UNION {?p1 a owl:IrreflexiveProperty .} " +
		 			"	UNION {?p1 a owl:ReflexiveProperty .} " +
		 			"	UNION {?p1 a owl:SymmetricProperty .} " +
		 			"	UNION{?p1 a owl:TransitiveProperty .} " +
		   
			    	"   ?p1 " + qName + " ?p2 . " +
		    		"   ?p1 rdfs:domain ?d1 . " +
		    		"   ?d1 rdfs:subClassOf ?sp1 . " +

		     		"   ?p2 rdfs:domain ?d2 . " +
		     		"   ?d2 rdfs:subClassOf ?sp2 . " +

		     		"  FILTER (?sp1 = ?sp2) " +
		     		"} GROUP BY ?p1 " +
		     	"}" +
		     "}";	        		
		
		return QueryFactory.create(queryString);
	}//end discoverDCCPR()	
	
	
	
	
	
	
	/****************************************************************/
	
	
	/**
	 * @return true - if resource exists in the model, false otherwise
	 */
	private boolean resourceExists(Model model, String resourceURI) {
		boolean result = false;

		//logger.debug("URI ask: " + resourceURI);
		
		String queryString =
				"ASK " +
				"WHERE {" +
				" <" + resourceURI + "> " + "?p ?o . " +
				" }";
			
		//Create the SPARQL ASK query
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		result = qe.execAsk();
		qe.close();
			
		if (result) { 
			return true;
		}	
			
		return result;
	}//end resourceExists()	
	
	/**
	 * @return String - the namespace URI of a Resource
	 */
	private String getNameSpace(String uri) {
		if (uri.contains("#")) {
		   	uri = uri.substring(0, uri.lastIndexOf("#"));
			return uri + "#";
		}		
		uri = uri.substring(0, uri.lastIndexOf("/"));		
		return uri + "/";
	}//end returnNameSpace()
}//end Class