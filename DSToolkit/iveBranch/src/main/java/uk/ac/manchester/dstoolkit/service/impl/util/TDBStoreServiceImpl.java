package uk.ac.manchester.dstoolkit.service.impl.util;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ActionStatus;
import uk.ac.manchester.dstoolkit.service.util.TDBStoreService;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;
import com.hp.hpl.jena.tdb.sys.TDBInternal;
import com.hp.hpl.jena.util.FileManager;

/**
 * Service class responsible for creating a Jena TDB Store. To create an TDB Store you are required to
 * specify the directory that will hold the Store on disk. This class wraps the operations over a TDB
 * Store, like creating the TDB on disk, connect to a NamedModel or the default Graph, loading 
 * triples in the model etc.
 * 
 * Note: TDB version 0.9.3 has some bugs with removing named graphs
 * 
 * @author Klitos Christodoulou
 */
public class TDBStoreServiceImpl implements TDBStoreService  {

	static Logger logger = Logger.getLogger(TDBStoreServiceImpl.class);
	
	private String TDB_PATH = "";
	private Dataset dataset = null;
	
	/*Constructor - specify the directory that will hold the TDB Store*/
	public TDBStoreServiceImpl(String dirPath) {
		TDB_PATH = dirPath;		
		dataset = initTDB();
	}	

	/*Initialise the TDB store*/
	protected Dataset initTDB() {		 
	 	/*Check if the directory exists otherwise create it*/
	 	File dir = new File(TDB_PATH);
		 	
	 	if (!dir.exists()) {
	 		 new File(TDB_PATH).mkdirs();	
	 	}	 
	     
	    return TDBFactory.createDataset(TDB_PATH);
	}//end initTDB()	
	
	/**
	 * Method that returns a dataset that is backed by a TDB store object
	 * @return Dataset
	 */
	public Dataset getDataset() {
		if (dataset == null) {
			//create or connect to Dataset backed by TDB Store object
			dataset = TDBFactory.createDataset(TDB_PATH);
			return dataset;
		}	

		return dataset;			
	}//end getDataset()
	
	/**
	 * @return DatasetGraph - Get this dataset in Graph form
	 */
	public DatasetGraph getDatasetGraph() {
		Dataset ds = this.getDataset();  		
		return ds.asDatasetGraph();
	}//end getDatasetGraph()
	
	/**
	 * @return DatasetGraphTDB - Get this dataset as a DatasetGraphTDB
	 */
	public DatasetGraphTDB getDatasetGraphTDB() {
		Dataset ds = this.getDataset();		
		DatasetGraphTDB dsg = TDBInternal.getDatasetGraphTDB(ds);		
		return dsg;
	}	
	
	/**
	 * Method for connecting to a model in the TDB Store. A Dataset consists of the
	 * default graph and a set of named graphs if exist. 
	 * 
	 * @param modelName - null to connect to the default graph, or specify a uri to connect to a 
	 * 					  named Graph. If the model of the named graph does not exists Jena will
	 * 					  create the model and return it to us.
	 * @return Model
	 */
    public Model getModel(String modelName) {
		logger.debug("in getModel()");
    	Model model = null;    	
        if (modelName == null || modelName.equals("")) {        	
        	Dataset dataset = this.getDataset();        	
        	model = dataset.getDefaultModel();
        } else {
        	Dataset dataset = this.getDataset(); 
        	/*getNamedModel() - model is created if not exists*/
        	model = dataset.getNamedModel(modelName);  
        }
        return model;
    }//end getModel()
   
    
    /***
     * Using Jena OntoModel 
     * 
     * //TODO: At the moment this uses Jena's reasoner, if needed changes to Pellet.
     * 
     * @param modelName - URI of the Named Graph model to retrieve
     * @param ontoSpec  - configuration choices for the ontology model, including the language profile in use,
     * 					  the reasoner, and the means of handling compound documents
     * @return OntModel - return an OntModel
     */
    public OntModel getOntModel(String modelName, OntModelSpec ontoSpec) {
		logger.debug("in getOntModel()");
		OntModel ontModel = null;
		//Get the base Jena model
		Model baseModel = this.getModel(modelName);
		if (baseModel != null) {
			//OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			OntModelSpec modelSpec = null;
			if (ontoSpec == null) {	
				modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			} else {
				modelSpec = new OntModelSpec(ontoSpec);
			}		
			
			ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel);
			return ontModel;
		}
    	return null;
    }//end getOntModel()    
    
    /***
     * Using Jena OntoModel 
     * 
     * //TODO: At the moment this uses Jena's reasoner, if needed changes to Pellet.
     * 
     * @param baseModel - based model backed up by Jena TDB
     * @param ontoSpec  - configuration choices for the ontology model, including the language profile in use,
     * 					  the reasoner, and the means of handling compound documents
     * @return OntModel - return an OntModel
     */
    public OntModel getOntModel(Model baseModel, OntModelSpec ontoSpec, Reasoner reasoner) {
		logger.debug("in getOntModel()");
		OntModel ontModel = null;

		if (baseModel != null) {
			//OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			OntModelSpec modelSpec = null;
			if (ontoSpec == null) {	
				/***
				 * Create the base model for the asserted ontology axioms. Note that
				 * this model should not define any reasoning infrustructure. We just want
				 * a simple OntModel that will store the ontology (e.g. OWL_MEM).
				 */				
				modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			} else {
				modelSpec = new OntModelSpec(ontoSpec);
			}		
			
			if (reasoner != null) {
				modelSpec.setReasoner(reasoner);	
			}
			
			ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel);
			
			logger.debug("ontModel" + ontModel);
			
			return ontModel;
		}
    	return null;
    }//end getOntModel()     
    
    /***
     * 
     * @param baseModel - based model backed up by Jena TDB
     * @param ontoSpec  - configuration choices for the ontology model, including the language profile in use,
     * 					  the reasoner, and the means of handling compound documents
     * @param docMgr    - assists with the processing and handling of ontology documents
     * @return
     */
    public OntModel getOntModel(Model baseModel, OntModelSpec ontoSpec, OntDocumentManager docMgr, Reasoner reasoner) {
		logger.debug("in getOntModel()");
		OntModel ontModel = null;

		if (baseModel != null) {
			//OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			OntModelSpec modelSpec = null;
			if (ontoSpec == null) {	
				/***
				 * Create the base model for the asserted ontology axioms. Note that
				 * this model should not define any reasoning infrustructure. We just want
				 * a simple OntModel that will store the ontology (e.g. OWL_MEM).
				 */				
				modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			} else {
				modelSpec = new OntModelSpec(ontoSpec);
			}		
			
			if (reasoner != null) {
				modelSpec.setReasoner(reasoner);	
			}			
			
			//Set the customise document manager 
			modelSpec.setDocumentManager(docMgr);	
			
			ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel);
			return ontModel;
		}
    	return null;
    }//end getOntModel()    
    
    /**
	 * @param modelName - null to connect to the default graph, or specify a uri to connect to a 
	 * 					  named Graph. If the model of the named graph does not exists Jena will
	 * 					  create the model and return it to us.
	 * 
     * @return Graph - get a named Graph from a named Model in the dataset
     */
    public Graph getGraph(String modelName) {    	
    	Model m = getModel(modelName);    	
    	return m.getGraph();
    }//end getGraph()    
    
    /**
	 * Adds a given model to the default model of this dataset.
	 * @param model
	 */
	public void addModel(Model model) {
		Dataset ds = this.getDataset();  		
		ds.getDefaultModel().add(model);
	}//end addModel()    

	
	/**
	 * Load ontology to the OntModel from file or URI
	 */
    public void loadOntologyToModel(OntModel ontoModel, String ontologyURI) {
    	
    	InputStream in = null;
    	
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.WRITE);
 
 		try {
 			if (ontoModel != null) {				
 				if (ontoModel.isEmpty()) { 				
 					//need to test whether this 
 					in = FileManager.get().open(ontologyURI);
	 			
	 		    	if(in == null)
	 		    		throw new IllegalArgumentException((new StringBuilder("File: ")).append(ontologyURI).append(" not found").toString());
	 		    
	 				ontoModel.read(in, null);
 				}//end if	 			
 			}//end if 			
 		} finally { 
 			try {
 				dataset.end();
 			} catch (Exception exe) {
 	 			logger.error("Exception while loading ontology URI: " + exe);
 	 		}
 		}    	
    }//end loadOntologyToModel()	
	
	
	/**
     * Uses FileManager.get().readModel to read the RDF dump to the model
     * 
     * @param modelName - uri of the named graph to connect to
     * @param sourceURL - path of the source
     * @return ActionStatus - a status message
     */
    public ActionStatus loadDataToModelFromRDFDump(String modelName, String sourceURL) {
 		logger.debug("in loadDataToModelFromRDFDump()");
 		ActionStatus status = null;
 		
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.WRITE);
 		try {
 	 		//Call to get the Model needs to be inside the transaction otherwise exception is caused
 			Model model = this.getModel(modelName);
 	 		
 			if (sourceURL.endsWith(".nq")) {
 			  //TODO: sort this out	
 			  //Quads can be loaded only in the default graph
 			} 
 			
	 		FileManager.get().readModel(model, sourceURL);
 			
            dataset.commit();
 		} finally { 
 			try {
 				dataset.end() ;
 			} catch (Exception exe) {
 	 			logger.error("Exception while reading URI: " + exe);
 	   		   	status = new ActionStatus(0, exe.getClass().getSimpleName());
 	 		} 	 		
 		} 		

 		status = new ActionStatus(1, "Loading Dump to TDB: OK"); 
 		return status;
    }//method
    

    /***
     * Load data from RDF dump to a named model if the named model is empty. 
     * 
     * @param modelName
     * @param sourceURL
     * @return ActionStatus - a status message
     */
    public ActionStatus loadDataToModelIfEmpty(String modelName, String sourceURL) {
 		logger.debug("in loadDataToModelIfEmpty()");
 		ActionStatus status = null;
 		
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.WRITE);
 		try {
 	 	
 			//get the model
 			Model model = this.getModel(modelName);
 			 			
 			//check if namedModel is empty, if empty means that it needs to be loaded
 			if (model.isEmpty()) {
 				//method is guessing the serialisation format and assumes base uri, if not it will return
 				//null. I have to have this in mind if errors
 	 			FileManager.get().readModel(model, sourceURL);
 	 			
 			    dataset.commit();
 			}//end if
 		} finally { 
 			try {
 				dataset.end();
 			} catch (Exception exe) {
 	 			logger.error("Exception while reading URI: " + exe);
 	   		   	status = new ActionStatus(0, exe.getClass().getSimpleName());
 	 		} 	 		
 		} 		

 		status = new ActionStatus(1, "Loading Dump to TDB: OK"); 
 		return status;
    }//method 
    
    /***
     * Load data from RDF dump to a named model if the named model is empty. 
     * With Transaction.
     * 
     * @param modelName
     * @param sourceURL
     * @return ActionStatus - a status message
     */
    public ActionStatus loadDataToModelIfEmpty(Model model, String sourceURL) {
 		logger.debug("in loadDataToModelIfEmpty()");
 		ActionStatus status = null;
 		
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.WRITE);
 		try {
 			//check if namedModel is empty, if empty means that it needs to be loaded
 			if ((model != null) && (model.isEmpty())) {
 				//method is guessing the serialisation format and assumes base uri, if not it will return
 				//null. I have to have this in mind if errors
 	 			FileManager.get().readModel(model, sourceURL);
 	 			
 			    dataset.commit();
 			}//end if
 		} finally { 
 			try {
 				dataset.end();
 			} catch (Exception exe) {
 	 			logger.error("Exception while reading URI: " + exe);
 	   		   	status = new ActionStatus(0, exe.getClass().getSimpleName());
 	 		} 	 		
 		} 		

 		status = new ActionStatus(1, "Loading Dump to TDB: OK"); 
 		return status;
    }//method  
    
    /***
     * Method without transaction
     * 
     * @param model
     * @param sourceURL
     * @return
     */
    public ActionStatus loadDataToModelIfEmptyNoTransaction(Model model, String sourceURL) {
  		logger.debug("in loadDataToModelIfEmpty()");
  		ActionStatus status = null;
  		
  		try {
  			//check if namedModel is empty, if empty means that it needs to be loaded
  			if ((model != null) && (model.isEmpty())) {
  				//method is guessing the serialisation format and assumes base uri, if not it will return
  				//null. I have to have this in mind if errors
  	 			FileManager.get().readModel(model, sourceURL);  	 			
  			}//end if
  		} catch (Exception exe) {
  	 			logger.error("Exception while reading URI: " + exe);
  	   		   	status = new ActionStatus(0, exe.getClass().getSimpleName());
  		}

  		status = new ActionStatus(1, "Loading Dump to TDB: OK"); 
  		return status;
     }//method  
	
    /**
     * Use this method if you have already opened a transaction
     * to remove a named Model from the Dataset
     */
    public void removeNamedModel(String uri) {
    	Dataset dataset = this.getDataset();
    	//try {    	
    		if (dataset.containsNamedModel(uri)) {
 	   			dataset.removeNamedModel(uri);
    		}
    	//} catch (Exception exe) {
    	//	logger.error("Exception while removing graph: " + exe);
    	//}
    }//end removeNamedModel()
    
    /**
     * This method seems to work better for removing a named graph with 
     * this version of TDB
     * @param uri - of the named model to remove
     */
    public void rmvNamedModel(String uri) {
   		Model nameModel = this.getModel(uri);
   		nameModel.begin();
    	try {
    		nameModel.removeAll();
    	} finally {
    		nameModel.commit();
    	}   	
    }//end rmvNamedModel    
   
    
    /**
     * Remove this model transactional 
     */
    public void removeNamedModelTrans(String uri) {
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.WRITE);
 		try {
 	    	if (dataset.containsNamedModel(uri)) {
 	 	   		dataset.removeNamedModel(uri);
 	    	}//end if 	    	
 	    	dataset.commit();
 		} finally {
 			dataset.end();
 		}
    }//end removeNamedModelTrans()
    
    /**
     * Print Named model to system out
     */
    public void printNamedModel(String uri) {
 		Dataset dataset = this.getDataset();
 		dataset.begin(ReadWrite.READ);
 		try {
 	    	if (dataset.containsNamedModel(uri)) {
 				Model resultsModel = this.getModel(uri);	
 	    		resultsModel.write(System.out); 	    	
 	    	}//end if
 	    } finally {
 			dataset.end();
 		}
    }//end removeNamedModelTrans()
    
    
    /**
     * @return Location - return the location of the TDB store
     */
    public com.hp.hpl.jena.tdb.base.file.Location getLocation() {
    	Dataset ds = getDataset();    	
    	return TDBFactory.location(ds);    	
    }//end getLocation()    
    
    /**
	 * @return String - the path of this TDB Store
	 */
	public String getStoreLocation() {
		return this.TDB_PATH;
	}//end getStoreLocation()    
    
    /**
     * Close the dataset
     */
    public void close() {
    	Dataset ds = getDataset();  
    	ds.close();
    }
}//end Class