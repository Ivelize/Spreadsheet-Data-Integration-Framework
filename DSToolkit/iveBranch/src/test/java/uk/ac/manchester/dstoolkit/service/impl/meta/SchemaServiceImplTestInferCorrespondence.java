package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class SchemaServiceImplTestInferCorrespondence {

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;

	private Set<Schema> sourceSchemas, targetSchemas;
	private List<Matching> matchings;

	private SuperAbstract sa1, sa2, sa3, sa4, sa5, sa6;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9, sl10, sl11, sl12, sl13, sl14, sl15, sl16;

	@Before
	public void setUp() throws Exception {
		sourceSchemas = new HashSet<Schema>();
		targetSchemas = new HashSet<Schema>();
		matchings = new ArrayList<Matching>();

		Schema schema1 = new Schema("schema1", null);
		sourceSchemas.add(schema1);

		sa1 = new SuperAbstract("sa1", schema1);
		sl1 = new SuperLexical("sl1", schema1);
		sl2 = new SuperLexical("sl2", schema1);
		sl3 = new SuperLexical("sl3", schema1);
		sl4 = new SuperLexical("sl4", schema1);

		sl1.setParentSuperAbstract(sa1);
		sl2.setParentSuperAbstract(sa1);
		sl3.setParentSuperAbstract(sa1);
		sl4.setParentSuperAbstract(sa1);

		sa2 = new SuperAbstract("sa2", schema1);
		sl5 = new SuperLexical("sl5", schema1);
		sl6 = new SuperLexical("sl6", schema1);
		sl7 = new SuperLexical("sl7", schema1);

		sl5.setParentSuperAbstract(sa2);
		sl6.setParentSuperAbstract(sa2);
		sl7.setParentSuperAbstract(sa2);

		sa5 = new SuperAbstract("sa5", schema1);
		sl13 = new SuperLexical("sl13", schema1);
		//sl14 = new SuperLexical("sl14", schema1);

		sl13.setParentSuperAbstract(sa5);
		//sl14.setParentSuperAbstract(sa5);

		Schema schema2 = new Schema("schema2", null);
		targetSchemas.add(schema2);

		sa3 = new SuperAbstract("sa3", schema2);
		sl8 = new SuperLexical("sl8", schema2);
		sl9 = new SuperLexical("sl9", schema2);
		sl10 = new SuperLexical("sl10", schema2);

		sl8.setParentSuperAbstract(sa3);
		sl9.setParentSuperAbstract(sa3);
		sl10.setParentSuperAbstract(sa3);

		sa4 = new SuperAbstract("sa4", schema2);
		sl11 = new SuperLexical("sl11", schema2);
		sl12 = new SuperLexical("sl12", schema2);

		sl11.setParentSuperAbstract(sa4);
		sl12.setParentSuperAbstract(sa4);

		sa6 = new SuperAbstract("sa6", schema2);
		sl15 = new SuperLexical("sl15", schema2);
		//sl16 = new SuperLexical("sl16", schema2);

		sl15.setParentSuperAbstract(sa6);
		//sl16.setParentSuperAbstract(sa6);

	}

	@Test
	public void testInferCorrespondenceTwoSuperAbstractsInEachSchemaTwoSimpleOneToOneCorrespondencesExpected() {

		Matching om1 = new OneToOneMatching(sa1, sa3, 0.8, null);
		Matching om2 = new OneToOneMatching(sl1, sl8, 0.9, null);
		Matching om3 = new OneToOneMatching(sl2, sl9, 0.65, null);
		Matching om4 = new OneToOneMatching(sl3, sl10, 0.7, null);
		Matching om5 = new OneToOneMatching(sl3, sl11, 0.4, null);
		Matching om6 = new OneToOneMatching(sa2, sa4, 0.7, null);
		Matching om7 = new OneToOneMatching(sl5, sl11, 0.69, null);
		Matching om8 = new OneToOneMatching(sl6, sl12, 0.85, null);
		Matching om9 = new OneToOneMatching(sl6, sl9, 0.35, null);
		Matching om10 = new OneToOneMatching(sa5, sa6, 0.31, null);

		matchings.add(om1);
		matchings.add(om2);
		matchings.add(om3);
		matchings.add(om4);
		matchings.add(om5);
		matchings.add(om6);
		matchings.add(om7);
		matchings.add(om8);
		matchings.add(om9);
		matchings.add(om10);

		schemaService.inferCorrespondence(sourceSchemas, targetSchemas, matchings, new HashMap<ControlParameterType, ControlParameter>());

		//TODO not a proper test as nothing checked, only checked the log file
		//TODO add assertions
	}

	@Test
	public void testInferCorrespondenceTwoSuperAbstractsInEachSchemaHP_vs_HPExpected() {

		//this doesn't work if only the SAs that are involved in the to many side are in schema or if the additional SAs aren't matched ... 
		//as they're not in the search space when not matched, so not taken into account when calculating tfidf 
		//and as idf = log10(N/n) = 0 due to N=1 and n=1

		Matching om1 = new OneToOneMatching(sa1, sa3, 0.8, null);
		Matching om2 = new OneToOneMatching(sa1, sa4, 0.75, null);
		Matching om3 = new OneToOneMatching(sl1, sl8, 0.87, null);
		Matching om4 = new OneToOneMatching(sl1, sl11, 0.69, null);
		Matching om5 = new OneToOneMatching(sl2, sl9, 0.73, null);
		Matching om6 = new OneToOneMatching(sl2, sl12, 0.87, null);
		Matching om7 = new OneToOneMatching(sl3, sl10, 0.79, null);
		Matching om8 = new OneToOneMatching(sa2, sa3, 0.35, null);
		Matching om9 = new OneToOneMatching(sl6, sl9, 0.42, null);
		Matching om10 = new OneToOneMatching(sl7, sl11, 0.51, null);
		Matching om11 = new OneToOneMatching(sa5, sa6, 0.31, null);

		matchings.add(om1);
		matchings.add(om2);
		matchings.add(om3);
		matchings.add(om4);
		matchings.add(om5);
		matchings.add(om6);
		matchings.add(om7);
		matchings.add(om8);
		matchings.add(om9);
		matchings.add(om10);
		matchings.add(om11);

		schemaService.inferCorrespondence(sourceSchemas, targetSchemas, matchings, new HashMap<ControlParameterType, ControlParameter>());

		//TODO not a proper test as nothing checked, only checked the log file
		//TODO add assertions
	}

	@Test
	public void testInferCorrespondenceTwoSuperAbstractsInEachSchemaSingle_vs_HPExpected() {

		//this doesn't work if only the SAs that are involved in the to many side are in schema or if the additional SAs aren't matched ... 
		//as they're not in the search space when not matched, so not taken into account when calculating tfidf 
		//and as idf = log10(N/n) = 0 due to N=1 and n=1

		Matching om1 = new OneToOneMatching(sa1, sa3, 0.8, null);
		Matching om2 = new OneToOneMatching(sa1, sa4, 0.75, null);
		Matching om3 = new OneToOneMatching(sl1, sl8, 0.87, null);
		Matching om4 = new OneToOneMatching(sl1, sl11, 0.69, null);
		Matching om5 = new OneToOneMatching(sl2, sl9, 0.73, null);
		Matching om6 = new OneToOneMatching(sl2, sl12, 0.87, null);
		Matching om7 = new OneToOneMatching(sl3, sl10, 0.79, null);
		Matching om8 = new OneToOneMatching(sa2, sa3, 0.35, null);
		Matching om11 = new OneToOneMatching(sa5, sa6, 0.31, null);

		matchings.add(om1);
		matchings.add(om2);
		matchings.add(om3);
		matchings.add(om4);
		matchings.add(om5);
		matchings.add(om6);
		matchings.add(om7);
		matchings.add(om8);
		matchings.add(om11);

		schemaService.inferCorrespondence(sourceSchemas, targetSchemas, matchings, new HashMap<ControlParameterType, ControlParameter>());

		//TODO not a proper test as nothing checked, only checked the log file
		//TODO add assertions
	}

}
