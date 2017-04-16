package uk.ac.manchester.dstoolkit.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.KeywordSearchEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.mappings.PredefinedMappingsLoaderService;

public class Main {

	static Logger logger = Logger.getLogger(Main.class);

	private static String databaseName;
	private static String url;
	private static String driverClass;
	private static String userName;
	private static String password;
	private static String description;

	private static String mondialIntegrPropsLoc = "./src/test/resources/datasources/mondialIntegrMySQL.properties";
	private static String mondialPropsLoc = "./src/test/resources/datasources/mondialMySQL.properties";

	//CityProvinceCountryContinentEurope

	private static String mondialCityProvinceCountryContinentEuropeNoRenamePropsLoc = "./src/test/resources/datasources/MondialCityProvinceCountryContinentEuropeNoRename.properties";
	private static String mondialCityProvinceCountryContinentEuropeWithRenamePropsLoc = "./src/test/resources/datasources/MondialCityProvinceCountryContinentEuropeWithRename.properties";

	//LanguageEconomyReligionOfCountriesEurope

	private static String mondialLanguageEconomyReligionOfCountriesEuropeNoRenamePropsLoc = "./src/test/resources/datasources/MondialLanguageEconomyReligionOfCountriesEuropeNoRename.properties";
	private static String mondialLanguageEconomyReligionOfCountriesEuropeWithRenamePropsLoc = "./src/test/resources/datasources/MondialLanguageEconomyReligionOfCountriesEuropeWithRename.properties";

	//IslandLakeMountain

	private static String mondialIslandLakeMountainPropsLoc = "./src/test/resources/datasources/MondialIslandLakeMountain.properties";

	//CityProvinceCountryContinentAfrica

	private static String mondialCityProvinceCountryContinentAfricaNoRenamePropsLoc = "./src/test/resources/datasources/MondialCityProvinceCountryContinentAfricaNoRename.properties";
	private static String mondialCityProvinceCountryContinentAfricaWithRenamePropsLoc = "./src/test/resources/datasources/MondialCityProvinceCountryContinentAfricaWithRename.properties";

	//LanguageEconomyReligionOfCountriesAfrica

	private static String mondialLanguageEconomyReligionOfCountriesAfricaNoRenamePropsLoc = "./src/test/resources/datasources/MondialLanguageEconomyReligionOfCountriesAfricaNoRename.properties";
	private static String mondialLanguageEconomyReligionOfCountriesAfricaWithRenamePropsLoc = "./src/test/resources/datasources/MondialLanguageEconomyReligionOfCountriesAfricaWithRename.properties";

	//ensembl_mart iceberg
	private static String ensemblMartIcebergPropsLoc = "./src/main/resources/datasources/ensembl_mart.properties";

	//ensembl_ontology iceberg
	private static String ensemblOntologyIcebergPropsLoc = "./src/main/resources/datasources/ensembl_ontology.properties";

	//kegg_iceberg
	private static String keggPropsLoc = "./src/main/resources/datasources/KEGG.properties";

	//sequence_mart
	private static String sequenceMartPropsLoc = "./src/main/resources/datasources/sequence_mart.properties";

	//SGDLite_iceberg
	private static String sgdLitePropsLoc = "./src/main/resources/datasources/SGDLite.properties";

	//yeast_core_iceberg
	private static String yeastCorePropsLoc = "./src/main/resources/datasources/yeast_core.properties";

	//yeast_funcgen_iceberg
	private static String yeastFuncgenPropsLoc = "./src/main/resources/datasources/yeast_funcgen.properties";

	//yeast_otherfeatures_iceberg
	private static String yeastOtherFeaturesPropsLoc = "./src/main/resources/datasources/yeast_otherfeatures.properties";

	//TODO queryGrammar can't handle alias

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/*.xml");

		//add user
		UserService userService = (UserService) context.getBean("userService");
		User guestUser = new User();
		guestUser.setUserName("connie");
		guestUser.setPassword("guest");
		guestUser.setFirstName("anonymous");
		guestUser.setEmail("anonymous.guest@guest.co.uk");
		userService.addUser(guestUser);

		DataspaceService dataspaceService = (DataspaceService) context.getBean("dataspaceService");
		Dataspace dataspace = new Dataspace("mondialDS1");
		dataspaceService.addDataspace(dataspace);

		Dataspace bioDataspace = new Dataspace("yeastDS");
		dataspaceService.addDataspace(bioDataspace);

		//add datasources
		DataSourceService dataSourceService = (DataSourceService) context.getBean("dataSourceService");
		ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService = (ExternalDataSourcePoolUtilService) context
				.getBean("externalDataSourcePoolUtilService");

		KeywordSearchEvaluationEngineService keywordSearchEvaluationEngineService = (KeywordSearchEvaluationEngineService) context
				.getBean("keywordSearchEvaluationEngineService");

		//******************************Yeast

		/*
		if (externalDataSourcePoolUtilService.getExternalDataSource("EnsemblMart") == null) {
			logger.debug(" adding EnsemblMart");
			loadConfiguration(ensemblMartIcebergPropsLoc);
			logger.debug("url: " + url);
			DataSource ensemblMartDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added EnsemblMart");
			bioDataspace.addDataSource(ensemblMartDS);
			ensemblMartDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(ensemblMartDS.getSchema());
			ensemblMartDS.getSchema().addDataspace(bioDataspace);
		}
		*/

		/*
		if (externalDataSourcePoolUtilService.getExternalDataSource("EnsemblOntology") == null) {
			logger.debug(" adding EnsemblOntology");
			loadConfiguration(ensemblOntologyIcebergPropsLoc);
			logger.debug("url: " + url);
			DataSource ensemblOntologyDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added EnsemblOntology");
			bioDataspace.addDataSource(ensemblOntologyDS);
			ensemblOntologyDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(ensemblOntologyDS.getSchema());
			ensemblOntologyDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("KEGG") == null) {
			logger.debug(" adding KEGG");
			loadConfiguration(keggPropsLoc);
			logger.debug("url: " + url);
			DataSource keggDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added KEGG");
			bioDataspace.addDataSource(keggDS);
			keggDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(keggDS.getSchema());
			keggDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("SequenceMart") == null) {
			logger.debug(" adding SequenceMart");
			loadConfiguration(sequenceMartPropsLoc);
			logger.debug("url: " + url);
			DataSource sequenceMartDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added SequenceMart");
			bioDataspace.addDataSource(sequenceMartDS);
			sequenceMartDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(sequenceMartDS.getSchema());
			sequenceMartDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("SGDLite") == null) {
			logger.debug(" adding SGDLite");
			loadConfiguration(sgdLitePropsLoc);
			logger.debug("url: " + url);
			DataSource sgdLiteDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added SGDLite");
			bioDataspace.addDataSource(sgdLiteDS);
			sgdLiteDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(sgdLiteDS.getSchema());
			sgdLiteDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("YeastCore") == null) {
			logger.debug(" adding YeastCore");
			loadConfiguration(yeastCorePropsLoc);
			logger.debug("url: " + url);
			DataSource yeastCoreDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added YeastCore");
			bioDataspace.addDataSource(yeastCoreDS);
			yeastCoreDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(yeastCoreDS.getSchema());
			yeastCoreDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("YeastFuncGen") == null) {
			logger.debug(" adding YeastFuncGen");
			loadConfiguration(yeastFuncgenPropsLoc);
			logger.debug("url: " + url);
			DataSource yeastFuncGenDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added YeastFuncGen");
			bioDataspace.addDataSource(yeastFuncGenDS);
			yeastFuncGenDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(yeastFuncGenDS.getSchema());
			yeastFuncGenDS.getSchema().addDataspace(bioDataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalDataSource("YeastOtherFeatures") == null) {
			logger.debug(" adding YeastOtherFeatures");
			loadConfiguration(yeastOtherFeaturesPropsLoc);
			logger.debug("url: " + url);
			DataSource yeastOtherFeaturesDS = dataSourceService.addDataSource(databaseName, description, driverClass, url, userName, password);
			logger.debug(" added YeastFuncGen");
			bioDataspace.addDataSource(yeastOtherFeaturesDS);
			yeastOtherFeaturesDS.addDataspace(bioDataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			bioDataspace.addSchema(yeastOtherFeaturesDS.getSchema());
			yeastOtherFeaturesDS.getSchema().addDataspace(bioDataspace);
		}
		*/

		//**************************** Mondial

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialIntegr") == null) {
			logger.debug(" adding MondialIntegr");
			loadConfiguration(mondialIntegrPropsLoc);
			logger.debug("url: " + url);
			DataSource mondialIntegrDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialIntegr");
			dataspace.addDataSource(mondialIntegrDS);
			mondialIntegrDS.addDataspace(dataspace);
			//TODO this might actually be better off being just a schema, instead of a DS, but may be ok for now
			dataspace.addSchema(mondialIntegrDS.getSchema());
			mondialIntegrDS.getSchema().addDataspace(dataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("Mondial") == null) {
			logger.debug(" adding Mondial");
			loadConfiguration(mondialPropsLoc);
			logger.debug("url: " + url);
			DataSource mondialDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added Mondial");
			dataspace.addDataSource(mondialDS);
			mondialDS.addDataspace(dataspace);
			dataspace.addSchema(mondialDS.getSchema());
			mondialDS.getSchema().addDataspace(dataspace);
		}

		//CityProvinceCountryContinentEurope

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentEurope") == null) {
			logger.debug(" adding MondialCityProvinceCountryContinentEurope");
			loadConfiguration(mondialCityProvinceCountryContinentEuropeNoRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialCityEuropeDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialCityProvinceCountryContinentEurope");
			dataspace.addDataSource(mondialCityEuropeDS);
			mondialCityEuropeDS.addDataspace(dataspace);
			dataspace.addSchema(mondialCityEuropeDS.getSchema());
			mondialCityEuropeDS.getSchema().addDataspace(dataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentEuropeWR") == null) {
			logger.debug(" adding MondialCityProvinceCountryContinentEuropeWR");
			loadConfiguration(mondialCityProvinceCountryContinentEuropeWithRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialCityEuropeWRDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialCityProvinceCountryContinentEuropeWR");
			dataspace.addDataSource(mondialCityEuropeWRDS);
			mondialCityEuropeWRDS.addDataspace(dataspace);
			dataspace.addSchema(mondialCityEuropeWRDS.getSchema());
			mondialCityEuropeWRDS.getSchema().addDataspace(dataspace);
		}

		//LanguageEconomyReligionOfCountriesEurope

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesEurope") == null) {
			logger.debug(" adding MondialLanguageEconomyReligionOfCountriesEurope");
			loadConfiguration(mondialLanguageEconomyReligionOfCountriesEuropeNoRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialLanguageEuropeDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName,
					password);
			logger.debug(" added MondialLanguageEconomyReligionOfCountriesEurope");
			dataspace.addDataSource(mondialLanguageEuropeDS);
			mondialLanguageEuropeDS.addDataspace(dataspace);
			dataspace.addSchema(mondialLanguageEuropeDS.getSchema());
			mondialLanguageEuropeDS.getSchema().addDataspace(dataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesEuropeWR") == null) {
			logger.debug(" adding MondialLanguageEconomyReligionOfCountriesEuropeWR");
			loadConfiguration(mondialLanguageEconomyReligionOfCountriesEuropeWithRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialLanguageEuropeWRDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName,
					password);
			logger.debug(" added MondialLanguageEconomyReligionOfCountriesEuropeWR");
			dataspace.addDataSource(mondialLanguageEuropeWRDS);
			mondialLanguageEuropeWRDS.addDataspace(dataspace);
			dataspace.addSchema(mondialLanguageEuropeWRDS.getSchema());
			mondialLanguageEuropeWRDS.getSchema().addDataspace(dataspace);
		}

		//IslandLakeMountain

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialIslandLakeMountain") == null) {
			logger.debug(" adding MondialIslandLakeMountain");
			loadConfiguration(mondialIslandLakeMountainPropsLoc);
			logger.debug("url: " + url);
			DataSource mondialIslandDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialIslandLakeMountain");
			dataspace.addDataSource(mondialIslandDS);
			mondialIslandDS.addDataspace(dataspace);
			dataspace.addSchema(mondialIslandDS.getSchema());
			mondialIslandDS.getSchema().addDataspace(dataspace);
		}

		//CityProvinceCountryContinentAfrica

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentAfrica") == null) {
			logger.debug(" adding MondialCityProvinceCountryContinentAfrica");
			loadConfiguration(mondialCityProvinceCountryContinentAfricaNoRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialCityAfricaDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialCityProvinceCountryContinentAfrica");
			dataspace.addDataSource(mondialCityAfricaDS);
			mondialCityAfricaDS.addDataspace(dataspace);
			dataspace.addSchema(mondialCityAfricaDS.getSchema());
			mondialCityAfricaDS.getSchema().addDataspace(dataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentAfricaWR") == null) {
			logger.debug(" adding MondialCityProvinceCountryContinentAfricaWR");
			loadConfiguration(mondialCityProvinceCountryContinentAfricaWithRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialCityAfricaWRDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName, password);
			logger.debug(" added MondialCityProvinceCountryContinentAfricaWR");
			dataspace.addDataSource(mondialCityAfricaWRDS);
			mondialCityAfricaWRDS.addDataspace(dataspace);
			dataspace.addSchema(mondialCityAfricaWRDS.getSchema());
			mondialCityAfricaWRDS.getSchema().addDataspace(dataspace);
		}

		//LanguageEconomyReligionOfCountriesAfrica

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesAfrica") == null) {
			logger.debug(" adding MondialLanguageEconomyReligionOfCountriesAfrica");
			loadConfiguration(mondialLanguageEconomyReligionOfCountriesAfricaNoRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialLanguageAfricaDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName,
					password);
			logger.debug(" added MondialLanguageEconomyReligionOfCountriesAfrica");
			dataspace.addDataSource(mondialLanguageAfricaDS);
			mondialLanguageAfricaDS.addDataspace(dataspace);
			dataspace.addSchema(mondialLanguageAfricaDS.getSchema());
			mondialLanguageAfricaDS.getSchema().addDataspace(dataspace);
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesAfricaWR") == null) {
			logger.debug(" adding MondialLanguageEconomyReligionOfCountriesAfricaWR");
			loadConfiguration(mondialLanguageEconomyReligionOfCountriesAfricaWithRenamePropsLoc);
			logger.debug("url: " + url);
			DataSource mondialLanguageAfricaWRDS = dataSourceService.addDataSource(databaseName, null, description, driverClass, url, userName,
					password);
			logger.debug(" added MondialLanguageEconomyReligionOfCountriesAfricaWR");
			dataspace.addDataSource(mondialLanguageAfricaWRDS);
			mondialLanguageAfricaWRDS.addDataspace(dataspace);
			dataspace.addSchema(mondialLanguageAfricaWRDS.getSchema());
			mondialLanguageAfricaWRDS.getSchema().addDataspace(dataspace);
		}

		//evaluate query

		/*
		String queryString = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(queryFileLocation));
			StringBuilder queryBuilder = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				queryBuilder.append(line);
				queryBuilder.append(" ");
			}
			br.close();
			queryString = queryBuilder.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

		SchemaRepository schemaRepository = (SchemaRepository) context.getBean("schemaRepository");
		Schema mondialSchema = schemaRepository.getSchemaByName("MondialIntegr");

		DataSourceRepository dataSourceRepository = (DataSourceRepository) context.getBean("dataSourceRepository");

		DataSource mondialIntegrDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");
		DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("Mondial");

		//CityProvinceCountryContinentEurope

		DataSource mondialCityProvinceCountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");

		//LanguageEconomyReligionOfCountriesEurope

		DataSource mondialLanguageEconomyReligionOfCountriesEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEurope");
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		//IslandLakeMountain

		DataSource mondialIslandLakeMountainDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIslandLakeMountain");

		//CityProvinceCountryContinentAfrica

		DataSource mondialCityProvinceCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");
		DataSource mondialCityProvinceCountryContinentAfricaWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfricaWR");

		//LanguageEconomyReligionOfCountriesAfrica

		DataSource mondialLanguageEconomyReligionOfCountriesAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfrica");
		DataSource mondialLanguageEconomyReligionOfCountriesAfricaWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfricaWR");

		PredefinedMappingsLoaderService predefinedMappingsLoaderService = (PredefinedMappingsLoaderService) context
				.getBean("predefinedMappingsLoaderService");
		predefinedMappingsLoaderService.loadMappingsForTests(dataspace, false);

		QueryService queryService = (QueryService) context.getBean("queryService");

		String queryString = "SELECT * FROM country c, city t WHERE c.code = t.country";
		String queryName = "JoinCountryCity";
		String description = "Get all countries with their cities";

		logger.debug("queryString: " + queryString);
		Query query1 = new Query(queryName, queryString);
		query1.setDescription(description);
		query1.getSchemas().add(mondialSchema);
		logger.debug("query1.getSchemas: " + query1.getSchemas());
		query1.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query1.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
		logger.debug("query1.getDataSource: " + query1.getDataSources());
		dataspace.addQuery(query1);
		query1.setDataspace(dataspace);
		queryService.addQuery(query1);

		logger.debug("query1.string: " + query1.getQueryString());
		logger.debug("query1.dataSources: " + query1.getDataSources());
		QueryResult queryResult1 = queryService.evaluateQuery(query1, null, guestUser, null);
		logger.debug("queryResult1.instances.size: " + queryResult1.getResultInstances().size());

		/*
		Query query2 = new Query(queryName, queryString);
		query2.setDescription(description);
		query2.getSchemas().add(mondialSchema);
		logger.debug("query2.getSchemas: " + query2.getSchemas());
		query2.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		logger.debug("query2.getDataSource: " + query2.getDataSources());
		dataspace.addQuery(query2);
		query2.setDataspace(dataspace);
		queryService.addQuery(query2);

		logger.error("query2.string: " + query2.getQueryString());
		logger.error("query2.dataSources: " + query2.getDataSources());
		QueryResult queryResult2 = queryService.evaluateQuery(query2, guestUser);
		logger.error("queryResult2.instances.size: " + queryResult2.getResultInstances().size());
		*/

		/*
		Query query3 = new Query(queryName, queryString);
		query3.setDescription(description);
		query3.getSchemas().add(mondialSchema);
		logger.debug("query3.getSchemas: " + query3.getSchemas());
		query3.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
		logger.debug("query3.getDataSource: " + query3.getDataSources());
		dataspace.addQuery(query3);
		query3.setDataspace(dataspace);
		queryService.addQuery(query3);

		logger.error("query3.string: " + query3.getQueryString());
		logger.error("query3.dataSources: " + query3.getDataSources());
		QueryResult queryResult3 = queryService.evaluateQuery(query3, guestUser);
		logger.error("queryResult3.instances.size: " + queryResult3.getResultInstances().size());
		*/

		/*
		Query query4 = new Query(queryName, queryString);
		query4.setDescription(description);
		query4.getSchemas().add(mondialSchema);
		logger.debug("query4.getSchemas: " + query4.getSchemas());
		query4.addDataSource(mondialIntegrDS);
		logger.debug("query4.getDataSource: " + query4.getDataSources());
		dataspace.addQuery(query4);
		query4.setDataspace(dataspace);
		queryService.addQuery(query4);

		logger.error("query4.string: " + query4.getQueryString());
		logger.error("query4.dataSources: " + query4.getDataSources());
		QueryResult queryResult4 = queryService.evaluateQuery(query4, guestUser);
		logger.error("queryResult4.instances.size: " + queryResult4.getResultInstances().size());
		*/

		/*
		Query query5 = new Query(queryName, queryString);
		query5.setDescription(description);
		query5.getSchemas().add(mondialSchema);
		logger.debug("query5.getSchemas: " + query5.getSchemas());
		query5.addDataSource(mondialDS);
		logger.debug("query5.getDataSource: " + query5.getDataSources());
		dataspace.addQuery(query5);
		query5.setDataspace(dataspace);
		queryService.addQuery(query5);

		logger.error("query5.string: " + query5.getQueryString());
		logger.error("query5.dataSources: " + query5.getDataSources());
		QueryResult queryResult5 = queryService.evaluateQuery(query5, guestUser);
		logger.error("queryResult5.instances.size: " + queryResult5.getResultInstances().size());
		*/

		/*
		Query query6 = new Query("JoinCountriesEthnicGroups", "SELECT * FROM country c, ethnicgroup e WHERE c.code = e.country;");
		query6.setDescription("Get all countries with their ethnic groups");
		query6.getSchemas().add(mondialSchema);
		logger.debug("query6.getSchemas: " + query6.getSchemas());
		query6.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query6.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeNoRenameDS);
		logger.debug("query6.getDataSource: " + query6.getDataSources());
		dataspace.addQuery(query6);
		query6.setDataspace(dataspace);
		queryService.addQuery(query6);

		logger.error("query6.string: " + query6.getQueryString());
		logger.error("query6.dataSources: " + query6.getDataSources());
		QueryResult queryResult6 = queryService.evaluateQuery(query6, guestUser);
		logger.error("queryResult6.instances.size: " + queryResult6.getResultInstances().size());
		 */

		//List<ResultInstance> resultInstances = keywordSearchEvaluationEngineService.evaluateKeywordQuery("YBR122C");

		OntologyTermService ontologyTermService = (OntologyTermService) context.getBean("ontologyTermService");

		OntologyTerm expectancyOT = new OntologyTerm("expectancy", DataType.BOOLEAN);
		OntologyTerm precisionOT = new OntologyTerm("precision", DataType.DOUBLE);
		OntologyTerm recallOT = new OntologyTerm("recall", DataType.DOUBLE);
		OntologyTerm fmeasureOT = new OntologyTerm("f-measure", DataType.DOUBLE);

		ontologyTermService.addOntologyTerm(expectancyOT);
		ontologyTermService.addOntologyTerm(precisionOT);
		ontologyTermService.addOntologyTerm(recallOT);
		ontologyTermService.addOntologyTerm(fmeasureOT);

	}

	private static void loadConfiguration(String fileName) {
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			databaseName = connectionProperties.getProperty("databaseName");
			url = connectionProperties.getProperty("connectionURL");
			driverClass = connectionProperties.getProperty("driverClass");
			userName = connectionProperties.getProperty("username");
			password = connectionProperties.getProperty("password");
			description = connectionProperties.getProperty("description");
			logger.debug("databaseName: " + databaseName);
			logger.debug("url: " + url);
			logger.debug("driverClass: " + driverClass);
			logger.debug("userName: " + userName);
			logger.debug("password: " + password);
			logger.debug("description: " + description);
		} catch (FileNotFoundException exc) {
			logger.error("exception raised in DataspacesRepository: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("properties file not found", ioexc);
			ioexc.printStackTrace();
		}
	}

}
