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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;

@Scope("prototype")
@Service
public class SubsumptionSemanticMatrix extends HierarchySemanticMatrix {
	
	private static Logger logger = Logger.getLogger(SubsumptionSemanticMatrix.class);
	
	/*Control parameter, on whether to use a reasoner or not*/
	private boolean usingInferencing;
	/*Hold a reference to the SDBStore that holds the RDFS vocabularies*/
	private SDBStoreServiceImpl sdbStore;
	
	/*Constructors*/
	public SubsumptionSemanticMatrix() {	
		logger.debug("in SubsumptionSemanticMatrix");
		usingInferencing = false;
		sdbStore = this.getSDBStoreService();
		/*Attach a URI agent on this matrix responsible for dereferencing*/
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("SubsumptionSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.SUBSUMPTION);
		this.setPrecedenceLevel(3);
	}
	
	public SubsumptionSemanticMatrix(SDBStoreServiceImpl store) {
		super(store);
		sdbStore = this.getSDBStoreService();
		usingInferencing = false;
		dereferenceURIAgentService = new DereferenceURIAgentServiceImpl(sdbStore);
		this.setSemMatrixName("SubsumptionSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.SUBSUMPTION);
		this.setPrecedenceLevel(3);
	}
	
	public SubsumptionSemanticMatrix(SDBStoreServiceImpl store, DereferenceURIAgentServiceImpl agent) {
		super(store);
		sdbStore = this.getSDBStoreService();
		usingInferencing = false;
		dereferenceURIAgentService = agent;
		this.setSemMatrixName("SubsumptionSemanticMatrix");
		this.setSemMatrixType(SemanticMatrixType.SUBSUMPTION);
		this.setPrecedenceLevel(3);
	}
	
	/**
	 * This method is responsible to generate the semantic matrix that organises rdfs:subClassOf 
	 * patterns from namespaces into a two dimensional structure.
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
		matrix.setType(SemanticMatrixType.SUBSUMPTION);
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
						SemanticMatrixEntry entry = this.findDecendantsFromAlignment(construct1, construct2, alignGraphModel);
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
					SemanticMatrixEntry entry = this.findDecendants(construct1, construct2);
					logger.debug("Entry is: " + entry);
					ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
					columnList.add(colIndex, entry);
				}//end inner for
			}//end for	
		}//end else
		
		return matrix;
	}//generateSemanticMatrix()	
	
	/**
	 * Find subClassOf relations from SuperAbstracts that are owl:Classes 
	 *
	 */
	public SemanticMatrixEntry findDecendantsFromAlignment(CanonicalModelConstruct construct1,
	 		  											   CanonicalModelConstruct construct2,  Model alignGraphModel) {
		logger.debug("in findDecendantsFromAlignment()");
		
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
				
			//Case 1: Determine if classA lessThan classB (classA < classB)	
			Query query = decendantsASKQueryAlignment(construct1URI, construct2URI, "<", threshold);
			QueryExecution qexec = QueryExecutionFactory.create(query, alignGraphModel);
			boolean lessThan = qexec.execAsk();
			
			try {
				if (lessThan) {
					entry = new SemanticMatrixEntry(construct1, construct2);
					entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
					entry.addCellValueToList(BooleanVariables.CSR);
					entry.setDirSymbol("&lt;");	
				}//end if					
			} finally {	qexec.close(); }
			
			//Case 2: Determine if classA greaterThan classB (classA > classB)
			if (!lessThan) {
				query = decendantsASKQueryAlignment(construct1URI, construct2URI, ">", threshold);
				qexec = QueryExecutionFactory.create(query, alignGraphModel);
				boolean greaterThan = qexec.execAsk();
		
				try {
					if (greaterThan) {
						entry = new SemanticMatrixEntry(construct1, construct2);
						entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
						entry.addCellValueToList(BooleanVariables.CSR);
						entry.setDirSymbol("&gt;");	
					}//end if					
				} finally {	qexec.close(); }				
			}		
		} else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
			//TODO: Reading ontology Class hierarchy only, in the future consider properties are well
		}//end if		
		
		return entry;		
	}//end findDecendantsFromAlignment()	
	
	/**
	 *  1 - for two SA have a subsumption relation
	 *  2 - two SP have a subsumption relation
	 * 
	 */
	public SemanticMatrixEntry findDecendants(CanonicalModelConstruct construct1,
			 						 		  CanonicalModelConstruct construct2) {
		logger.debug("in findDecendants()");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);		
		SemanticMatrixEntry entry = null;
		Model baseModel_1 = null;
		Model baseModel_2 = null;
		String construct1URI = null;
		String construct2URI = null;
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;			
		
		/*Call super class method to find the property of this construct that hold its URI*/
		construct1URI = this.getConstructPropURI(construct1).getValue();			
		construct2URI = this.getConstructPropURI(construct2).getValue();
		logger.debug("construct1URI: " + construct1URI);
		logger.debug("construct2URI: " + construct2URI);		
		
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
			/*Find the NamedGraph of the first constructURI, if exists then proceed*/
			baseModel_1 = this.subjectURIexistsDatasetSELECT(construct1URI);
			//logger.debug("baseModel_1: " + baseModel_1);
			if (baseModel_1 != null) {
				baseModel_2 = this.subjectURIexistsDatasetSELECT(construct2URI);
				//logger.debug("construct2model: " + baseModel_2);
				if (baseModel_2 != null) {
					//Using a Reasoner
					if (usingInferencing) {
						logger.debug("Using inferencing mode: " + usingInferencing);
						/*Create the Ontology models to do inferencing*/
						OntModel ontModel_1 = sdbStore.getOntModel(baseModel_1);
						OntModel ontModel_2 = sdbStore.getOntModel(baseModel_2);
						logger.debug("ontModel_1: " + ontModel_1);
						logger.debug("ontModel_2: " + ontModel_2);
					
						if ((ontModel_1 != null) && (ontModel_2 != null)) {
						
							/*Get the direct subClasses of - Construct 1*/
							OntClass class1 = ontModel_1.getOntClass(construct1URI);
							//if (class1.hasSubClass(cls));
														
							//TODO: Complete this method, using hasSubclass methods etc. 
							/*Get the direct subClasses of - Construct 2*/
							//See which one has the other's uri in its list and create the new cell SemanticMatrixEntry 
						
							/* NOTE: In case I cannot do inferencing in SDB consider changing it to TDB in the filesystem 
							this will require a change to the architecture because at the moment I store RDF graphs into
							a persistent storage. Instead I will store all RDF graphs into a TDB database in the filesystem
							need to consider this since it will increase the speed of reading the files and eliminating the 
							need for a connection to a persistent storage. 
							 */
							
							/* NOTE: 
							I need to consider using an OntModel instead of the Base Model and have all the owl:import requests ignored 
							or timeout when an owl:import URI is not there. Another solution could be to use the advice from the mailing
							list and make the model to search the existing SDB store for the imports instead of dereferencing them.  
							 */
							
						}						
					} else {
						//If option to use a reasoner is not enabled
						logger.debug("Using inferencing mode: " + usingInferencing);
						
						ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.subClassOf);
						ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.subClassOf);
						
						boolean cons1SubClassOfconst2 = this.isSubsumed(set_1, construct2URI);
						boolean cons2SubClassOfconst1 = this.isSubsumed(set_2, construct1URI);

						if (cons1SubClassOfconst2 != cons2SubClassOfconst1) {
							
							if (cons1SubClassOfconst2) {
								entry = new SemanticMatrixEntry(construct1, construct2);
								entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
								entry.addCellValueToList(BooleanVariables.CSR);
								entry.setDirSymbol("&gt;");
							} else if (cons2SubClassOfconst1) {
								entry = new SemanticMatrixEntry(construct2, construct1);							
								entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
								entry.addCellValueToList(BooleanVariables.CSR);
								entry.setDirSymbol("&lt;");						
							}							
						} else {
							//if both true then equivalent classes
							//what happens if both of them are false, //NOTE: fix this
							logger.debug("Skip");
						}						
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
						
						ResultSet set_1 = this.getResultSetForSubjectURIandPredicate(baseModel_1, construct1URI, RDFS.subPropertyOf);
						ResultSet set_2 = this.getResultSetForSubjectURIandPredicate(baseModel_2, construct2URI, RDFS.subPropertyOf);
						
						boolean cons1SubPropOfconst2 = this.isSubsumed(set_1, construct2URI);
						boolean cons2SubPropOfconst1 = this.isSubsumed(set_2, construct1URI);
						
						if (cons1SubPropOfconst2 != cons2SubPropOfconst1) {
							
							if (cons1SubPropOfconst2) {
								entry = new SemanticMatrixEntry(construct1, construct2);
								entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
								entry.addCellValueToList(BooleanVariables.PSR);
								entry.setDirSymbol("&gt;");								
							} else if (cons2SubPropOfconst1) {
								entry = new SemanticMatrixEntry(construct2, construct1);
								entry.setTypeOfEntry(SemanticMatrixType.SUBSUMPTION);
								entry.addCellValueToList(BooleanVariables.PSR);
								entry.setDirSymbol("&lt;"); 
							}							
						} else {
							//if both true then equivalent classes
							//what happens if both of them are false, //NOTE: fix this
							logger.debug("Skip");
						}						
					}//end if not inference					
				}//end inner if
			}//end if	
		
		}//end if both constructs are SuperLexical		
		return entry;
	}//end findDirectDecendants()
	
	/**
	 * Check for subclass/subproperty without a reasoner.
	 * 
	 * @param set_1 - the result set of the construct to search whether is a subclass of the resource
	 * @param res1 - the resource to check
	 * @return true - if the construct is a subClassOf/ subPropertyOf the resource, false otherwise 
	 */
	public boolean isSubsumed(ResultSet set, String constructURI) {
		logger.debug("Checking subClassOf/subPropertyOf...");
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
	 * @return true - if there is an entry in the alignment for the construct URIs
	 * 
	 * @param classA lessThan classB (indicated by <)
	 * @param classA greaterThan classB (indicated by >)
	 */
	private com.hp.hpl.jena.query.Query decendantsASKQueryAlignment(String entity1URI, String entity2URI, String relation , double threshold) {
		 String queryString =  this.getNSPrefixes() + 	
	        		" ASK " +
	        		" WHERE { " +
	        		"   ?s	align2:entity1 <" + entity1URI + "> ;" + 
	        		"   	align2:entity2 <" + entity2URI + "> ;" +
	        		"   	align2:relation ?relation ;" +	
	        		"   	align2:measure  ?score ." +	        		
	        		"   FILTER ( ?relation = \"" + relation +"\" && ?score > " + threshold + " ) " +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()	
}//end SubsumptionSemanticMatrix
