package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.exceptions.PrimitiveMatcherException;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.DereferenceURIAgentService;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

/***
 * 
 * This class only works on rdfs:label predicates only
 * 
 * @author klitos
 *
 */

@Scope("prototype")
@Service
public class RDFSLabelMatcherServiceImpl extends AnnotationMatcherServiceImpl {
	
	private static Logger logger = Logger.getLogger(RDFSLabelMatcherServiceImpl.class);
	
	@Autowired
	@Qualifier("dereferenceURIAgentService")
	private DereferenceURIAgentService dereferenceURIAgentService;
	
	/*Source schema List<RDFSLabelEntry> [Construct][String]*/
	//private List<RDFSLabelEntry> sourceSchemaConstructs;
	/*Target schema List<RDFSLabelEntry> [Construct][String]*/
	//private List<RDFSLabelEntry> targetSchemaConstructs;
	
	private SDBStoreServiceImpl sdbStoreService;

	public RDFSLabelMatcherServiceImpl() {
		logger.debug("in RDFSLabelMatcherServiceImpl");
		/*Get access to the metadata SDBStore*/
		sdbStoreService = this.getMetaDataSDBStore();
		this.setName("RDFSLabelMatcher");
		this.setMatcherType(MatcherType.RDFS_LABEL_MATCHER);
	}		
	
	/**
	 * When a matcher is called this method is firstly called.
	 * 
	 * //TODO Add the PrimitiveMatcherException to the other Matchers.match() methods
	 * 
	 * @param constructs1
	 * @param constructs2
	 * @return
	 */
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match (return float[][])");
		try {
			if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0) {
				 logger.error("This matcher is a primitive matcher and should not have any child matchers (lowest level matcher).");
		   	  	throw new PrimitiveMatcherException("Primitive matcher with child matchers");
			}//end if
			
			/*Issue SPARQL queries to get the List<RDFSLabelEntry> for each set of constructs*/
			//Delete this I will get the rdfs:label while I am doing the matching and not in advance
			//getRDFSLabel(constructs1, constructs2);
			
			/*Construct a similarity matrix that will hold the match similarity scores
			*   - contructs1 (source constructs at the rows)
			*   - contructs2 (target constructs at the columns)
			*/
			float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
			logger.info("constructs1.size(): " + constructs1.size());
			logger.info("constructs2.size(): " + constructs2.size());
			/*Check the size of the new lists*/
			//logger.info("sourceSchemaConstructs.size(): " + sourceSchemaConstructs.size());
			//logger.info("targetSchemaConstructs.size(): " + targetSchemaConstructs.size());			
			/*Monitor the time to start the construction of the similarity matrix*/
			long startTime = System.nanoTime();			
			logger.info("start matching all constructs with RDFSLabelMatcher: " + startTime);
			for (CanonicalModelConstruct sourceConstruct : constructs1) {		
				for (CanonicalModelConstruct targetConstruct : constructs2) {	
					logger.debug("sourceConstruct: " + sourceConstruct);
					logger.debug("targetConstruct: " + targetConstruct);
					logger.debug("sourceConstruct.getName: " + sourceConstruct.getName());
					logger.debug("targetConstruct.getName: " + targetConstruct.getName());
					long startTimeSingleMatch = System.nanoTime();
					logger.info("start matching two constructs with RDFSLabelMatcher: " + startTimeSingleMatch);
					Statement sourceRDFSLabel = getRDFSLabel(sourceConstruct);
					Statement targetRDSFLabel = getRDFSLabel(targetConstruct);
					logger.debug("sourceRDFSLabel: " + sourceRDFSLabel);
					logger.debug("targetRDSFLabel: " + targetRDSFLabel);
					
					//simMatrix[constructs1.indexOf(sourceConstruct)][constructs2.indexOf(targetConstruct)] = this.match(sourceRDFSLabel, targetRDSFLabel);
					

					
					
				}//end inner for
			}//end for			 
		 } catch (PrimitiveMatcherException exe) {
			 logger.debug("Exception: " + exe);
			 return null;
		 }		
		
		return null;	
	}//end match()
	

	
	
	//---- Supportive Methods ----//
	/**
	 * Method that searches for rdfs:label 
	 *  - For now it looks only for rdfs:labels @ english
	 *  
	 *  //TODO: den xriazete ta arrayLists na  ta svisw
	 *  
	 */
	private Statement getRDFSLabel(CanonicalModelConstruct construct) {
		logger.debug("in getRDFSLabel()");		
		logger.debug("SDBStoreServiceImpl:" + sdbStoreService);
		CanonicalModelProperty constructPropURI = null;
		CanonicalModelProperty constructPropNS = null;				
		String constructURI = null;
		String namespaceURI = null;
		Statement result = null;
		
		/*Get access to the metadata SDBStore*/
		sdbStoreService = this.getMetaDataSDBStore();		
		
		if (sdbStoreService != null) {
			long startTime = System.nanoTime();			
			logger.info("start collection of rdfs:label values");			
			/*Get the rdfs:label for the source schema*/
			//sourceSchemaConstructs = new ArrayList<RDFSLabelEntry>();
			
				/*Get the namespace URI of that construct*/
				constructPropNS = construct.getProperty("namespaceURI");
				namespaceURI = constructPropNS.getValue();				
				boolean exists = sdbStoreService.nameModelExists(namespaceURI);
				if (exists) {
					/*If the model exists get a reference to that model*/
					logger.info("Get the model for Named Graph with URI: " + namespaceURI);	
					Model model = sdbStoreService.getModel(namespaceURI);
					logger.info("Model is: " + model);	
					
					/*Get the constructURI of this construct*/
					if (construct instanceof SuperAbstract) {
						logger.info("Construct is SA : " + construct.getName());
						constructPropURI = construct.getProperty("rdfTypeValue");
					} else {
						constructPropURI = construct.getProperty("constructURI");
					}
					
					/*Get the constructURI string if there is none then "idk"*/
					if (constructPropURI == null) { 
						/*If URI is not available the use the string "idk"*/
						constructURI = "idk";
					} else {
						constructURI = constructPropURI.getValue();
					}
					
					/*If a URI exists*/
					if (!namespaceURI.equals("idk")) {
						logger.info("ConstructURI : " + constructURI);
						
						/*Ask a SPARQL query over the model to search for the constructURI [check whether the subject exists]*/
						boolean askQueryResult = subjectURIexistsASK(model, constructURI);
						logger.info("ConstructURI : " + constructURI + " | exists: " + askQueryResult);
						
						if (askQueryResult) {
							logger.info("ConstructURI exists");
							Resource res = model.getResource(constructURI);
							
							Statement label = this.searchForRDFSLabel(res);
							logger.debug("rdfs:label found: " + label);	
							
							//return rdfs:label
							if (label != null)
								result = label;
							
				
							
													 	
							//TODO klitos: make sure that the index is the same
							//Add it to the list
							//sourceSchemaConstructs.add(new RDFSLabelEntry(sourceConstruct,label));
							//logger.debug("Source construct index: " + sourceConstructs.indexOf(sourceConstruct));
				
						}//end inner if				
					
					}//end if
				}//end if				
		
			
			/*Get the rdfs:label for the target schema*/
			//targetSchemaConstructs = new ArrayList<RDFSLabelEntry>();			
		}//end if		
		
		return result;
	}//end getRDFSLabel()	
	
	/**
	 * Search for rdfs:label @ en
	 * @param res
	 * @return Statement of rdfs:label
	 */
	public Statement searchForRDFSLabel(Resource res) {
		/*Find all rdfs:labels for this Resource - it might be more than 1*/
		StmtIterator iter =  res.listProperties(RDFS.label);
		
		Statement label = null;		
		while (iter.hasNext()) {
			label = iter.nextStatement();
			
			if (label.getObject().isLiteral()) {
				if (label.getLanguage().equals("en")) {
					return label;
				} else if (label.getLanguage().equals("")){
					return label;
				}
			}
			
			logger.info("label lang: " + label.getLanguage());
			logger.info("label object string: " + label.getString());								
		}
		return label;
	}//end searchForRDFSLabel()
	
	
	//TODO: refactor this method and instead call the same method that exists in the DereferenceURIAgent.
	public boolean subjectURIexistsASK(Model model, String constructURI) {
		logger.debug("in subjectURIexistsASK()");
		
		String queryString =
				"ASK" +
				"WHERE {" +
				" <" + constructURI + "> " + "?p ?o . " +
				" }";
			
			//Create the SPARQL ASK query
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);

		return qe.execAsk();
	}//end executeSPARQLSelectQuery()	
	
	
	/*Source schema List<RDFSAnnotations> [Construct][String]
	public List<RDFSLabelEntry> getSchemaConstructs() {
		return this.getSchemaConstructs();
	}

	public List<RDFSLabelEntry> getTargetConstructs() {
		return this.getTargetConstructs();
	}*/
}//end RDFSLabelMatcherServiceImpl
