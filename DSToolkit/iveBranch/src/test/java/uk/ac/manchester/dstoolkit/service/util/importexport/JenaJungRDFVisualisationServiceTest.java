package uk.ac.manchester.dstoolkit.service.util.importexport;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;

/***
 * This is more like a Client for testing various new functionality in terms of 
 * visualise RDF graph generator class instead of a proper JUnit test class.
 *  
 * @author klitos
 */

public class JenaJungRDFVisualisationServiceTest extends RDFAbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(JenaJungRDFVisualisationServiceTest.class);
	
	@Autowired
	@Qualifier("jenaJungRDFVisualisationService")
	private JenaJungRDFVisualisationService jenaJungRDFVisualisationService;
	
	@Before
	public void setUp() {		
	}//end setUp()
	

	/**
	 * Visualise RDF graphs using JenaJung 
	 * 
	 * //TODO: Test it after sorting out the Jena TDB persistent storage 
	 * 
	 */
	@Test
	public void testJenaJungRDFVisualisationMethods() throws ExecutionException {
		logger.debug("in testGraphvizExportMethods()");
	
		jenaJungRDFVisualisationService.visualiseRDFGraph("http://www.cs.man.ac.uk/~christk6/foaf.rdf");		
	}//end testJenaJungRDFVisualisationMethods()		
	
}//end class
