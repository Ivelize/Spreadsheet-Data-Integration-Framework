package uk.ac.manchester.dstoolkit.service.util.importexport;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;

/***
 * This is more like a Client for testing various new functionality in terms of 
 * the new Graphviz dot generator class instead of a proper JUnit test class.
 *  
 * @author klitos
 */
public class GraphvizDotGeneratorServiceImplTest extends RDFAbstractIntegrationTest {
	
	private static Logger logger = Logger.getLogger(GraphvizDotGeneratorServiceImplTest.class);
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Before
	public void setUp() {		
	}//end setUp()
	
	/**
	 * GRAPHVIZ TESTING
	 * 
	 * Save a simple Graph as a DOT file and then export it as a PNG image.
	 * @throws ExecutionException
	 */
	@Test
	public void testGraphvizExportMethods() throws ExecutionException {
		logger.debug("in testGraphvizExportMethods()");
		
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("graph schema {").append("\n");
		stringBuilder.append("rankdir=LR;").append("\n");
		stringBuilder.append("ranksep=0.5;").append("\n");
		stringBuilder.append("node [shape=record];").append("\n");
		stringBuilder.append("A; }");		

		File temp = graphvizDotGeneratorService.exportAsDOTFile(stringBuilder.toString(), "Schema", null);	
		graphvizDotGeneratorService.exportDOT2PNG(temp, "png", null, null);
	}//end testGraphvizExportMethods()
	
	/***
	 * Print a Contingency Table using DOT language
	 * 
	 */
	@Test
	public void testGraphvizContingencyTable() throws ExecutionException {
		
	}
	
	
	
}//end Class

















