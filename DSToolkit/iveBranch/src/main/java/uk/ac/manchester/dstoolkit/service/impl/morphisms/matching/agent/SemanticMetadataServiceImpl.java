package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema.DereferenceURIAgentServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.DereferenceURIAgentService;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/***
 * This is the top class in the hierarchy, it needs to implement methods registered in the Interface SemanticMetadataService.
 * The subclasses that extent this Class will override some of the methods of this class or have their on variations of 
 * implementation. This is an Abstract class and therefore it cannot be instantiated.
 * 
 *  			          SemanticMetadataServiceImpl(Abstract)
 *            ---------------------------|----------------------------------------------
 *            |						 	 |                   |					        |
 *   HierarchySemanticMatrix 	DomainSemanticMatrix	RangeSemanticMatrix		NameSpaceSemMatrix
 * 
 * @author klitos
 *
 */
public abstract class SemanticMetadataServiceImpl implements SemanticMetadataService {

	private static Logger logger = Logger.getLogger(SemanticMetadataServiceImpl.class);

	protected DereferenceURIAgentService dereferenceURIAgentService;
	
	private Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
	
	private List<SemanticMetadataService> childSemanticMatricesList = new ArrayList<SemanticMetadataService>();
	
	/*Hold the Type of this semantic-matrix*/
	private SemanticMatrixType semanticMatrixType;
	
	/*Hold a reference to the SDBStore that holds the RDFS vocabularies*/
	private SDBStoreServiceImpl sdbStoreService;
	
	/*Hold a reference to Jena TDB Store*/
	public TDBStoreServiceImpl tdbStore;
	
	/*Hold the level of precedence*/
	private PrecedenceLevel level;
	
	/*TDB Store model that holds the alignment*/
	public String alignGraphURI;
	public Model alignGraphModel;
	
	/*Control parameter, on whether to use a reasoner or not*/
	public boolean usingInferencing;
	
	/*Consider pair to be equivalent if above threshold*/
	public double threshold;
	
	/*BenchmarkType to indicate experiment mode*/
	public BenchmarkType benchmarkType;
	
	/*Set the index of this semantic-matrix*/
	private int index;
	/*Set the name of this semantic-matrix*/
	private String name;
	
	//---- Getter/Setter methods ----//
	public void setPrecedenceLevel(int l) {
		if (l == 1)
			this.level = PrecedenceLevel.FIRST;
		else if (l == 2)
			this.level = PrecedenceLevel.SECOND;
		else if (l == 3)
			this.level = PrecedenceLevel.THIRD;
		else if (l == 3)
			this.level = PrecedenceLevel.FORTH;
		else if (l == 3)
			this.level = PrecedenceLevel.FIFTH;
	}
	
	public PrecedenceLevel getPrecedenceLevel() {
		return this.level;
	}	
	
	public void setSemMatrixName(String name) {
		this.name = name;
	}
	
	public String getSemMatrixName() {
		return name;
	}
	
	public void setSemMatrixType(SemanticMatrixType type) {
		this.semanticMatrixType = type;
	}

	public SemanticMatrixType getSemMatrixType() {
		return semanticMatrixType;
	}	
	
	public SDBStoreServiceImpl getSDBStoreService() {
		logger.info("sdbStore : " + sdbStoreService);
		return this.sdbStoreService;
	}
	
	public TDBStoreServiceImpl getTDBStoreService() {
		logger.info("tdbStore : " + tdbStore);
		return this.tdbStore;
	}
	
	public void setSDBStoreService(SDBStoreServiceImpl store) {
		logger.info("Set the store: " + store);
		this.sdbStoreService = store;
	}
	
	public void setURIAgentService(SDBStoreServiceImpl store) {
		logger.info("Set the store: " + store);
		this.sdbStoreService = store;
	}
	
	public void attachSemMatrix(SemanticMetadataService m) {
		this.childSemanticMatricesList.add(m);
	}
	
	public void attachTDBStoreService(TDBStoreServiceImpl store) {
		logger.info("Set tdbStore : " + store);
		this.tdbStore = store;
	}
	
	public List<SemanticMetadataService> getAttachedSemanticMatrices() {
		return Collections.unmodifiableList(childSemanticMatricesList);
	}	
	
	public void addControlParameter(ControlParameter controlParameter) {
		this.controlParameters.put(controlParameter.getName(), controlParameter);
	}
	
	public Map<ControlParameterType, ControlParameter> getControlParameters() {
		return Collections.unmodifiableMap(controlParameters);
	}
	
	/**
	 * @return String - List of common namespace prefixes to attach on queries
	 */
	public String getNSPrefixes() {		
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
    	        "PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment> " +
    	        "PREFIX align2: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> ";		
		return prefixes;
	}//end getNSPrefixes()	
	
	
	//---- Semantic Matrices Manipulation ----//
	/***
	 * Method that accumulates all the semantic evidences collected in a single matrix representation
	 * 
	 * @param sourceConstructs
	 * @param targetConstructs
	 * @param semMatrixCube
	 * @return
	 */
	public static SemanticMatrix concatenate(List<CanonicalModelConstruct> sourceConstructs,
			List<CanonicalModelConstruct> targetConstructs, List<SemanticMatrix> semMatrixCube) {		
		logger.debug("in concatenate()");
		logger.debug("size of sem cube: " + semMatrixCube.size());
		SemanticMatrix concatMatrix = new SemanticMatrix(sourceConstructs.size(), targetConstructs.size());
		concatMatrix.setType(SemanticMatrixType.CONCATENATE_MATRIX);
		
		for (CanonicalModelConstruct construct1 : sourceConstructs) {
			for (CanonicalModelConstruct construct2 : targetConstructs) {
				int rowIndex = sourceConstructs.indexOf(construct1);
				int colIndex = targetConstructs.indexOf(construct2);
				
				SemanticMatrixEntry cell = null;
		
				for (SemanticMatrix matrix : semMatrixCube) {
					 SemanticMatrixEntry tempCell = matrix.getCellSemanticEntry(rowIndex, colIndex);
					 
					 if (tempCell != null ) {
						 if (cell == null) {
							 cell = new SemanticMatrixEntry();
							 cell.setTypeOfEntry(SemanticMatrixType.CONCATENATE_MATRIX);
						 }
						
						 Set<BooleanVariables> bVars = tempCell.getCellValue();					 
						 cell.getCellValue().addAll(bVars);
					 }					 
				 }//end for			
			
				 /*Add cell to [row][column]*/
				 ArrayList<SemanticMatrixEntry> columnList = concatMatrix.getRow(rowIndex);
				 columnList.add(colIndex, cell);				
			}//end for
		}//end for
	
		return concatMatrix;
	}//end concatenate()	
	
	//---- Supportive Methods ----//
	
	/**
	 * Method that returns the property of a construct that holds its actual URI.
	 * 
	 * @return 	CanonicalModelProperty - the URI of this construct
	 */
	public CanonicalModelProperty getConstructPropURI(CanonicalModelConstruct construct) {
		CanonicalModelProperty constructPropURI = null;
		if (construct == null) {
			return constructPropURI;
		}		
		
		if (construct instanceof SuperAbstract) {
			logger.info("Construct is SA : " + construct.getName());
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
	
	/** 
	 * @param CanonicalModelConstruct
	 * @return a Resource object for this construct, this is a new Resource object and not the actual 
	 * resource from a Named Graph.
	 */
	public com.hp.hpl.jena.rdf.model.Resource getResourceFromConstruct(CanonicalModelConstruct construct) {
		CanonicalModelProperty uriProp = this.getConstructPropURI(construct);
		if (uriProp == null) {
			return null;
		}		
		String constructURI = uriProp.getValue();
		Resource res = ResourceFactory.createResource(constructURI);	
		return res;		
	}//end fromStringToResource()	
	
	/**
	 * @param String
	 * @return a Resource object for this construct, this is a new Resource object and not the actual 
	 * resource from a Named Graph.
	 */
	public com.hp.hpl.jena.rdf.model.Resource getResourceFromConstruct(String constructURI) {
		return ResourceFactory.createResource(constructURI);		
	}//end fromStringToResource()	
	
	/**
	 * Method that return a Resource object given a Model in sdb_metadata if exists. If the Resource does not
	 * exists in the model is now created so be careful.
	 */
	public com.hp.hpl.jena.rdf.model.Resource getResourceFromConstruct(Model model, String constructURI) {
		return model.getResource(constructURI);
	}
	
	/**
	 * Method that returns the namespace URI of this construct
	 * 
	 * @return 	CanonicalModelProperty - the namespace URI of this construct.
	 * 			Namespace URI is also used as the name of the namedgraphs in the SDBStore.
	 */
	public CanonicalModelProperty getConstructPropNS(CanonicalModelConstruct construct) {
		CanonicalModelProperty constructPropNS = null;
		if (construct == null) {
			return constructPropNS;
		}		
		constructPropNS = construct.getProperty("namespaceURI");
		return constructPropNS;
	}//end getConstructPropNS()	
	
	
	/**
	 * @see DereferenceURIAgentServiceImpl
	 * @param constructURI
	 * @return Model - The named graph model that contains this Resource as a Subject
	 *               - if Model == null means that it does not exists in any Named Graph in the Dataset
	 */
	public Model subjectURIexistsDatasetSELECT(String constructURI) {
		return dereferenceURIAgentService.subjectURIexistsDatasetSELECT(constructURI);		
	}//end subjectURIexistsASK()
	
	/**
	 * Using a SPARQL ASK query check whether the particular URI exists in the specific model provided.
	 * @param model
	 * @see DereferenceURIAgentServiceImpl
	 * @param constructURI
	 * return true - if the subject URI exists in the Named Graph, false otherwise.
	 */
	public boolean subjectURIexistsASK(Model model, String constructURI) {
		return dereferenceURIAgentService.subjectURIexistsASK(model, constructURI);		
	}//end subjectURIexistsASK()	
	
	/**
	 * Method that uses a SPARQL ASK query to check whether a particular Named Graph exists in the Dataset.
	 * @param graphName
	 * @see DereferenceURIAgentServiceImpl
	 * @return true - if the Graph exists, false otherwise.
	 */
	public boolean isGraphExistsASK(String graphName) {
		return dereferenceURIAgentService.isGraphExistsASK(graphName);
	}//end executeSPARQLSelectQuery()	
	
	/**
	 * Method to check whether a particular Named Graph exists in the Dataset (without using a SPARQL query).
	 * @param graphName
	 * @see DereferenceURIAgentServiceImpl
	 * @return true - if the Graph exists, false otherwise.
	 */
	public boolean isGraphExists(String graphName) {
		return dereferenceURIAgentService.isGraphExists(graphName);
	}//end isGraphExists()
	
	/**
	 * @see DereferenceURIAgentServiceImpl
	 */
	public ResultSet getResultSetForSubjectURIandPredicate(Model model, String constructURI, com.hp.hpl.jena.rdf.model.Property pred) {
		return dereferenceURIAgentService.getResultSetForSubjectURIandPredicate(model, constructURI, pred);
	}

	public String getAlignGraphURI() {
		return alignGraphURI;
	}

	public void setAlignGraphURI(String alignGraphURI) {
		this.alignGraphURI = alignGraphURI;
	}

	public Model getAlignGraphModel() {
		return alignGraphModel;
	}

	public void setAlignGraphModel(Model alignGraphModel) {
		this.alignGraphModel = alignGraphModel;
	}

	public boolean isUsingInferencing() {
		return usingInferencing;
	}

	public void setUsingInferencing(boolean usingInferencing) {
		this.usingInferencing = usingInferencing;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public BenchmarkType getBenchmarkType() {
		return benchmarkType;
	}

	public void setBenchmarkType(BenchmarkType benchmarkType) {
		this.benchmarkType = benchmarkType;
	}
}//end SemanticMetadataServiceImpl
