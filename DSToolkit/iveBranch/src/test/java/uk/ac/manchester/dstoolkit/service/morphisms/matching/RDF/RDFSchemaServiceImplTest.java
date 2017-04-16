package uk.ac.manchester.dstoolkit.service.morphisms.matching.RDF;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.SPARQLQueriesLibrary;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.JenaJungRDFVisualisationService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.store.DatasetStore;

/***
 * This is more like a Client for testing various new functionality in terms of 
 * the new architecture for matching RDF instead of a proper JUnit test class.
 *  
 * //TODO: [S.O.S] use this class for testing the new functionality. 
 *  
 * @author klitos
 */
public class RDFSchemaServiceImplTest extends RDFAbstractIntegrationTest {
	
	private static Logger logger = Logger.getLogger(RDFSchemaServiceImplTest.class);
	
	/*Configuration files for SDBStores, sdb_graphs and sdb_metadata*/
	private static String jenaSDBMetaDataPropLoc = "./src/main/resources/datasources/jenaSDBmeta.properties";
	private static String jenaSDBGraphsPropLoc   = "./src/main/resources/datasources/jenaSDBgraphs.properties";
	
	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Autowired
	@Qualifier("jenaJungRDFVisualisationService")
	private JenaJungRDFVisualisationService jenaJungRDFVisualisationService;
	
	private Schema testSchema1 = null;
	private Schema testSchema2 = null;	
		
	
	@Override
	@Before
	public void setUp() {
		super.setUp();

		/*Get the two test schemas from the schemaRepository*/
		testSchema1 = schemaRepository.getSchemaByName("magnatuneRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchema");
     }//end setUp()
	
	
	/**
	 * Test class for testing schemaEnrichment() in SchemaServiceImpl Class.
	 * @throws ExecutionException
	 */
	@Test
	public void testSchemaEnrichment() throws ExecutionException {
		logger.debug("in testSchemaEnrichment()");		

		/**
		 * Shows an example of calling the method to enrich the schemas with annotations. The annotations are stored
		 * as named graphs in an sdb_metadata store
		 */
		schemaService.schemaEnrichment(testSchema1, testSchema2);
			
	}//end testSchemaEnrichment()

	
	/**
	 * test for queries
	 * 
	 * @throws ExecutionException
	 */
	@Test
	public void testQueryMethods() throws ExecutionException {
		logger.debug("in testQueryMethods()");
		
		SDBStoreServiceImpl metaDataSDBStore = loadRDFSourceUtilService.getSDBStoreForDB(jenaSDBMetaDataPropLoc);
		logger.debug("SDBStoreServiceImpl:" + metaDataSDBStore);
		
		//test querying 
		//TODO: A class that will have all SPARQL queries inside, i have done this
		//Note some vocabularies may output a RDFDefaultErrorHandler error
		
        String queryString2 = "SELECT ?s ?p ?o WHERE {GRAPH <http://www.holygoat.co.uk/owl/redwood/0.1/tags/>  { ?s "+
        												"?p" + " ?o}}" ;
        
        String queryString3 = SPARQLQueriesLibrary.LIST_ALL_NAMED_GRAPHS;
        
        String queryString = "SELECT ?s ?p ?o WHERE {GRAPH <http://purl.org/ontology/mo/>  { <http://purl.org/ontology/mo/Performance> "+
				"?p" + " ?o}}" ;
        
         
        Query query = QueryFactory.create(queryString) ;
		
        Store store = metaDataSDBStore.getSDBStore();
		
        Dataset ds = DatasetStore.create(store) ;
        QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
        try {
            ResultSet rs = qe.execSelect() ;
            ResultSetFormatter.out(rs) ;
        } finally { qe.close() ; }
		
	}//end testGraphvizExportMethods()	
	
	
	/**
	 * Output structure of the schemata with Graphviz DOT language
	 * 
	 * @throws ExecutionException
	 */
	@Test
	public void testGraphvizExportMethods() throws ExecutionException {
		logger.debug("in testGraphvizExportMethods()");
		
		//String dotString = graphvizDotGeneratorService.generateDot(testSchema1);
		
		String dotString = graphvizDotGeneratorService.generateDot(testSchema1, "vertical", true, false);
		
		File temp = graphvizDotGeneratorService.exportAsDOTFile(dotString, "Schema", null);	
		graphvizDotGeneratorService.exportDOT2PNG(temp, "png", null, null);
		
	}//end testGraphvizExportMethods()
}//end Class



