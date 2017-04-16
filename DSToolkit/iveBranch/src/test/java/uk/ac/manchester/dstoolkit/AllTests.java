package uk.ac.manchester.dstoolkit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.manchester.dstoolkit.domain.user.UserTest;
import uk.ac.manchester.dstoolkit.dto.DozerMappingIntegrationTest;
import uk.ac.manchester.dstoolkit.dto.DozerMappingTest;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults.HibernateResultInstanceRepositoryIntegrationTest;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.user.UserRepositoryTest;
import uk.ac.manchester.dstoolkit.service.impl.annotation.AnnotationServiceIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.meta.BioDataSourceServiceImplIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.meta.BioSchemaServiceImplTest;
import uk.ac.manchester.dstoolkit.service.impl.meta.DataSourceServiceImplIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImplTest;
import uk.ac.manchester.dstoolkit.service.impl.query.QueryServiceImplIntegrationMappingSelectionTest;
import uk.ac.manchester.dstoolkit.service.impl.query.QueryServiceImplIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.QueryEvaluationEngineIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.queryexpander.QueryExpanderIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.queryexpander.QueryExpanderServiceImplTest;
import uk.ac.manchester.dstoolkit.service.impl.query.queryoptimiser.LogicalQueryOptimiserIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.querytranslator.GlobalQueryTranslatorIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.querytranslator.LocalQueryTranslator2SQLIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.query.querytranslator.LocalQueryTranslator2SQLServiceImplTest;
import uk.ac.manchester.dstoolkit.service.impl.query.querytranslator.LocalQueryTranslator2XQueryIntegrationTest;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoexclude.ImportSchemaElementsToExcludeFromXMLServiceImplTest;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoinclude.ImportSchemaElementsToIncludeFromXMLServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses(value = { UserTest.class, DozerMappingIntegrationTest.class, DozerMappingTest.class,
		HibernateResultInstanceRepositoryIntegrationTest.class, UserRepositoryTest.class, AnnotationServiceIntegrationTest.class,
		DataSourceServiceImplIntegrationTest.class, BioDataSourceServiceImplIntegrationTest.class, BioSchemaServiceImplTest.class,
		NGramMatcherServiceImplTest.class, QueryServiceImplIntegrationMappingSelectionTest.class, QueryServiceImplIntegrationTest.class,
		QueryEvaluationEngineIntegrationTest.class, QueryExpanderIntegrationTest.class, QueryExpanderServiceImplTest.class,
		LogicalQueryOptimiserIntegrationTest.class, GlobalQueryTranslatorIntegrationTest.class, LocalQueryTranslator2SQLIntegrationTest.class,
		LocalQueryTranslator2SQLServiceImplTest.class, LocalQueryTranslator2XQueryIntegrationTest.class,
		ImportSchemaElementsToExcludeFromXMLServiceImplTest.class, ImportSchemaElementsToIncludeFromXMLServiceImplTest.class })
public class AllTests {

	//
	//

	//UserServiceTest.class, 
	//UserServiceIntegrationTest.class,
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for uk.ac.manchester.dstoolkit");
		//$JUnit-BEGIN$
		//TODO add endTransaction tests to check for lazy loading errors 
		//$JUnit-END$
		return suite;
	}

	//LocalQueryTranslator2XQueryIntegrationTest.class,
	//, QueryExpanderIntegrationTest.class

}
