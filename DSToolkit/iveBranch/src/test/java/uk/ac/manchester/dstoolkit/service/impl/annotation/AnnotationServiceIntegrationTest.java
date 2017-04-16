package uk.ac.manchester.dstoolkit.service.impl.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;

public class AnnotationServiceIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(AnnotationServiceIntegrationTest.class);

	//TODO still need to test what happens when the query is re-run, more feedback gathered and the annotations and propagated to the mappings
	//TODO the current implementation in AnnotationServiceImpl isn't right.
	//The annotation from the resultInstances needs to be propagated to the corresponding construct(s) 
	//over which the query was posed and from there to the mappings that populated the constructs.
	//but need to record which mapping was used to produce the queryResults

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("annotationService")
	private AnnotationService annotationService;

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("ontologyTermService")
	private OntologyTermService ontologyTermService;

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	private QueryResult queryResult;
	private Long resultInstanceId;
	private OntologyTerm expectancyOT;
	private OntologyTerm precisionOT;
	private OntologyTerm recallOT;
	private OntologyTerm fmeasureOT;
	private OntologyTerm statisticalErrorOT;
	private OntologyTerm fractionOfAnnotatedResultsOT;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		//TODO add africa to lakes database
		//TODO add tests for annotation other than expectancy
		//TODO need to test that each resultInstance has correct mapping associated with it

		String selectStarFromQuery = "Select * from City";
		String queryName = "AllCities";
		Schema mondialSchema = schemaRepository.getSchemaByName("MondialIntegr");

		DataSource mondialCityProvinceCountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		DataSource mondialCityProvinceCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");

		DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
		query.setUser(currentUser);

		queryService.addQuery(query);
		queryRepository.flush();

		queryResult = queryService.evaluateQuery(query, null, currentUser, null);

		queryResultRepository.flush();
		entityManager.flush();

		List<ResultInstance> resultInstances = queryResult.getResultInstances();

		logger.debug("resultInstances.size(): " + resultInstances.size());

		resultInstanceId = resultInstances.get(0).getId();

		if (ontologyTermRepository.getOntologyTermWithName("precision") == null) {

			expectancyOT = new OntologyTerm("expectancy", DataType.BOOLEAN);
			precisionOT = new OntologyTerm("precision", DataType.DOUBLE);
			recallOT = new OntologyTerm("recall", DataType.DOUBLE);
			fmeasureOT = new OntologyTerm("f-measure", DataType.DOUBLE);
			fractionOfAnnotatedResultsOT = new OntologyTerm("fractionOfAnnotatedResults", DataType.DOUBLE);

			Set<String> statisticalErrorValues = new LinkedHashSet<String>();
			statisticalErrorValues.add("tp");
			statisticalErrorValues.add("fp");
			statisticalErrorValues.add("fn");

			statisticalErrorOT = new OntologyTerm("statisticalError", statisticalErrorValues);

			ontologyTermService.addOntologyTerm(statisticalErrorOT);
			ontologyTermService.addOntologyTerm(expectancyOT);
			ontologyTermService.addOntologyTerm(precisionOT);
			ontologyTermService.addOntologyTerm(recallOT);
			ontologyTermService.addOntologyTerm(fmeasureOT);
			ontologyTermService.addOntologyTerm(fractionOfAnnotatedResultsOT);
		} else {
			expectancyOT = ontologyTermRepository.getOntologyTermWithName("expectancy");
			precisionOT = ontologyTermRepository.getOntologyTermWithName("precision");
			recallOT = ontologyTermRepository.getOntologyTermWithName("recall");
			fmeasureOT = ontologyTermRepository.getOntologyTermWithName("f-measure");
			fractionOfAnnotatedResultsOT = ontologyTermRepository.getOntologyTermWithName("fractionOfAnnotatedResults");
			statisticalErrorOT = ontologyTermRepository.getOntologyTermWithName("statisticalError");
		}

	}

	@Test
	public void testAnnotateMappingWithPrecisionAnnotation() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		assertEquals(2, mappings.size());

		Mapping map = mappings.iterator().next();
		Long mappingId = map.getId();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, true, currentUser);

		Mapping mapping = mappingRepository.find(mappingId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, currentUser);
		//List<Annotation> annotations = mapping.getAnnotations();

		assertNotNull(annotations);
		assertEquals(1, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("0.56", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(mapping.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(mapping, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateMappingWithSamePrecisionAnnotationDuplicatesAllowed() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		assertEquals(2, mappings.size());

		Mapping map = mappings.iterator().next();
		Long mappingId = map.getId();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, true, currentUser);

		Mapping mapping = mappingRepository.find(mappingId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, currentUser);

		//List<Annotation> annotations = mapping.getAnnotations();

		assertNotNull(annotations);
		assertEquals(1, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("0.56", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());
		Date timestamp1 = annotation1.getTimestamp();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, true, currentUser);

		Mapping mapping2 = mappingRepository.find(mappingId);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping2, currentUser);

		//List<Annotation> annotations2 = mapping2.getAnnotations();

		assertNotNull(annotations2);
		assertEquals(1, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();
		Annotation annotation2 = annotations2It.next();

		assertEquals("0.56", annotation2.getValue());
		assertNotNull(annotation2.getId());
		assertEquals(1, annotation2.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation2.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation2.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation2.getOntologyTerm());
		assertNotNull(annotation2.getOntologyTerm().getId());
		assertEquals(currentUser, annotation2.getUser());
		Date timestamp2 = annotation2.getTimestamp();

		assertTrue(timestamp2.after(timestamp1));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateMappingWithDifferentPrecisionAnnotationDuplicatesAllowed() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		assertEquals(2, mappings.size());

		Mapping map = mappings.iterator().next();
		Long mappingId = map.getId();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, true, currentUser);

		Mapping mapping = mappingRepository.find(mappingId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, currentUser);

		//List<Annotation> annotations = mapping.getAnnotations();

		assertNotNull(annotations);
		assertEquals(1, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("0.56", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());
		Date timestamp1 = annotation1.getTimestamp();

		annotationService.annotate("precision", "0.78", mappingId, null, constrainingModelManagementConstructs, true, currentUser);

		Mapping mapping2 = mappingRepository.find(mappingId);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping2, currentUser);

		//List<Annotation> annotations2 = mapping2.getAnnotations();

		assertNotNull(annotations2);
		assertEquals(2, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();
		Annotation annotation21 = annotations2It.next();

		assertEquals("0.56", annotation21.getValue());
		assertNotNull(annotation21.getId());
		assertEquals(1, annotation21.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation21.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation21.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation21.getOntologyTerm());
		assertNotNull(annotation21.getOntologyTerm().getId());
		assertEquals(currentUser, annotation21.getUser());
		Date timestamp21 = annotation21.getTimestamp();

		assertTrue(timestamp21.equals(timestamp1));

		Annotation annotation22 = annotations2It.next();

		assertEquals("0.78", annotation22.getValue());
		assertNotNull(annotation22.getId());
		assertEquals(1, annotation22.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation22.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation22.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation22.getOntologyTerm());
		assertNotNull(annotation22.getOntologyTerm().getId());
		assertEquals(currentUser, annotation22.getUser());
		Date timestamp22 = annotation22.getTimestamp();

		assertTrue(timestamp22.after(timestamp1));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateMappingWithSamePrecisionAnnotationNoDuplicatesAllowed() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		assertEquals(2, mappings.size());

		Mapping map = mappings.iterator().next();
		Long mappingId = map.getId();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, false, currentUser);

		Mapping mapping = mappingRepository.find(mappingId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, currentUser);

		//List<Annotation> annotations = mapping.getAnnotations();

		assertNotNull(annotations);
		assertEquals(1, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("0.56", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());
		Date timestamp1 = annotation1.getTimestamp();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, false, currentUser);

		Mapping mapping2 = mappingRepository.find(mappingId);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping2, currentUser);

		//List<Annotation> annotations2 = mapping2.getAnnotations();

		assertNotNull(annotations2);
		assertEquals(1, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();
		Annotation annotation2 = annotations2It.next();

		assertEquals("0.56", annotation2.getValue());
		assertNotNull(annotation2.getId());
		assertEquals(1, annotation2.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation2.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation2.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation2.getOntologyTerm());
		assertNotNull(annotation2.getOntologyTerm().getId());
		assertEquals(currentUser, annotation2.getUser());
		Date timestamp2 = annotation2.getTimestamp();

		assertTrue(timestamp2.after(timestamp1));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateMappingWithDifferentPrecisionAnnotationNoDuplicatesAllowed() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		assertEquals(2, mappings.size());

		Mapping map = mappings.iterator().next();
		Long mappingId = map.getId();

		annotationService.annotate("precision", "0.56", mappingId, null, constrainingModelManagementConstructs, false, currentUser);

		Mapping mapping = mappingRepository.find(mappingId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, currentUser);

		//List<Annotation> annotations = mapping.getAnnotations();

		assertNotNull(annotations);
		assertEquals(1, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("0.56", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());
		Date timestamp1 = annotation1.getTimestamp();

		annotationService.annotate("precision", "0.78", mappingId, null, constrainingModelManagementConstructs, false, currentUser);

		Mapping mapping2 = mappingRepository.find(mappingId);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping2, currentUser);

		//List<Annotation> annotations2 = mapping2.getAnnotations();

		assertNotNull(annotations2);
		assertEquals(1, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();
		Annotation annotation2 = annotations2It.next();

		assertEquals("0.78", annotation2.getValue());
		assertNotNull(annotation2.getId());
		assertEquals(1, annotation2.getAnnotatedModelManagementConstructs().size());
		assertEquals(map.getId(), annotation2.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(map, annotation2.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), annotation1.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), annotation1.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, annotation2.getOntologyTerm());
		assertNotNull(annotation2.getOntologyTerm().getId());
		assertEquals(currentUser, annotation2.getUser());
		Date timestamp2 = annotation2.getTimestamp();

		assertTrue(timestamp2.after(timestamp1));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateResultInstanceWithExpectancyAnnotationAndInferStatisticalErrorTPAnnotation() {

		assertNotNull(queryResult.getQuery().getUser());

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		annotationService.annotate("expectancy", "true", resultInstanceId, null, constrainingModelManagementConstructs, true, currentUser);

		ResultInstance resultInstance = resultInstanceRepository.find(resultInstanceId);

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance, currentUser);

		//List<Annotation> annotations = resultInstance.getAnnotations();

		assertNotNull(annotations);
		assertEquals(2, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("true", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());

		Annotation annotation2 = annotationsIt.next();

		assertEquals("tp", annotation2.getValue());
		assertNotNull(annotation2.getId());
		assertEquals(1, annotation2.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance.getId(), annotation2.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance, annotation2.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation2.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation2.getOntologyTerm());
		assertNotNull(annotation2.getOntologyTerm().getId());
		assertEquals(currentUser, annotation2.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateMissingResultInstanceWithExpectancyAnnotationAndInferStatisticalErrorFNAnnotation() {

		//TODO think about it: how do you decide which mappings should have produced a FN resultInstance? how can you annotate mappings using FN resultInstances?
		//could assign all the mappings that were used to produce the queryResult to the missing resultInstances - do this for now
		//also add missing resultInstance to queryResult which should have contained it
		//queryResult needs to be updated, which isn't possible in the generic implementation in AnnotationServiceImpl ... needs to be ensured somewhere else... not ideal solution though

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		assertEquals(19, queryResult.getResultInstances().size());

		ResultInstance otherResultInstance = resultInstanceRepository.find(resultInstanceId);

		ResultInstance missingResultInstance = new ResultInstance();
		missingResultInstance.setResultType(otherResultInstance.getResultType());
		ResultValue value1 = new ResultValue("City.Name", "Edinburgh");
		ResultValue value2 = new ResultValue("City.Country", "GB");
		ResultValue value3 = new ResultValue("City.Province", "Lothian");
		ResultValue value4 = new ResultValue("City.Population", "447600");
		ResultValue value5 = new ResultValue("City.Longitude", "-3.18333");
		ResultValue value6 = new ResultValue("City.Latitude", "55.9167");

		missingResultInstance.addResultValue("City.Name", value1);
		missingResultInstance.addResultValue("City.Country", value2);
		missingResultInstance.addResultValue("City.Province", value3);
		missingResultInstance.addResultValue("City.Population", value4);
		missingResultInstance.addResultValue("City.Longitude", value5);
		missingResultInstance.addResultValue("City.Latitude", value6);

		missingResultInstance.setUserSpecified(true);
		missingResultInstance.addAllMappings(queryResult.getMappings());
		queryResult.addResultInstance(missingResultInstance);

		resultInstanceRepository.save(missingResultInstance);

		entityManager.flush();

		annotationService
				.annotate("expectancy", "true", missingResultInstance, queryResult, constrainingModelManagementConstructs, true, currentUser);
		queryResultRepository.update(queryResult);
		queryResultRepository.flush();

		entityManager.flush();

		assertEquals(20, queryResult.getResultInstances().size());
		assertNotNull(missingResultInstance.getId());

		ResultInstance resultInstance = resultInstanceRepository.find(missingResultInstance.getId());

		assertEquals(2, resultInstance.getMappings().size());

		List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance, currentUser);

		//List<Annotation> annotations = resultInstance.getAnnotations();

		assertNotNull(annotations);
		assertEquals(2, annotations.size());

		Iterator<Annotation> annotationsIt = annotations.iterator();
		Annotation annotation1 = annotationsIt.next();

		assertEquals("true", annotation1.getValue());
		assertNotNull(annotation1.getId());
		assertEquals(1, annotation1.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance.getId(), annotation1.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance, annotation1.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation1.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation1.getOntologyTerm());
		assertNotNull(annotation1.getOntologyTerm().getId());
		assertEquals(currentUser, annotation1.getUser());

		Annotation annotation2 = annotationsIt.next();

		assertEquals("fn", annotation2.getValue());
		assertNotNull(annotation2.getId());
		assertEquals(1, annotation2.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance.getId(), annotation2.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance, annotation2.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation2.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation2.getOntologyTerm());
		assertNotNull(annotation2.getOntologyTerm().getId());
		assertEquals(currentUser, annotation2.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());

	}

	@Test
	public void testAnnotateResultInstanceTwiceWithSameExpectancyAnnotationAndInferStatisticalErrorTPAnnotationDuplicatesAllowed() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, true,
				currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance1: " + resultInstance1);

		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);

		//List<Annotation> annotations1 = resultInstance1.getAnnotations();

		assertNotNull(annotations1);
		assertEquals(2, annotations1.size());

		Iterator<Annotation> annotations1It = annotations1.iterator();

		Annotation annotation11 = annotations1It.next();
		logger.debug("annotation11: " + annotation11);
		Date timestamp11 = annotation11.getTimestamp();
		int version11 = annotation11.getVersion();

		logger.debug("timestamp11: " + timestamp11);
		logger.debug("version11: " + version11);

		assertEquals("true", annotation11.getValue());
		assertNotNull(annotation11.getId());
		logger.debug("annotation11.getId(): " + annotation11.getId());
		assertEquals(1, annotation11.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation11.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation11.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation11.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation11.getOntologyTerm());
		assertNotNull(annotation11.getOntologyTerm().getId());
		assertEquals(currentUser, annotation11.getUser());

		Annotation annotation12 = annotations1It.next();
		logger.debug("annotation12: " + annotation12);
		Date timestamp12 = annotation12.getTimestamp();
		int version12 = annotation12.getVersion();

		logger.debug("timestamp12: " + timestamp12);
		logger.debug("version12: " + version12);

		assertEquals("tp", annotation12.getValue());
		assertNotNull(annotation12.getId());
		assertEquals(1, annotation12.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation12.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation12.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation12.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation12.getOntologyTerm());
		assertNotNull(annotation12.getOntologyTerm().getId());
		assertEquals(currentUser, annotation12.getUser());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, true,
				currentUser);

		ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance2: " + resultInstance2);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2, currentUser);

		//List<Annotation> annotations2 = resultInstance2.getAnnotations();

		logger.debug("annotations2: " + annotations2);

		assertNotNull(annotations2);
		for (Annotation annotation : annotations2) {
			logger.debug("annotation.getId(): " + annotation.getId());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			logger.debug("annotation.getAnnotatedModelManagementConstructs(): " + annotation.getAnnotatedModelManagementConstructs());
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getConstrainingModelManagementConstructs(): " + annotation.getConstrainingModelManagementConstructs());
		}
		assertEquals(2, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();

		Annotation annotation21 = annotations2It.next();
		logger.debug("annotation21: " + annotation21);
		Date timestamp21 = annotation21.getTimestamp();
		int version21 = annotation21.getVersion();

		logger.debug("timestamp21: " + timestamp21);
		logger.debug("version21: " + version21);

		assertEquals("true", annotation21.getValue());
		assertNotNull(annotation21.getId());
		logger.debug("annotation21.getId(): " + annotation21.getId());
		assertEquals(1, annotation21.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation21.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation21.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation21.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation21.getOntologyTerm());
		assertNotNull(annotation21.getOntologyTerm().getId());
		assertEquals(currentUser, annotation21.getUser());

		assertEquals(annotation11, annotation21);
		assertEquals(annotation11.getId(), annotation21.getId());
		assertEquals(resultInstance1, resultInstance2);
		assertEquals(resultInstance1.getId(), resultInstance2.getId());
		assertFalse(timestamp11.equals(timestamp21));
		assertTrue(timestamp21.after(timestamp11));

		Annotation annotation22 = annotations2It.next();
		logger.debug("annotation22: " + annotation22);
		Date timestamp22 = annotation22.getTimestamp();
		int version22 = annotation22.getVersion();

		logger.debug("timestamp22: " + timestamp22);
		logger.debug("version22: " + version22);

		assertEquals("tp", annotation22.getValue());
		assertNotNull(annotation22.getId());
		logger.debug("annotation22.getId(): " + annotation22.getId());
		assertEquals(1, annotation22.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation22.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation22.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation22.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation22.getOntologyTerm());
		assertNotNull(annotation22.getOntologyTerm().getId());
		assertEquals(currentUser, annotation22.getUser());

		assertEquals(annotation12, annotation22);
		assertEquals(annotation12.getId(), annotation22.getId());
		assertEquals(resultInstance1, resultInstance2);
		assertEquals(resultInstance1.getId(), resultInstance2.getId());
		assertFalse(timestamp12.equals(timestamp22));
		assertTrue(timestamp22.after(timestamp12));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateResultInstanceTwiceWithDifferentExpectancyAnnotationAndInferStatisticalErrorFPandTPDuplicatesAllowed() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, true,
				currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance1: " + resultInstance1);

		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);

		//List<Annotation> annotations1 = resultInstance1.getAnnotations();

		assertNotNull(annotations1);
		assertEquals(2, annotations1.size());

		Iterator<Annotation> annotations1It = annotations1.iterator();

		Annotation annotation11 = annotations1It.next();
		logger.debug("annotation11: " + annotation11);
		Date timestamp11 = annotation11.getTimestamp();
		int version11 = annotation11.getVersion();

		logger.debug("timestamp11: " + timestamp11);
		logger.debug("version11: " + version11);

		assertEquals("true", annotation11.getValue());
		assertNotNull(annotation11.getId());
		logger.debug("annotation11.getId(): " + annotation11.getId());
		assertEquals(1, annotation11.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation11.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation11.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation11.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation11.getOntologyTerm());
		assertNotNull(annotation11.getOntologyTerm().getId());
		assertEquals(currentUser, annotation11.getUser());

		Annotation annotation12 = annotations1It.next();
		logger.debug("annotation12: " + annotation12);
		Date timestamp12 = annotation12.getTimestamp();
		int version12 = annotation12.getVersion();

		logger.debug("timestamp12: " + timestamp12);
		logger.debug("version12: " + version12);

		assertEquals("tp", annotation12.getValue());
		assertNotNull(annotation12.getId());
		assertEquals(1, annotation12.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation12.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation12.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation12.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation12.getOntologyTerm());
		assertNotNull(annotation12.getOntologyTerm().getId());
		assertEquals(currentUser, annotation12.getUser());

		annotationService.annotate("expectancy", "false", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, true,
				currentUser);

		ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance2: " + resultInstance2);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2, currentUser);

		//List<Annotation> annotations2 = resultInstance2.getAnnotations();

		logger.debug("annotations2: " + annotations2);

		assertNotNull(annotations2);
		assertEquals(4, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();

		Annotation annotation21 = annotations2It.next();
		logger.debug("annotation21: " + annotation21);
		Date timestamp21 = annotation21.getTimestamp();
		int version21 = annotation21.getVersion();

		logger.debug("timestamp21: " + timestamp21);
		logger.debug("version21: " + version21);

		assertEquals("true", annotation21.getValue());
		assertNotNull(annotation21.getId());
		logger.debug("annotation21.getId(): " + annotation21.getId());
		assertEquals(1, annotation21.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation21.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation21.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation21.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation21.getOntologyTerm());
		assertNotNull(annotation21.getOntologyTerm().getId());
		assertEquals(currentUser, annotation21.getUser());

		assertEquals(annotation11, annotation21);
		assertEquals(annotation11.getId(), annotation21.getId());
		assertEquals(resultInstance1, resultInstance2);
		assertEquals(resultInstance1.getId(), resultInstance2.getId());
		assertTrue(timestamp11.equals(timestamp21));
		assertTrue(version11 == version21);

		Annotation annotation22 = annotations2It.next();
		logger.debug("annotation22: " + annotation22);
		Date timestamp22 = annotation22.getTimestamp();
		int version22 = annotation22.getVersion();

		logger.debug("timestamp22: " + timestamp22);
		logger.debug("version22: " + version22);

		assertEquals("tp", annotation22.getValue());
		assertNotNull(annotation22.getId());
		logger.debug("annotation22.getId(): " + annotation22.getId());
		assertEquals(1, annotation22.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation22.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation22.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation22.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation22.getOntologyTerm());
		assertNotNull(annotation22.getOntologyTerm().getId());
		assertEquals(currentUser, annotation22.getUser());

		assertEquals(annotation12, annotation22);
		assertEquals(annotation12.getId(), annotation22.getId());
		assertTrue(timestamp12.equals(timestamp22));
		assertTrue(version12 == version22);

		Annotation annotation23 = annotations2It.next();
		logger.debug("annotation23: " + annotation23);
		Date timestamp23 = annotation23.getTimestamp();
		int version23 = annotation23.getVersion();

		logger.debug("timestamp23: " + timestamp23);
		logger.debug("version23: " + version23);

		assertEquals("false", annotation23.getValue());
		assertNotNull(annotation23.getId());
		logger.debug("annotation23.getId(): " + annotation23.getId());
		assertEquals(1, annotation23.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation23.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation23.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation23.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation23.getOntologyTerm());
		assertNotNull(annotation23.getOntologyTerm().getId());
		assertEquals(currentUser, annotation23.getUser());

		assertFalse(annotation11.equals(annotation23));
		assertFalse(annotation11.getId() == annotation23.getId());

		Annotation annotation24 = annotations2It.next();
		logger.debug("annotation24: " + annotation24);
		Date timestamp24 = annotation24.getTimestamp();
		int version24 = annotation24.getVersion();

		logger.debug("timestamp24: " + timestamp24);
		logger.debug("version24: " + version24);

		assertEquals("fp", annotation24.getValue());
		assertNotNull(annotation24.getId());
		logger.debug("annotation24.getId(): " + annotation24.getId());
		assertEquals(1, annotation24.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation24.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation24.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation24.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation24.getOntologyTerm());
		assertNotNull(annotation24.getOntologyTerm().getId());
		assertEquals(currentUser, annotation24.getUser());

		assertFalse(annotation12.equals(annotation24));
		assertFalse(annotation12.getId() == annotation24.getId());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateResultInstanceTwiceWithSameExpectancyAnnotationAndInferStatisticalErrorTPNoDuplicatesAllowed() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, false,
				currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance1: " + resultInstance1);

		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);

		//List<Annotation> annotations1 = resultInstance1.getAnnotations();

		assertNotNull(annotations1);
		assertEquals(2, annotations1.size());

		Iterator<Annotation> annotations1It = annotations1.iterator();

		Annotation annotation11 = annotations1It.next();
		logger.debug("annotation11: " + annotation11);
		Date timestamp11 = annotation11.getTimestamp();
		int version11 = annotation11.getVersion();

		logger.debug("timestamp11: " + timestamp11);
		logger.debug("version11: " + version11);

		assertEquals("true", annotation11.getValue());
		assertNotNull(annotation11.getId());
		logger.debug("annotation11.getId(): " + annotation11.getId());
		assertEquals(1, annotation11.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation11.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation11.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation11.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation11.getOntologyTerm());
		assertNotNull(annotation11.getOntologyTerm().getId());
		assertEquals(currentUser, annotation11.getUser());

		Annotation annotation12 = annotations1It.next();
		logger.debug("annotation12: " + annotation12);
		Date timestamp12 = annotation12.getTimestamp();
		int version12 = annotation12.getVersion();

		assertEquals("tp", annotation12.getValue());
		assertNotNull(annotation12.getId());
		assertEquals(1, annotation12.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation12.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation12.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation12.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation12.getOntologyTerm());
		assertNotNull(annotation12.getOntologyTerm().getId());
		assertEquals(currentUser, annotation12.getUser());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, false,
				currentUser);

		ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance2: " + resultInstance2);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2, currentUser);

		//List<Annotation> annotations2 = resultInstance2.getAnnotations();

		assertNotNull(annotations2);
		for (Annotation annotation : annotations2) {
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			logger.debug("annotation.getAnnotatedModelManagementConstructs(): " + annotation.getAnnotatedModelManagementConstructs());
			logger.debug("annotation.getValue(): " + annotation.getValue());
		}
		assertEquals(2, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();

		Annotation annotation21 = annotations2It.next();
		logger.debug("annotation21: " + annotation21);
		Date timestamp21 = annotation21.getTimestamp();
		int version21 = annotation21.getVersion();

		logger.debug("timestamp21: " + timestamp21);
		logger.debug("version21: " + version21);

		assertEquals("true", annotation21.getValue());
		assertNotNull(annotation21.getId());
		logger.debug("annotation21.getId(): " + annotation21.getId());
		assertEquals(1, annotation21.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation21.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation21.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation21.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation21.getOntologyTerm());
		assertNotNull(annotation21.getOntologyTerm().getId());
		assertEquals(currentUser, annotation21.getUser());

		assertEquals(annotation11, annotation21);
		assertEquals(annotation11.getId(), annotation21.getId());
		assertEquals(resultInstance1, resultInstance2);
		assertEquals(resultInstance1.getId(), resultInstance2.getId());
		assertFalse(timestamp11.equals(timestamp21));
		assertTrue(timestamp21.after(timestamp11));

		Annotation annotation22 = annotations2It.next();
		logger.debug("annotation22: " + annotation22);
		Date timestamp22 = annotation22.getTimestamp();
		int version22 = annotation22.getVersion();

		logger.debug("timestamp22: " + timestamp22);
		logger.debug("version22: " + version22);

		assertEquals("tp", annotation22.getValue());
		assertNotNull(annotation22.getId());
		assertEquals(1, annotation22.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation22.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation22.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation22.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation22.getOntologyTerm());
		assertNotNull(annotation22.getOntologyTerm().getId());
		assertEquals(currentUser, annotation22.getUser());

		assertEquals(annotation12, annotation22);
		assertEquals(annotation12.getId(), annotation22.getId());
		assertFalse(timestamp12.equals(timestamp22));
		assertTrue(timestamp22.after(timestamp12));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testAnnotateResultInstanceTwiceWithDifferentExpectancyAnnotationAndInferStatisticalErrorTPandFPNoDuplicatesAllowed() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());

		annotationService.annotate("expectancy", "true", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, false,
				currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance1: " + resultInstance1);

		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);

		//List<Annotation> annotations1 = resultInstance1.getAnnotations();

		assertNotNull(annotations1);
		assertEquals(2, annotations1.size());

		Iterator<Annotation> annotations1It = annotations1.iterator();

		Annotation annotation11 = annotations1It.next();
		logger.debug("annotation11: " + annotation11);
		Date timestamp11 = annotation11.getTimestamp();
		int version11 = annotation11.getVersion();

		logger.debug("timestamp11: " + timestamp11);
		logger.debug("version11: " + version11);

		assertEquals("true", annotation11.getValue());
		assertNotNull(annotation11.getId());
		logger.debug("annotation11.getId(): " + annotation11.getId());
		assertEquals(1, annotation11.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation11.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation11.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation11.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation11.getOntologyTerm());
		assertNotNull(annotation11.getOntologyTerm().getId());
		assertEquals(currentUser, annotation11.getUser());

		Annotation annotation12 = annotations1It.next();
		logger.debug("annotation12: " + annotation12);
		Date timestamp12 = annotation12.getTimestamp();
		int version12 = annotation12.getVersion();

		assertEquals("tp", annotation12.getValue());
		assertNotNull(annotation12.getId());
		assertEquals(1, annotation12.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance1.getId(), annotation12.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance1, annotation12.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation12.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation12.getOntologyTerm());
		assertNotNull(annotation12.getOntologyTerm().getId());
		assertEquals(currentUser, annotation12.getUser());

		annotationService.annotate("expectancy", "false", resultInstanceId, queryResult.getId(), constrainingModelManagementConstructs, false,
				currentUser);

		ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstanceId);
		logger.debug("resultInstance2: " + resultInstance2);

		List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2, currentUser);

		//List<Annotation> annotations2 = resultInstance2.getAnnotations();

		assertNotNull(annotations2);
		for (Annotation annotation : annotations2) {
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			logger.debug("annotation.getAnnotatedModelManagementConstructs(): " + annotation.getAnnotatedModelManagementConstructs());
			logger.debug("annotation.getValue(): " + annotation.getValue());
		}
		assertEquals(2, annotations2.size());

		Iterator<Annotation> annotations2It = annotations2.iterator();

		Annotation annotation21 = annotations2It.next();
		logger.debug("annotation21: " + annotation21);
		Date timestamp21 = annotation21.getTimestamp();
		int version21 = annotation21.getVersion();

		logger.debug("timestamp21: " + timestamp21);
		logger.debug("version21: " + version21);

		assertEquals("false", annotation21.getValue());
		assertNotNull(annotation21.getId());
		logger.debug("annotation21.getId(): " + annotation21.getId());
		assertEquals(1, annotation21.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation21.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation21.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation21.getConstrainingModelManagementConstructs().size());
		assertEquals(expectancyOT, annotation21.getOntologyTerm());
		assertNotNull(annotation21.getOntologyTerm().getId());
		assertEquals(currentUser, annotation21.getUser());

		assertEquals(annotation11, annotation21);
		assertEquals(annotation11.getId(), annotation21.getId());
		assertEquals(resultInstance1, resultInstance2);
		assertEquals(resultInstance1.getId(), resultInstance2.getId());
		assertFalse(timestamp11.equals(timestamp21));
		assertTrue(timestamp21.after(timestamp11));

		Annotation annotation22 = annotations2It.next();
		logger.debug("annotation22: " + annotation22);
		Date timestamp22 = annotation22.getTimestamp();
		int version22 = annotation22.getVersion();

		assertEquals("fp", annotation22.getValue());
		assertNotNull(annotation22.getId());
		assertEquals(1, annotation22.getAnnotatedModelManagementConstructs().size());
		assertEquals(resultInstance2.getId(), annotation22.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(resultInstance2, annotation22.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(0, annotation22.getConstrainingModelManagementConstructs().size());
		assertEquals(statisticalErrorOT, annotation22.getOntologyTerm());
		assertNotNull(annotation22.getOntologyTerm().getId());
		assertEquals(currentUser, annotation22.getUser());

		assertEquals(annotation12, annotation22);
		assertEquals(annotation12.getId(), annotation22.getId());
		assertFalse(timestamp12.equals(timestamp22));
		assertTrue(timestamp22.after(timestamp12));

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testPropagateTPandFPAnnotationFromResultInstancesToMappingsThatProducedTheResultInstances() {
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Mapping europeMapping = null;
		Mapping africaMapping = null;

		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			constructsToAnnotate.add(mapping);
			mapping = mappingRepository.fetchConstructs(mapping);
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope"))
				europeMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentAfrica"))
				africaMapping = mapping;
		}

		logger.debug("europeMapping: " + europeMapping);
		logger.debug("africaMapping: " + africaMapping);

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		//TODO this tests that each resultInstance has the mapping that produced that resultInstance associated with it, should move into the test class for the expander
		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			assertNotNull(resultInstance.getId());
			assertEquals(1, resultInstance.getMappings().size());
			String country = resultInstance.getResultValue("city.country").getValue();
			logger.debug("country: " + country);
			if (country.equals("DK") || country.equals("GB") || country.equals("D") || country.equals("IRL")) {
				logger.debug("europe");
				assertEquals(europeMapping, resultInstance.getMappings().iterator().next());
				if (country.equals("D")) {
					logger.debug("D");
					annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(), null, true, currentUser);
					ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
					List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
							currentUser);
					assertEquals(2, annotations2.size());
					//assertEquals(2, resultInstance2.getAnnotations().size());
					//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					annotationsOfResultInstancesToPropagate.addAll(annotations2);
				} else if (country.equals("GB")) {
					logger.debug("GB");
					annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(), null, true, currentUser);
					ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
					List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
							currentUser);
					assertEquals(2, annotations2.size());
					//assertEquals(2, resultInstance2.getAnnotations().size());
					//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					annotationsOfResultInstancesToPropagate.addAll(annotations2);
				}
			} else if (country.equals("DZ") || country.equals("GH") || country.equals("MA") || country.equals("RT") || country.equals("WSA")) {
				logger.debug("africa");
				assertEquals(africaMapping, resultInstance.getMappings().iterator().next());
			} else
				logger.error("unexpected country");
		}

		for (Annotation annotation : annotationsOfResultInstancesToPropagate) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			assertNotNull(annotation.getId());
		}

		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingModelManagementConstructs,
				false, currentUser);

		Mapping eMapping = mappingService.findMapping(europeMapping.getId());
		Mapping aMapping = mappingService.findMapping(africaMapping.getId());

		//List<Annotation> eAnnotations = eMapping.getAnnotations();
		List<Annotation> eAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(eMapping, currentUser);

		assertEquals(4, eAnnotations.size());

		Annotation eAnnotationPr = eAnnotations.get(0);
		Annotation eAnnotationRec = eAnnotations.get(1);
		Annotation eAnnotationFM = eAnnotations.get(2);
		Annotation eAnnotationFrAnRes = eAnnotations.get(3);

		List<Annotation> aAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(aMapping, currentUser);

		assertEquals(1, aAnnotations.size());

		//Annotation aAnnotationFrAnRes = aMapping.getAnnotations().get(0);
		Annotation aAnnotationFrAnRes = aAnnotations.get(0);

		double precisionValue = 4.0 / (4.0 + 3.0);
		double recallValue = 4.0 / (4.0 + 0.0);
		double fmeasureValue = 2.0 * (precisionValue * recallValue) / (precisionValue + recallValue);
		double efractionOfAnnotatedResults = (4.0 + 3.0 + 0.0) / 10.0;

		double afractionOfAnnotatedResults = (0.0 + 0.0 + 0.0) / 9.0;

		logger.debug("precisionValue: " + precisionValue); //0.5714285714285714
		logger.debug("recallValue: " + recallValue); //1.0
		logger.debug("fmeasureValue: " + fmeasureValue); //0.7272727272727273
		logger.debug("efractionOfAnnotatedResults: " + efractionOfAnnotatedResults); //0.7
		logger.debug("afractionOfAnnotatedResults: " + afractionOfAnnotatedResults); //0.0

		assertEquals(new Double(precisionValue).toString(), eAnnotationPr.getValue());
		assertNotNull(eAnnotationPr.getId());
		assertEquals(1, eAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, eAnnotationPr.getOntologyTerm());
		assertNotNull(eAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationPr.getUser());

		assertEquals(new Double(recallValue).toString(), eAnnotationRec.getValue());
		assertNotNull(eAnnotationRec.getId());
		assertEquals(1, eAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, eAnnotationRec.getOntologyTerm());
		assertNotNull(eAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationRec.getUser());

		assertEquals(new Double(fmeasureValue).toString(), eAnnotationFM.getValue());
		assertNotNull(eAnnotationFM.getId());
		assertEquals(1, eAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, eAnnotationFM.getOntologyTerm());
		assertNotNull(eAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFM.getUser());

		assertEquals(new Double(efractionOfAnnotatedResults).toString(), eAnnotationFrAnRes.getValue());
		assertNotNull(eAnnotationFrAnRes.getId());
		assertEquals(1, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, eAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(eAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFrAnRes.getUser());

		assertEquals(new Double(0.0).toString(), aAnnotationFrAnRes.getValue());
		assertNotNull(aAnnotationFrAnRes.getId());
		assertEquals(1, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, aAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(aAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationFrAnRes.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testPropagateTPFPandFNAnnotationFromResultInstancesToMappingsThatProducedTheResultInstances() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();

		Mapping europeMapping = null;
		Mapping africaMapping = null;

		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			constructsToAnnotate.add(mapping);
			mapping = mappingRepository.fetchConstructs(mapping);
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope"))
				europeMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentAfrica"))
				africaMapping = mapping;
		}

		logger.debug("europeMapping: " + europeMapping);
		logger.debug("africaMapping: " + africaMapping);

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		assertEquals(19, queryResult.getResultInstances().size());

		ResultInstance otherResultInstance = resultInstanceRepository.find(resultInstanceId);

		ResultInstance missingResultInstance = new ResultInstance();
		missingResultInstance.setResultType(otherResultInstance.getResultType());
		ResultValue value1 = new ResultValue("city.name", "Edinburgh");
		ResultValue value2 = new ResultValue("city.country", "GB");
		ResultValue value3 = new ResultValue("city.province", "Lothian");
		ResultValue value4 = new ResultValue("city.population", "447600");
		ResultValue value5 = new ResultValue("city.longitude", "-3.18333");
		ResultValue value6 = new ResultValue("city.latitude", "55.9167");

		missingResultInstance.addResultValue("city.name", value1);
		missingResultInstance.addResultValue("city.country", value2);
		missingResultInstance.addResultValue("city.province", value3);
		missingResultInstance.addResultValue("city.population", value4);
		missingResultInstance.addResultValue("city.longitude", value5);
		missingResultInstance.addResultValue("city.latitude", value6);

		missingResultInstance.setQuery(queryResult.getQuery());
		missingResultInstance.setUserSpecified(true);
		missingResultInstance.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance to queryResult");
		queryResult.addResultInstance(missingResultInstance);
		logger.debug("added missing resultInstance to queryResult");

		resultInstanceRepository.save(missingResultInstance);
		entityManager.flush();

		annotationService.annotate("expectancy", "true", missingResultInstance, queryResult, null, true, currentUser);
		queryResultRepository.update(queryResult);
		queryResultRepository.flush();

		entityManager.flush();

		assertEquals(20, queryResult.getResultInstances().size());
		assertNotNull(missingResultInstance.getId());

		ResultInstance resultInstance1 = resultInstanceRepository.find(missingResultInstance.getId());
		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);
		assertEquals(2, annotations1.size());
		//assertEquals(2, resultInstance1.getAnnotations().size());
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance1.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations1);

		assertEquals(2, resultInstance1.getMappings().size());

		//TODO this tests that each resultInstance has the mapping that produced that resultInstance associated with it, should move into the test class for the expander
		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			assertNotNull(resultInstance.getId());
			logger.debug("resultInstance.getResultValue(city.name):" + resultInstance.getResultValue("city.name"));
			logger.debug("resultInstance.getMappings().size(): " + resultInstance.getMappings().size());
			if (resultInstance.getResultValue("city.name").getValue().equals("Edinburgh")) {
				logger.debug("found Edinburgh");
				assertEquals(2, resultInstance.getMappings().size());
			} else {
				logger.debug("didn't find Edinburgh");
				assertEquals(1, resultInstance.getMappings().size());

				String country = resultInstance.getResultValue("city.country").getValue();
				logger.debug("country: " + country);
				if (country.equals("DK") || country.equals("GB") || country.equals("D") || country.equals("IRL")) {
					logger.debug("europe");
					assertEquals(europeMapping, resultInstance.getMappings().iterator().next());
					if (country.equals("D")) {
						logger.debug("D");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (country.equals("GB")) {
						logger.debug("GB");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					}
				} else if (country.equals("DZ") || country.equals("GH") || country.equals("MA") || country.equals("RT") || country.equals("WSA")) {
					logger.debug("africa");
					assertEquals(africaMapping, resultInstance.getMappings().iterator().next());
				} else
					logger.error("unexpected country");
			}
		}

		for (Annotation annotation : annotationsOfResultInstancesToPropagate) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			assertNotNull(annotation.getId());
		}

		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingModelManagementConstructs,
				false, currentUser);

		Mapping eMapping = mappingService.findMapping(europeMapping.getId());
		Mapping aMapping = mappingService.findMapping(africaMapping.getId());

		List<Annotation> eAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(eMapping, currentUser);
		List<Annotation> aAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(aMapping, currentUser);

		//assertEquals(4, eMapping.getAnnotations().size());
		//assertEquals(1, aMapping.getAnnotations().size());

		assertEquals(4, eAnnotations.size());
		assertEquals(1, aAnnotations.size());

		//List<Annotation> eAnnotations = eMapping.getAnnotations();
		Annotation eAnnotationPr = eAnnotations.get(0);
		Annotation eAnnotationRec = eAnnotations.get(1);
		Annotation eAnnotationFM = eAnnotations.get(2);
		Annotation eAnnotationFrAnRes = eAnnotations.get(3);

		//Annotation aAnnotationFrAnRes = aMapping.getAnnotations().get(0);
		Annotation aAnnotationFrAnRes = aAnnotations.get(0);

		double precisionValue = 4.0 / (4.0 + 3.0);
		double recallValue = 4.0 / (4.0 + 1.0);
		double fmeasureValue = 2.0 * (precisionValue * recallValue) / (precisionValue + recallValue);
		double efractionOfAnnotatedResults = (4.0 + 3.0 + 1.0) / 11.0;

		double afractionOfAnnotatedResults = (0.0 + 0.0 + 1.0) / 10.0;

		logger.debug("precisionValue: " + precisionValue); //0.5714285714285714
		logger.debug("recallValue: " + recallValue); //0.8
		logger.debug("fmeasureValue: " + fmeasureValue); //0.6666666666666666
		logger.debug("efractionOfAnnotatedResults: " + efractionOfAnnotatedResults); //0.7272727272727273

		logger.debug("afractionOfAnnotatedResults: " + afractionOfAnnotatedResults); //0.1

		assertEquals(new Double(precisionValue).toString(), eAnnotationPr.getValue());
		assertNotNull(eAnnotationPr.getId());
		assertEquals(1, eAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, eAnnotationPr.getOntologyTerm());
		assertNotNull(eAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationPr.getUser());

		assertEquals(new Double(recallValue).toString(), eAnnotationRec.getValue());
		assertNotNull(eAnnotationRec.getId());
		assertEquals(1, eAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, eAnnotationRec.getOntologyTerm());
		assertNotNull(eAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationRec.getUser());

		assertEquals(new Double(fmeasureValue).toString(), eAnnotationFM.getValue());
		assertNotNull(eAnnotationFM.getId());
		assertEquals(1, eAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, eAnnotationFM.getOntologyTerm());
		assertNotNull(eAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFM.getUser());

		assertEquals(new Double(efractionOfAnnotatedResults).toString(), eAnnotationFrAnRes.getValue());
		assertNotNull(eAnnotationFrAnRes.getId());
		assertEquals(1, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, eAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(eAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFrAnRes.getUser());

		assertEquals(new Double(afractionOfAnnotatedResults).toString(), aAnnotationFrAnRes.getValue());
		assertNotNull(aAnnotationFrAnRes.getId());
		assertEquals(1, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, aAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(aAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationFrAnRes.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}

	@Test
	public void testPropagateAnnotationsFromMappingsToQueryResults() {

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();

		Mapping europeMapping = null;
		Mapping africaMapping = null;

		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			constructsToAnnotate.add(mapping);
			mapping = mappingRepository.fetchConstructs(mapping);
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope"))
				europeMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentAfrica"))
				africaMapping = mapping;
		}

		logger.debug("europeMapping: " + europeMapping);
		logger.debug("africaMapping: " + africaMapping);

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		assertEquals(19, queryResult.getResultInstances().size());

		ResultInstance otherResultInstance = resultInstanceRepository.find(resultInstanceId);

		ResultInstance missingResultInstance = new ResultInstance();
		missingResultInstance.setResultType(otherResultInstance.getResultType());
		ResultValue value1 = new ResultValue("city.name", "Edinburgh");
		ResultValue value2 = new ResultValue("city.country", "GB");
		ResultValue value3 = new ResultValue("city.province", "Lothian");
		ResultValue value4 = new ResultValue("city.population", "447600");
		ResultValue value5 = new ResultValue("city.longitude", "-3.18333");
		ResultValue value6 = new ResultValue("city.latitude", "55.9167");

		missingResultInstance.addResultValue("city.name", value1);
		missingResultInstance.addResultValue("city.country", value2);
		missingResultInstance.addResultValue("city.province", value3);
		missingResultInstance.addResultValue("city.population", value4);
		missingResultInstance.addResultValue("city.longitude", value5);
		missingResultInstance.addResultValue("city.latitude", value6);

		missingResultInstance.setQuery(queryResult.getQuery());
		missingResultInstance.setUserSpecified(true);
		missingResultInstance.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance to queryResult");
		queryResult.addResultInstance(missingResultInstance);
		logger.debug("added missing resultInstance to queryResult");

		resultInstanceRepository.save(missingResultInstance);
		entityManager.flush();

		annotationService.annotate("expectancy", "true", missingResultInstance, queryResult, null, true, currentUser);

		entityManager.flush();

		ResultInstance resultInstance1 = resultInstanceRepository.find(missingResultInstance.getId());
		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);

		assertEquals(2, annotations1.size());
		//assertEquals(2, resultInstance1.getAnnotations().size());
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance1.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations1);

		assertEquals(2, resultInstance1.getMappings().size());

		ResultInstance missingResultInstance2 = new ResultInstance();
		missingResultInstance2.setResultType(otherResultInstance.getResultType());
		ResultValue value21 = new ResultValue("city.name", "Safi");
		ResultValue value22 = new ResultValue("city.country", "MA");
		ResultValue value23 = new ResultValue("city.province", "Morocco");
		ResultValue value24 = new ResultValue("city.population", "376038");
		ResultValue value25 = new ResultValue("city.longitude", "0");
		ResultValue value26 = new ResultValue("city.latitude", "0");

		missingResultInstance2.addResultValue("city.name", value21);
		missingResultInstance2.addResultValue("city.country", value22);
		missingResultInstance2.addResultValue("city.province", value23);
		missingResultInstance2.addResultValue("city.population", value24);
		missingResultInstance2.addResultValue("city.longitude", value25);
		missingResultInstance2.addResultValue("city.latitude", value26);

		missingResultInstance2.setQuery(queryResult.getQuery());
		missingResultInstance2.setUserSpecified(true);
		missingResultInstance2.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance2 to queryResult");
		queryResult.addResultInstance(missingResultInstance2);
		logger.debug("added missing resultInstance to queryResult");

		annotationService.annotate("expectancy", "true", missingResultInstance2, queryResult, null, true, currentUser);

		ResultInstance resultInstance21 = resultInstanceRepository.find(missingResultInstance2.getId());
		List<Annotation> annotations21 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance21, currentUser);
		assertEquals(2, annotations21.size());
		//assertEquals(2, resultInstance21.getAnnotations().size());
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance21.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations21);

		assertEquals(2, resultInstance21.getMappings().size());

		/*
		ResultInstance missingResultInstance3 = new ResultInstance();
		missingResultInstance3.setResultType(otherResultInstance.getResultType());
		ResultValue value31 = new ResultValue("city.name", "Constantine");
		ResultValue value32 = new ResultValue("city.country", "DZ");
		ResultValue value33 = new ResultValue("city.province", "Algeria");
		ResultValue value34 = new ResultValue("city.population", "440842");
		ResultValue value35 = new ResultValue("city.longitude", "0");
		ResultValue value36 = new ResultValue("city.latitude", "0");

		missingResultInstance3.addResultValue("city.name", value31);
		missingResultInstance3.addResultValue("city.country", value32);
		missingResultInstance3.addResultValue("city.province", value33);
		missingResultInstance3.addResultValue("city.population", value34);
		missingResultInstance3.addResultValue("city.longitude", value35);
		missingResultInstance3.addResultValue("city.latitude", value36);

		missingResultInstance3.setUserSpecified(true);
		missingResultInstance3.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance3 to queryResult");
		queryResult.addResultInstance(missingResultInstance3);
		logger.debug("added missing resultInstance to queryResult");

		annotationService.annotate("expectancy", "true", missingResultInstance3, null, true, currentUser);
		*/

		queryResultRepository.update(queryResult);
		queryResultRepository.flush();

		assertEquals(21, queryResult.getResultInstances().size());
		assertNotNull(missingResultInstance.getId());
		assertNotNull(missingResultInstance2.getId());
		//assertNotNull(missingResultInstance3.getId());

		//TODO this tests that each resultInstance has the mapping that produced that resultInstance associated with it, should move into the test class for the expander
		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			assertNotNull(resultInstance.getId());
			logger.debug("resultInstance.getResultValue(city.name):" + resultInstance.getResultValue("city.name"));
			logger.debug("resultInstance.getMappings().size(): " + resultInstance.getMappings().size());
			if (resultInstance.getResultValue("city.name").getValue().equals("Edinburgh")
					|| resultInstance.getResultValue("city.name").getValue().equals("Safi")) {
				//|| resultInstance.getResultValue("city.name").getValue().equals("Constantine")) {
				logger.debug("found Edinburgh, Safi or Constantine");
				assertEquals(2, resultInstance.getMappings().size());
			} else {
				logger.debug("didn't find Edinburgh or Safi"); //or Constantine");
				assertEquals(1, resultInstance.getMappings().size());

				String country = resultInstance.getResultValue("city.country").getValue();
				logger.debug("country: " + country);
				if (country.equals("DK") || country.equals("GB") || country.equals("D") || country.equals("IRL")) {
					logger.debug("europe");
					assertEquals(europeMapping, resultInstance.getMappings().iterator().next());
					if (country.equals("D")) {
						logger.debug("D");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (country.equals("GB")) {
						logger.debug("GB");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					}
				} else if (country.equals("DZ") || country.equals("GH") || country.equals("MA") || country.equals("RT") || country.equals("WSA")) {
					logger.debug("africa");
					assertEquals(africaMapping, resultInstance.getMappings().iterator().next());
					if (country.equals("WSA")) {
						logger.debug("WSA");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (country.equals("GH")) {
						logger.debug("GH");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						assertEquals(2, annotations2.size());
						//assertEquals(2, resultInstance2.getAnnotations().size());
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					}
				} else
					logger.error("unexpected country");
			}
		}

		for (Annotation annotation : annotationsOfResultInstancesToPropagate) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			assertNotNull(annotation.getId());
		}

		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingModelManagementConstructs,
				false, currentUser);

		Mapping eMapping = mappingService.findMapping(europeMapping.getId());
		Mapping aMapping = mappingService.findMapping(africaMapping.getId());

		//List<Annotation> eAnnotations = eMapping.getAnnotations();
		//List<Annotation> aAnnotations = aMapping.getAnnotations();
		List<Annotation> eAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(eMapping, currentUser);
		List<Annotation> aAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(aMapping, currentUser);

		//assertEquals(4, eMapping.getAnnotations().size());
		//assertEquals(4, aMapping.getAnnotations().size());
		assertEquals(4, eAnnotations.size());
		assertEquals(4, aAnnotations.size());

		Annotation eAnnotationPr = eAnnotations.get(0);
		Annotation eAnnotationRec = eAnnotations.get(1);
		Annotation eAnnotationFM = eAnnotations.get(2);
		Annotation eAnnotationFrAnRes = eAnnotations.get(3);

		Annotation aAnnotationPr = aAnnotations.get(0);
		Annotation aAnnotationRec = aAnnotations.get(1);
		Annotation aAnnotationFM = aAnnotations.get(2);
		Annotation aAnnotationFrAnRes = aAnnotations.get(3);

		double ePrecisionValue = 4.0 / (4.0 + 3.0);
		double eRecallValue = 4.0 / (4.0 + 2.0);
		double eFmeasureValue = 2.0 * (ePrecisionValue * eRecallValue) / (ePrecisionValue + eRecallValue);
		double eFractionOfAnnotatedResults = (4.0 + 3.0 + 2.0) / 12.0;

		double aPrecisionValue = 2.0 / (2.0 + 1.0);
		double aRecallValue = 2.0 / (2.0 + 2.0);
		double aFmeasureValue = 2.0 * (aPrecisionValue * aRecallValue) / (aPrecisionValue + aRecallValue);
		double aFractionOfAnnotatedResults = (2.0 + 1.0 + 2.0) / 11.0;

		logger.debug("ePrecisionValue: " + ePrecisionValue);
		logger.debug("eRecallValue: " + eRecallValue);
		logger.debug("eFmeasureValue: " + eFmeasureValue);
		logger.debug("eFractionOfAnnotatedResults: " + eFractionOfAnnotatedResults);

		logger.debug("aPrecisionValue: " + aPrecisionValue);
		logger.debug("aRecallValue: " + aRecallValue);
		logger.debug("aFmeasureValue: " + aFmeasureValue);
		logger.debug("aFractionOfAnnotatedResults: " + aFractionOfAnnotatedResults);

		assertEquals(new Double(ePrecisionValue).toString(), eAnnotationPr.getValue());
		assertNotNull(eAnnotationPr.getId());
		assertEquals(1, eAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, eAnnotationPr.getOntologyTerm());
		assertNotNull(eAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationPr.getUser());

		assertEquals(new Double(eRecallValue).toString(), eAnnotationRec.getValue());
		assertNotNull(eAnnotationRec.getId());
		assertEquals(1, eAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, eAnnotationRec.getOntologyTerm());
		assertNotNull(eAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationRec.getUser());

		assertEquals(new Double(eFmeasureValue).toString(), eAnnotationFM.getValue());
		assertNotNull(eAnnotationFM.getId());
		assertEquals(1, eAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, eAnnotationFM.getOntologyTerm());
		assertNotNull(eAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFM.getUser());

		assertEquals(new Double(eFractionOfAnnotatedResults).toString(), eAnnotationFrAnRes.getValue());
		assertNotNull(eAnnotationFrAnRes.getId());
		assertEquals(1, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(eMapping.getId(), eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(eMapping, eAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, eAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), eAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, eAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(eAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, eAnnotationFrAnRes.getUser());

		assertEquals(new Double(aPrecisionValue).toString(), aAnnotationPr.getValue());
		assertNotNull(aAnnotationPr.getId());
		assertEquals(1, aAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, aAnnotationPr.getOntologyTerm());
		assertNotNull(aAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationPr.getUser());

		assertEquals(new Double(aRecallValue).toString(), aAnnotationRec.getValue());
		assertNotNull(aAnnotationRec.getId());
		assertEquals(1, aAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, aAnnotationRec.getOntologyTerm());
		assertNotNull(aAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationRec.getUser());

		assertEquals(new Double(aFmeasureValue).toString(), aAnnotationFM.getValue());
		assertNotNull(aAnnotationFM.getId());
		assertEquals(1, aAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, aAnnotationFM.getOntologyTerm());
		assertNotNull(aAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationFM.getUser());

		assertEquals(new Double(aFractionOfAnnotatedResults).toString(), aAnnotationFrAnRes.getValue());
		assertNotNull(aAnnotationFrAnRes.getId());
		assertEquals(1, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(aMapping.getId(), aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(aMapping, aAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, aAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), aAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, aAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(aAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, aAnnotationFrAnRes.getUser());

		//TODO think about this: it's not really the mapping annotations that get propagated,
		//it's the annotation of the resultInstances that are retrieved by these mappings for this query
		//annotation of queryResult is constrained by the mappings used to expand the query for that particular queryResult
		Set<Annotation> annotationsToBePropagated = new LinkedHashSet<Annotation>();
		annotationsToBePropagated.addAll(eAnnotations);
		annotationsToBePropagated.addAll(aAnnotations);

		Set<ModelManagementConstruct> constructsToBeAnnotated = new LinkedHashSet<ModelManagementConstruct>();
		QueryResult updatedQueryResult = queryResultService.findQueryResult(queryResult.getId());
		constructsToBeAnnotated.add(updatedQueryResult);

		Set<ModelManagementConstruct> constrainingModelManagementConstructsMappings = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructsMappings.add(eMapping);
		constrainingModelManagementConstructsMappings.add(aMapping);

		annotationService.propagateAnnotation(annotationsToBePropagated, constructsToBeAnnotated, constrainingModelManagementConstructsMappings,
				false, currentUser);

		QueryResult fetchedQueryResult = queryResultService.findQueryResult(queryResult.getId());

		List<Annotation> qrAnnotations = annotationRepository
				.getAnnotationsForModelManagementConstructProvidedByUser(fetchedQueryResult, currentUser);
		//List<Annotation> qrAnnotations = fetchedQueryResult.getAnnotations();

		assertEquals(4, qrAnnotations.size());
		//assertEquals(4, fetchedQueryResult.getAnnotations().size());

		Annotation qrAnnotationPr = qrAnnotations.get(0);
		Annotation qrAnnotationRec = qrAnnotations.get(1);
		Annotation qrAnnotationFM = qrAnnotations.get(2);
		Annotation qrAnnotationFrAnRes = qrAnnotations.get(3);

		double qrPrecisionValue = 6.0 / (6.0 + 4.0);
		double qrRecallValue = 6.0 / (6.0 + 2.0);
		double qrFmeasureValue = 2.0 * (qrPrecisionValue * qrRecallValue) / (qrPrecisionValue + qrRecallValue);
		double qrFractionOfAnnotatedResults = (6.0 + 4.0 + 2.0) / 21.0;

		logger.debug("qrPrecisionValue: " + qrPrecisionValue);
		logger.debug("qrRecallValue: " + qrRecallValue);
		logger.debug("qrFmeasureValue: " + qrFmeasureValue);
		logger.debug("qrFractionOfAnnotatedResults: " + qrFractionOfAnnotatedResults);

		assertEquals(new Double(qrPrecisionValue).toString(), qrAnnotationPr.getValue());
		assertNotNull(qrAnnotationPr.getId());
		assertEquals(1, qrAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(precisionOT, qrAnnotationPr.getOntologyTerm());
		assertNotNull(qrAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, qrAnnotationPr.getUser());

		assertEquals(new Double(qrRecallValue).toString(), qrAnnotationRec.getValue());
		assertNotNull(qrAnnotationRec.getId());
		assertEquals(1, qrAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(recallOT, qrAnnotationRec.getOntologyTerm());
		assertNotNull(qrAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, qrAnnotationRec.getUser());

		assertEquals(new Double(qrFmeasureValue).toString(), qrAnnotationFM.getValue());
		assertNotNull(qrAnnotationFM.getId());
		assertEquals(1, qrAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(fmeasureOT, qrAnnotationFM.getOntologyTerm());
		assertNotNull(qrAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, qrAnnotationFM.getUser());

		assertEquals(new Double(qrFractionOfAnnotatedResults).toString(), qrAnnotationFrAnRes.getValue());
		assertNotNull(qrAnnotationFrAnRes.getId());
		assertEquals(1, qrAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(fractionOfAnnotatedResultsOT, qrAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(qrAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, qrAnnotationFrAnRes.getUser());

		List<Annotation> allAnnotations = annotationRepository.findAll();
		logger.debug("allAnnotations: " + allAnnotations);
		logger.debug("allAnnotations.size(): " + allAnnotations.size());
	}
}
