package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema.DereferenceURIAgentServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.VOCAB;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;


/***
 * //NOTE: With SPARQL 1.1 I can use property paths to query.
 *
 * @author klitos
 */

@Scope("prototype")
@Service
public class EquivalenceSemanticMatrix extends HierarchySemanticMatrix {
	
	private static Logger logger = Logger.getLogger(EquivalenceSemanticMatrix.class);
	
	/*Hold a reference to the SDBStore that holds the RDFS vocabularies*/
	private SDBStoreServiceImpl sdbStore;
	
	/*Constructors*/
	public EquivalenceSemanticMatrix() {	
		logger.debug("in EquivalenceSemanticMatrix");
		sdbStore = this.getSDBStoreService();	
		/*Attach a URI agent on this matrix responsible for dereferencing*/
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("EquivalenceSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.EQUIVALENCE);
		this.setPrecedenceLevel(1);
	}
	
	public EquivalenceSemanticMatrix(SDBStoreServiceImpl store) {
		super(store);
		sdbStore = this.getSDBStoreService();
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("EquivalenceSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.EQUIVALENCE);
		this.setPrecedenceLevel(1);
	}
	
	public EquivalenceSemanticMatrix(SDBStoreServiceImpl store, DereferenceURIAgentServiceImpl agent) {
		super(store);
		sdbStore = this.getSDBStoreService();
		dereferenceURIAgentService = agent;
		this.setSemMatrixName("EquivalenceSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.EQUIVALENCE);
		this.setPrecedenceLevel(1);
	}

	/***
	 * This method is responsible to generate the semantic matrix that organises information from namespaces
	 * on whether two Classes or Properties are equivalent searches for:
	 *   - identical URIs and both Classes
	 *   - identical URIs and both Properties
	 *   or
	 *   - owl:equivalentClass
	 *   - owl:equivalentProperty
	 *   or
	 *   - subClassOf in both directions
	 *   
	 *   Note: URIs in RDF are case of sensitive
	 */ 
	public SemanticMatrix generateSemanticMatrix(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs,
			int indexOfMatrix) {		
		logger.debug("in generateSemanticMatrix()");
		logger.info("SDBStoreServiceImpl: " + sdbStore);
		int rows = sourceConstructs.size();
		int columns = targetConstructs.size();
		logger.info("sourceConstructs [rows]: " + rows);
		logger.info("targetConstructs [columns]: " + columns);
		SemanticMatrix matrix = new SemanticMatrix(rows, columns, indexOfMatrix);
		/*To be used for precedence level*/
		matrix.setType(SemanticMatrixType.EQUIVALENCE);
		/*Check if it has control parameters*/
		Map<ControlParameterType, ControlParameter> controlParameters = this.getControlParameters();
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.USE_REASONER))) {
			usingInferencing = controlParameters.get(ControlParameterType.USE_REASONER).isBool();
		}
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALIGNMENT_THRESHOLD))) {
			threshold = Double.parseDouble(controlParameters.get(ControlParameterType.ALIGNMENT_THRESHOLD).getValue());
		}		
		
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.EXPERIMENT))) {
			benchmarkType = controlParameters.get(ControlParameterType.EXPERIMENT).getBenchmarkType();
		}
		
		
		//Check if annotations are given by an alignment or whether to search SDB to find them
		if (benchmarkType.equals(BenchmarkType.SIMULATE_SEMANTIC_ANNOTATIONS)) {			
			logger.info("Mode: " + benchmarkType);
			
			Dataset dataset = this.getTDBStoreService().getDataset();
			dataset.begin(ReadWrite.READ);		
			try {
				//Look the given alignment to find any semantic annotations
				alignGraphModel = this.getTDBStoreService().getModel(this.getAlignGraphURI());
			
				for (CanonicalModelConstruct construct1 : sourceConstructs) {
					for (CanonicalModelConstruct construct2 : targetConstructs) {
						/*Get the [row][column] position to add the cell entry to*/
						int rowIndex = sourceConstructs.indexOf(construct1);
						int colIndex = targetConstructs.indexOf(construct2);
						//logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
						SemanticMatrixEntry entry = this.findEquivalenceFromAlignment(construct1, construct2, alignGraphModel);
						//logger.debug("Entry is: " + entry);
						ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
						columnList.add(colIndex, entry);	
					}//end inner for
				}//end for			
			} finally { 
				dataset.end();
			}		
		} else {
			//Look SDB Store for the semantic annotations		
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					//logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
					SemanticMatrixEntry entry = this.findEquivalence(construct1, construct2);
					//logger.debug("Entry is: " + entry);
					ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
					columnList.add(colIndex, entry);				
				}//end inner for
			}//end for		
		}//end else
		
		return matrix;
	}//generateSemanticMatrix()
	
		
	/**
	 * This method discovers equivalence relations from a GIVEN ALIGNMENT
	 */
	public SemanticMatrixEntry findEquivalenceFromAlignment(CanonicalModelConstruct construct1,
											   CanonicalModelConstruct construct2, Model alignGraphModel) {	
		logger.debug("in findEquivalenceFromAlignment()");
		
		SemanticMatrixEntry entry = null;
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
				((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;			
			
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
			String construct1URI = this.getConstructURIns(construct1);		
			String construct2URI = this.getConstructURIns(construct2);	
			logger.debug("construct1URI: " + construct1URI);
			logger.debug("construct2URI: " + construct2URI);
				
			//Create a query over the TDB Store to search for equivalence from the given alignment
			Query query = equivalentASKQueryAlignment(construct1URI, construct2URI, threshold);				
			QueryExecution qexec = QueryExecutionFactory.create(query, alignGraphModel);
			boolean equivalenceExists = qexec.execAsk();
				
			try {
				if (equivalenceExists) {
					entry = new SemanticMatrixEntry(construct1, construct2);
					entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
					//add this evidence found to the collection of BooleanVariables for this cell
					entry.addCellValueToList(BooleanVariables.CEC);	
				} else {
					//logger.debug("Nothing was found in the given alignment");
					//TODO: If nothing was found in the alignment then search the models from the SDBStore
					
				}
			} finally {	qexec.close(); }
		} else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
			//TODO: Reading ontology Class hierarchy only, in the future consider properties are well
		}//end if	
		
		return entry;
	}//findEquivalenceFromAlignment()
	
	
	/**
	 * Observe the semantic annotations from Jena SDBStore and search for equivalence
	 */
	public SemanticMatrixEntry findEquivalence(CanonicalModelConstruct construct1,
	 		  								   CanonicalModelConstruct construct2) {		
		logger.debug("in findEquivalence()");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);		

		SemanticMatrixEntry entry = null;
		Model baseModel_1 = null;
		Model baseModel_2 = null;
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;
		
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
			/*Call method from this class SuperClass to find the property of this construct that hold its URI*/
			String construct1URI = this.getConstructPropURI(construct1).getValue();			
			String construct2URI = this.getConstructPropURI(construct2).getValue();
			logger.debug("construct1URI: " + construct1URI);
			logger.debug("construct2URI: " + construct2URI);
			
			/*Firstly, check whether their URIs are the same this means that they refer to the same Resource*/
			if (construct1URI.equals(construct2URI)) {
				logger.debug("Resources have equal URIs");
				entry = new SemanticMatrixEntry(construct1, construct2);
				//entry.setDirSymbol("&equiv;");
				entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
				entry.addCellValueToList(BooleanVariables.CSURI);				
				return entry;
			} 			
			
			/*Find the NamedGraph of the first constructURI, if exists then proceed*/
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				if (baseModel_2 != null) {
					//Using a Reasoner
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						//consider using OntModel and OntClass for doing the reasoning
						//see the SubsumptionSemanticMatrix for more info
						//skip this case for now
					} else {	
						//Without a Reasoner
						logger.debug("Using inferencing mode: " + usingInferencing);
												
						/*Check for owl:sameAs, intensional meaning is the same*/
						ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, OWL.sameAs);
						boolean cons1SameAsConst2 = this.hasEquivalence(set_1, construct2URI);
						
						if (cons1SameAsConst2) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CSA);						
						}	
						
						ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, OWL.sameAs);
						boolean cons2SameAsConst1 = this.hasEquivalence(set_2, construct1URI);	
						
						if (cons2SameAsConst1) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CSA);					
						}
						
						/*Otherwise look for owl:equivalentClass*/
						set_1 = null;
						set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, OWL.equivalentClass);
						boolean cons1OwlEquiOfconst2 = this.hasEquivalence(set_1, construct2URI);
												
						if (cons1OwlEquiOfconst2) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CEC);	
						}						
			
						set_2 = null;
						set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, OWL.equivalentClass);
						boolean cons2OwlEquiOfconst1 = this.hasEquivalence(set_2, construct1URI);						
						
						if (cons2OwlEquiOfconst1) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CEC);						
						}
						
						/*Otherwise look for skos:exactMatch*/
						set_1 = null;
						set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, VOCAB.skosExactMatch);
						boolean cons1skosMatchOfconst2 = this.hasEquivalence(set_1, construct2URI);
												
						if (cons1skosMatchOfconst2) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CEM);							
						}
						
						
						set_2 = null;
						set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, VOCAB.skosExactMatch);
						boolean cons2skosMatchOfconst1 = this.hasEquivalence(set_2, construct1URI);						
						
						if (cons2skosMatchOfconst1) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CEM);						
						}						
																		
						/*Make sure the sets are empty*/
						set_1 = null;
						set_2 = null;
						
						/*Lastly, check for subClassOf in both directions*/
						set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.subClassOf);
						set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.subClassOf);
						
						boolean cons1SubClassOfconst2 = this.isSubClass(set_1, construct2URI);
						boolean cons2SubClassOfconst1 = this.isSubClass(set_2, construct1URI);
						
						if (cons1SubClassOfconst2 && cons2SubClassOfconst1) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							//add this evidence found to the collection of BooleanVariables for this cell
							entry.addCellValueToList(BooleanVariables.CBSR);			
						}						
					}//end else										
				}//end inner if			
			}//end if			
		} else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
			
			/* Note: SuperLexicals are also superRelationship in my schemas, because I am considering superRelationship as constrains over
			 *        SuperLexicals therefor to me are all superLexicals  */
			
			/*Call method from this class SuperClass to find the property of this construct that hold its URI*/
			String construct1URI = this.getConstructPropURI(construct1).getValue();			
			String construct2URI = this.getConstructPropURI(construct2).getValue();
			logger.debug("construct1URI: " + construct1URI);
			logger.debug("construct2URI: " + construct2URI);
			
			/*Firstly, check whether their URIs are the same by checking if their Resource objects are equal*/
			if (construct1URI.equals(construct2URI)) {
				logger.debug("Resources have equal URIs");
				entry = new SemanticMatrixEntry(construct1, construct2);
				entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
				entry.addCellValueToList(BooleanVariables.PSURI);					
				return entry;
			} 
			
			/*Find the NamedGraph of the first constructURI, if exists then proceed*/
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				if (baseModel_2 != null) {					
					//Using a Reasoner
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						//consider using OntModel and OntClass for doing the reasoning
						//see the SubsumptionSemanticMatrix for more info
						//skip this case for now
					} else {
						//Without a Reasoner
						logger.debug("Using inferencing mode: " + usingInferencing);
						
						/**
						 * Owl:sameAs and skos:exactMatch can only be on Classes 
						 */
						
						/*Otherwise look for owl:equivalentProperty*/
						ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, OWL.equivalentProperty);
						boolean cons1OwlEquiOfconst2 = this.hasEquivalence(set_1, construct2URI);
												
						if (cons1OwlEquiOfconst2) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							entry.addCellValueToList(BooleanVariables.PEP);
							return entry;							
						}						
			
						ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, OWL.equivalentProperty);
						boolean cons2OwlEquiOfconst1 = this.hasEquivalence(set_2, construct1URI);						
						
						if (cons2OwlEquiOfconst1) {
							entry = new SemanticMatrixEntry(construct1, construct2);
							entry.setTypeOfEntry(SemanticMatrixType.EQUIVALENCE);
							entry.addCellValueToList(BooleanVariables.PEP);
							return entry;						
						}					
					}//end without reasoner				
				}//end if
			}//end if
		}//end if		
		
		return entry;
	}//end findEquivalence()	
	
	/**
	 * Check for a subclass relation without a reasoner.
	 * 
	 * @param set_1 - the result set of the construct to search whether is a subclass of the resource
	 * @param res1 - the resource to check
	 * @return true - if the construct is a subClassOf the resource, false otherwise 
	 */
	public boolean isSubClass(ResultSet set, String constructURI) {
		while (set.hasNext()) {
		    QuerySolution soln = set.nextSolution() ;
		    RDFNode res2 = soln.get("o");
		    if (constructURI.equals(res2.toString())) {		    	
		    	return true;
		    }//end if
		}//end while
		return false;	
	}//end isSubClass()
	
	/**
	 * 
	 * @param set
	 * @param constructURI
	 * @return
	 */
	public boolean hasEquivalence(ResultSet set, String constructURI) {
		while (set.hasNext()) {
		    QuerySolution soln = set.nextSolution() ;
		    RDFNode res2 = soln.get("o");
		    if (constructURI.equals(res2.toString())) {		    	
		    	return true;
		    }//end if
		}//end while
		return false;	
	}//end isSubClass()	
	
	/***
	 * @return true - if owl:sameAs exists and has an object uri of res1
	 * 		   false - otherwise
	 */
	public static boolean hasSameAs(ResultSet set_1, String res1) {
		System.out.println("Checking owl:sameAs ...");
		 System.out.println("res1: " + res1);
		while (set_1.hasNext()) {
		    QuerySolution soln = set_1.nextSolution() ;
		    RDFNode res2 = soln.get("o");
		    if (res1.equals(res2.toString())) {		    	
		    	return true;
		    }
		}
		return false;	
	}//end hasSameAs()	
	
	/***
	 * @return true - if there is an entry in the alignment for the construct URIs
	 */
	private com.hp.hpl.jena.query.Query equivalentASKQueryAlignment(String entity1URI, String entity2URI, double threshold) {
		 String queryString =  this.getNSPrefixes() + 	
	        		" ASK " +
	        		" WHERE { " +
	        		"   ?s	align2:entity1 <" + entity1URI + "> ;" + 
	        		"   	align2:entity2 <" + entity2URI + "> ;" +
	        		"   	align2:relation ?relation ;" +	
	        		"   	align2:measure  ?score ." +	        		
	        		"   FILTER ( ?relation = \"=\" && ?score > " + threshold + " ) " +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()
}//end Class