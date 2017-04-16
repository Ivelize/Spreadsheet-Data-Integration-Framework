package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SPARQLService;

public class LocalQueryTranslator2SPARQLServiceImplTest {

	private static Logger logger = Logger.getLogger(LocalQueryTranslator2SPARQLServiceImplTest.class);	
		
	@Autowired
	@Qualifier("localQueryTranslator2SPARQLService")
	private LocalQueryTranslator2SPARQLService localQueryTranslator2SPARQLService;
	
	@Before
	public void setUp() {
		
	}//end setUp()	
	
	@Test
	public void testScanOperator() {

	}
	
}//end class
