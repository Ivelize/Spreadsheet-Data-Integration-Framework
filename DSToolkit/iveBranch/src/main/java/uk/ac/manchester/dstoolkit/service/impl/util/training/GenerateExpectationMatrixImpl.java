package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ExpectationMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixType;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.ClassesAlign;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.PredicatesAlign;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.util.importexport.ExpMatrix.ImportExpMatrixService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.GenerateExpectationMatrix;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/***
 * This class provides a Service that is responsible for generating the Expectation Matrix for the two Schemas
 * by looking at the two schemas and their actual Alignment from an XML file. The XML document holds the 
 * actual alignment between the two schemas.  
 * 
 * @author Klitos Christodoulou
 */
//@Service(value = "generateExpectationMatrix")
public class GenerateExpectationMatrixImpl implements GenerateExpectationMatrix {

	static Logger logger = Logger.getLogger(GenerateExpectationMatrixImpl.class);
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	//Parameters for the constructing an Expectation matrix from a Jena Model 
	private TDBStoreServiceImpl tdbStore;
	
	/*Hold the semantic matrix for this GT*/
	private SemanticMatrix matrixExp = null;
	
	/*Attach service that reads AlignXML file to this service*/
	ImportExpMatrixService importExpMatrix = null;
	
	//Constructor - Generate Expectation Matrix from XML file 
	public GenerateExpectationMatrixImpl(ImportExpMatrixService importExpMatrix){
		this.importExpMatrix = importExpMatrix;
	}
	
	//Constructor - Generate Expectation Matrix from a Jena Model
	public GenerateExpectationMatrixImpl(TDBStoreServiceImpl tdb){
		this.tdbStore = tdb;
	}
	
	//Use this method to construct an Expectation Matrix from a given Alignment
	public SemanticMatrix generateExpectationMatrixFromModel(List<CanonicalModelConstruct> sourceConstructs,
															List<CanonicalModelConstruct> targetConstructs,
															String expModelURI) {
		logger.debug("in generateExpectationMatrixFromModel");
		SemanticMatrix matrix = null;	
		
		if (sourceConstructs != null && targetConstructs != null && tdbStore != null) {
			Dataset dataset = this.tdbStore.getDataset();
			dataset.begin(ReadWrite.READ);	
			
			try {
				int rows = sourceConstructs.size();
				int columns = targetConstructs.size();
				
				//Look the given alignment to find any semantic annotations
				Model expMatrixModel = this.tdbStore.getModel(expModelURI);
			
				/*The following matrix will be used to hold the Ground Truth*/
				matrix = new SemanticMatrix(rows, columns);
				matrix.setType(SemanticMatrixType.EXPECTATION_MATRIX);
			
				for (CanonicalModelConstruct sourceConstruct : sourceConstructs) {		
					for (CanonicalModelConstruct targetConstruct : targetConstructs) {
						int rowIndex = sourceConstructs.indexOf(sourceConstruct);
						int colIndex = targetConstructs.indexOf(targetConstruct);
					
						SemanticMatrixEntry entry = this.findAlignmentFromModel(sourceConstruct, targetConstruct, expMatrixModel);
					
						ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
						columnList.add(colIndex, entry);						
					}//end for
				}//end for

			} finally { 
				dataset.end();
			}		
		}//end if		
		
		if (matrix != null) {
			matrixExp = matrix;
		}
		
		return matrix;		
	}//end generateExpectationMatrixFromModel()	
	
	/***
	 * This method is responsible for Generating Ground Truth for training the Adj. functions
	 * 
	 *	//TODO: When integrate schema inference algorithm with DSToolkit I may need to use more variables than
	 *	//localname to make the comparison here, but for now this is fine.
	 */
	public SemanticMatrix generateExpectationMatrix(List<CanonicalModelConstruct> sourceConstructs, List<CanonicalModelConstruct> targetConstructs) {
		logger.debug("in generateExpectationMatrix");
		SemanticMatrix matrix = null;		
				
		if (sourceConstructs != null && targetConstructs != null && this.importExpMatrix != null) {						
			int rows = sourceConstructs.size();
			int columns = targetConstructs.size();
			//logger.info("sourceConstructs [rows]: " + rows);
			//logger.info("targetConstructs [columns]: " + columns);		
			/*The following matrix will be used to hold the Ground Truth*/
			matrix = new SemanticMatrix(rows, columns);
			matrix.setType(SemanticMatrixType.EXPECTATION_MATRIX);
			
			for (CanonicalModelConstruct sourceConstruct : sourceConstructs) {		
				for (CanonicalModelConstruct targetConstruct : targetConstructs) {
					int rowIndex = sourceConstructs.indexOf(sourceConstruct);
					int colIndex = targetConstructs.indexOf(targetConstruct);
					//logger.info("position [row][column]: [" + rowIndex + ", " + colIndex + "]");
					/*Get an entry with the GT*/
					ExpectationMatrixEntry entry = this.findAlignment(sourceConstruct, targetConstruct);
					//logger.debug("Entry is: " + entry);
					ArrayList<SemanticMatrixEntry> columnList = matrix.getRow(rowIndex);
					columnList.add(colIndex, entry);
				}//end inner for
			}//end for			
			
		}//end if		
		
		if (matrix != null) {
			matrixExp = matrix;
		}
		
		return matrix;
	}//end generateGroundTruth()	
		
	/***
	 * If there is an Alignment in the Jena Model
	 * @param construct1
	 * @param construct2
	 * @param alignGraphModel - a Jena Model that has the alignment in, need to read the alignment first
	 */
	public ExpectationMatrixEntry findAlignmentFromModel(CanonicalModelConstruct construct1,
														CanonicalModelConstruct construct2,
														Model expMatrixModel) {
		ExpectationMatrixEntry entry = null;
	
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
			
			//Create a query over the Model that holds the Expectation Matrix as Triples
			Query query = discoverAlignmentSPARQL(construct1URI, construct2URI);				
			QueryExecution qexec = QueryExecutionFactory.create(query, expMatrixModel);
			ResultSet rs = qexec.execSelect();

			//It should only exists one alignment in the Expectation Matrix
			try {
				if (rs.hasNext()) {
					QuerySolution soln = rs.nextSolution() ;
					double synScore = soln.getLiteral("score").getDouble();
					String evidence = soln.getLiteral("evidence").getString();
					
					List<BooleanVariables> evidenceAsList = getEvidence(evidence);
				
					entry = new ExpectationMatrixEntry((float) synScore, evidenceAsList);
				}//end try	
			} finally { qexec.close() ; }			
		}//end if	
		
		return entry;	
	}//end findAlignmentFromModel()
	
	
	/***
	 * If there exists an Alignment in the xml file then create a new SemanticMatrixEntryGT to encapsulate the details.
	 * @param construct1
	 * @param construct2
	 * @return a cell entry object
	 */
	public ExpectationMatrixEntry findAlignment(CanonicalModelConstruct construct1,
		    									CanonicalModelConstruct construct2) {
		
		//logger.debug("in findAlignment()");
		//logger.debug("construct1: " + construct1);
		//logger.debug("construct2: " + construct2);
		
		ExpectationMatrixEntry entry = null;
		CanonicalModelProperty uri1Prop = null;
		CanonicalModelProperty uri2Prop = null;
		String construct1URI  = null;
		String construct2URI  = null;	
		
		//Cases to SKIP
		if (((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperLexical)) ||
			 ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperAbstract)) )
			return null;
		
		/*Get the property that holds the URI for this property*/
		uri1Prop = this.getConstructPropURI(construct1);
		uri2Prop = this.getConstructPropURI(construct2);
		
		if (uri1Prop == null) {
			construct1URI = "idk";
		} else {
			construct1URI = uri1Prop.getValue();
		}
		
		if (uri2Prop == null) {
			construct2URI = "idk";
		} else {
			construct2URI = uri2Prop.getValue();
		}
		
		/*1. Check whether constructs are both SuperAbtsracts*/
		if ((construct1 instanceof SuperAbstract) && (construct2 instanceof SuperAbstract)) {
    		//logger.debug("construct1URI: " + construct1URI);
			//logger.debug("construct2URI: " + construct2URI);
			/*Transform the URIs of the SAs into Resource so you can compare them*/
			Resource res1 = this.fromStringToResource(construct1URI);
			Resource res2 = this.fromStringToResource(construct2URI);
    		//logger.debug("res1URI: " + res1.getURI());
			//logger.debug("res2URI: " + res2.getURI());
			
			/*Call the method from the service to find a true Alignment*/
			ClassesAlign mapFound = importExpMatrix.findClassAlign(res1, res2);
			if (mapFound != null) {
				/*An alignment has been found*/
				entry = new ExpectationMatrixEntry(mapFound);
				/*Return a cell entry with a ClassesAlign object attached to it*/
				return entry;
			}//end if			
		} else if ((construct1 instanceof SuperLexical) && (construct2 instanceof SuperLexical)) {
    		//logger.debug("construct1URI: " + construct1URI);
			//logger.debug("construct2URI: " + construct2URI);
			/*Transform the URIs of the SLs into Resource so you can compare them*/
			Resource res1 = this.fromStringToResource(construct1URI);
			Resource res2 = this.fromStringToResource(construct2URI);			
			
			/*To make sure that we refer to the correct mapping get the SA for each superlexical*/
			SuperAbstract construct1SA = ((SuperLexical) construct1).getParentSuperAbstract();
			SuperAbstract construct2SA = ((SuperLexical) construct2).getParentSuperAbstract();
			
			Resource res1SA = this.fromSAToResource(construct1SA);
			Resource res2SA = this.fromSAToResource(construct2SA);
			
			PredicatesAlign mapFound = importExpMatrix.findPredAlign(res1, res2, res1SA, res2SA);
			//logger.debug("predicate mapFound: " + mapFound);
			
			if (mapFound != null) {
				/*An alignment has been found*/
				entry = new ExpectationMatrixEntry(mapFound);
				/*Return a cell entry with a PredicatesAlign object attached to it*/				
				return entry;
			}//end inner if			
		}//end if		
		return entry;		
	}//end findAlignment()	
	
	/***
	 * URIs in RDF are case of sensitive.
	 * @param URI
	 * @return a Resource object from this URI
	 */
	public Resource fromStringToResource(String uri) {
		Resource res = ResourceFactory.createResource(uri);	
		return res;		
	}//end fromStringToResource()
	
	/**
	 * Get a Resource object out of this SA
	 * @param uri 
	 * @return Resource
	 */
	public Resource fromSAToResource(SuperAbstract constructSA) {
		CanonicalModelProperty uriProp = this.getConstructPropURI(constructSA);
		String constructURI = uriProp.getValue();
		Resource res = ResourceFactory.createResource(constructURI);	
		return res;		
	}//end fromStringToResource()	
		
	/***
	 * Return a CanonicalModelProperty that holds the URI of this Construct.
	 * @param construct
	 * @return CanonicalModelProperty
	 */
	public CanonicalModelProperty getConstructPropURI(CanonicalModelConstruct construct) {
		CanonicalModelProperty constructPropURI = null;
		if (construct == null) {
			return constructPropURI;
		}		
		
		if (construct instanceof SuperAbstract) {
			//logger.info("Construct is SA : " + construct.getName());
			constructPropURI = construct.getProperty("rdfTypeValue");
		} else {
			constructPropURI = construct.getProperty("constructURI");
		}		
		return constructPropURI;
	}//end getConstructPropURI()
	
	/**
	 * Method: that returns the URI of the construct along with the namespace
	 */
	public String getConstructURIns(CanonicalModelConstruct construct) {
		CanonicalModelProperty constructPropURI = null;
		StringBuilder uri = null;
	
		if (construct instanceof SuperAbstract) {
			constructPropURI = construct.getProperty("namespaceURI");
		}
		
		uri = new StringBuilder(constructPropURI.getValue()).append(construct.getName());	
	
		return uri.toString();
	}//end getConstructURIns()
	
	/***
	 * @return - a set of BooleanVariables as a List<BooleanVariables>
	 */
	private List<BooleanVariables> getEvidence(String evid) {
		List<BooleanVariables> evidence = new ArrayList<BooleanVariables>(); 
		
		//Split string into an array
		String [] arrayOfEvidence = evid.split(",");
		
		for (int i=0; i<arrayOfEvidence.length; i++) {
			evidence.add(BooleanVariables.fromValue(arrayOfEvidence[i].trim()));
		}//end for		
	
		return evidence;	
	}//end getEvidence()	
	
	/**
	 * @return - Common used Namespace Prefixes
	 */
	private String getNSPrefixes() {		
		String prefixes = 
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
    	        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
    	        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
    	        "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> " +
    	        "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> " +
    	        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
    	        "PREFIX void: <http://vocab.deri.ie/void#> " +
    	        "PREFIX j.0: <x-ns://train.metadata/#> " +
    	        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
    	        "PREFIX omv: <http://omv.ontoware.org/2007/05/mappingomv#> " +
    	        "PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment> " +
    	        "PREFIX align2: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> ";		
		return prefixes;
	}//end getNSPrefixes()
	
	/**
	 * Returns a ResultSet will all the classes that this class is a subClassOf
	 */
	private com.hp.hpl.jena.query.Query discoverAlignmentSPARQL(String entityURI1, String entityURI2) {
		 String queryString =  this.getNSPrefixes() + 	
	        		" SELECT ?score ?evidence " +
	        		" WHERE { " +
	        		"   ?s	align:entity1 <" + entityURI1 + "> ;" + 
	        		"   	align:entity2 <" + entityURI2 + "> ;" +
	        		"		omv:hasEvidence ?evidence ;" +
	        		"   	align:measure  ?score ." +	        		
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()
}//end class