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

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.vocabulary.RDFS;

@Scope("prototype")
@Service
public class ShareSuperClassSemMatrix extends HierarchySemanticMatrix {

	private static Logger logger = Logger.getLogger(ShareSuperClassSemMatrix.class);
	
	/*Control parameter, on whether to use a reasoner or not*/
	private boolean usingInferencing;
	/*Hold a reference to the SDBStore that holds the RDFS vocabularies*/
	private SDBStoreServiceImpl sdbStore;
	
	/*Constructors*/
	public ShareSuperClassSemMatrix() {	
		logger.debug("in ShareSuperClassSemMatrix");
		sdbStore = this.getSDBStoreService();	
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("ShareSuperClassSemMatrix");
		this.setSemMatrixType(SemanticMatrixType.SHARE_SUPERCLASS);
		this.setPrecedenceLevel(2);
	}
	
	public ShareSuperClassSemMatrix(SDBStoreServiceImpl store) {
		super(store);
		sdbStore = this.getSDBStoreService();
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("ShareSuperClassSemMatrix");
		this.setSemMatrixType(SemanticMatrixType.SHARE_SUPERCLASS);
		this.setPrecedenceLevel(2);
	}
	
	public ShareSuperClassSemMatrix(SDBStoreServiceImpl store, DereferenceURIAgentServiceImpl agent) {
		super(store);
		sdbStore = this.getSDBStoreService();
		dereferenceURIAgentService = agent;
		this.setSemMatrixName("ShareSuperClassSemMatrix");
		this.setSemMatrixType(SemanticMatrixType.SHARE_SUPERCLASS);
		this.setPrecedenceLevel(2);
	}

	/**
	 * This method is responsible to generate the semantic matrix that organises information from namespaces
	 * on whether two Classes share the same super-class and organises such info into a two dimensional structure.
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
		matrix.setType(SemanticMatrixType.SHARE_SUPERCLASS);	
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
						SemanticMatrixEntry entry = this.findSuperClassesFromAlignment(construct1, construct2, alignGraphModel);
						//logger.debug("Entry is: " + entry);
						ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
						columnList.add(colIndex, entry);	
					}//end inner for
				}//end for				
			} finally { 
				dataset.end();
			}		
		} else {	
			for (CanonicalModelConstruct construct1 : sourceConstructs) {
				for (CanonicalModelConstruct construct2 : targetConstructs) {
					/*Get the [row][column] position to add the cell entry to*/
					int rowIndex = sourceConstructs.indexOf(construct1);
					int colIndex = targetConstructs.indexOf(construct2);
					logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
					/*Call method to find the pattern [without inference - only direct descendants]*/
					//NOTE: consider the use of a reasoner
					SemanticMatrixEntry entry = this.findSuperClasses(construct1, construct2);
					logger.debug("Entry is: " + entry);
					ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
					columnList.add(colIndex, entry);
				}//end inner for
			}//end for	
		}//end else
		
		return matrix;		
	}//end generateSemanticMatrix()
	
	
	/**
	 * This method discovers equivalence relations from a GIVEN ALIGNMENT
	 */
	public SemanticMatrixEntry findSuperClassesFromAlignment(CanonicalModelConstruct construct1,
											   				 CanonicalModelConstruct construct2, Model alignGraphModel) {	
		logger.debug("in findSuperClassesFromAlignment()");
	
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
			
			if (!construct1URI.equals(construct2URI)) {		
					
				Query query1 = discoverSuperClassesSPARQL(construct1URI, threshold);				
				QueryExecution qexec1 = QueryExecutionFactory.create(query1, alignGraphModel);
									
				Query query2 = discoverSuperClassesSPARQL(construct2URI, threshold);				
				QueryExecution qexec2 = QueryExecutionFactory.create(query2, alignGraphModel);
				
				ResultSet set_1 = qexec1.execSelect();
				ResultSet set_2 = qexec2.execSelect();				
				try {
					boolean shareSuperClass = this.shareSuperClass(set_1, set_2);
									
					if (shareSuperClass) {
						entry = new SemanticMatrixEntry(construct1, construct2);
						entry.setTypeOfEntry(SemanticMatrixType.SHARE_SUPERCLASS);
						entry.addCellValueToList(BooleanVariables.CSP);						
					}//end if					
				} finally {				
					qexec1.close();
					qexec2.close();					
				}				
			}//end if			
		}//end if
		
		return entry;	
	}//end findSuperClassesFromAlignment()	
	
	/**
	 * This method will find direct superClasses of the constructs and check whether the constructs share the same
	 * direct super-class.
	 * 
	 * @param construct1 - source schema construct
	 * @param construct2 - target schema construct
	 * @return - a SemanticMatrixEntry that holds information on whether the constructs share the same super-class.
	 */
	public SemanticMatrixEntry findSuperClasses(CanonicalModelConstruct construct1,
	 		  									CanonicalModelConstruct construct2) {
		logger.debug("in findSuperClasses()");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);	

		SemanticMatrixEntry entry = null;
		Model baseModel_1 = null;
		Model baseModel_2 = null;
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;
		
		/*Call super class method to find the property of this construct that hold its URI*/
		String construct1URI = this.getConstructPropURI(construct1).getValue();			
		String construct2URI = this.getConstructPropURI(construct2).getValue();
		logger.debug("construct1URI: " + construct1URI);
		logger.debug("construct2URI: " + construct2URI);
		
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
			/*Find the NamedGraph of the first constructURI, if exists then proceed*/
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);
			//TODO - complete this method
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				//logger.debug("construct2model: " + baseModel_2);
				if (baseModel_2 !=null) {
					//Using a Reasoner
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						/*Create the Ontology models to do inferencing*/
						OntModel ontModel_1 = sdbStore.getOntModel(baseModel_1);
						OntModel ontModel_2 = sdbStore.getOntModel(baseModel_2);
					
						if ((ontModel_1 != null) && (ontModel_2 != null)) {

							/*Get the direct superClassOf - Construct 1*/
							OntClass class_1 = ontModel_1.getOntClass(construct1URI);
							logger.debug("class1: " + class_1);
							
							OntClass superClassOf_1 = class_1.getSuperClass();
							logger.debug("superClassOf_1: " + superClassOf_1);
			
							/*Get the direct superClassOf - Construct 2*/
							OntClass class_2 = ontModel_2.getOntClass(construct1URI);
							logger.debug("class2: " + class_2);
							
							OntClass superClassOf_2 = class_2.getSuperClass();
							logger.debug("superClassOf_2: " + superClassOf_2);							
							
							//TODO: test the reasoner later, for now just do it manually
							
							//if (class1.hasSubClass(cls));
														
							//TODO: Complete this method, using hasSubclass methods etc. 
							/*Get the direct subClasses of - Construct 2*/
							//See which one has the other's uri in its list and create the new cell SemanticMatrixEntry 
						}						
					} else {
						//Without a Reasoner
						logger.debug("Using inferencing mode: " + usingInferencing);
												
						/*If Classes have the same URI do not search for same super parent*/
						if (!construct1URI.equals(construct2URI)) {						
							ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.subClassOf);
							ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.subClassOf);						
						
							boolean shareSuperClass = this.shareSuperClass(set_1, set_2);
						
							if (shareSuperClass) {
								entry = new SemanticMatrixEntry(construct1, construct2);
								entry.setTypeOfEntry(SemanticMatrixType.SHARE_SUPERCLASS);
								entry.addCellValueToList(BooleanVariables.CSP);						
							}//end if
						}//end if
					}//end else 
				}//end inner if			
			}//end if			
		} else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
		  /*2. Check whether constructs are both SuperLexicals*/
			/*Find the NamedGraph of the first constructURI, if exists then proceed*/
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);
			
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				//logger.debug("construct2model: " + baseModel_2);
				if (baseModel_2 != null) {
					//If option to use a reasoner is enabled
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						//NOTE: This functionality is not complete						
					} else {
						logger.debug("Using inferencing mode: " + usingInferencing);
						
						/*If Predicates have the same URI do not search for same super parent property*/
						if (!construct1URI.equals(construct2URI)) {							
							ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.subPropertyOf);
							ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.subPropertyOf);	
							
							boolean shareSuperClass = this.shareSuperClass(set_1, set_2);
							
							if (shareSuperClass) {
								entry = new SemanticMatrixEntry(construct1, construct2);
								entry.setTypeOfEntry(SemanticMatrixType.SHARE_SUPERCLASS);
								entry.addCellValueToList(BooleanVariables.PSP);				
							}//end if							
						}//end if			
					}//end if not inference
				}//end if
			}//end inner if
		}//end if
		return entry;
	}//end findSuperClasses		
	
	/**
	 * This method searches whether two constructs share the same direct super-class by looking their subClassOf predicates.
	 * It does not use any reasoning.
	 * 
	 * @param set_1 - all rdfs:subClassOf/ rdfs:subPropertyOf statements of this construct
	 * @param set_2 - all rdfs:subClassOf/ rdfs:subPropertyOf statements of this construct
	 * @return true - if constructs share the same superclass, false otherwise.
	 * 
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
	 * Returns a ResultSet will all the classes that this class is a subClassOf
	 */
	private com.hp.hpl.jena.query.Query discoverSuperClassesSPARQL(String entityURI, double threshold) {
		 String queryString =  this.getNSPrefixes() + 	
	        		" SELECT ?superClass " +
	        		" WHERE { " +
	        		"   ?s	align2:entity1 <" + entityURI + "> ;" + 
	        		"   	align2:entity2  ?superClass ;" +
	        		"   	align2:relation ?relation ;" +	
	        		"   	align2:measure  ?score ." +	        		
	        		"   FILTER ( ?relation = \"<\" && ?score > " + threshold + " ) " +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()
	
}//end SameSuperClassSemMatrix
