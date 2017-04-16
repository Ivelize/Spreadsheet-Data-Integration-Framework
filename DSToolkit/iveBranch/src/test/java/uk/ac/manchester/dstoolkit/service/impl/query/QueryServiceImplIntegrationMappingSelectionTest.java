package uk.ac.manchester.dstoolkit.service.impl.query;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;

public class QueryServiceImplIntegrationMappingSelectionTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(QueryServiceImplIntegrationMappingSelectionTest.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Autowired
	@Qualifier("annotationService")
	private AnnotationService annotationService;

	@Autowired
	@Qualifier("ontologyTermService")
	private OntologyTermService ontologyTermService;

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	Long queryId;
	DataSource mondialDS;
	DataSource mondialCityProvinceCountryContinentEuropeNoRenameDS;
	DataSource mondialCityProvinceCountryContinentAfricaNoRenameDS;
	DataSource mondialCityProvinceNACountryContinentEuropeNoRenameDS;
	DataSource mondialCityProvinceNACountryContinentAfricaNoRenameDS;

	//TODO add the test below somewhere else; the expander isn't assigning the correct mapping to the operators, affecting the calculation of precision and recall - check this

	@Override
	@Before
	public void setUp() {
		super.setUp();

		String selectStarFromQuery = "Select * from City";
		String queryName = "AllCities";
		Schema mondialIntegrSchema = schemaRepository.getSchemaByName("MondialIntegr");

		mondialDS = dataSourceRepository.getDataSourceWithSchemaName("Mondial");
		mondialCityProvinceCountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		mondialCityProvinceCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");
		mondialCityProvinceNACountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceNACountryContinentEurope");
		mondialCityProvinceNACountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceNACountryContinentAfrica");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialIntegrSchema);
		//query.addDataSource(mondialDS);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
		query.addDataSource(mondialCityProvinceNACountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceNACountryContinentAfricaNoRenameDS);
		query.setUser(currentUser);

		queryService.addQuery(query);

		queryId = query.getId();

		if (ontologyTermRepository.getOntologyTermWithName("precision") == null) {

			OntologyTerm expectancyOT = new OntologyTerm("expectancy", DataType.BOOLEAN);
			OntologyTerm precisionOT = new OntologyTerm("precision", DataType.DOUBLE);
			OntologyTerm recallOT = new OntologyTerm("recall", DataType.DOUBLE);
			OntologyTerm fmeasureOT = new OntologyTerm("f-measure", DataType.DOUBLE);
			OntologyTerm fractionOfAnnotatedResultsOT = new OntologyTerm("fractionOfAnnotatedResults", DataType.DOUBLE);

			Set<String> statisticalErrorValues = new LinkedHashSet<String>();
			statisticalErrorValues.add("tp");
			statisticalErrorValues.add("fp");
			statisticalErrorValues.add("fn");

			OntologyTerm statisticalErrorOT = new OntologyTerm("statisticalError", statisticalErrorValues);

			ontologyTermService.addOntologyTerm(statisticalErrorOT);
			ontologyTermService.addOntologyTerm(expectancyOT);
			ontologyTermService.addOntologyTerm(precisionOT);
			ontologyTermService.addOntologyTerm(recallOT);
			ontologyTermService.addOntologyTerm(fmeasureOT);
			ontologyTermService.addOntologyTerm(fractionOfAnnotatedResultsOT);
		}

		//run query over mondialCityProvinceCountryContinentEuropeNoRenameDS, mondialCityProvinceCountryContinentAfricaNoRenameDS, mondialCityProvinceNACountryContinentEuropeNoRenameDS, mondialCityProvinceNACountryContinentAfricaNoRenameDS, annotate results and mappings

		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		logger.debug("resultInstances.size(): " + resultInstances.size());

		Long resultInstanceId = resultInstances.get(0).getId();

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();

		Mapping europeProvinceMapping = null;
		Mapping africaProvinceMapping = null;
		Mapping europeProvinceNAMapping = null;
		Mapping africaProvinceNAMapping = null;

		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			constructsToAnnotate.add(mapping);
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope"))
				europeProvinceMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentAfrica"))
				africaProvinceMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceNACountryContinentEurope"))
				europeProvinceNAMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceNACountryContinentAfrica"))
				africaProvinceNAMapping = mapping;
		}

		logger.debug("europeProvinceMapping: " + europeProvinceMapping);
		logger.debug("africaProvinceMapping: " + africaProvinceMapping);
		logger.debug("europeProvinceNAMapping: " + europeProvinceNAMapping);
		logger.debug("africaProvinceNAMapping: " + africaProvinceNAMapping);

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

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
		logger.debug("about to add missing resultInstance to queryResult");
		queryResult.addResultInstance(missingResultInstance);
		logger.debug("added missing resultInstance to queryResult");

		//resultInstanceRepository.save(missingResultInstance);
		annotationService.annotate("expectancy", "true", missingResultInstance, queryResult, null, true, currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(missingResultInstance.getId());
		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance1.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations1);

		ResultInstance missingResultInstance2 = new ResultInstance();
		missingResultInstance2.setResultType(otherResultInstance.getResultType());
		ResultValue value21 = new ResultValue("City.Name", "Safi");
		ResultValue value22 = new ResultValue("City.Country", "MA");
		ResultValue value23 = new ResultValue("City.Province", "Morocco");
		ResultValue value24 = new ResultValue("City.Population", "376038");
		ResultValue value25 = new ResultValue("City.Longitude", "0");
		ResultValue value26 = new ResultValue("City.Latitude", "0");

		missingResultInstance2.addResultValue("City.Name", value21);
		missingResultInstance2.addResultValue("City.Country", value22);
		missingResultInstance2.addResultValue("City.Province", value23);
		missingResultInstance2.addResultValue("City.Population", value24);
		missingResultInstance2.addResultValue("City.Longitude", value25);
		missingResultInstance2.addResultValue("City.Latitude", value26);

		missingResultInstance2.setUserSpecified(true);
		missingResultInstance2.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance2 to queryResult");
		queryResult.addResultInstance(missingResultInstance2);
		logger.debug("added missing resultInstance to queryResult");

		//resultInstanceRepository.save(missingResultInstance2);
		annotationService.annotate("expectancy", "true", missingResultInstance2, queryResult, null, true, currentUser);

		ResultInstance resultInstance21 = resultInstanceRepository.find(missingResultInstance2.getId());
		List<Annotation> annotations21 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance21, currentUser);
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance21.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations21);

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

		ResultInstance resultInstance22 = resultInstanceRepository.find(missingResultInstance3.getId());
		annotationsOfResultInstancesToPropagate.addAll(resultInstance22.getAnnotations());
		*/

		queryResultRepository.update(queryResult);
		queryResultRepository.flush();

		logger.debug("resultInstances.size(): " + resultInstances.size());
		logger.debug("queryResult.getResultInstances().size(): " + queryResult.getResultInstances().size());

		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			assertNotNull(resultInstance.getId());
			logger.debug("resultInstance.getResultValue(City.Name):" + resultInstance.getResultValue("City.Name"));
			logger.debug("resultInstance.getMappings().size(): " + resultInstance.getMappings().size());
			if (resultInstance.getResultValue("City.Name").getValue().equals("Edinburgh")
					|| resultInstance.getResultValue("City.Name").getValue().equals("Safi")
					|| resultInstance.getResultValue("City.Name").getValue().equals("Constantine")) {
				logger.debug("found Edinburgh, Safi or Constantine");
			} else {
				logger.debug("didn't find Edinburgh, Safi or Constantine");
				String country = resultInstance.getResultValue("City.Country").getValue();
				logger.debug("country: " + country);
				String province = resultInstance.getResultValue("City.Province").getValue();
				logger.debug("province: " + province);
				if (country.equals("DK") || country.equals("GB") || country.equals("D") || country.equals("IRL")) {
					logger.debug("europe");
					if (country.equals("D")) {
						logger.debug("D");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (country.equals("GB")) {
						logger.debug("GB");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (province.equals("NA")) {
						logger.debug("province: NA");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					}
				} else if (country.equals("DZ") || country.equals("GH") || country.equals("MA") || country.equals("RT") || country.equals("WSA")) {
					logger.debug("africa");
					if (country.equals("WSA")) {
						logger.debug("WSA");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (country.equals("GH")) {
						logger.debug("GH");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					} else if (province.equals("NA")) {
						logger.debug("province: NA");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), queryResult.getId(),
								constrainingModelManagementConstructs, true, currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						List<Annotation> annotations2 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance2,
								currentUser);
						//annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
						annotationsOfResultInstancesToPropagate.addAll(annotations2);
					}
				} else
					logger.error("unexpected country");
			}
		}

		annotationRepository.flush();

		for (Annotation annotation : annotationsOfResultInstancesToPropagate) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			logger.debug("annotation.getAnnotatedModelManagementConstructs().iterator().next(): "
					+ annotation.getAnnotatedModelManagementConstructs().iterator().next());
		}

		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingModelManagementConstructs,
				false, currentUser);
		annotationRepository.flush();

		Mapping epMapping = mappingService.findMapping(europeProvinceMapping.getId());
		Mapping apMapping = mappingService.findMapping(africaProvinceMapping.getId());
		Mapping epNAMapping = mappingService.findMapping(europeProvinceNAMapping.getId());
		Mapping apNAMapping = mappingService.findMapping(africaProvinceNAMapping.getId());

		logger.debug("epMapping: " + epMapping);
		logger.debug("apMapping: " + apMapping);
		logger.debug("epNAMapping: " + epNAMapping);
		logger.debug("apNAMapping: " + apNAMapping);

		//List<Annotation> epAnnotations = epMapping.getAnnotations();
		List<Annotation> epAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(epMapping, currentUser);
		logger.debug("epAnnotations: " + epAnnotations);
		Annotation epAnnotationPr = epAnnotations.get(0);
		Annotation epAnnotationRec = epAnnotations.get(1);
		Annotation epAnnotationFM = epAnnotations.get(2);
		Annotation epAnnotationFrAnRes = epAnnotations.get(3);

		logger.debug("epAnnotationPr: " + epAnnotationPr.getValue());
		logger.debug("epAnnotationRec: " + epAnnotationRec.getValue());
		logger.debug("epAnnotationFM: " + epAnnotationFM.getValue());
		logger.debug("epAnnotationFrAnRes: " + epAnnotationFrAnRes.getValue());

		//List<Annotation> apAnnotations = apMapping.getAnnotations();
		List<Annotation> apAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(apMapping, currentUser);
		logger.debug("apAnnotations: " + apAnnotations);
		Annotation apAnnotationPr = apAnnotations.get(0);
		Annotation apAnnotationRec = apAnnotations.get(1);
		Annotation apAnnotationFM = apAnnotations.get(2);
		Annotation apAnnotationFrAnRes = apAnnotations.get(3);

		logger.debug("apAnnotationPr: " + apAnnotationPr.getValue());
		logger.debug("apAnnotationRec: " + apAnnotationRec.getValue());
		logger.debug("apAnnotationFM: " + apAnnotationFM.getValue());
		logger.debug("apAnnotationFrAnRes: " + apAnnotationFrAnRes.getValue());

		//List<Annotation> epNAAnnotations = epNAMapping.getAnnotations();
		List<Annotation> epNAAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(epNAMapping, currentUser);
		logger.debug("epNAAnnotations: " + epNAAnnotations);
		Annotation epNAAnnotationPr = epNAAnnotations.get(0);
		Annotation epNAAnnotationRec = epNAAnnotations.get(1);
		Annotation epNAAnnotationFM = epNAAnnotations.get(2);
		Annotation epNAAnnotationFrAnRes = epNAAnnotations.get(3);

		logger.debug("epNAAnnotationPr: " + epNAAnnotationPr.getValue());
		logger.debug("epNAAnnotationRec: " + epNAAnnotationRec.getValue());
		logger.debug("epNAAnnotationFM: " + epNAAnnotationFM.getValue());
		logger.debug("epNAAnnotationFrAnRes: " + epNAAnnotationFrAnRes.getValue());

		//List<Annotation> apNAAnnotations = apNAMapping.getAnnotations();
		List<Annotation> apNAAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(apNAMapping, currentUser);
		logger.debug("apNAAnnotations: " + apNAAnnotations);
		Annotation apNAAnnotationPr = apNAAnnotations.get(0);
		Annotation apNAAnnotationRec = apNAAnnotations.get(1);
		Annotation apNAAnnotationFM = apNAAnnotations.get(2);
		Annotation apNAAnnotationFrAnRes = apNAAnnotations.get(3);

		logger.debug("apNAAnnotationPr: " + apNAAnnotationPr.getValue());
		logger.debug("apNAAnnotationRec: " + apNAAnnotationRec.getValue());
		logger.debug("apNAAnnotationFM: " + apNAAnnotationFM.getValue());
		logger.debug("apNAAnnotationFrAnRes: " + apNAAnnotationFrAnRes.getValue());

		//TODO think about this: it's not really the mapping annotations that get propagated,
		//it's the annotation of the resultInstances that are retrieved by these mappings for this query
		//annotation of queryResult is constrained by the mappings used to expand the query for that particular queryResult
		Set<Annotation> annotationsToBePropagated = new LinkedHashSet<Annotation>();
		annotationsToBePropagated.addAll(epAnnotations);
		annotationsToBePropagated.addAll(apAnnotations);
		annotationsToBePropagated.addAll(epNAAnnotations);
		annotationsToBePropagated.addAll(apNAAnnotations);
		//annotationsToBePropagated.addAll(epMapping.getAnnotations());
		//annotationsToBePropagated.addAll(apMapping.getAnnotations());
		//annotationsToBePropagated.addAll(epNAMapping.getAnnotations());
		//annotationsToBePropagated.addAll(apNAMapping.getAnnotations());

		Set<ModelManagementConstruct> constructsToBeAnnotated = new LinkedHashSet<ModelManagementConstruct>();
		QueryResult updatedQueryResult = queryResultService.findQueryResult(queryResult.getId());
		constructsToBeAnnotated.add(updatedQueryResult);

		Set<ModelManagementConstruct> constrainingModelManagementConstructsMappings = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructsMappings.add(epMapping);
		constrainingModelManagementConstructsMappings.add(apMapping);
		constrainingModelManagementConstructsMappings.add(epNAMapping);
		constrainingModelManagementConstructsMappings.add(apNAMapping);

		annotationService.propagateAnnotation(annotationsToBePropagated, constructsToBeAnnotated, constrainingModelManagementConstructsMappings,
				false, currentUser);

		annotationRepository.flush();

		QueryResult fetchedQueryResult = queryResultService.findQueryResult(queryResult.getId());

		List<Annotation> qrpAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(fetchedQueryResult,
				currentUser);
		logger.debug("qrpAnnotations: " + qrpAnnotations);
		//List<Annotation> qrpAnnotations = fetchedQueryResult.getAnnotations();
		Annotation qrpAnnotationPr = qrpAnnotations.get(0);
		Annotation qrpAnnotationRec = qrpAnnotations.get(1);
		Annotation qrpAnnotationFM = qrpAnnotations.get(2);
		Annotation qrpAnnotationFrAnRes = qrpAnnotations.get(3);

		logger.debug("qrpAnnotationPr: " + qrpAnnotationPr.getValue());
		logger.debug("qrpAnnotationRec: " + qrpAnnotationRec.getValue());
		logger.debug("qrpAnnotationFM: " + qrpAnnotationFM.getValue());
		logger.debug("qrpAnnotationFrAnRes: " + qrpAnnotationFrAnRes.getValue());

		//resultInstanceRepository.flush();
		//queryResultRepository.flush();
		//annotationRepository.flush();
	}

	@Test
	public void testEvaluateQuerySelectStarFromCitySelectMappingsQueryUserMapOfStringControlParameter() {

		List<ResultInstance> allInstances = resultInstanceRepository.findAll();
		logger.debug("allInstances(): " + allInstances.size());
		for (ResultInstance resultInstance : allInstances) {
			logger.debug("resultInstance: " + resultInstance);
			List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance, currentUser);
			//logger.debug("resultInstance.getAnnotation: " + resultInstance.getAnnotations());
			logger.debug("resultInstance.getAnnotation: " + annotations);
			for (Annotation annotation : annotations) {
				logger.debug("annotation: " + annotation);
				logger.debug("annotation.getValue(): " + annotation.getValue());
				logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
				logger.debug("annotation.getAnnotatedModelManagementConstructs().size(): "
						+ annotation.getAnnotatedModelManagementConstructs().size());
				logger.debug("annotation.getAnnotatedModelManagementConstructs().iterator().next(): "
						+ annotation.getAnnotatedModelManagementConstructs().iterator().next());
				logger.debug("annotation.getAnnotatedModelManagementConstructs().iterator().next().getId(): "
						+ annotation.getAnnotatedModelManagementConstructs().iterator().next().getId());
			}
		}

		List<Annotation> annotations = annotationRepository.findAll();
		logger.debug("annotations.size(): " + annotations.size());

		for (Annotation annotation : annotations) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			logger.debug("annotation.getAnnotatedModelManagementConstructs().size(): " + annotation.getAnnotatedModelManagementConstructs().size());
			logger.debug("annotation.getAnnotatedModelManagementConstructs().iterator().next(): "
					+ annotation.getAnnotatedModelManagementConstructs().iterator().next());
			logger.debug("annotation.getAnnotatedModelManagementConstructs().iterator().next().getId(): "
					+ annotation.getAnnotatedModelManagementConstructs().iterator().next().getId());
		}

		Query queryPna = queryService.findQuery(queryId);
		logger.debug("queryPna: " + queryPna);
		logger.debug("queryPna.getDataSources().size(): " + queryPna.getDataSources().size());
		logger.debug("queryPna.getDataSources(): " + queryPna.getDataSources());

		ControlParameter precisionThresholdControlParameter = new ControlParameter(ControlParameterType.PRECISION_THRESHOLD, "0.4");
		Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		controlParameters.put(ControlParameterType.PRECISION_THRESHOLD, precisionThresholdControlParameter);

		QueryResult queryResultPna = queryService.evaluateQuery(queryPna, null, currentUser, controlParameters);
		List<ResultInstance> resultInstancesPna = queryResultPna.getResultInstances();
		logger.debug("evaluated query using mappingSelection");
		logger.debug("queryResultPna.getMappings().size(): " + queryResultPna.getMappings().size());
		logger.debug("queryResultPna.getMappings(): " + queryResultPna.getMappings());

	}

}
