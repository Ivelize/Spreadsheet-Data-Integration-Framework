package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.BioAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;

/**
 * @author ruhaila
 * @author chedeler
 */

public class BioSchemaServiceImplTest extends BioAbstractIntegrationTest {

	List<Matching> matchesList;
	Schema schema1;
	Schema schema2;
	Schema schema3;
	List<MatcherService> defaultMatchers;
	MatcherService ngramMatcherForNameMatcher;
	MatcherService datatypeMatcherForNameMatcher;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	protected static User currentUser;
	protected static Dataspace currentDataspace;

	static Logger logger = Logger.getLogger(BioSchemaServiceImplTest.class);

	@Override
	@Before
	public void setUp() {
		super.setUp();

		defaultMatchers = new ArrayList<MatcherService>();
		//NGram matcher
		ngramMatcherForNameMatcher = new NGramMatcherServiceImpl(3);
		defaultMatchers.add(ngramMatcherForNameMatcher);

		schema1 = schemaRepository.getSchemaByName("Stanford");
		schema2 = schemaRepository.getSchemaByName("ArrayExpressXML");
		schema3 = schemaRepository.getSchemaByName("GeoXML");
	}

	/*
	 * Ruhaila
	 * commented out by Conny as not really testing match and renamed from testMatch2 into testSchemaBasedMatch
	 * 
	@Test
	public void testSchemaBasedMatch() {
		logger.debug("schema1: " + schema1.getName());
		logger.debug("schema2: " + schema2.getName());
		matchesList = schemaService.match(schema2, schema3, defaultMatchers);
		String str = null;
		double score = 0;
		String construct1Name = null;
		String construct2Name = null;
		int cnt = 1;

		for (Matching aMatch : matchesList) {
			Set<CanonicalModelConstruct> constructs1 = aMatch.getConstructs1();
			Set<CanonicalModelConstruct> constructs2 = aMatch.getConstructs2();

			for (CanonicalModelConstruct construct1 : constructs1) {
				construct1Name = construct1.getName();
				logger.debug("construct1Name: " + construct1Name);
			}

			for (CanonicalModelConstruct construct2 : constructs2) {
				construct2Name = construct2.getName();
				logger.debug("construct2Name: " + construct2Name);
			}

			score = aMatch.getScore();
			logger.debug("score: " + score);

			str = aMatch.getMatcherName();
			logger.debug("str: " + str);

			logger.debug("cnt " + cnt++);
			logger.debug("construct1 - name:" + construct1Name);
			logger.debug("construct2 - name:" + construct2Name);
			logger.debug("score : " + aMatch.getScore());
		}
		assertEquals("NGramMatcher", str);
	}
	*/
}
