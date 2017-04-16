package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.util.ArrayList;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.training.SemEvidenceDataAnalysisUtil;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/*********************************************************************************************************
 * 									TrainingServiceUtilImpl (Abstract)
 * 													|
 * 					________________________________|___________________________________
 * 					|								|									|
 * 					|								|									|
 * 		SynEvidenceTrainingUtilImpl		SemEvidenceDataAnalysisUtilImpl		SemEvidenceTrainingUtilImpl
 * 												(this class)
 *  
 *  
 * This class is responsible for posing several SPARQL queries over the materialised vesrion of the LOV SPARQL
 * endpoint. The purpose of this class is to create an RDF graph that summarises the results of analysing the
 * data from the LOV endpoint. The RDF-graph is then made persistent in a TDB store. 
 *  
 *  
 *  
 * @author klitos
 * 
 * Reminder on OWL semantics
 *  Link: http://www.infowebml.ws/rdf-owl/differentFrom.htm
 * 
 * Reminder on TDB transactions
 *  Link: http://jena.apache.org/documentation/tdb/tdb_transactions.html
 *  
 * Reminder on how to interrogate Jena Model programmatically
 *  Link: http://jena.apache.org/tutorials/rdf_api.html#ch-Querying%20a%20Model
 *  
 * Reminder on query execution
 *  Link: http://jena.apache.org/documentation/query/app_api.html 
 */

public class SemEvidenceDataAnalysisUtilImpl extends TrainingServiceUtilImpl implements SemEvidenceDataAnalysisUtil {
	private static Logger logger = Logger.getLogger(SemEvidenceDataAnalysisUtilImpl.class);

	private TDBStoreServiceImpl tdbStore = null;
	
	/*Hold the URIs of the named graphs*/
	private String DATA_ANALYSIS;
	private String ENDPOINT_DATA;
	
	/*Constructor*/
	public SemEvidenceDataAnalysisUtilImpl(TDBStoreServiceImpl store, String analysisResultsGraphURI, String dataGraphURI) {	
		tdbStore = store;
		DATA_ANALYSIS = analysisResultsGraphURI;
		ENDPOINT_DATA = dataGraphURI;		
	}
		
	
	/**
	 * Method responsible for executing SPARQL queries required for the Analysis of the Dataset
	 * //TODO: for now the analysis prints a string representation of the data
	 *        - use Graphviz to output them as plots
	 *        - store the results in the appropriate named graph
	 *        
	 * @param reset - reset the named model that holds the data from analysis, by removing all the 
	 * 				  triples from it.
	 */
	public void doDataAnalysis(boolean reset) {		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);
		
		Model modelQuery = null;
		
		try {

			/*Model to perform the queries on and get the stats for the analysis plots*/
			modelQuery = tdbStore.getModel(ENDPOINT_DATA);			
			
			/*Model to store the result of the analysis*/
			Model resultsModel = tdbStore.getModel(DATA_ANALYSIS);	
						
			/*Reset the model by removing then named graph if exists*/
			if ((resultsModel != null) && (reset)) {
				logger.debug("Model cleanup ");
				tdbStore.removeNamedModel(DATA_ANALYSIS);	
				resultsModel = tdbStore.getModel(DATA_ANALYSIS);				
			}//end if
			
			/* Check if the named model to store the Data Analysis graph is empty and if it is
			   then do the analysis it, otherwise skip, meaning that it has been created already */			
			if (resultsModel.isEmpty()) {
						
				/*Prepare a prefix map to be used to return Qnames of the properties*/
				PrefixMapping pmap = this.createPrefixMap();
				
				/*Setup the axioms (predicates) to do the analysis on*/
				ArrayList<Property> listOfProperties = new ArrayList<Property>(); 

				// Properties that are used as axioms to indicate some notion of Equivalence (to be used for the Set of Equivalent pairs)
				listOfProperties.add( OWL.sameAs );
				listOfProperties.add( OWL.equivalentClass );
				listOfProperties.add( VOCAB.skosExactMatch ); //Classes only
				listOfProperties.add( OWL.equivalentProperty );	//Properties only
			
				// Properties that are used as axioms to indicate some notion of Non-equivalence (to be used for the Set of Non-Equivalence pairs)
				listOfProperties.add( OWL.differentFrom ); 
				listOfProperties.add( OWL.disjointWith ); //Classes only	
				
				/*Setup list of Custom Rules to do the analysis on*/
				ArrayList<Property> listOfRules = new ArrayList<Property>(); 
				listOfRules.add( VOCAB.onRule1 );
				
	
				//For listOfProperties
				for (Property axiom : listOfProperties) {

					/*Use resultsModel to store the results of this data analysis as RDF triples with anonymous URIs (BNodes)*/
					Resource bNode = resultsModel.createResource();
					bNode.addProperty( VOCAB.onPredicate, axiom );		
						
					//Get Query to count total number of pairs for each axiom
					String qName = pmap.qnameFor(axiom.getURI());
					logger.debug("qName : " + qName);
					Query query = countTotalNoPairs(qName);
			
					QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
		
					/**
					 * Count the number of total pairs that use this axiom
					 */
					try {
						ResultSet results = qexec.execSelect() ;
						for ( ; results.hasNext() ; ) {
							QuerySolution soln = results.nextSolution() ;
							Literal total_pairs = soln.getLiteral("total_pairs") ;			    	
							//Save the result as an RDF-statement
							Statement stmt = resultsModel.createStatement(
									bNode, VOCAB.totalNoPairs, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );
							resultsModel.add(stmt);							
							
						}//end for			
					} finally {
						qexec.close();
					}	
		
					//Second Query
					logger.debug("qName : " + qName);
			
					//applies to owl:sameAs, owl:equivalentClass (explicit)
					if (axiom.equals(OWL.sameAs) || axiom.equals(OWL.equivalentClass) || axiom.equals(VOCAB.skosExactMatch) 
							|| axiom.equals(OWL.differentFrom) || axiom.equals(OWL.disjointWith)) {
						query = countPairsNoClasses(qName);
					} else if (axiom.equals(OWL.equivalentProperty)) {
						query = countPairsNoProperties(qName);
					}
				
					qexec = QueryExecutionFactory.create(query, modelQuery);
			
					try {
						ResultSet results = qexec.execSelect() ;
						for ( ; results.hasNext() ; ) {
							QuerySolution soln = results.nextSolution() ;
							Literal total_pairs = soln.getLiteral("total_pairs") ;
							//Save the result as an RDF-statement
							Statement stmt = null;
							if (axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch) || axiom.equals(OWL.differentFrom)) {
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoClasses, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );
							} else if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.disjointWith)) {			   				
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoClassesExplicit, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );			   				
							} else if (axiom.equals(OWL.equivalentProperty)) {
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoPropsExplicit, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );
							}
			   		
							resultsModel.add(stmt);		    	
						}//end for					
					} finally {
						qexec.close();
					}//end finally
			
					//Third Query			
					logger.debug("qName : " + qName);
			
					if (axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch) || axiom.equals(OWL.differentFrom)) {
						query = countPairsNoProperties(qName);				
					} else if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.disjointWith)) {	
						query = countExplicitImplicitClassesProps(qName);
					} else if (axiom.equals(OWL.equivalentProperty)) {
						query = countExplicitImplicitClassesProps(qName);
					}
			
					qexec = QueryExecutionFactory.create(query, modelQuery);
			
					try {
						ResultSet results = qexec.execSelect() ;
						for ( ; results.hasNext() ; ) {
							QuerySolution soln = results.nextSolution() ;
							Literal total_pairs = soln.getLiteral("total_pairs") ;
							//Save the result as an RDF-statement
							Statement stmt = null;
							if (axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch) || axiom.equals(OWL.differentFrom)) {
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoProperties, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );
							} else if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.disjointWith)) {	
								//here we calculate the number of pairs that are not stated explicitly that the are Classes
								//we count the number of explicitClasses - totalNumberOfExplicitImplicitClasses
								Statement stm_expli = bNode.getProperty( VOCAB.pairsNoClassesExplicit );
								int noExplicitClasses = stm_expli.getObject().asLiteral().getInt(); 
			   			
								int noAllClasses = total_pairs.getInt();
						
								int noImplicitClasses = noAllClasses - noExplicitClasses;
						
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoClassesImplicit, resultsModel.createTypedLiteral(new Integer(noImplicitClasses)) );		 
							} else if (axiom.equals(OWL.equivalentProperty)) {
								//here we calculate the number of pairs that are not stated explicitly that the are Properties
								//we count the number of explicitProperties - totalNumberOfExplicitImplicitProperties
								Statement stm_expli = bNode.getProperty( VOCAB.pairsNoPropsExplicit );
								int noExplicitProps = stm_expli.getObject().asLiteral().getInt(); 
			   			
								int noAllClasses = total_pairs.getInt();
						
								int noImplicitProps = noAllClasses - noExplicitProps;
						
								stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoPropsImplicit, resultsModel.createTypedLiteral(new Integer(noImplicitProps)) );								
							}
			   		
							resultsModel.add(stmt);		    	
						}//end for					
					} finally {
						qexec.close();
					}
			
					//Forth Query (membership conditions)			
					if ( axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.equivalentProperty) || axiom.equals(OWL.disjointWith)) {	
						logger.debug("qName : " + qName);
						if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.disjointWith)) {
							query = countMembershipContClasses(qName);
						} else if (axiom.equals(OWL.equivalentProperty)) {
							query = countMembershipContProps(qName);
						}
				
						qexec = QueryExecutionFactory.create(query, modelQuery);
			
						try {
							ResultSet results = qexec.execSelect() ;
							for ( ; results.hasNext() ; ) {
								QuerySolution soln = results.nextSolution() ;
								Literal total_pairs = soln.getLiteral("total_pairs") ;
								//Save the result as an RDF-statement
								Statement stmt = null;
								if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.equivalentProperty) || axiom.equals(OWL.disjointWith)) {	
									stmt = resultsModel.createStatement(
										bNode, VOCAB.pairsNoMemberRestrict, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );		 
								} 							
								resultsModel.add(stmt);		    	
							}//end for					
						} finally {
							qexec.close();
						}
					}//end if	
				
					/*Remaining pairs are classified as Instances, because owl:sameAs is used mostly at the instance-level*/
					if (axiom.equals(OWL.sameAs) || axiom.equals(VOCAB.skosExactMatch) || axiom.equals(OWL.differentFrom)) {
						logger.debug("axiom : " + axiom);
						
						Statement stm1 = bNode.getProperty(VOCAB.totalNoPairs);
						
						int totalPairs = stm1.getObject().asLiteral().getInt(); 
				
						//discovered Classes
						Statement stm2 = bNode.getProperty(VOCAB.pairsNoClasses);
						int totalClasses = stm2.getObject().asLiteral().getInt(); 
				
						//discovered Properties
						Statement stm3 = bNode.getProperty(VOCAB.pairsNoProperties);
						int totalProps = stm3.getObject().asLiteral().getInt();
				
						//count remaining pairs and store them
						int remaining = totalPairs - (totalClasses + totalProps);	
				
						//Save the result as an RDF-statement
						Statement rem_stmt = resultsModel.createStatement(
								bNode, VOCAB.pairsNoInstances, resultsModel.createTypedLiteral(new Integer(remaining)) );
						resultsModel.add(rem_stmt);	
				
						int sum = remaining + totalClasses + totalProps;
				
						if (sum != totalPairs) {					
							int other = totalPairs - sum;					
							Statement other_stmt = resultsModel.createStatement(
									bNode, VOCAB.pairsNoOther, resultsModel.createTypedLiteral(new Integer(other)) );
							resultsModel.add(other_stmt);	
						}				
					}//end if			
			
					if (axiom.equals(OWL.equivalentClass) || axiom.equals(OWL.disjointWith)) {
						Statement stm = bNode.getProperty(VOCAB.totalNoPairs);
						int totalPairs = stm.getObject().asLiteral().getInt(); 
				
						//discovered explicit Classes
						stm = bNode.getProperty(VOCAB.pairsNoClassesExplicit);
						int totalExplicitClasses = stm.getObject().asLiteral().getInt(); 
				
						//discovered implicit Classes
						stm = bNode.getProperty(VOCAB.pairsNoClassesImplicit);
						int totalImplicitClasses = stm.getObject().asLiteral().getInt();
				
						//discovered pairs that participate as membership conditions using owl:equivalentClass
						stm = bNode.getProperty(VOCAB.pairsNoMemberRestrict);
						int totalMemberCond = stm.getObject().asLiteral().getInt();
				
						int sum = totalExplicitClasses + totalImplicitClasses + totalMemberCond;
				
						if (sum != totalPairs) {					
							int other = totalPairs - sum;					
							Statement other_stmt = resultsModel.createStatement(
									bNode, VOCAB.pairsNoOther, resultsModel.createTypedLiteral(new Integer(other)) );
							resultsModel.add(other_stmt);	
						}
					}//end if
				
					if (axiom.equals(OWL.equivalentProperty)) {
						Statement stm = bNode.getProperty(VOCAB.totalNoPairs);
						int totalPairs = stm.getObject().asLiteral().getInt(); 
				
						//discovered explicit Properties
						stm = bNode.getProperty(VOCAB.pairsNoPropsExplicit);
						int totalExplicitProps = stm.getObject().asLiteral().getInt(); 
				
						//discovered implicit Classes
						stm = bNode.getProperty(VOCAB.pairsNoPropsImplicit);
						int totalImplicitProps = stm.getObject().asLiteral().getInt();
				
						//discovered pairs that participate as membership conditions using owl:equivalentClass
						stm = bNode.getProperty(VOCAB.pairsNoMemberRestrict);
						int totalMemberCond = stm.getObject().asLiteral().getInt();
				
						int sum = totalExplicitProps + totalImplicitProps + totalMemberCond;
				
						if (sum != totalPairs) {					
							int other = totalPairs - sum;					
							Statement other_stmt = resultsModel.createStatement(
									bNode, VOCAB.pairsNoOther, resultsModel.createTypedLiteral(new Integer(other)) );
							resultsModel.add(other_stmt);	
						}
					}//end if				
				}//end for	
						
				/***
				 * Find the counts for the special rules of non-equivalent Properties,
				 * using the listOfRules
				 */
				this.doAnalysisNonEquiProps(listOfRules, modelQuery, resultsModel);
				
				
				/**
				 * Immediately call the method to construct the total counts
				 * for each set of equivalent/non-equivalent Classes or Properties
				 * and make the persistent as a subject with rdf:type Dataset. Meaning
				 * the subject that describes the sets.
				 */
				this.createResultsTable(resultsModel);			
				
				/*Commit the dataset*/
				dataset.commit();
				
				//Print it to test
				RDFDataMgr.write(System.out, resultsModel, Lang.RDFXML);
				
				
			}//end if
		} finally {
			dataset.end();			
		}//end finally	
	}//end doDataAnalysis()	

	
	/**
	 * Find non-equivalent Properties we have created custom rules, this method is responsible for executing
	 * and storing the results of these rules.
	 */
	private void doAnalysisNonEquiProps(ArrayList<Property> listOfRules, Model modelQuery, Model resultsModel) {
		
		for (Property axiom : listOfRules) {
			
			/*Use resultsModel to store the results of this data analysis as RDF triples with anonymous URIs (BNodes)*/
			Resource bNode = resultsModel.createResource();
			bNode.addProperty( VOCAB.onPredicate, axiom );	
			
			Query query = null;
			
			if (axiom.equals(VOCAB.onRule1)) {				
				query = getSumOfPairsOfNonEquivalentPropsRule1();				
			}			
			
			QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
			
			/**
			 * Count the number of total pairs that use this axiom
			 */
			try {
				ResultSet results = qexec.execSelect() ;
				for ( ; results.hasNext() ; ) {
					QuerySolution soln = results.nextSolution() ;
					Literal total_pairs = soln.getLiteral("total_pairs") ;			    	
					//Save the result as an RDF-statement
					Statement stmt = resultsModel.createStatement(
							bNode, VOCAB.totalNoPairs, resultsModel.createTypedLiteral(new Integer(total_pairs.getInt())) );
					resultsModel.add(stmt);				
			
				}//end for			
			} finally {
				qexec.close();
			}	
			
		}//end for	
		
	}//end method	
	
	
	/**
	 * Documentation for TDB transactions: http://jena.apache.org/documentation/tdb/tdb_transactions.html
	 *
	 * @return DataAnalysisSummary - object that summarises the results of analysing the SPARQL endpoint
	 */
	private DataAnalysisSummary createResultsTable(Model resultsModel) {
		
		DataAnalysisSummary table = null;		
		
		//Create an object that will hold the analysis of the SPARQL endpoint
		table = new DataAnalysisSummary(); 
			
		//Make them persistent, represent them as a Dataset
		Resource bNode = resultsModel.createResource();	
		bNode.addProperty(RDF.type, resultsModel.createProperty("http://vocab.deri.ie/void#Dataset"));	
			
		//Get total pairs of Equivalent Classes
		Query query = calcTotalPairsOfEquivalentClasses2();			
		QueryExecution qexec = QueryExecutionFactory.create(query, resultsModel);
		try {
			ResultSet results = qexec.execSelect() ;
			if (results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				Literal owlPairs = soln.getLiteral("pairsOfClassesOwl") ;
				Literal explicit = soln.getLiteral("explicit") ;
				Literal implicit = soln.getLiteral("implicit") ;
				Literal skosPairs = soln.getLiteral("pairsOfClassesSkos") ;				
				logger.debug("Count Class Pairs: owlPairs: " + owlPairs + " | explicit: " + explicit + " | implicit: " + implicit + " | skosPairs: " + skosPairs);
				
				long sum = owlPairs.getLong() + explicit.getLong() + implicit.getLong() + skosPairs.getLong();

				//Store the number: TotalPairsEquiClasses to table
				table.setTotalPairsEquiClasses(sum);
					
				Statement stmt = resultsModel.createStatement(
						bNode, VOCAB.totalPairsEquiClasses, resultsModel.createTypedLiteral(new Long(sum)) );
				resultsModel.add(stmt);			
			}//end if
		} finally {
			qexec.close();
		}
			
		//Get total pairs of Equivalent Properties
		query = calcTotalPairsOfEquivalentProps1();			
		qexec = QueryExecutionFactory.create(query, resultsModel);			
		try {
			ResultSet results = qexec.execSelect() ;
			if (results.hasNext()) {
				QuerySolution soln = results.nextSolution() ;
				Literal owlPairs = soln.getLiteral("pairsOfPropsOwl") ;
				Literal explicit = soln.getLiteral("explicit") ;
				Literal implicit = soln.getLiteral("implicit") ;
				Literal skosPairs = soln.getLiteral("pairsOfPropsSkos") ;				
				logger.debug("Count Props Pairs: owlPairs: " + owlPairs + " | explicit: " + explicit + " | implicit: " + implicit + " | skosPairs: " + skosPairs);
								
				long sum = owlPairs.getLong() + explicit.getLong() + implicit.getLong() + skosPairs.getLong();
				
				//Store the number: TotalPairsEquiProps to table
				table.setTotalPairsEquiProps(sum);
				
				Statement stmt = resultsModel.createStatement(
						bNode, VOCAB.totalPairsEquiProps, resultsModel.createTypedLiteral(new Long(sum)) );
				resultsModel.add(stmt);					
			}
		} finally {
			qexec.close();
		}
			
		//Annotate with rdfs:comment
		Statement stmt = resultsModel.createStatement(
				bNode, RDFS.comment, resultsModel.createLiteral("Summary of Equivalent/non-equivalent pairs of Classes & Properties") );
		resultsModel.add(stmt);	
			
			
		//Get total pairs of Non-equivalent Classes
		query = calcTotalPairsOfNonEquivalentClasses();			
		qexec = QueryExecutionFactory.create(query, resultsModel);			
		try {
			ResultSet results = qexec.execSelect() ;
			if (results.hasNext()) { 
				QuerySolution soln = results.nextSolution() ;
				Literal owlDiffFrom = soln.getLiteral("nonEquiClassesDiffFrom") ;
				Literal explicit = soln.getLiteral("explicit") ;
				Literal implicit = soln.getLiteral("implicit") ;			
				logger.debug("Count Class Non Pairs: owlDiffFrom: " + owlDiffFrom + " | explicit: " + explicit + " | implicit: " + implicit);
				
				long sum = owlDiffFrom.getLong() + explicit.getLong() + implicit.getLong();
				
				//Store the number: TotalPairsNonEquiClasses to table
				table.setTotalPairsNonEquiClasses(sum);
					
				Statement stmtNon = resultsModel.createStatement(
						bNode, VOCAB.totalPairsNonEquiClasses, resultsModel.createTypedLiteral(new Long(sum)) );
				resultsModel.add(stmtNon);					
			}
		} finally {
			qexec.close();
		}			
		
     	//Get total pairs of Non-equivalent Properties
		query = calcTotalPairsOfNonEquivalentProps();	
		qexec = QueryExecutionFactory.create(query, resultsModel);	
		
		try {
			ResultSet results = qexec.execSelect() ;
			if (results.hasNext()) { 
				QuerySolution soln = results.nextSolution() ;
				Literal owlDiffFrom = soln.getLiteral("nonEquiPropsDiffFrom") ;
				Literal sum_rule1 = soln.getLiteral("rule1") ;
				logger.debug("Count Props Non Pairs: owlDiffFrom: " + owlDiffFrom + " | rule1: " + sum_rule1);
				
				long sum = owlDiffFrom.getLong() + sum_rule1.getLong();
				
				//Store the number: TotalPairsNonEquiClasses to table
				table.setTotalPairsNonEquiProps(sum);
					
				Statement stmtNon = resultsModel.createStatement(
						bNode, VOCAB.totalPairsNonEquiProps, resultsModel.createTypedLiteral(new Long(sum)) );
				resultsModel.add(stmtNon);					
			}
		} finally {
			qexec.close();
		}		
			
		return table;
	}//end getResultsTable()	
	
	/********************************************
	 * SPARQL - Total Pairs of Equivalent Classes
	 ********************************************/
	private com.hp.hpl.jena.query.Query calcTotalPairsOfEquivalentClasses1() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT ((?pairsOfClassesOwl + ?explicit + ?implicit + ?pairsOfClassesSkos) AS ?total) " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?pairsOfClassesOwl " +
	        		"  WHERE { " +
	        		"	    ?s1 j.0:onPredicate owl:sameAs . " +
	        		"		?s1 j.0:pairsNoClasses ?pairsOfClassesOwl . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?explicit ?implicit " +
	        		"  WHERE { " +
	        		"	    ?s2 j.0:onPredicate owl:equivalentClass . " +
	        		"		?s2 j.0:pairsNoClassesExplicit ?explicit . " +
	        		" 		?s2 j.0:pairsNoClassesImplicit ?implicit . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?pairsOfClassesSkos " +
	        		"  WHERE { " +
	        		"	    ?s3 j.0:onPredicate skos:exactMatch . " +
	        		"		?s3 j.0:pairsNoClasses ?pairsOfClassesSkos . " +
	        		"  } " +
	        		" } " +	        		
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfEquivalentClasses()	
	
	private com.hp.hpl.jena.query.Query calcTotalPairsOfEquivalentClasses2() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT ?pairsOfClassesOwl ?explicit ?implicit ?pairsOfClassesSkos " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?pairsOfClassesOwl " +
	        		"  WHERE { " +
	        		"	    ?s1 j.0:onPredicate owl:sameAs . " +
	        		"		?s1 j.0:pairsNoClasses ?pairsOfClassesOwl . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?explicit ?implicit " +
	        		"  WHERE { " +
	        		"	    ?s2 j.0:onPredicate owl:equivalentClass . " +
	        		"		?s2 j.0:pairsNoClassesExplicit ?explicit . " +
	        		" 		?s2 j.0:pairsNoClassesImplicit ?implicit . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?pairsOfClassesSkos " +
	        		"  WHERE { " +
	        		"	    ?s3 j.0:onPredicate skos:exactMatch . " +
	        		"		?s3 j.0:pairsNoClasses ?pairsOfClassesSkos . " +
	        		"  } " +
	        		" } " +	        		
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfEquivalentClasses()
	
	/***********************************************
	 * SPARQL - Total Pairs of Equivalent Properties
	 ***********************************************/
	private com.hp.hpl.jena.query.Query calcTotalPairsOfEquivalentProps1() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT ?pairsOfPropsOwl ?explicit ?implicit ?pairsOfPropsSkos " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?pairsOfPropsOwl " +
	        		"  WHERE { " +
	        		"	    ?s1 j.0:onPredicate owl:sameAs . " +
	        		"		?s1 j.0:pairsNoProps ?pairsOfPropsOwl . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?explicit ?implicit " +
	        		"  WHERE { " +
	        		"	    ?s2 j.0:onPredicate owl:equivalentProperty . " +
	        		"		?s2 j.0:pairsNoPropsExplicit ?explicit . " +
	        		" 		?s2 j.0:pairsNoPropsImplicit ?implicit . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?pairsOfPropsSkos " +
	        		"  WHERE { " +
	        		"	    ?s3 j.0:onPredicate skos:exactMatch . " +
	        		"		?s3 j.0:pairsNoProps ?pairsOfPropsSkos . " +
	        		"  } " +
	        		" } " +	        		
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfEquivalentProps()	
	
	
	/*************************************************
	 * SPARQL - Total Pairs of Non-equivalent Classes, discovered using axioms:
	 *   - owl:differentFrom
	 *   - owl:disjointWith	 * 
	 *************************************************/
	private com.hp.hpl.jena.query.Query calcTotalPairsOfNonEquivalentClasses() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT ?nonEquiClassesDiffFrom ?explicit ?implicit " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?nonEquiClassesDiffFrom " +
	        		"  WHERE { " +
	        		"	    ?s1 j.0:onPredicate owl:differentFrom . " +
	        		"		?s1 j.0:pairsNoClasses ?nonEquiClassesDiffFrom . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?explicit ?implicit " +
	        		"  WHERE { " +
	        		"	    ?s2 j.0:onPredicate owl:disjointWith . " +
	        		"		?s2 j.0:pairsNoClassesExplicit ?explicit . " +
	        		" 		?s2 j.0:pairsNoClassesImplicit ?implicit . " +
	        		"  } " +
	        		" } " +
			   		"}";		
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentClasses()
	
	/***************************************************************************
	 * SPARQL - Total Pairs of Non-equivalent Properties - Rule 1 from proposal
	 ***************************************************************************/
	
	/**
	 * @return - Will return the total count (SUM) of non-equivalent pair of properties
	 * 			 according to Rule a from the proposal, described by the following 
	 * 			 SPARQL query
	 */
	private com.hp.hpl.jena.query.Query getSumOfPairsOfNonEquivalentPropsRule1() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT DISTINCT ?prop1 (COUNT(DISTINCT ?prop2) AS ?pair) " +
	        		"  WHERE " +
	        		"  { " +
	        		"	{ "+
	        		"	  SELECT ?prop1 ?range1 " +
	        		"	  WHERE { ?prop1 a rdf:Property . " +
	        		"			  ?prop1 rdfs:range ?range1 . " +
	        		" 	  } " +
	        		"   } " +
	        		"   { " +
	        		"     SELECT ?prop2 ?range2 " +
	        		" 	  WHERE { ?prop2 a rdf:Property . " +
	        		" 			  ?prop2 rdfs:range ?range2 . " +
	        		"  	  } " +
	        		"   } " +
	        		"   FILTER (?range1 != ?range2)" +
	        		"   FILTER EXISTS { ?range1 owl:disjointWith ?range2 } " +
	        		"  } GROUP BY ?prop1" +
	        		" } " +
	        		"}";	 
		 
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentClasses()
	
	
	
	/***************************************************************************
	 * SPARQL - Total Pairs of Non-equivalent Properties - Rule 2 from proposal
	 ***************************************************************************/

	/**
	 * @return - This query will return some pairs <prop1, prop2> back, this is for my Rule2.
	 * I will use this kind of pairs to find evidences for my likelihoods
	 */
	private com.hp.hpl.jena.query.Query getPairsOfNonEquivalentPropsRule2(boolean hasLimit, int limit) {
		
		/*Notice that this query is only for rdf:Property */
		
		String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT ?prop1 ?prop2 " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?prop1 ?domain1 ?range1 " +
	        		"  WHERE { " +
	        		"	    ?prop1 a rdf:Property . " +
	        		"		?prop1 rdfs:domain ?domain1 . " +
	        		" 		?prop1 rdfs:range ?range1 . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?prop2 ?domain2 ?range2 " +
	        		"  WHERE { " +
	        		"	    ?prop2 a rdf:Property . " +
	        		"		?prop2 rdfs:domain ?domain2 . " +
	        		" 		?prop2 rdfs:range ?range2 . " +
	        		"  } " +
	        		" } " +
	        		" FILTER ((?domain1 != ?domain2) && (?range1 != ?range2) ) " +
	        		" FILTER EXISTS { ?domain1 owl:disjointWith ?domain2 } ";			   		
		 
		 if (hasLimit) {
			 queryString = queryString + "} LIMIT " + limit;
		 } else {
			 queryString = queryString + "}";
	     }			 
		
		return QueryFactory.create(queryString);		
	}//end getPairsOfNonEquivalentProps()
	
	/**
	 * @return - Will return the total count (SUM) of non-equivalent pair of properties
	 * 			 according to Rule 2 from the proposal, decribed by the following 
	 * 			 SPARQL query
	 */
	private com.hp.hpl.jena.query.Query getSumOfPairsOfNonEquivalentPropsRule2(boolean hasLimit, int limit) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT DISTINCT ?prop1 (COUNT(DISTINCT ?prop2) AS ?pair) " +
	        		"  WHERE " +
	        		"  { " +
	        		"	{ "+
	        		"	  SELECT ?prop1 ?domain1 ?range1 " +
	        		"	  WHERE { ?prop1 a rdf:Property . " +
	        		"	          ?prop1 rdfs:domain ?domain1 . " +
	        		"			  ?prop1 rdfs:range ?range1 . " +
	        		" 	  } " +
	        		"   } " +
	        		"   { " +
	        		"     SELECT ?prop2 ?domain2 ?range2 " +
	        		" 	  WHERE { ?prop2 a rdf:Property . " +
	        		" 			  ?prop2 rdfs:domain ?domain2 . " +
	        		" 			  ?prop2 rdfs:range ?range2 . " +
	        		"  	  } " +
	        		"   } " +
	        		"   FILTER ((?domain1 != ?domain2) && (?range1 != ?range2) )" +
	        		"   FILTER EXISTS { ?domain1 owl:disjointWith ?domain2 } " +
	        		"  } GROUP BY ?prop1" +
	        		" } " +
	        		"}";	 
		 
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentClasses()
	
	/**************************************************************************
	 * SPARQL - Total Pairs of Non-equivalent Props, discovered using axioms:
	 *   - custom Rule1
	 **************************************************************************/
	private com.hp.hpl.jena.query.Query calcTotalPairsOfNonEquivalentProps() {
		 String queryString =  getNSPrefixes() +	
				    "SELECT ?nonEquiPropsDiffFrom ?rule1 " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT ?nonEquiPropsDiffFrom " +
	        		"  WHERE { " +
	        		"	    ?s1 j.0:onPredicate owl:differentFrom . " +
	        		"		?s1 j.0:pairsNoProps ?nonEquiPropsDiffFrom . " +
	        		"  } " +
	        		" } " +
				    " { " + 
	        		"  SELECT ?rule1 " +
	        		"  WHERE { " +
	        		"	    ?s2 j.0:onPredicate j.0:rule1 . " +
	        		"		?s2 j.0:totalNoOfPairs ?rule1 ." +
	        		"  } " +
	        		" } " +
			   		"}";		
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentProps()
	
	
	/**
	 * @return - This query will return a list showing ?prop1 | pair_count
	 */
	private com.hp.hpl.jena.query.Query calcTotalPairsOfNonEquivalentProps2(boolean hasLimit, int limit) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT ?prop1 ?pair " +
				    "WHERE " +
				    "{ " +
				    " { " + 
	        		"  SELECT DISTINCT ?prop1 (COUNT(DISTINCT ?prop2) AS ?pair) " +
	        		"  WHERE " +
	        		"  { " +
	        		"	{ "+
	        		"	  SELECT ?prop1 ?domain1 ?range1 " +
	        		"	  WHERE { ?prop1 a rdf:Property . " +
	        		"	          ?prop1 rdfs:domain ?domain1 . " +
	        		"			  ?prop1 rdfs:range ?range1 . " +
	        		" 	  } " +
	        		"   } " +
	        		"   { " +
	        		"     SELECT ?prop2 ?domain2 ?range2 " +
	        		" 	  WHERE { ?prop2 a rdf:Property . " +
	        		" 			  ?prop2 rdfs:domain ?domain2 . " +
	        		" 			  ?prop2 rdfs:range ?range2 . " +
	        		"  	  } " +
	        		"   } " +
	        		"   FILTER ((?domain1 != ?domain2) && (?range1 != ?range2) )" +
	        		"   FILTER EXISTS { ?domain1 owl:disjointWith ?domain2 } " +
	        		"  } GROUP BY ?prop1" +
	        		" } ";
		 
		 if (hasLimit) {
			 queryString = queryString + "} LIMIT " + limit;
		 } else {
			 queryString = queryString + "}";
	     }		 
		
		return QueryFactory.create(queryString);		
	}//end calcTotalPairsOfNonEquivalentClasses()	
	
	
	/*****************
	 * SPARQL Queries
	 *****************/
	
	/**
	 * @return - the total number of pairs of constructs (either Classes/Properties) that have the axiom
	 */
	private com.hp.hpl.jena.query.Query countTotalNoPairs(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(?elem2) AS ?pair) " +
	        		"  WHERE { " +
	        		"    ?elem1 " + qName + " ?elem2 . " +	        		
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countTotalOccurrences()	
	
	/**
	 * @return - the total number of pairs of constructs that are stated to be Classes and have the axiom
	 * applies to : owl:sameAs, owl:equivalentClass (explicit)
	 */
	private com.hp.hpl.jena.query.Query countPairsNoClasses(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(DISTINCT ?elem2) AS ?pair) " +
	        		"  WHERE { " +
	        		" 			{?elem1 a rdfs:Class .} " +
	        		" 			UNION {?elem1 a owl:Class .} " +
	        		"   		?elem1 ?elem1_predicates ?elem2 . " +
	        		"			FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem2))" +
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countPairsNoClasses()
	
	/***
	 * @return - all pairs that have occurrence of the axiom, and are Classes either explicitly or implicitly
	 */
	private com.hp.hpl.jena.query.Query countExplicitImplicitClassesProps(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(DISTINCT ?elem2) AS ?pair) " +
	        		"  WHERE { " +
	        		"   		?elem1 ?elem1_predicates ?elem2 . " +
	        		"			FILTER (?elem1_predicates = " + qName + " && !isBlank(?elem1) && !isBlank(?elem2))" +
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countExplicitImplicitClasses()
	
	/**
	 * @return - owl:equivalentClass is used for membership conditions we want to skip such pairs,
	 * we discover them with the following query.
	 */
	private com.hp.hpl.jena.query.Query countMembershipContClasses(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(DISTINCT ?elem2) AS ?pair) " +
	        		"  WHERE { " +
	        		" 			{?elem1 a rdfs:Class .} " +
	        		" 			UNION {?elem1 a owl:Class .} " +
	        		"   		?elem1 ?elem1_predicates ?elem2 . " +
	        		"			FILTER (?elem1_predicates = " + qName + " && isBlank(?elem2))" +
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countMembershipContClasses()
	
	/**
	 * @return - owl:equivalentProperty is used for membership conditions we want to skip such pairs,
	 * we discover them with the following query.
	 */
	private com.hp.hpl.jena.query.Query countMembershipContProps(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(DISTINCT ?elem2) AS ?pair) " +
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
	        		"			FILTER (?elem1_predicates = " + qName + " && isBlank(?elem2))" +
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countMembershipContProps()	
	
	/**
	 * @return - the total number of pairs of constructs that are stated to be Properties (any property) and have the axiom
	 */
	private com.hp.hpl.jena.query.Query countPairsNoProperties(String qName) {
		 String queryString =  getNSPrefixes() +	
				    "SELECT DISTINCT (SUM(?pair) AS ?total_pairs) " +
				    "WHERE { " +
				    " { " + 
	        		"  SELECT DISTINCT ?elem1 (COUNT(DISTINCT ?elem2) AS ?pair) " +
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
	        		"  } GROUP BY ?elem1 " +
	        		" }" +
	        		"}";		
		
		return QueryFactory.create(queryString);		
	}//end countPairsNoProperties()	
}//end class