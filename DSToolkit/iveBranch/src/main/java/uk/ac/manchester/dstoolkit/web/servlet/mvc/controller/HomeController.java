package uk.ac.manchester.dstoolkit.web.servlet.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.dto.models.canonical.AttributeDTO;
import uk.ac.manchester.dstoolkit.dto.models.canonical.EntityDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.DataSourceDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.SchemaDTO;
import uk.ac.manchester.dstoolkit.dto.models.morphisms.MorphismDTO;
import uk.ac.manchester.dstoolkit.dto.models.morphisms.MorphismSetDTO;
import uk.ac.manchester.dstoolkit.dto.models.query.EvaluateQueryParametersDTO;
import uk.ac.manchester.dstoolkit.dto.models.query.QueryDTO;
import uk.ac.manchester.dstoolkit.dto.models.query.queryResults.QueryResultDTO;
import uk.ac.manchester.dstoolkit.dto.models.query.queryResults.ResultTupleDTO;
import uk.ac.manchester.dstoolkit.dto.models.query.queryResults.ResultValueDTO;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.mappings.PredefinedMappingsLoaderService;

@Controller
@RequestMapping({ "/", "/home" })
public class HomeController {

	static Logger logger = Logger.getLogger(HomeController.class);

	private boolean isInitialised = false;
	private boolean hasAnnotation = false;
	private boolean hasMissingResultTuples = false;
	private boolean hasQueryResults = false;
	private User currentUser;
	private Dataspace dataspace;
	private final Map<Long, String> queryResultExpandedQuerySvgMap = new HashMap<Long, String>();
	private final Map<Long, String> queryResultMappingsSvgMap = new HashMap<Long, String>();

	//private final String graphvizIcebergProperties = "./WEB-INF/classes/META-INF/spring/graphviz_iceberg.properties";
	//private final String graphvizMacProperties = "./WEB-INF/classes/META-INF/spring/graphviz_mac.properties";
	//private final String mondialIntegrationProperties = "./WEB-INF/classes/META-INF/spring/mondialIntegration.properties";
	//private final String mondialSourcesProperties = "./WEB-INF/classes/META-INF/spring/mondialSources.properties";

	private final boolean onIceberg = false;

	//@Value("#{properties['mondialIntegration.url']}")
	private String mondialIntegrationUrl = "jdbc:mysql://localhost/MondialIntegration";

	//@Value("#{properties['mondialIntegration.description']}")
	private String mondialIntegrationDescription  = "Mondial Integration Schema (no data)";
	//@Value("#{properties['mondialIntegration.driverClass']}")
	private String mondialIntegrationDriverClass = "com.mysql.jdbc.Driver";
	//@Value("#{properties['mondialIntegration.userName']}")
	private String mondialIntegrationUserName = "root";
	//@Value("#{properties['mondialIntegration.password']}")
	private String mondialIntegrationPassword = "root";
/*
	@Value("#{properties['mondialSources.numberOfSources']}")
	private int numberOfSources;

	@Value("#{properties['mondialSources.sourceName0']}")
	private String sourceName0;
	@Value("#{properties['mondialSources.sourceUrl0']}")
	private String sourceUrl0;
	@Value("#{properties['mondialSources.sourceDescription0']}")
	private String sourceDescription0;
	@Value("#{properties['mondialSources.sourceDriverClass0']}")
	private String sourceDriverClass0;
	@Value("#{properties['mondialSources.sourceUserName0']}")
	private String sourceUserName0;
	@Value("#{properties['mondialSources.sourcePassword0']}")
	private String sourcePassword0;
	@Value("#{properties['mondialSources.sourceType0']}")
	private String sourceType0;

	@Value("#{properties['mondialSources.sourceName1']}")
	private String sourceName1;
	@Value("#{properties['mondialSources.sourceUrl1']}")
	private String sourceUrl1;
	@Value("#{properties['mondialSources.sourceDescription1']}")
	private String sourceDescription1;
	@Value("#{properties['mondialSources.sourceDriverClass1']}")
	private String sourceDriverClass1;
	@Value("#{properties['mondialSources.sourceUserName1']}")
	private String sourceUserName1;
	@Value("#{properties['mondialSources.sourcePassword1']}")
	private String sourcePassword1;
	@Value("#{properties['mondialSources.sourceType1']}")
	private String sourceType1;

	@Value("#{properties['mondialSources.sourceName2']}")
	private String sourceName2;
	@Value("#{properties['mondialSources.sourceUrl2']}")
	private String sourceUrl2;
	@Value("#{properties['mondialSources.sourceDescription2']}")
	private String sourceDescription2;
	@Value("#{properties['mondialSources.sourceDriverClass2']}")
	private String sourceDriverClass2;
	@Value("#{properties['mondialSources.sourceUserName2']}")
	private String sourceUserName2;
	@Value("#{properties['mondialSources.sourcePassword2']}")
	private String sourcePassword2;
	@Value("#{properties['mondialSources.sourceType2']}")
	private String sourceType2;

	@Value("#{properties['mondialSources.sourceName3']}")
	private String sourceName3;
	@Value("#{properties['mondialSources.sourceUrl3']}")
	private String sourceUrl3;
	@Value("#{properties['mondialSources.sourceDescription3']}")
	private String sourceDescription3;
	@Value("#{properties['mondialSources.sourceDriverClass3']}")
	private String sourceDriverClass3;
	@Value("#{properties['mondialSources.sourceUserName3']}")
	private String sourceUserName3;
	@Value("#{properties['mondialSources.sourcePassword3']}")
	private String sourcePassword3;
	@Value("#{properties['mondialSources.sourceType3']}")
	private String sourceType3;

	@Value("#{properties['mondialSources.sourceName4']}")
	private String sourceName4;
	@Value("#{properties['mondialSources.sourceUrl4']}")
	private String sourceUrl4;
	@Value("#{properties['mondialSources.sourceDescription4']}")
	private String sourceDescription4;
	@Value("#{properties['mondialSources.sourceDriverClass4']}")
	private String sourceDriverClass4;
	@Value("#{properties['mondialSources.sourceUserName4']}")
	private String sourceUserName4;
	@Value("#{properties['mondialSources.sourcePassword4']}")
	private String sourcePassword4;
	@Value("#{properties['mondialSources.sourceType4']}")
	private String sourceType4;

	@Value("#{properties['mondialSources.sourceName5']}")
	private String sourceName5;
	@Value("#{properties['mondialSources.sourceUrl5']}")
	private String sourceUrl5;
	@Value("#{properties['mondialSources.sourceDescription5']}")
	private String sourceDescription5;
	@Value("#{properties['mondialSources.sourceDriverClass5']}")
	private String sourceDriverClass5;
	@Value("#{properties['mondialSources.sourceUserName5']}")
	private String sourceUserName5;
	@Value("#{properties['mondialSources.sourcePassword5']}")
	private String sourcePassword5;
	@Value("#{properties['mondialSources.sourceType5']}")
	private String sourceType5;

	@Value("#{properties['mondialSources.sourceName6']}")
	private String sourceName6;
	@Value("#{properties['mondialSources.sourceUrl6']}")
	private String sourceUrl6;
	@Value("#{properties['mondialSources.sourceDescription6']}")
	private String sourceDescription6;
	@Value("#{properties['mondialSources.sourceDriverClass6']}")
	private String sourceDriverClass6;
	@Value("#{properties['mondialSources.sourceUserName6']}")
	private String sourceUserName6;
	@Value("#{properties['mondialSources.sourcePassword6']}")
	private String sourcePassword6;
	@Value("#{properties['mondialSources.sourceType6']}")
	private String sourceType6;

	@Value("#{properties['mondialSources.sourceName7']}")
	private String sourceName7;
	@Value("#{properties['mondialSources.sourceUrl7']}")
	private String sourceUrl7;
	@Value("#{properties['mondialSources.sourceDescription7']}")
	private String sourceDescription7;
	@Value("#{properties['mondialSources.sourceDriverClass7']}")
	private String sourceDriverClass7;
	@Value("#{properties['mondialSources.sourceUserName7']}")
	private String sourceUserName7;
	@Value("#{properties['mondialSources.sourcePassword7']}")
	private String sourcePassword7;
	@Value("#{properties['mondialSources.sourceType7']}")
	private String sourceType7;

	@Value("#{properties['mondialSources.sourceName8']}")
	private String sourceName8;
	@Value("#{properties['mondialSources.sourceUrl8']}")
	private String sourceUrl8;
	@Value("#{properties['mondialSources.sourceDescription8']}")
	private String sourceDescription8;
	@Value("#{properties['mondialSources.sourceDriverClass8']}")
	private String sourceDriverClass8;
	@Value("#{properties['mondialSources.sourceUserName8']}")
	private String sourceUserName8;
	@Value("#{properties['mondialSources.sourcePassword8']}")
	private String sourcePassword8;
	@Value("#{properties['mondialSources.sourceType8']}")
	private String sourceType8;

	@Value("#{properties['mondialSources.sourceName9']}")
	private String sourceName9;
	@Value("#{properties['mondialSources.sourceUrl9']}")
	private String sourceUrl9;
	@Value("#{properties['mondialSources.sourceDescription9']}")
	private String sourceDescription9;
	@Value("#{properties['mondialSources.sourceDriverClass9']}")
	private String sourceDriverClass9;
	@Value("#{properties['mondialSources.sourceUserName9']}")
	private String sourceUserName9;
	@Value("#{properties['mondialSources.sourcePassword9']}")
	private String sourcePassword9;
	@Value("#{properties['mondialSources.sourceType9']}")
	private String sourceType9;

	@Value("#{properties['mondialSources.sourceName10']}")
	private String sourceName10;
	@Value("#{properties['mondialSources.sourceUrl10']}")
	private String sourceUrl10;
	@Value("#{properties['mondialSources.sourceDescription10']}")
	private String sourceDescription10;
	@Value("#{properties['mondialSources.sourceDriverClass10']}")
	private String sourceDriverClass10;
	@Value("#{properties['mondialSources.sourceUserName10']}")
	private String sourceUserName10;
	@Value("#{properties['mondialSources.sourcePassword10']}")
	private String sourcePassword10;
	@Value("#{properties['mondialSources.sourceType10']}")
	private String sourceType10;

	@Value("#{properties['mondialSources.sourceName11']}")
	private String sourceName11;
	@Value("#{properties['mondialSources.sourceUrl11']}")
	private String sourceUrl11;
	@Value("#{properties['mondialSources.sourceDescription11']}")
	private String sourceDescription11;
	@Value("#{properties['mondialSources.sourceDriverClass11']}")
	private String sourceDriverClass11;
	@Value("#{properties['mondialSources.sourceUserName11']}")
	private String sourceUserName11;
	@Value("#{properties['mondialSources.sourcePassword11']}")
	private String sourcePassword11;
	@Value("#{properties['mondialSources.sourceType11']}")
	private String sourceType11;

	@Value("#{properties['mondialSources.sourceName12']}")
	private String sourceName12;
	@Value("#{properties['mondialSources.sourceUrl12']}")
	private String sourceUrl12;
	@Value("#{properties['mondialSources.sourceDescription12']}")
	private String sourceDescription12;
	@Value("#{properties['mondialSources.sourceDriverClass12']}")
	private String sourceDriverClass12;
	@Value("#{properties['mondialSources.sourceUserName12']}")
	private String sourceUserName12;
	@Value("#{properties['mondialSources.sourcePassword12']}")
	private String sourcePassword12;
	@Value("#{properties['mondialSources.sourceType12']}")
	private String sourceType12;
   */
	/*
	@Value("#{properties['mondialSources.sourceName13']}")
	private String sourceName13;
	@Value("#{properties['mondialSources.sourceUrl13']}")
	private String sourceUrl13;
	@Value("#{properties['mondialSources.sourceDescription13']}")
	private String sourceDescription13;
	@Value("#{properties['mondialSources.sourceDriverClass13']}")
	private String sourceDriverClass13;
	@Value("#{properties['mondialSources.sourceUserName13']}")
	private String sourceUserName13;
	@Value("#{properties['mondialSources.sourcePassword13']}")
	private String sourcePassword13;
	@Value("#{properties['mondialSources.sourceType13']}")
	private String sourceType13;
	*/

	/*
	private static String countryProvinceNullContinentAfricaUrl = "jdbc:mysql://localhost/CountryProvinceNullContinentAfrica";
	private static String countryProvinceNullContinentAfricaDescription = "CountryProvinceNull, Continent etc., Africa";

	private static String countryProvinceNullContinentAsiaUrl = "jdbc:mysql://localhost/CountryProvinceNullContinentAsia";
	private static String countryProvinceNullContinentAsiaDescription = "CountryProvinceNull, Continent etc., Asia";

	private static String countryProvinceNullContinentAmericaUrl = "jdbc:mysql://localhost/CountryProvinceNullContinentAmerica";
	private static String countryProvinceNullContinentAmericaDescription = "CountryProvinceNull, Continent etc., America";

	private static String countryProvinceNullContinentAustraliaUrl = "jdbc:mysql://localhost/CountryProvinceNullContinentAustralia";
	private static String countryProvinceNullContinentAustraliaDescription = "CountryProvinceNull, Continent etc., Australia";

	private static String countryProvinceNullContinentEuropeUrl = "jdbc:mysql://localhost/CountryProvinceNullContinentEurope";
	private static String countryProvinceNullContinentEuropeDescription = "CountryProvinceNull, Continent etc., Europe";
	*/

	/*
	private static String provinceCountryContinentAfricaUrl = "jdbc:mysql://localhost/ProvinceCountryContinentAfrica";
	private static String provinceCountryContinentAfricaDescription = "Province, Country, Continent etc., Africa";

	private static String provinceCountryContinentAsiaUrl = "jdbc:mysql://localhost/ProvinceCountryContinentAsia";
	private static String provinceCountryContinentAsiaDescription = "Province, Country, Continent etc., Asia";

	private static String provinceCountryContinentAmericaUrl = "jdbc:mysql://localhost/ProvinceCountryContinentAmerica";
	private static String provinceCountryContinentAmericaDescription = "Province, Country, Continent etc., America";

	private static String provinceCountryContinentAustraliaUrl = "jdbc:mysql://localhost/ProvinceCountryContinentAustralia";
	private static String provinceCountryContinentAustraliaDescription = "Province, Country, Continent etc., Australia";

	private static String provinceCountryContinentEuropeUrl = "jdbc:mysql://localhost/ProvinceCountryContinentEurope";
	private static String provinceCountryContinentEuropeDescription = "Province, Country, Continent etc., Europe";

	private static String provinceCountryContinentMixUrl = "jdbc:mysql://localhost/ProvinceCountryContinentMix";
	private static String provinceCountryContinentMixDescription = "Province, Country, Continent etc., Mix";
	*/

	//private static String mondialUrl = "jdbc:mysql://localhost/Mondial";
	//private static String mondialDescription = "Mondial";

	//private static String mondialCityProvinceCountryContinentEuropeUrl = "jdbc:mysql://localhost/MondialCityProvinceCountryContinentEuropeNoRename";
	//private static String mondialCityProvinceCountryContinentEuropeDescription = "Mondial - City, Province, Country, Continent etc., Europe";

	//private static String mondialCityProvinceNACountryContinentEuropeUrl = "jdbc:mysql://localhost/MondialCityProvinceNACountryContinentEuropeNoRename";
	//private static String mondialCityProvinceNACountryContinentEuropeDescription = "Mondial - City, ProvinceNA, Country, Continent etc., Europe";

	//private static String mondialCityProvinceCountryContinentEuropeWRUrl = "jdbc:mysql://localhost/MondialCityProvinceCountryContinentEuropeWithRename";
	//private static String mondialCityProvinceCountryContinentEuropeWRDescription = "Mondial - City, Province, Country, Continent etc., Europe";

	//private static String mondialCityProvinceCountryContinentAfricaUrl = "jdbc:mysql://localhost/MondialCityProvinceCountryContinentAfricaNoRename";
	//private static String mondialCityProvinceCountryContinentAfricaDescription = "Mondial - City, Province, Country, Continent etc., Africa";

	//private static String mondialCityProvinceNACountryContinentAfricaUrl = "jdbc:mysql://localhost/MondialCityProvinceNACountryContinentAfricaNoRename";
	//private static String mondialCityProvinceNACountryContinentAfricaDescription = "Mondial - City, ProvinceNA, Country, Continent etc., Africa";

	//private static String mondialCityProvinceCountryContinentAfricaWRUrl = "jdbc:mysql://localhost/MondialCityProvinceCountryContinentAfricaWithRename";
	//private static String mondialCityProvinceCountryContinentAfricaWRDescription = "Mondial - City, Province, Country, Continent etc., Africa";

	/*
	private static String islandLakeMountainUrl = "jdbc:mysql://localhost/IslandLakeMountain";
	private static String islandLakeMountainDescription = "Islands, Lakes, Mountains etc.";

	private static String islandLakeMountainMixUrl = "jdbc:mysql://localhost/IslandLakeMountainMix";
	private static String islandLakeMountainMixDescription = "Islands, Lakes, Mountains etc., Mix";

	private static String languageEconomyReligionOfCountriesAfricaUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesAfrica";
	private static String languageEconomyReligionOfCountriesAfricaDescription = "Language, Economy, Religion etc. of Countries, Africa";

	private static String languageEconomyReligionOfCountriesAmericaUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesAmerica";
	private static String languageEconomyReligionOfCountriesAmericaDescription = "Language, Economy, Religion etc. of Countries, America";

	private static String languageEconomyReligionOfCountriesAsiaUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesAsia";
	private static String languageEconomyReligionOfCountriesAsiaDescription = "Language, Economy, Religion etc. of Countries, Asia";

	private static String languageEconomyReligionOfCountriesAustraliaUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesAustralia";
	private static String languageEconomyReligionOfCountriesAustraliaDescription = "Language, Economy, Religion etc. of Countries, Australia";

	private static String languageEconomyReligionOfCountriesEuropeUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesEurope";
	private static String languageEconomyReligionOfCountriesEuropeDescription = "Language, Economy, Religion etc. of Countries, Europe";

	private static String languageEconomyReligionOfCountriesMixUrl = "jdbc:mysql://localhost/LanguageEconomyReligionOfCountriesMix";
	private static String languageEconomyReligionOfCountriesMixDescription = "Language, Economy, Religion etc. of Countries, Mix";
	*/

	//private static String mondialLanguageEconomyReligionOfCountriesEuropeUrl = "jdbc:mysql://localhost/MondialLanguageEconomyReligionOfCountriesEuropeNoRename";
	//private static String mondialLanguageEconomyReligionOfCountriesEuropeDescription = "Mondial - Language, Economy, Religion etc. of Countries, Europe";

	//private static String mondialLanguageEconomyReligionOfCountriesEuropeWRUrl = "jdbc:mysql://localhost/MondialLanguageEconomyReligionOfCountriesEuropeWithRename";
	//private static String mondialLanguageEconomyReligionOfCountriesEuropeWRDescription = "Mondial - Language, Economy, Religion etc. of Countries, Europe";

	//private static String mondialLanguageEconomyReligionOfCountriesAfricaUrl = "jdbc:mysql://localhost/MondialLanguageEconomyReligionOfCountriesAfricaNoRename";
	//private static String mondialLanguageEconomyReligionOfCountriesAfricaDescription = "Mondial - Language, Economy, Religion etc. of Countries, Africa";

	//private static String mondialLanguageEconomyReligionOfCountriesAfricaWRUrl = "jdbc:mysql://localhost/MondialLanguageEconomyReligionOfCountriesAfricaWithRename";
	//private static String mondialLanguageEconomyReligionOfCountriesAfricaWRDescription = "Mondial - Language, Economy, Religion etc. of Countries, Africa";

	//private static String mondialIslandLakeMountainUrl = "jdbc:mysql://localhost/MondialIslandLakeMountain";
	//private static String mondialIslandLakeMountainDescription = "Mondial - Islands, Lakes, Mountains etc.";
/*
	@Value("#{properties['mac.graphvizLocation']}")
	private String macGraphvizLocation; // = "/usr/local/bin/dot";
	@Value("#{properties['mac.graphvizParameters']}")
	private String macGraphvizParameters; // = "-Tsvg";
	@Value("#{properties['mac.queryDotFile']}")
	private String macQueryDotFile; // = "/Users/chedeler/Documents/temp/query.dot";
	@Value("#{properties['mac.mappingDotFile']}")
	private String macMappingDotFile; // = "/Users/chedeler/Documents/temp/mapping.dot"; 

	@Value("#{properties['iceberg.graphvizLocation']}")
	private String icebergGraphvizLocation; // = "C:/Program Files/Graphviz 2.28/bin/dot";
	@Value("#{properties['iceberg.graphvizParameters']}")
	private String icebergGraphvizParameters; // = "-Tsvg";
	@Value("#{properties['iceberg.queryDotFile']}")
	private String icebergQueryDotFile; // = "C:/temp/query.dot";
	@Value("#{properties['iceberg.mappingDotFile']}")
	private String icebergMappingDotFile; // = "C:/temp/mapping.dot";
*/
	//TODO: integration of Grphviz is a hack, should be moved in separate service and be parameterisable with locations etc.

	private boolean loadedMappings = false;
	private boolean loadedQueries = false;

	@Autowired
	@Qualifier("dataspaceService")
	private DataspaceService dataspaceService;

	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;

	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGenerator;

	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("ontologyTermService")
	private OntologyTermService ontologyTermService;

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("predefinedMappingsLoaderService")
	private PredefinedMappingsLoaderService predefinedMappingsLoaderService;

	@Autowired
	@Qualifier("annotationService")
	private AnnotationService annotationService;

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	@Autowired
	private Mapper dozerMapper;

	private QueryResult currentQueryResult;
	private final List<String> currentOrderedListOfFieldNames = new ArrayList<String>();
	private final Map<String, String> mappingsSvgForQueryResultMap = new HashMap<String, String>();
	private final Map<String, String> expandedQuerySvgForQueryResultMap = new HashMap<String, String>();
/*
	private void loadMondialSources() {
		logger.debug("in loadMondialSources");
		logger.debug("fileName: + fileName");

		logger.debug("numberOfSources: " + numberOfSources);

		logger.debug("sourceName0: " + this.sourceName0);
		logger.debug("sourceUrl0: " + sourceUrl0);
		logger.debug("sourceDescription0: " + sourceDescription0);
		logger.debug("sourceDriverClass0: " + sourceDriverClass0);
		logger.debug("sourceUserName0: " + sourceUserName0);
		logger.debug("sourcePassword0: " + sourcePassword0);
		logger.debug("sourceType0: " + sourceType0);

		if (sourceType0.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName0) == null) {
				logger.debug("adding " + sourceName0);
				logger.debug("sourceUrl0: " + sourceUrl0);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName0, null, sourceDescription0, sourceDriverClass0, sourceUrl0,
						sourceUserName0, sourcePassword0, null);
				logger.debug("added " + sourceName0);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName1: " + this.sourceName1);
		logger.debug("sourceUrl1: " + sourceUrl1);
		logger.debug("sourceDescription1: " + sourceDescription1);
		logger.debug("sourceDriverClass1: " + sourceDriverClass1);
		logger.debug("sourceUserName1: " + sourceUserName1);
		logger.debug("sourcePassword1: " + sourcePassword1);
		logger.debug("sourceType1: " + sourceType1);

		if (sourceType1.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName1) == null) {
				logger.debug("adding " + sourceName1);
				logger.debug("sourceUrl1: " + sourceUrl1);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName1, null, sourceDescription1, sourceDriverClass1, sourceUrl1,
						sourceUserName1, sourcePassword1, null);
				logger.debug("added " + sourceName1);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName2: " + this.sourceName2);
		logger.debug("sourceUrl2: " + sourceUrl2);
		logger.debug("sourceDescription2: " + sourceDescription2);
		logger.debug("sourceDriverClass2: " + sourceDriverClass2);
		logger.debug("sourceUserName2: " + sourceUserName2);
		logger.debug("sourcePassword2: " + sourcePassword2);
		logger.debug("sourceType2: " + sourceType2);

		if (sourceType2.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName2) == null) {
				logger.debug("adding " + sourceName2);
				logger.debug("sourceUrl2: " + sourceUrl2);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName2, null, sourceDescription2, sourceDriverClass2, sourceUrl2,
						sourceUserName2, sourcePassword2, null);
				logger.debug("added " + sourceName2);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName3: " + this.sourceName3);
		logger.debug("sourceUrl3: " + sourceUrl3);
		logger.debug("sourceDescription3: " + sourceDescription3);
		logger.debug("sourceDriverClass3: " + sourceDriverClass3);
		logger.debug("sourceUserName3: " + sourceUserName3);
		logger.debug("sourcePassword3: " + sourcePassword3);
		logger.debug("sourceType3: " + sourceType3);

		if (sourceType3.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName3) == null) {
				logger.debug("adding " + sourceName3);
				logger.debug("sourceUrl3: " + sourceUrl3);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName3, null, sourceDescription3, sourceDriverClass3, sourceUrl3,
						sourceUserName3, sourcePassword3, null);
				logger.debug("added " + sourceName3);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName4: " + this.sourceName4);
		logger.debug("sourceUrl4: " + sourceUrl4);
		logger.debug("sourceDescription4: " + sourceDescription4);
		logger.debug("sourceDriverClass4: " + sourceDriverClass4);
		logger.debug("sourceUserName4: " + sourceUserName4);
		logger.debug("sourcePassword4: " + sourcePassword4);
		logger.debug("sourceType4: " + sourceType4);

		if (sourceType4.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName4) == null) {
				logger.debug("adding " + sourceName4);
				logger.debug("sourceUrl4: " + sourceUrl4);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName4, null, sourceDescription4, sourceDriverClass4, sourceUrl4,
						sourceUserName4, sourcePassword4, null);
				logger.debug("added " + sourceName4);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName5: " + this.sourceName5);
		logger.debug("sourceUrl5: " + sourceUrl5);
		logger.debug("sourceDescription5: " + sourceDescription5);
		logger.debug("sourceDriverClass: " + sourceDriverClass5);
		logger.debug("sourceUserName5: " + sourceUserName5);
		logger.debug("sourcePassword5: " + sourcePassword5);
		logger.debug("sourceType5: " + sourceType5);

		if (sourceType5.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName5) == null) {
				logger.debug("adding " + sourceName5);
				logger.debug("sourceUrl5: " + sourceUrl5);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName5, null, sourceDescription5, sourceDriverClass5, sourceUrl5,
						sourceUserName5, sourcePassword5, null);
				logger.debug("added " + sourceName5);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName6: " + this.sourceName6);
		logger.debug("sourceUrl6: " + sourceUrl6);
		logger.debug("sourceDescription6: " + sourceDescription6);
		logger.debug("sourceDriverClass6: " + sourceDriverClass6);
		logger.debug("sourceUserName6: " + sourceUserName6);
		logger.debug("sourcePassword6: " + sourcePassword6);
		logger.debug("sourceType6: " + sourceType6);

		if (sourceType6.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName6) == null) {
				logger.debug("adding " + sourceName6);
				logger.debug("sourceUrl6: " + sourceUrl6);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName6, null, sourceDescription6, sourceDriverClass6, sourceUrl6,
						sourceUserName6, sourcePassword6, null);
				logger.debug("added " + sourceName6);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName7: " + this.sourceName7);
		logger.debug("sourceUrl7: " + sourceUrl7);
		logger.debug("sourceDescription7: " + sourceDescription7);
		logger.debug("sourceDriverClass7: " + sourceDriverClass7);
		logger.debug("sourceUserName7: " + sourceUserName7);
		logger.debug("sourcePassword7: " + sourcePassword7);
		logger.debug("sourceType7: " + sourceType7);

		if (sourceType7.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName7) == null) {
				logger.debug("adding " + sourceName7);
				logger.debug("sourceUrl7: " + sourceUrl7);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName7, null, sourceDescription7, sourceDriverClass7, sourceUrl7,
						sourceUserName7, sourcePassword7, null);
				logger.debug("added " + sourceName7);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName8: " + this.sourceName8);
		logger.debug("sourceUrl8: " + sourceUrl8);
		logger.debug("sourceDescription8: " + sourceDescription8);
		logger.debug("sourceDriverClass8: " + sourceDriverClass8);
		logger.debug("sourceUserName8: " + sourceUserName8);
		logger.debug("sourcePassword8: " + sourcePassword8);
		logger.debug("sourceType8: " + sourceType8);

		if (sourceType8.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName8) == null) {
				logger.debug("adding " + sourceName8);
				logger.debug("sourceUrl8: " + sourceUrl8);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName8, null, sourceDescription8, sourceDriverClass8, sourceUrl8,
						sourceUserName8, sourcePassword8, null);
				logger.debug("added " + sourceName8);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName9: " + this.sourceName9);
		logger.debug("sourceUrl9: " + sourceUrl9);
		logger.debug("sourceDescription9: " + sourceDescription9);
		logger.debug("sourceDriverClass9: " + sourceDriverClass9);
		logger.debug("sourceUserName9: " + sourceUserName9);
		logger.debug("sourcePassword9: " + sourcePassword9);
		logger.debug("sourceType9: " + sourceType9);

		if (sourceType9.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName9) == null) {
				logger.debug("adding " + sourceName9);
				logger.debug("sourceUrl9: " + sourceUrl9);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName9, null, sourceDescription9, sourceDriverClass9, sourceUrl9,
						sourceUserName9, sourcePassword9, null);
				logger.debug("added " + sourceName9);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName10: " + this.sourceName10);
		logger.debug("sourceUrl10: " + sourceUrl10);
		logger.debug("sourceDescription10: " + sourceDescription10);
		logger.debug("sourceDriverClass10: " + sourceDriverClass10);
		logger.debug("sourceUserName10: " + sourceUserName10);
		logger.debug("sourcePassword10: " + sourcePassword10);
		logger.debug("sourceType10: " + sourceType10);

		if (sourceType10.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName10) == null) {
				logger.debug("adding " + sourceName10);
				logger.debug("sourceUrl10: " + sourceUrl10);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName10, null, sourceDescription10, sourceDriverClass10, sourceUrl10,
						sourceUserName10, sourcePassword10, null);
				logger.debug("added " + sourceName10);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName11: " + this.sourceName11);
		logger.debug("sourceUrl11: " + sourceUrl11);
		logger.debug("sourceDescription11: " + sourceDescription11);
		logger.debug("sourceDriverClass11: " + sourceDriverClass11);
		logger.debug("sourceUserName11: " + sourceUserName11);
		logger.debug("sourcePassword11: " + sourcePassword11);
		logger.debug("sourceType11: " + sourceType11);

		if (sourceType11.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName11) == null) {
				logger.debug("adding " + sourceName11);
				logger.debug("sourceUrl11: " + sourceUrl11);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName11, null, sourceDescription11, sourceDriverClass11, sourceUrl11,
						sourceUserName11, sourcePassword11, null);
				logger.debug("added " + sourceName11);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		logger.debug("sourceName12: " + this.sourceName12);
		logger.debug("sourceUrl12: " + sourceUrl12);
		logger.debug("sourceDescription12: " + sourceDescription12);
		logger.debug("sourceDriverClass12: " + sourceDriverClass12);
		logger.debug("sourceUserName12: " + sourceUserName12);
		logger.debug("sourcePassword12: " + sourcePassword12);
		logger.debug("sourceType12: " + sourceType12);

		if (sourceType12.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName12) == null) {
				logger.debug("adding " + sourceName12);
				logger.debug("sourceUrl12: " + sourceUrl12);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName12, null, sourceDescription12, sourceDriverClass12, sourceUrl12,
						sourceUserName12, sourcePassword12, null);
				logger.debug("added " + sourceName12);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}

		/*
		logger.debug("sourceName13: " + this.sourceName13);
		logger.debug("sourceUrl13: " + sourceUrl13);
		logger.debug("sourceDescription13: " + sourceDescription13);
		logger.debug("sourceDriverClass13: " + sourceDriverClass13);
		logger.debug("sourceUserName13: " + sourceUserName13);
		logger.debug("sourcePassword13: " + sourcePassword13);
		logger.debug("sourceType13: " + sourceType13);

		if (sourceType13.equals("relational")) {
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceName13) == null) {
				logger.debug("adding " + sourceName13);
				logger.debug("sourceUrl13: " + sourceUrl13);
				DataSource sourceDS = dataSourceService.addDataSource(sourceName13, sourceDescription13, sourceDriverClass13, sourceUrl13,
						sourceUserName13, sourcePassword13);
				logger.debug("added " + sourceName13);
				dataspace.addDataSource(sourceDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(sourceDS.getSchema());
			}
		}
		
	} 
  */

	/*
	private void loadMondialIntegrationConfiguration(String fileName) {
		logger.debug("in loadMondialIntegrationConfiguration");
		logger.debug("fileName: " + fileName);
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			mondialIntegrationUrl = connectionProperties.getProperty("mondialIntegrationUrl");
			mondialIntegrationDescription = connectionProperties.getProperty("mondialIntegrationDescription");
			driverClass = connectionProperties.getProperty("driverClass");
			userName = connectionProperties.getProperty("userName");
			password = connectionProperties.getProperty("password");
			logger.debug("mondialIntegrationUrl: " + mondialIntegrationUrl);
			logger.debug("mondialIntegrationDescription: " + mondialIntegrationDescription);
			logger.debug("driverClass: " + driverClass);
			logger.debug("userName: " + userName);
			logger.debug("password: " + password);
		} catch (FileNotFoundException exc) {
			logger.error("exception raised: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("properties file not found", ioexc);
			ioexc.printStackTrace();
		}
	}
	*/

	/*
	private void loadGraphvizConfiguration(String fileName) {
		logger.debug("in loadGraphvizConfiguration");
		logger.debug("fileName");
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			graphvizLocation = connectionProperties.getProperty("graphvizLocation");
			graphvizParameters = connectionProperties.getProperty("graphvizParameters");
			queryDotFile = connectionProperties.getProperty("queryDotFile");
			mappingDotFile = connectionProperties.getProperty("mappingDotFile");
			logger.debug("graphvizLocation: " + graphvizLocation);
			logger.debug("graphvizParameters: " + graphvizParameters);
			logger.debug("queryDotFile: " + queryDotFile);
			logger.debug("mappingDotFile: " + mappingDotFile);
		} catch (FileNotFoundException exc) {
			logger.error("exception raised: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("properties file not found", ioexc);
			ioexc.printStackTrace();
		}
	}
	*/

	@RequestMapping(method = RequestMethod.GET, value = "/dataspace/{dataspaceId}/nextUniqueId")
	@ResponseBody()
	public Long getNextUniqueId(@PathVariable Long dataspaceId) {
		Long id = schemaRepository.getNextId();
		logger.debug("id: " + id);
		return id;
	}

	//TODO: add responseStatus
	@RequestMapping(method = RequestMethod.POST, value = "/dataspace/{dataspaceId}/queryResult/{queryResultId}/resultTupleAnnotation")
	@ResponseBody()
	public boolean addResultTupleAnnotations(@PathVariable Long dataspaceId, @PathVariable Long queryResultId,
			@RequestBody Map<Long, Map<String, String>> annotations) {
		logger.debug("in addResultTupleAnnotations");
		logger.debug("dataspaceID: " + dataspaceId);
		logger.debug("queryResultID: " + queryResultId);
		logger.debug("annotations: " + annotations);
		//TODO: dataspaceId ignored for now

		QueryResult queryResult = queryResultService.findQueryResult(queryResultId);
		logger.debug("queryResult: " + queryResult);
		;
		//Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		//constrainingModelManagementConstructs.add(queryResult.getQuery());
		//constrainingModelManagementConstructs.addAll(queryResult.getMappings());
		logger.debug("query: " + queryResult.getQuery());
		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		for (Map<String, String> annotation : annotations.values()) {

			Long resultInstanceId = Long.parseLong(annotation.get("annotatedConstructId"));
			logger.debug("resultInstanceId: " + resultInstanceId);
			ResultInstance resultInstance = resultInstanceRepository.find(resultInstanceId);
			logger.debug("resultInstance: " + resultInstance);
			Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
			constrainingModelManagementConstructs.add(queryResult.getQuery());
			constrainingModelManagementConstructs.addAll(resultInstance.getMappings());
			String annotationTerm = annotation.get("annotationTerm");
			String annotationValue = annotation.get("annotationValue");
			logger.debug("annotationTerm: " + annotationTerm);
			logger.debug("annotationValue: " + annotationValue);
			if (annotationTerm.equals("expectancy")) {
				if (annotationValue.equals("true")) {
					annotationService.annotate("expectancy", "true", resultInstance, queryResult, constrainingModelManagementConstructs, false,
							currentUser);
					List<Annotation> annots = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance,
							currentUser);
					annotationsOfResultInstancesToPropagate.addAll(annots);
				} else if (annotationValue.equals("false")) {
					annotationService.annotate("expectancy", "false", resultInstance, queryResult, constrainingModelManagementConstructs, false,
							currentUser);
					List<Annotation> annots = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance,
							currentUser);
					annotationsOfResultInstancesToPropagate.addAll(annots);
				}
			} else {
				logger.debug("wrong type of annotation, not expectancy: " + annotationTerm);
				return false;
			}
		}

		logger.debug("annotationsOfResultInstancesToPropagate: " + annotationsOfResultInstancesToPropagate);
		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();
		constructsToAnnotate.addAll(mappings);
		logger.debug("constructsToAnnotate: " + constructsToAnnotate);

		Set<ModelManagementConstruct> constrainingConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingConstructs.add(queryResult.getQuery());
		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingConstructs, false,
				currentUser);

		hasAnnotation = true;
		return true;
	}

	//TODO: add responseStatus
	@RequestMapping(method = RequestMethod.POST, value = "/dataspace/{dataspaceId}/queryResult/{queryResultId}/resultTuple")
	@ResponseBody()
	public Long addMissingResultTuple(@PathVariable Long dataspaceId, @PathVariable Long queryResultId, @RequestBody ResultTupleDTO resultTuple) {
		logger.debug("in addMissingResultTuple");
		logger.debug("dataspaceID: " + dataspaceId);
		logger.debug("queryResultID: " + queryResultId);
		logger.debug("resultTuple: " + resultTuple);
		logger.debug("resultTuple.getQueryResultId(): " + resultTuple.getQueryResultId());
		//TODO: dataspaceId ignored for now

		QueryResult queryResult = queryResultRepository.find(queryResultId);
		logger.debug("queryResult: " + queryResult);
		;
		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());
		constrainingModelManagementConstructs.addAll(queryResult.getMappings());
		logger.debug("query: " + queryResult.getQuery());

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		ResultInstance missingResultInstance = new ResultInstance();
		missingResultInstance.setQuery(queryResult.getQuery());
		missingResultInstance.setResultType(queryResult.getResultType());

		for (ResultValueDTO resultValue : resultTuple.getResultValues().values()) {
			logger.debug("resultValue.getId(): " + resultValue.getId());
			logger.debug("resultValue.getName(): " + resultValue.getName());
			logger.debug("resultValue.getValue(): " + resultValue.getValue());

			ResultValue value = new ResultValue(resultValue.getName(), resultValue.getValue());
			missingResultInstance.addResultValue(resultValue.getName(), value);
		}

		missingResultInstance.setUserSpecified(true);
		missingResultInstance.addAllMappings(queryResult.getMappings());
		queryResult.addResultInstance(missingResultInstance);
		//resultInstanceRepository.save(missingResultInstance);
		//resultInstanceRepository.flush();

		annotationService
				.annotate("expectancy", "true", missingResultInstance, queryResult, constrainingModelManagementConstructs, true, currentUser);
		logger.debug("missingResultInstance.getId(): " + missingResultInstance.getId());

		List<Annotation> annots = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(missingResultInstance, currentUser);
		annotationsOfResultInstancesToPropagate.addAll(annots);
		logger.debug("annotationsOfResultInstancesToPropagate: " + annotationsOfResultInstancesToPropagate);

		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();
		constructsToAnnotate.addAll(queryResult.getMappings());
		logger.debug("constructsToAnnotate: " + constructsToAnnotate);

		Set<ModelManagementConstruct> constrainingConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingConstructs.add(queryResult.getQuery());
		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingConstructs, false,
				currentUser);

		hasMissingResultTuples = true;
		return missingResultInstance.getId();
	}

	//TODO: add responseStatus
	@RequestMapping(method = RequestMethod.POST, value = "/dataspace/{dataspaceId}/query")
	@ResponseBody()
	public Long addNewQuery(@PathVariable Long dataspaceId, @RequestBody QueryDTO queryDTO) {
		logger.debug("in addNewQuery");
		logger.debug("dataspaceID: " + dataspaceId);
		logger.debug("queryDTO: " + queryDTO);
		logger.debug("queryDTO.getSchemaId(): " + queryDTO.getSchemaId());
		//TODO: dataspaceId ignored for now

		Schema querySchema = schemaRepository.find(queryDTO.getSchemaId());
		logger.debug("querySchema: " + querySchema.getName());
		logger.debug("querySchema.getId: " + querySchema.getId());

		String queryString = queryDTO.getQueryString();
		logger.debug("queryString: " + queryString);
		String description = queryDTO.getDescription();
		logger.debug("description: " + description);
		String queryName = queryDTO.getQueryName();
		logger.debug("queryName: " + queryName);

		Query query = new Query(queryName, queryString);
		query.setDescription(description);
		query.addSchema(querySchema);

		logger.debug("query.getSchemas: " + query.getSchemas());

		dataspace.addQuery(query);
		queryService.addQuery(query);

		Long queryId = query.getId();
		logger.debug("id: " + queryId);

		return queryId;
	}

	//TODO: sort out queryExpansion when query is posed over one of the source schemas that are set
	//as datasources over which the query is to be evaluated ... might just generally clone the querytree
	//without expansion, i.e., add another union without any expansion

	//TODO: add responseStatus
	@RequestMapping(method = RequestMethod.POST, value = "/dataspace/{dataspaceId}/query/{queryId}")
	@ResponseBody()
	public QueryResultDTO evaluateQueryAndGetQueryResult(@PathVariable Long dataspaceId, @PathVariable Long queryId,
			@RequestBody EvaluateQueryParametersDTO evaluateQueryParametersDTO) {
		logger.debug("in evaluateQueryAndGetQueryResult");
		logger.debug("dataspaceId: " + dataspaceId);
		logger.debug("queryId: " + queryId);
		logger.debug("evaluateQueryParametersDTO: " + evaluateQueryParametersDTO);
		//TODO: dataspaceId ignored for now

		logger.debug("evaluateQueryParametersDTO.getSelectedPrecisionOrRecall(): " + evaluateQueryParametersDTO.getSelectedPrecisionOrRecall());

		Query query = queryRepository.find(queryId);
		logger.debug("query: " + query);

		Map<String, ControlParameter> controlParameters = null;

		if (evaluateQueryParametersDTO.getSelectedPrecisionOrRecall() != null
				&& !evaluateQueryParametersDTO.getSelectedPrecisionOrRecall().equals("")
				&& !evaluateQueryParametersDTO.getSelectedPrecisionOrRecall().equals("notSelected")) {
			logger.debug("got control parameters");
			controlParameters = new HashMap<String, ControlParameter>();
			ControlParameter thresholdTypeControlParameter = null;// new ControlParameter("thresholdType",
					//evaluateQueryParametersDTO.getSelectedPrecisionOrRecall());
			logger.debug("thresholdTypeControlParameter: " + thresholdTypeControlParameter);
			ControlParameter thresholdValueControlParameter = null ;//new ControlParameter("thresholdValue",
					//evaluateQueryParametersDTO.getSelectedPrecisionOrRecallValue());
			logger.debug("thresholdValueControlParameter: " + thresholdValueControlParameter);
			controlParameters = new HashMap<String, ControlParameter>();
			controlParameters.put("thresholdType", thresholdTypeControlParameter);
			controlParameters.put("thresholdValue", thresholdValueControlParameter);
		}
		logger.debug("controlParameters: " + controlParameters);

		for (String dataSourceName : evaluateQueryParametersDTO.getDataSources()) {
			logger.debug("dataSourceName: " + dataSourceName);
			DataSource dataSource = dataSourceRepository.getDataSourceWithSchemaName(dataSourceName);
			logger.debug("dataSource: " + dataSource);
			query.addDataSource(dataSource);
		}

		logger.debug("before evaluating query");

		QueryResult queryResult = null; //queryService.evaluateQuery(query, null, currentUser, controlParameters);
		//logger.debug("after evaluating query");

		QueryResultDTO queryResultDTO = new QueryResultDTO();
		queryResultDTO.setDataSources(evaluateQueryParametersDTO.getDataSources());
		queryResultDTO.setName(evaluateQueryParametersDTO.getName());
		queryResultDTO.setQueryId(Long.parseLong(evaluateQueryParametersDTO.getQueryId()));
		queryResultDTO.setQueryName(evaluateQueryParametersDTO.getQueryName());
		queryResultDTO.setQueryString(evaluateQueryParametersDTO.getQueryString());
		queryResultDTO.setSchemaId(Long.parseLong(evaluateQueryParametersDTO.getSchemaId()));
		queryResultDTO.setSchemaName(evaluateQueryParametersDTO.getSchemaName());
		queryResultDTO.setSelectedPrecisionOrRecall(evaluateQueryParametersDTO.getSelectedPrecisionOrRecall());
		if (evaluateQueryParametersDTO.getSelectedPrecisionOrRecallValue() != null)
			queryResultDTO.setSelectedPrecisionOrRecallValue(Double.parseDouble(evaluateQueryParametersDTO.getSelectedPrecisionOrRecallValue()));
		else
			queryResultDTO.setSelectedPrecisionOrRecallValue(-1);

		MappingOperator rootOperatorOfExpandedQuery = queryResult.getQuery().getRootOperatorOfExpandedQuery();
		logger.debug("rootOperatorOfExpandedQuery: " + rootOperatorOfExpandedQuery);
		String queryDot = graphvizDotGenerator.generateDot(rootOperatorOfExpandedQuery, currentUser);
		logger.debug("queryDot: " + queryDot);
/*
		String queryDotFile;
		String mappingDotFile;
		String graphvizLocation;
		String graphvizParameters;
		if (this.onIceberg) {
			graphvizLocation = this.icebergGraphvizLocation;
			graphvizParameters = this.icebergGraphvizParameters;
			queryDotFile = this.icebergQueryDotFile;
			mappingDotFile = this.icebergMappingDotFile;
		} else {
			graphvizLocation = this.macGraphvizLocation;
			graphvizParameters = this.macGraphvizParameters;
			queryDotFile = this.macQueryDotFile;
			mappingDotFile = this.macMappingDotFile;
		}

		try {
			logger.debug("before writing queryDot to file");

			Writer out = new OutputStreamWriter(new FileOutputStream(queryDotFile));
			out.write(queryDot);
			out.flush();
			out.close();

			logger.debug("queryDot file written");

			logger.debug("before creating ProcessBuilder");
			logger.debug("graphvizLocation: " + graphvizLocation);
			logger.debug("graphvizParameters: " + graphvizParameters);
			logger.debug("queryDotFile: " + queryDotFile);

			String[] commandStr = new String[] { graphvizLocation, "-Tsvg", queryDotFile };

			Process p = new ProcessBuilder(commandStr).start();
			logger.debug("p: " + p);

			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder svgStringBuilder = new StringBuilder();
			//svgStringBuilder.append("<div>");
			String line;
			logger.debug("getting output from graphviz process");
			boolean appendToStringBuilder = false;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("<svg"))
					appendToStringBuilder = true;
				if (appendToStringBuilder)
					svgStringBuilder.append(line);
				logger.debug(line);
			}
			logger.debug("Program terminated!");
			//svgStringBuilder.append("</div>");
			String svgString = svgStringBuilder.toString();
			logger.debug("svgString: " + svgString);
			this.queryResultExpandedQuerySvgMap.put(queryResult.getId(), svgString);

		} catch (IOException e) {
			logger.error("error: " + e);
			e.printStackTrace();
		}

		Set<Mapping> mappings = queryResult.getMappings();
		logger.debug("mappings: " + mappings);
		String mappingsDot = graphvizDotGenerator.generateDot(mappings, currentUser);
		logger.debug("mappingsDot: " + mappingsDot);

		try {
			logger.debug("before writing mappingsDot to file");

			Writer out = new OutputStreamWriter(new FileOutputStream(mappingDotFile));
			out.write(mappingsDot);
			out.flush();
			out.close();

			logger.debug("mappingsDot file written");

			logger.debug("before creating ProcessBuilder");
			logger.debug("graphvizLocation: " + graphvizLocation);
			logger.debug("graphvizParameters: " + graphvizParameters);
			logger.debug("mappingDotFile: " + mappingDotFile);

			String[] commandStr = new String[] { graphvizLocation, "-Tsvg", mappingDotFile };

			Process p = new ProcessBuilder(commandStr).start();
			logger.debug("p: " + p);

			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder svgStringBuilder = new StringBuilder();
			//svgStringBuilder.append("<div>");
			String line;
			logger.debug("getting output from graphviz process");
			boolean appendToStringBuilder = false;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("<svg"))
					appendToStringBuilder = true;
				if (appendToStringBuilder)
					svgStringBuilder.append(line);
				logger.debug(line);
			}
			logger.debug("Program terminated!");
			//svgStringBuilder.append("</div>");
			String svgString = svgStringBuilder.toString();
			logger.debug("svgString: " + svgString);
			this.queryResultMappingsSvgMap.put(queryResult.getId(), svgString);

		} catch (IOException e) {
			logger.error("error: " + e);
			e.printStackTrace();
		}
*/
		logger.debug("queryResult.id: " + queryResult.getId());
		queryResultDTO.setId(queryResult.getId());

		MorphismSetDTO morphismSet = new MorphismSetDTO();
		morphismSet.setId("morph_" + queryResult.getId());
		morphismSet.setType("mapping");
		morphismSet.setName("Mapping for " + queryResultDTO.getName());
		morphismSet.addConstructSet1ID(queryResultDTO.getSchemaId());
		//for (String dataSourceName : evaluateQueryParametersDTO.getDataSources()) {
		for (Schema schema : queryResult.getSchemasOfDataSourcesQueried()) {
			//logger.debug("dataSourceName: " + dataSourceName);
			//Schema schema = schemaRepository.getSchemaByName(dataSourceName);
			logger.debug("schema: " + schema);
			morphismSet.addConstructSet2ID(schema.getId());
		}

		logger.debug("queryResult.getMappings(): " + queryResult.getMappings());
		for (Mapping mapping : queryResult.getMappings()) {
			logger.debug("mapping: " + mapping);
			logger.debug("mapping.id: " + mapping.getId());
			logger.debug("queryResultDTO: " + queryResultDTO);

			MorphismDTO morphism = new MorphismDTO();
			morphism.setId(mapping.getId());
			String query1String = mapping.getQuery1String();
			String constructName = query1String.substring(query1String.indexOf("FROM") + 4).trim();
			morphism.setQuery1String(constructName);
			morphism.setQuery2String(mapping.getQuery2String());
			for (CanonicalModelConstruct construct1 : mapping.getConstructs1()) {
				morphism.addConstructSet1(construct1.getId());
			}
			for (CanonicalModelConstruct construct2 : mapping.getConstructs2()) {
				morphism.addConstructSet2(construct2.getId());
			}
			List<Annotation> precisionAnnotations = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(
					mapping, "precision", currentUser);
			if (precisionAnnotations == null || precisionAnnotations.size() == 0)
				logger.debug("no precision annotation found for mapping");
			else if (precisionAnnotations.size() > 1)
				logger.error("more than one precision annotation for mapping - TODO: sort this");
			else {
				Annotation precision = precisionAnnotations.get(0);
				logger.debug("found precision for mapping: " + precision.getValue());
				morphism.setPrecision(Double.parseDouble(precision.getValue()));
			}
			List<Annotation> recallAnnotations = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(
					mapping, "recall", currentUser);
			if (recallAnnotations == null || recallAnnotations.size() == 0)
				logger.debug("no recall annotation found for mapping");
			else if (recallAnnotations.size() > 1)
				logger.error("more than one recall annotation for mapping - TODO: sort this");
			else {
				Annotation recall = recallAnnotations.get(0);
				logger.debug("found recall for mapping: " + recall.getValue());
				morphism.setRecall(Double.parseDouble(recall.getValue()));
			}
			morphismSet.addMorphism(morphism);
		}
		queryResultDTO.setMorphismSet(morphismSet);
		logger.debug("queryResultDTO.getMorphismSet(): " + queryResultDTO.getMorphismSet());
		logger.debug("queryResultDTO.getMorphismSet().getId(): " + queryResultDTO.getMorphismSet().getId());
		logger.debug("queryResultDTO.getMorphismSet().getMorphisms(): " + queryResultDTO.getMorphismSet().getMorphisms());

		queryResultDTO.addResultColumnName("id");
		logger.debug("queryResult.getResultTpe(): " + queryResult.getResultType());
		if (queryResult.getResultType() != null) {
			for (String resultFieldName : queryResult.getResultType().getResultFields().keySet()) {
				queryResultDTO.addResultColumnName(resultFieldName);
				logger.debug("resultFieldName: " + resultFieldName);
			}
		}

		String[][] resultSet = new String[queryResult.getResultInstances().size()][queryResultDTO.getResultSetColumnNames().size()];
		queryResultDTO.setNumberOfResultTuples(queryResult.getResultInstances().size());
		int noResultInstance = 0;
		for (ResultInstance resultInstance : queryResult.getResultInstances()) {
			int noColumn = 0;
			resultSet[noResultInstance][noColumn] = resultInstance.getId().toString();
			logger.debug("resultSet[noResultInstance][noColumn]: " + resultSet[noResultInstance][noColumn]);
			noColumn++;
			for (String resultFieldName : queryResultDTO.getResultSetColumnNames()) {
				logger.debug("resultFieldName: " + resultFieldName);
				if (!resultFieldName.equals("id")) {
					logger.debug("noResultInstance: " + noResultInstance);
					logger.debug("noColumn: " + noColumn);
					logger.debug("resultInstance.getResultValue(resultFieldName): " + resultInstance.getResultValue(resultFieldName));
					if (resultInstance.getResultValue(resultFieldName) != null) {
						logger.debug("resultInstance.getResultValue(resultFieldName).getValue(): "
								+ resultInstance.getResultValue(resultFieldName).getValue());
						resultSet[noResultInstance][noColumn] = resultInstance.getResultValue(resultFieldName).getValue();
						logger.debug("resultSet[noResultInstance][noColumn]: " + resultSet[noResultInstance][noColumn]);
					} else {
						resultSet[noResultInstance][noColumn] = null;
					}
					noColumn++;
				}
			}
			noResultInstance++;
		}
		queryResultDTO.setResultSet(resultSet);
		logger.debug("resultSet: " + resultSet);

		for (Annotation annotation : queryResult.getAnnotations()) {
			if (annotation.getOntologyTerm().getName().equals("precision"))
				queryResultDTO.setPrecision(Double.parseDouble(annotation.getValue()));
			if (annotation.getOntologyTerm().getName().equals("recall"))
				queryResultDTO.setRecall(Double.parseDouble(annotation.getValue()));
		}
		hasQueryResults = true;

		return queryResultDTO;

		//return null;
	}

	//TODO: add responseStatus
	@RequestMapping(method = RequestMethod.POST, value = "/dataspace/{dataspaceId}/datasource")
	@ResponseBody()
	public SchemaDTO createDatasourceAndGetSchema(@PathVariable Long dataspaceId, @RequestBody DataSourceDTO datasourceDTO) {
		logger.debug("in createDataSourceAndGetSchema");
		logger.debug("dataspaceId: " + dataspaceId);
		logger.debug("datasourceDTO: " + datasourceDTO);
		//TODO: dataspaceId ignored for now
		DataSource datasource = null;
		Schema schema = null;
		if (datasourceDTO.getSchemaURL() == null) {

			datasource = dataSourceService.addDataSource(datasourceDTO.getName(), datasourceDTO.getSchemaName(), datasourceDTO.getDescription(),
					datasourceDTO.getDriverClass(), datasourceDTO.getConnectionURL(), datasourceDTO.getUserName(), datasourceDTO.getPassword(),
					datasourceDTO.getSchemaElementsToExcludeXmlFileLocation());
		} else {
			datasource = dataSourceService.addDataSource(datasourceDTO.getName(), datasourceDTO.getSchemaName(), datasourceDTO.getDescription(),
					datasourceDTO.getDriverClass(), datasourceDTO.getConnectionURL(), datasourceDTO.getSchemaURL(), datasourceDTO.getUserName(),
					datasourceDTO.getPassword());
		}
		dataspace.addDataSource(datasource);
		schema = datasource.getSchema();
		logger.debug("schema.getCanonicalModelConstructs.size: " + schema.getCanonicalModelConstructs().size());
		//TODO: something is weird here, there are only 5 SAs in the database, but I'm getting 10 here - sort this out
		int noOfSa = 0;
		for (CanonicalModelConstruct construct : schema.getCanonicalModelConstructs()) {
			if (construct.getTypeOfConstruct().equals(ConstructType.SUPER_ABSTRACT))
				noOfSa++;
		}
		logger.debug("noOfSa: " + noOfSa);

		dataspace.addSchema(schema);
		datasourceDTO.setId(datasource.getId());

		logger.debug("about to convert schema: " + schema);
		SchemaDTO schemaDTO = this.dozerMapper.map(schema, SchemaDTO.class);
		logger.debug("converted schema into scheamDTO: " + schemaDTO);
		logger.debug("***************** before converting constructs in schema *************************");
		logger.debug("schema.getCanonicalModelConstructs().size(): " + schema.getCanonicalModelConstructs().size());
		Set<CanonicalModelConstruct> constructs = schema.getCanonicalModelConstructs();
		int noOfTs = 0;
		for (CanonicalModelConstruct construct : constructs) {
			logger.debug("construct.type: " + construct.getTypeOfConstruct());
			logger.debug("construct.name: " + construct.getName());
			if (construct instanceof SuperAbstract) {
				logger.debug("construct is superAbstract: " + construct);
				SuperAbstract superAbstract = (SuperAbstract) construct;
				EntityDTO entity = this.dozerMapper.map(superAbstract, EntityDTO.class);
				logger.debug("converted superAbstract to entity: " + entity);
				noOfTs++;
				schemaDTO.addEntity(entity);
				logger.debug("added entity to schemaDTO");
				for (SuperLexical superLexical : superAbstract.getSuperLexicals()) {
					logger.debug("about to convert superLexical: " + superLexical);
					AttributeDTO attribute = this.dozerMapper.map(superLexical, AttributeDTO.class);
					logger.debug("converted superLexical into attribute: " + attribute);
					entity.addAttribute(attribute);
				}
			}
		}
		logger.debug("noOfTs: " + noOfTs);
		return schemaDTO;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataspace/{dataspaceId}/queries")
	public @ResponseBody
	List<QueryDTO> getQueries(@PathVariable Long dataspaceId) {
		logger.debug("in getQueries");
		List<QueryDTO> queryDTOs = new ArrayList<QueryDTO>();
		//TODO: dataspaceId ignored for now
		List<Query> queries = queryRepository.getAllQueriesWithSchemasOrderedByQueryId();
		logger.debug("got queries from service: " + queries.size());
		for (Query query : queries) {
			QueryDTO queryDTO = this.dozerMapper.map(query, QueryDTO.class);
			queryDTO.setSchemaId(query.getSchemas().iterator().next().getId());
			queryDTOs.add(queryDTO);
		}
		return queryDTOs;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataspace/{dataspaceId}/queryResult/{queryResultId}/expandedQuery")
	public @ResponseBody
	String getExpandedQueryForQueryResult(@PathVariable Long dataspaceId, @PathVariable Long queryResultId) {
		logger.debug("in getExpandedQueryForQueryResult");
		String expandedQuerySvg = this.queryResultExpandedQuerySvgMap.get(queryResultId);
		logger.debug("expandedQuerySvg: " + expandedQuerySvg);
		return expandedQuerySvg;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataspace/{dataspaceId}/queryResult/{queryResultId}/usedMappings")
	public @ResponseBody
	String getMappingsUsedForQueryResult(@PathVariable Long dataspaceId, @PathVariable Long queryResultId) {
		logger.debug("in getMappingsUsedForQueryResult");
		String usedMappingsSvg = this.queryResultMappingsSvgMap.get(queryResultId);
		logger.debug("usedMappingsSvg: " + usedMappingsSvg);
		return usedMappingsSvg;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/dataspace/{dataspaceId}/schemas")
	public @ResponseBody
	List<SchemaDTO> getSchemas(@PathVariable Long dataspaceId) {
		//TODO: dataspaceID is ignored for now

		if (!isInitialised)
			init();
		else {
			logger.debug("hasAnnotation: " + hasAnnotation);
			logger.debug("hasMissingResultTuples: " + hasMissingResultTuples);
			logger.debug("hasQueryResults: " + hasQueryResults);
			List<Annotation> allAnnotations = annotationRepository.findAll();

			if (allAnnotations != null && allAnnotations.size() > 0) {
				logger.debug("hasAnnotation: " + hasAnnotation);
				logger.debug("hasMissingResultTuples: " + hasMissingResultTuples);
				for (Annotation annotation : allAnnotations) {
					for (ModelManagementConstruct annotatedConstruct : annotation.getAnnotatedModelManagementConstructs()) {
						annotatedConstruct.removeAnnotation(annotation);
					}
					List<ModelManagementConstruct> constructsToRemove = new ArrayList<ModelManagementConstruct>();
					for (ModelManagementConstruct constrainingConstruct : annotation.getConstrainingModelManagementConstructs()) {
						constructsToRemove.add(constrainingConstruct);
					}
					for (ModelManagementConstruct constructToRemove : constructsToRemove) {
						annotation.removeConstrainingModelManagementConstruct(constructToRemove);
					}
					annotationRepository.delete(annotation);
				}
				hasAnnotation = false;
				hasMissingResultTuples = false;
			}

			List<QueryResult> queryResults = queryResultRepository.findAll();
			if (queryResults != null && queryResults.size() > 0) {
				logger.debug("hasQueryResults");
				for (QueryResult queryResult : queryResults) {
					List<ResultInstance> resultInstancesToRemove = new ArrayList<ResultInstance>();
					for (ResultInstance resultInstance : queryResult.getResultInstances()) {
						resultInstancesToRemove.add(resultInstance);
					}
					for (ResultInstance resultInstance : resultInstancesToRemove) {
						queryResult.removeResultInstance(resultInstance);
					}
					//queryResultRepository.flush();
					//queryResultRepository.delete(queryResult);
					hasQueryResults = false;
				}
			}

			//List<ResultInstance> resultInstances = resultInstanceRepository.findAll();
			//if (resultInstances != null && resultInstances.size() > 0) {
			//	logger.debug("got resultInstances");
			//	for (ResultInstance resultInstance : resultInstances) {
			resultInstanceRepository.delete(resultInstanceRepository.findAll());
			//	}
			//}
			//resultInstanceRepository.flush();

			//queryResults = queryResultRepository.findAll();
			//if (queryResults != null && queryResults.size() > 0) {
			//	logger.debug("hasQueryResults");
			//for (QueryResult queryResult : queryResults) {
			queryResultRepository.delete(queryResultRepository.findAll());
			//}
			hasQueryResults = false;
			//}
		}

		List<Schema> schemas = schemaRepository.findAll();
		List<SchemaDTO> schemaDTOs = new ArrayList<SchemaDTO>();
		for (Schema schema : schemas) {
			SchemaDTO schemaDTO = this.dozerMapper.map(schema, SchemaDTO.class);
			for (CanonicalModelConstruct construct : schema.getCanonicalModelConstructs()) {
				if (construct instanceof SuperAbstract) {
					SuperAbstract superAbstract = (SuperAbstract) construct;
					EntityDTO entity = this.dozerMapper.map(superAbstract, EntityDTO.class);
					schemaDTO.addEntity(entity);
					for (SuperLexical superLexical : superAbstract.getSuperLexicals()) {
						AttributeDTO attribute = this.dozerMapper.map(superLexical, AttributeDTO.class);
						entity.addAttribute(attribute);
					}
				}
			}
			schemaDTOs.add(schemaDTO);
		}
		return schemaDTOs;
	}

	/*
	@RequestMapping({ "/", "/home" })
	public String showHomePage(Map<String, Object> model) {
		logger.debug("in showHomePage");
		init();
		return "index";
	}
	*/

	/*
	@RequestMapping(value = "runquery", method = RequestMethod.GET)
	public @ResponseBody
	String runQuery(@RequestParam String queryId, @RequestParam String precisionOrRecall, @RequestParam String precisionOrRecallThreshold) {
		logger.debug("in runQuery");
		logger.debug("queryId: " + queryId);
		logger.debug("precisionOrRecall: " + precisionOrRecall);
		logger.debug("precisionOrRecallThreshold: " + precisionOrRecallThreshold);

		List<Query> queries = queryRepository.getAllQueriesWithSchemasOrderedByQueryId();
		Query query = queries.get(new Integer(queryId) - 1);

		//Query query = queryService.getQueryWithIdWithSchemasDataSources(new Long(queryId));// findQuery(new Long(queryId));
		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);
		logger.debug("got queryResult: " + queryResult);
		currentQueryResult = queryResult;

		//TODO: add precision and recall

		logger.debug("queryResult.instances.size: " + queryResult.getResultInstances().size());
		if (queryResult.getMappings() != null)
			logger.debug("queryResult.mappings.size: " + queryResult.getMappings().size());

		return createTab(queryResult);
	}
	*/

	//TODO: should probably return an indicator whether it's saved it or not
	//TODO: params shouldn't all be String
	/*
	@RequestMapping(value = "saveResultAnnotation", method = RequestMethod.POST)
	//public void saveResultAnnotation(@RequestParam String resultInstanceId, @RequestParam String changedValue, @RequestParam String value) {
	public void saveResultAnnotation(@RequestParam String id, @RequestParam String expected, @RequestParam String notExpected) {
		logger.debug("in saveResultAnnotation");
		logger.debug("id: " + id);
		logger.debug("expected: " + expected);
		logger.debug("notExpected: " + notExpected);
		if (expected.equals("Yes") || (notExpected.equals("Yes"))) {
			logger.debug("found feedback");
			boolean isExpected = false;
			boolean isNotExpected = false;
			if (expected.equals("Yes"))
				isExpected = true;
			if (notExpected.equals("Yes"))
				isNotExpected = true;
			if (isExpected && isNotExpected)
				logger.error("got conflicting feedback, ignore");
			//TODO: change this to use new methods in AnnotationServiceImpl
			//queryResultService.addExpectancyAnnotationToQueryResultInstance(new Long(id), isExpected, isNotExpected, currentUser);
		}
	}
	*/

	/*
	@RequestMapping(value = "annotateMappings", method = RequestMethod.POST)
	public void annotateMappings() {
		logger.debug("in annotateMappings");
		//TODO: change this to use new methods in AnnotationServiceImpl
		//mappingService.annotateMappingsUsedToProduceQueryResult(currentQueryResult, currentUser);
	}
	*/

	/*
	private String createTab(QueryResult queryResult) {
		logger.debug("in createTab");

		this.currentQueryResult = queryResult;
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		Set<Mapping> mappings = queryResult.getMappings();

		logger.debug("queryResult: " + queryResult);
		logger.debug("resultInstances: " + resultInstances);
		logger.debug("mappings: " + mappings);

		String estimatedPrecision = "NA";
		String estimatedRecall = "NA";

		List<Annotation> annotationsOfQueryResult = queryResult.getAnnotations();
		for (Annotation annotation : annotationsOfQueryResult) {
			if (annotation.getOntologyTerm().getName().equals("precision"))
				estimatedPrecision = annotation.getValue();
			if (annotation.getOntologyTerm().getName().equals("recall"))
				estimatedRecall = annotation.getValue();
		}

		StringBuilder jsonSB = new StringBuilder();
		jsonSB.append("<div id=\"result_tab\">\n");
		jsonSB.append("<div id=\"tab_container\">\n");
		jsonSB.append("<div id=\"resultsTable_container\">\n");
		jsonSB.append("<h1><span>Query results </span> -- <span> Estimated Precision = ");
		jsonSB.append(estimatedPrecision);
		jsonSB.append("</span> -- <span> Estimated Recall = ");
		jsonSB.append(estimatedRecall);
		jsonSB.append("</span></h1>\n");
		//jsonSB.append("<form id=\"resultsTableForm\">\n");
		jsonSB.append("<table id=\"resultsTable\">\n");

		/*
		int i = 1;
		List<String> orderedListOfFieldNames = new ArrayList<String>();
		for (ResultInstance resultInstance : resultInstances) {
			Map<String, String> resultFieldNameResultValueMap = resultInstance.getResultFieldNameResultValueMap();
			Set<String> resultFieldNames = resultFieldNameResultValueMap.keySet();
			if (i == 1) {
				jsonSB.append("<thead>\n");
				jsonSB.append("<tr>\n");
				jsonSB.append("<th>");
				jsonSB.append("id");
				jsonSB.append("</th>\n");
				for (String resultFieldName : resultFieldNames) {
					orderedListOfFieldNames.add(resultFieldName);
					jsonSB.append("<th>");
					jsonSB.append(resultFieldName);
					jsonSB.append("</th>\n");
				}
				jsonSB.append("<th>");
				jsonSB.append("expected");
				jsonSB.append("</th>\n");
				jsonSB.append("<th>");
				jsonSB.append("not expected");
				jsonSB.append("</th>\n");
				jsonSB.append("</tr>\n");
				jsonSB.append("</thead>\n");
				jsonSB.append("<tbody>\n");
			}
			jsonSB.append("<tr>\n");
			jsonSB.append("<td>");
			jsonSB.append(resultInstance.getId());
			jsonSB.append("</td>\n");
			for (String resultFieldName : orderedListOfFieldNames) {
				jsonSB.append("<td>");
				jsonSB.append(resultFieldNameResultValueMap.get(resultFieldName));
				jsonSB.append("</td>\n");
			}
			jsonSB.append("<td>");
			jsonSB.append("No");
			jsonSB.append("</td>\n");
			jsonSB.append("<td>");
			jsonSB.append("No");
			jsonSB.append("</td>\n");
			jsonSB.append("</tr>\n");
			i++;
		}
		jsonSB.append("</tbody>\n");
		
		jsonSB.append("</table>\n");
		//jsonSB.append("<div id=\"resultsTablePager\">\n");
		//jsonSB.append("</div>\n");
		//jsonSB.append("</form>\n");
		jsonSB.append("</div>\n");
		jsonSB.append("<div id=\"feedbackButtons\">\n");
		jsonSB.append("<div id=\"addMissingResultButton\">\n");
		jsonSB.append("<button id=\"addMissingResult\">\n");
		jsonSB.append("Add missing result tuple\n");
		jsonSB.append("</button>\n");
		jsonSB.append("</div>\n");
		jsonSB.append("<button id =\"loadFeedback\">\n");
		jsonSB.append("Load Feedback\n");
		jsonSB.append("</button>\n");
		jsonSB.append("<button id =\"applyFeedback\">\n");
		jsonSB.append("Apply Feedback\n");
		jsonSB.append("</button>\n");
		jsonSB.append("</div>\n");
		jsonSB.append("<div id=\"mappingsTable_container\">\n");
		jsonSB.append("<h1>Mappings</h1>\n");
		jsonSB.append("<p>This is the list of mappings that have been used to retrieve the results shown above.</p>\n");
		jsonSB.append("<table id=\"mappingsTable\">\n");

		//TODO: sort out annotation model

		int i = 1;
		if (mappings != null) {
			for (Mapping mapping : mappings) {
				if (i == 1) {
					jsonSB.append("<thead>\n");
					jsonSB.append("<tr>\n");

					jsonSB.append("<th>");
					jsonSB.append("id");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("schema1");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("query1");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("schema2");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("query2");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("precision");
					jsonSB.append("</th>\n");
					jsonSB.append("<th>");
					jsonSB.append("recall");
					jsonSB.append("</th>\n");

					jsonSB.append("</tr>\n");
					jsonSB.append("</thead>\n");
					jsonSB.append("<tbody>\n");
				}
				jsonSB.append("<tr>\n");

				jsonSB.append("<td>");
				jsonSB.append(mapping.getId());
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				jsonSB.append(mapping.getQuery1().getSchemas().iterator().next().getName());
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				jsonSB.append(mapping.getQuery1().getQueryString());
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				jsonSB.append(mapping.getQuery2().getSchemas().iterator().next().getName());
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				jsonSB.append(mapping.getQuery2().getQueryString());
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				//if (mappingRepository.getAnnotationValueForMappingAndGivenOntologyTermName(mapping, "precision") == null)
				List<Annotation> precisionA = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(
						mapping, "precision", currentUser);
				if (precisionA == null || precisionA.size() == 0)
					jsonSB.append("NA");
				else if (precisionA.size() == 1)
					jsonSB.append(precisionA.get(0).getValue());
				else
					logger.error("more than one precision annotation for query result - TODO: sort this");
				jsonSB.append("</td>\n");
				jsonSB.append("<td>");
				//if (mappingRepository.getAnnotationValueForMappingAndGivenOntologyTermName(mapping, "recall") == null)
				List<Annotation> recallA = annotationRepository.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(mapping,
						"recall", currentUser);
				if (recallA == null || recallA.size() == 0)
					jsonSB.append("NA");
				else if (recallA.size() == 1)
					jsonSB.append(recallA.get(0).getValue());
				else
					logger.error("more than one recall annotation for query result - TODO: sort this");
				jsonSB.append("</td>\n");

				jsonSB.append("</tr>\n");
				i++;
			}
		}

		jsonSB.append("</table>\n");
		//jsonSB.append("<div id=\"mappingsTablePager\">\n");
		//jsonSB.append("</div>\n");
		jsonSB.append("</div>\n");
		jsonSB.append("</div>\n");
		jsonSB.append("</div>\n");

		jsonSB.append("<script type=\"text/javascript\">\n");
		jsonSB.append("$(function(){\n");
		//jsonSB.append("var lastSelResultId = -1;\n");
		//jsonSB.append("tableToGrid(\"#resultsTable\", {height: 120,\n");

		jsonSB.append("$(\"#resultsTable\").jqGrid({\n");
		jsonSB.append("url: 'home/getqueryresults',\n");
		jsonSB.append("datatype: \"json\",\n");
		jsonSB.append("mtype: 'GET',\n");
		jsonSB.append("height: 120,\n");
		//jsonSB.append("colNames: ['id', '");

		List<String> orderedListOfFieldNames = new ArrayList<String>();
		logger.debug("resultInstances: " + resultInstances);
		if (resultInstances != null && resultInstances.size() > 0) {
			ResultInstance resultInstance = resultInstances.iterator().next();
			Map<String, ResultValue> resultFieldNameResultValueMap = resultInstance.getResultFieldNameResultValueMap();
			Set<String> resultFieldNames = resultFieldNameResultValueMap.keySet();
			for (String resultFieldName : resultFieldNames) {
				orderedListOfFieldNames.add(resultFieldName);
			}
			this.currentOrderedListOfFieldNames = orderedListOfFieldNames;
		}

		/*
		for (String resultFieldName : orderedListOfFieldNames) {
			jsonSB.append(resultFieldName);
			jsonSB.append("', '");
		}
		jsonSB.append("expected', 'not expected'],\n");
		
		jsonSB.append("colModel: [\n");
		jsonSB.append("{\n");
		jsonSB.append("name: 'id',\n");
		jsonSB.append("index: 'id',\n");
		jsonSB.append("width: '50'\n");
		jsonSB.append("},\n");

		for (String resultFieldName : orderedListOfFieldNames) {
			jsonSB.append("{\n");
			jsonSB.append("name: '");
			jsonSB.append(resultFieldName);
			jsonSB.append("',\n");
			jsonSB.append("index: '");
			jsonSB.append(resultFieldName);
			jsonSB.append("',\n");
			jsonSB.append("width: '");
			jsonSB.append(900 / orderedListOfFieldNames.size());
			jsonSB.append("'\n");
			jsonSB.append("},\n");
		}
		jsonSB.append("{\n");
		jsonSB.append("name: 'expected',\n");
		jsonSB.append("index: 'expected',\n");
		jsonSB.append("width: '70',\n");
		jsonSB.append("align: 'center',\n");
		jsonSB.append("formatter:'checkbox',\n");
		jsonSB.append("formatoptions:{disabled:\"false\"},\n");
		jsonSB.append("editable: true,\n");
		jsonSB.append("edittype:'checkbox',\n");
		jsonSB.append("editoptions: {value:\"Yes:No\"}\n");
		jsonSB.append("},\n");
		jsonSB.append("{\n");
		jsonSB.append("name: 'notExpected',\n");
		jsonSB.append("index: 'notExpected',\n");
		jsonSB.append("width: '70',\n");
		jsonSB.append("align: 'center',\n");
		jsonSB.append("formatter:'checkbox',\n");
		jsonSB.append("formatoptions:{disabled:\"false\"},\n");
		jsonSB.append("editable: true,\n");
		jsonSB.append("edittype:'checkbox',\n");
		jsonSB.append("editoptions: {value:\"Yes:No\"}\n");
		jsonSB.append("}\n");
		jsonSB.append("],\n");
		jsonSB.append("jsonReader: {\n");
		jsonSB.append("repeatitems: false\n");
		jsonSB.append("}\n"); //,\n");
		//jsonSB.append("onSelectRow: function(id){\n");
		//jsonSB.append("if(id){\n");
		//jsonSB.append("var rowData = $(\"#resultsTable\").getRowData(id);\n");
		//jsonSB.append("alert(\"id:\" + id + \" expected: \" + rowData.expected);");
		//jsonSB.append(orderedListOfFieldNames.size() + 1);
		//jsonSB.append("]);");
		//jsonSB.append("alert(\"id=\"+id+\" expected=\"+valuesOfLastSelRow.expected+\" not expected=\"+valuesOfLastSelRow.notExpected);");
		//jsonSB.append("lastSelResultId=id;\n");
		//jsonSB.append("}\n");
		//jsonSB.append("},\n");
		//jsonSB.append("editurl: 'home/saveResultAnnotation'\n");
		jsonSB.append("});\n");
		jsonSB.append("tableToGrid(\"#mappingsTable\", {height: 80});\n");
		jsonSB.append("$(\"#applyFeedback\").click(function(){\n");
		jsonSB.append("var ids = $(\"#resultsTable\").getDataIDs();\n");
		//jsonSB.append("alert(\"ids: \" + ids + \"ids.length: \" + ids.length);\n");
		//jsonSB.append("var paras=[];\n");
		jsonSB.append("for(var i=0;i<ids.length;i++){\n");
		jsonSB.append("var row=$(\"#resultsTable\").getRowData(ids[i]);\n");
		//jsonSB.append("alert(\"id:\" + ids[i] + \" expected: \" + row.expected + \" notExpected: \" + row.notExpected);\n");
		//jsonSB.append("paras.push($.param({id: ids[i], expected: row.expected, notExpected: row.notExpected}));\n");
		//$.ajax({
		//      type: "POST",
		//      url: "/someurl.do",
		//      data: paras.join('and'),
		//      success: function(msg){
		//          alert(msg);
		//      }
		//  });
		jsonSB.append("$.post('home/saveResultAnnotation', $.param({id: ids[i], expected: row.expected, notExpected: row.notExpected}));\n");
		jsonSB.append("}\n");
		jsonSB.append("$.post('home/annotateMappings');\n");
		//jsonSB.append("$.post('home/saveResultAnnotation', $.param(gridData));\n");
		//jsonSB.append("type: 'post',\n");
		//jsonSB.append("data: gridData});\n");
		jsonSB.append("});\n");
		jsonSB.append("});\n");
		//jsonSB.append("$(\"#resultsTable\").saveRow(\"id\", false);\n");
		jsonSB.append("</script>");

		logger.debug(jsonSB.toString());

		return jsonSB.toString();
	}
	*/

	/*
	@RequestMapping(value = "getqueryresults", method = RequestMethod.GET)
	public @ResponseBody
	String getQueryResults() {
		logger.debug("in getQueryResults");
		return convertQueryResultsIntoJsonForJQgrid(this.currentQueryResult, this.currentOrderedListOfFieldNames);
	}
	*/

	/*
	private String convertQueryResultsIntoJsonForJQgrid(QueryResult queryResult, List<String> orderedListOfFieldNames) {
		logger.debug("in convertQueryResultsIntoJsonForJQgrid");
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		StringBuilder jsonSB = new StringBuilder();
		jsonSB.append("{ \n");
		jsonSB.append("\"page\" : \"1\", \n");
		jsonSB.append("\"total\" : \"1\", \n");
		jsonSB.append("\"records\" : \"");
		jsonSB.append(resultInstances.size());
		jsonSB.append("\", \n");
		jsonSB.append("\"rows\" : [ \n");
		int i = 1;
		for (ResultInstance resultInstance : resultInstances) {
			jsonSB.append("{\"id\":\"");
			jsonSB.append(resultInstance.getId());
			jsonSB.append("\",");
			for (String resultFieldName : orderedListOfFieldNames) {
				jsonSB.append("\"");
				jsonSB.append(resultFieldName);
				jsonSB.append("\":\"");
				jsonSB.append(resultInstance.getResultValue(resultFieldName).getValue());
				jsonSB.append("\",");
			}
			jsonSB.append("\"expected\":\"No\",");
			jsonSB.append("\"notExpected\":\"No\"}");
			if (i < resultInstances.size())
				jsonSB.append(",");
			jsonSB.append("\n");
			i++;
		}
		jsonSB.append("] \n");
		jsonSB.append("}");

		logger.debug(jsonSB.toString());

		return jsonSB.toString();
	}
	*/

	/*
	private String convertQueriesIntoJsonForJQgrid(List<Query> queries) {
		logger.debug("in convertQueriesIntoJsonForJQgrid");
		StringBuilder jsonSB = new StringBuilder();
		jsonSB.append("{ \n");
		jsonSB.append("\"page\" : \"1\", \n");
		jsonSB.append("\"total\" : \"1\", \n");
		/*
		int totalNumberOfPages = queries.size() / 5;
		if (totalNumberOfPages % 5 > 0)
			totalNumberOfPages++;
		jsonSB.append(totalNumberOfPages);
		jsonSB.append("\", \n");
		
		jsonSB.append("\"records\" : \"");
		jsonSB.append(queries.size());
		jsonSB.append("\", \n");
		jsonSB.append("\"rows\" : [ \n");
		int i = 1;
		for (Query query : queries) {
			jsonSB.append("{\"id\":\"");
			jsonSB.append(query.getId());
			jsonSB.append("\",\"queryname\":\"");
			jsonSB.append(query.getQueryName());
			jsonSB.append("\",\"description\":\"");
			jsonSB.append(query.getDescription());
			jsonSB.append("\",\"schema\":\"");
			jsonSB.append(query.getSchemas().iterator().next().getName());
			jsonSB.append("\",\"query\":\"");
			jsonSB.append(query.getQueryString());
			jsonSB.append("\"}");
			if (i < queries.size())
				jsonSB.append(",");
			jsonSB.append("\n");
			i++;
		}
		jsonSB.append("] \n");
		jsonSB.append("}");

		logger.debug(jsonSB.toString());

		return jsonSB.toString();
	}
	*/

	/*
	private String convertQueriesIntoHtmlForJQgrid(List<Query> queries) {
		logger.debug("in convertQueriesIntoHtmlForJQgrid");
		StringBuilder htmlSB = new StringBuilder();
		htmlSB.append("<table id=\"queriesTable\">");
		int i = 1;
		for (Query query : queries) {
			if (i == 1) {
				htmlSB.append("<thead>\n");
				htmlSB.append("<tr>\n");

				htmlSB.append("<th>");
				htmlSB.append("id");
				htmlSB.append("</th>\n");
				htmlSB.append("<th>");
				htmlSB.append("queryname");
				htmlSB.append("</th>\n");
				htmlSB.append("<th>");
				htmlSB.append("description");
				htmlSB.append("</th>\n");
				htmlSB.append("<th>");
				htmlSB.append("schema");
				htmlSB.append("</th>\n");
				htmlSB.append("<th>");
				htmlSB.append("query");
				htmlSB.append("</th>\n");

				htmlSB.append("</tr>\n");
				htmlSB.append("</thead>\n");
				htmlSB.append("<tbody>\n");
			}

			htmlSB.append("<tr>\n");

			htmlSB.append("<td>");
			htmlSB.append(query.getId());
			htmlSB.append("</td>\n");
			htmlSB.append("<td>");
			htmlSB.append(query.getQueryName());
			htmlSB.append("</td>\n");
			htmlSB.append("<td>");
			htmlSB.append(query.getDescription());
			htmlSB.append("</td>\n");
			htmlSB.append("<td>");
			htmlSB.append(query.getSchemas().iterator().next().getName());
			htmlSB.append("</td>\n");
			htmlSB.append("<td>");
			htmlSB.append(query.getQueryString());
			htmlSB.append("</td>\n");

			htmlSB.append("</tr>\n");
			i++;
		}
		htmlSB.append("</table>");

		logger.debug(htmlSB.toString());
		return htmlSB.toString();
	}
	*/

	private void init() {

		//TODO: check for open connects and close and reopen them
		//TODO: remove result tuples

		if (!isInitialised) {

			/*
			String fileName = "";
			if (onIceberg)
				fileName = this.graphvizIcebergProperties;
			else
				fileName = this.graphvizMacProperties;
			this.loadGraphvizConfiguration(fileName);
			this.loadMondialIntegrationConfiguration(this.mondialIntegrationProperties);
			*/

			logger.debug("userService: " + userService);

			//add user		
			User connieUser = new User();
			connieUser.setUserName("connie");
			connieUser.setPassword("connie");
			connieUser.setFirstName("connie");
			connieUser.setEmail("anonymous.guest@guest.co.uk");
			userService.addUser(connieUser);
			currentUser = connieUser;

			dataspace = new Dataspace("mondialDS1");
			dataspace.addUser(connieUser);
			connieUser.addDataspace(dataspace);
			dataspaceService.addDataspace(dataspace);
			userRepository.update(connieUser);

			logger.debug("mondialIntegrationUrl: " + mondialIntegrationUrl);
			logger.debug("mondialIntegrationDescription: " + mondialIntegrationDescription);
			logger.debug("mondialIntegrationDriverClass: " + mondialIntegrationDriverClass);
			logger.debug("mondialIntegrationUserName: " + mondialIntegrationUserName);
			logger.debug("mondialIntegrationPassword: " + mondialIntegrationPassword);

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialIntegration") == null) {
				logger.debug("adding MondialIntegration");
				logger.debug("mondialIntegrationUrl: " + mondialIntegrationUrl);

				DataSource mondialIntegrationDS = dataSourceService.addDataSource("MondialIntegration", null, mondialIntegrationDescription,
						mondialIntegrationDriverClass, mondialIntegrationUrl, mondialIntegrationUserName, mondialIntegrationPassword, null);

				logger.debug("added MondialIntegration");
				dataspace.addDataSource(mondialIntegrationDS);
				//TODO: this might actually be better off being just a schema, instead of a DS, but may be ok for now
				dataspace.addSchema(mondialIntegrationDS.getSchema());
			}

			//this.loadMondialSources();

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("Mondial") == null) {
				logger.debug("adding Mondial");
				logger.debug("mondialUrl: " + mondialUrl);
				DataSource mondialDS = dataSourceService.addDataSource("Mondial", mondialDescription, driverClass, mondialUrl, userName, password);
				logger.debug("added Mondial");
				dataspace.addDataSource(mondialDS);
				dataspace.addSchema(mondialDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentAfrica") == null) {
				logger.debug("adding ProvinceCountryContinentAfrica");
				logger.debug("ProvinceCountryContinentAfricaUrl: " + provinceCountryContinentAfricaUrl);
				DataSource provinceAfricaDS = dataSourceService.addDataSource("ProvinceCountryContinentAfrica",
						provinceCountryContinentAfricaDescription, driverClass, provinceCountryContinentAfricaUrl, userName, password);
				logger.debug("added ProvinceCountryContinentAfrica");
				dataspace.addDataSource(provinceAfricaDS);
				dataspace.addSchema(provinceAfricaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentAsia") == null) {
				logger.debug("adding ProvinceCountryContinentAsia");
				logger.debug("ProvinceCountryContinentAsiaUrl: " + provinceCountryContinentAsiaUrl);
				DataSource provinceAsiaDS = dataSourceService.addDataSource("ProvinceCountryContinentAsia", provinceCountryContinentAsiaDescription,
						driverClass, provinceCountryContinentAsiaUrl, userName, password);
				logger.debug("added ProvinceCountryContinentAsia");
				dataspace.addDataSource(provinceAsiaDS);
				dataspace.addSchema(provinceAsiaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentAmerica") == null) {
				logger.debug("adding ProvinceCountryContinentAmerica");
				logger.debug("ProvinceCountryContinentAmericaUrl: " + provinceCountryContinentAmericaUrl);
				DataSource provinceAmericaDS = dataSourceService.addDataSource("ProvinceCountryContinentAmerica",
						provinceCountryContinentAmericaDescription, driverClass, provinceCountryContinentAmericaUrl, userName, password);
				logger.debug("added ProvinceCountryContinentAmerica");
				dataspace.addDataSource(provinceAmericaDS);
				dataspace.addSchema(provinceAmericaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentAustralia") == null) {
				logger.debug("adding ProvinceCountryContinentAustralia");
				logger.debug("ProvinceCountryContinentAustraliaUrl: " + provinceCountryContinentAustraliaUrl);
				DataSource provinceAustraliaDS = dataSourceService.addDataSource("ProvinceCountryContinentAustralia",
						provinceCountryContinentAustraliaDescription, driverClass, provinceCountryContinentAustraliaUrl, userName, password);
				logger.debug("added ProvinceCountryContinentAustralia");
				dataspace.addDataSource(provinceAustraliaDS);
				dataspace.addSchema(provinceAustraliaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentEurope") == null) {
				logger.debug("adding ProvinceCountryContinentEurope");
				logger.debug("ProvinceCountryContinentEuropeUrl: " + provinceCountryContinentEuropeUrl);
				DataSource provinceEuropeDS = dataSourceService.addDataSource("ProvinceCountryContinentEurope",
						provinceCountryContinentEuropeDescription, driverClass, provinceCountryContinentEuropeUrl, userName, password);
				logger.debug("added ProvinceCountryContinentEurope");
				dataspace.addDataSource(provinceEuropeDS);
				dataspace.addSchema(provinceEuropeDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ProvinceCountryContinentMix") == null) {
				logger.debug("adding ProvinceCountryContinentMix");
				logger.debug("ProvinceCountryContinentMixUrl: " + provinceCountryContinentMixUrl);
				DataSource provinceMixDS = dataSourceService.addDataSource("ProvinceCountryContinentMix", provinceCountryContinentMixDescription,
						driverClass, provinceCountryContinentMixUrl, userName, password);
				logger.debug("added ProvinceCountryContinentMix");
				dataspace.addDataSource(provinceMixDS);
				dataspace.addSchema(provinceMixDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("CountryProvinceNullContinentAfrica") == null) {
				logger.debug("adding CountryProvinceNullContinentAfrica");
				logger.debug("CountryProvinceNullContinentAfricaUrl: " + countryProvinceNullContinentAfricaUrl);
				DataSource countryProvinceNullAfricaDS = dataSourceService.addDataSource("CountryProvinceNullContinentAfrica",
						countryProvinceNullContinentAfricaDescription, driverClass, countryProvinceNullContinentAfricaUrl, userName, password);
				logger.debug("addedCountry ProvinceNullContinentAfrica");
				dataspace.addDataSource(countryProvinceNullAfricaDS);
				dataspace.addSchema(countryProvinceNullAfricaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("CountryProvinceNullContinentAsia") == null) {
				logger.debug("adding CountryProvinceNullContinentAsia");
				logger.debug("CountryProvinceNullContinentAsiaUrl: " + countryProvinceNullContinentAsiaUrl);
				DataSource countryProvinceNullAsiaDS = dataSourceService.addDataSource("CountryProvinceNullContinentAsia",
						countryProvinceNullContinentAsiaDescription, driverClass, countryProvinceNullContinentAsiaUrl, userName, password);
				logger.debug("added CountryProvinceNullContinentAsia");
				dataspace.addDataSource(countryProvinceNullAsiaDS);
				dataspace.addSchema(countryProvinceNullAsiaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("CountryProvinceNullContinentAmerica") == null) {
				logger.debug("adding CountryProvinceNullContinentAmerica");
				logger.debug("CountryProvinceNullContinentAmericaUrl: " + countryProvinceNullContinentAmericaUrl);
				DataSource countryProvinceNullAmericaDS = dataSourceService.addDataSource("CountryProvinceNullContinentAmerica",
						countryProvinceNullContinentAmericaDescription, driverClass, countryProvinceNullContinentAmericaUrl, userName, password);
				logger.debug("added CityProvinceNullCountryContinentAmerica");
				dataspace.addDataSource(countryProvinceNullAmericaDS);
				dataspace.addSchema(countryProvinceNullAmericaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("CountryProvinceNullContinentAustralia") == null) {
				logger.debug("adding CountryProvinceNullContinentAustralia");
				logger.debug("CountryProvinceNullContinentAustraliaUrl: " + countryProvinceNullContinentAustraliaUrl);
				DataSource countryProvinceNullAustraliaDS = dataSourceService.addDataSource("CountryProvinceNullContinentAustralia",
						countryProvinceNullContinentAustraliaDescription, driverClass, countryProvinceNullContinentAustraliaUrl, userName, password);
				logger.debug("added CountryProvinceNullContinentAustralia");
				dataspace.addDataSource(countryProvinceNullAustraliaDS);
				dataspace.addSchema(countryProvinceNullAustraliaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("CountryProvinceNullContinentEurope") == null) {
				logger.debug("adding CountryProvinceNullContinentEurope");
				logger.debug("CountryProvinceNullContinentEuropeUrl: " + countryProvinceNullContinentEuropeUrl);
				DataSource countryProvinceNullEuropeDS = dataSourceService.addDataSource("CountryProvinceNullContinentEurope",
						countryProvinceNullContinentEuropeDescription, driverClass, countryProvinceNullContinentEuropeUrl, userName, password);
				logger.debug("added CountryProvinceNullContinentEurope");
				dataspace.addDataSource(countryProvinceNullEuropeDS);
				dataspace.addSchema(countryProvinceNullEuropeDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentEurope") == null) {
				logger.debug("adding MondialCityProvinceCountryContinentEurope");
				logger.debug("MondialCityProvinceCountryContinentEuropeUrl: " + mondialCityProvinceCountryContinentEuropeUrl);
				DataSource mondialCityEuropeDS = dataSourceService.addDataSource("MondialCityProvinceCountryContinentEurope",
						mondialCityProvinceCountryContinentEuropeDescription, driverClass, mondialCityProvinceCountryContinentEuropeUrl, userName,
						password);
				logger.debug("added MondialCityProvinceCountryContinentEurope");
				dataspace.addDataSource(mondialCityEuropeDS);
				dataspace.addSchema(mondialCityEuropeDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceNACountryContinentEurope") == null) {
				logger.debug("adding MondialCityProvinceNACountryContinentEurope");
				logger.debug("MondialCityProvinceNACountryContinentEuropeUrl: " + mondialCityProvinceNACountryContinentEuropeUrl);
				DataSource mondialCityProvinceNAEuropeDS = dataSourceService.addDataSource("MondialCityProvinceNACountryContinentEurope",
						mondialCityProvinceNACountryContinentEuropeDescription, driverClass, mondialCityProvinceNACountryContinentEuropeUrl,
						userName, password);
				logger.debug("added MondialCityProvinceNACountryContinentEurope");
				dataspace.addDataSource(mondialCityProvinceNAEuropeDS);
				dataspace.addSchema(mondialCityProvinceNAEuropeDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentEuropeWR") == null) {
				logger.debug("adding MondialCityProvinceCountryContinentEuropeWR");
				logger.debug("MondialCityProvinceCountryContinentEuropeWRUrl: " + mondialCityProvinceCountryContinentEuropeWRUrl);
				DataSource mondialCityEuropeWRDS = dataSourceService.addDataSource("MondialCityProvinceCountryContinentEuropeWR",
						mondialCityProvinceCountryContinentEuropeWRDescription, driverClass, mondialCityProvinceCountryContinentEuropeWRUrl,
						userName, password);
				logger.debug("added MondialCityProvinceCountryContinentEuropeWR");
				dataspace.addDataSource(mondialCityEuropeWRDS);
				dataspace.addSchema(mondialCityEuropeWRDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentAfrica") == null) {
				logger.debug("adding MondialCityProvinceCountryContinentAfrica");
				logger.debug("MondialCityProvinceCountryContinentAfricaUrl: " + mondialCityProvinceCountryContinentAfricaUrl);
				DataSource mondialCityAfricaDS = dataSourceService.addDataSource("MondialCityProvinceCountryContinentAfrica",
						mondialCityProvinceCountryContinentAfricaDescription, driverClass, mondialCityProvinceCountryContinentAfricaUrl, userName,
						password);
				logger.debug("added MondialCityProvinceCountryContinentAfrica");
				dataspace.addDataSource(mondialCityAfricaDS);
				dataspace.addSchema(mondialCityAfricaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceNACountryContinentAfrica") == null) {
				logger.debug("adding MondialCityProvinceNACountryContinentAfrica");
				logger.debug("MondialCityProvinceNACountryContinentAfricaUrl: " + mondialCityProvinceNACountryContinentAfricaUrl);
				DataSource mondialCityProvinceNAAfricaDS = dataSourceService.addDataSource("MondialCityProvinceNACountryContinentAfrica",
						mondialCityProvinceNACountryContinentAfricaDescription, driverClass, mondialCityProvinceNACountryContinentAfricaUrl,
						userName, password);
				logger.debug("added MondialCityProvinceNACountryContinentAfrica");
				dataspace.addDataSource(mondialCityProvinceNAAfricaDS);
				dataspace.addSchema(mondialCityProvinceNAAfricaDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialCityProvinceCountryContinentAfricaWR") == null) {
				logger.debug("adding MondialCityProvinceCountryContinentAfricaWR");
				logger.debug("MondialCityProvinceCountryContinentAfricaWRUrl: " + mondialCityProvinceCountryContinentAfricaWRUrl);
				DataSource mondialCityAfricaWRDS = dataSourceService.addDataSource("MondialCityProvinceCountryContinentAfricaWR",
						mondialCityProvinceCountryContinentAfricaWRDescription, driverClass, mondialCityProvinceCountryContinentAfricaWRUrl,
						userName, password);
				logger.debug("added MondialCityProvinceCountryContinentAfricaWR");
				dataspace.addDataSource(mondialCityAfricaWRDS);
				dataspace.addSchema(mondialCityAfricaWRDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesAfrica") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesAfrica");
				logger.debug("languageEconomyReligionOfCountriesAfricaUrl: " + languageEconomyReligionOfCountriesAfricaUrl);
				DataSource languageAfricaDS = dataSourceService.addDataSource("LanguageEconomyReligionOfCountriesAfrica",
						languageEconomyReligionOfCountriesAfricaDescription, driverClass, languageEconomyReligionOfCountriesAfricaUrl, userName,
						password);
				logger.debug("added LanguageEconomyReligionOfCountriesAfrica");
				dataspace.addDataSource(languageAfricaDS);
				dataspace.addSchema(languageAfricaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesAmerica") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesAmerica");
				logger.debug("languageEconomyReligionOfCountriesAmericaUrl: " + languageEconomyReligionOfCountriesAmericaUrl);
				DataSource languageAmericaDS = dataSourceService.addDataSource("LanguageEconomyReligionOfCountriesAmerica",
						languageEconomyReligionOfCountriesAmericaDescription, driverClass, languageEconomyReligionOfCountriesAmericaUrl, userName,
						password);
				logger.debug("added LanguageEconomyReligionOfCountriesAmerica");
				dataspace.addDataSource(languageAmericaDS);
				dataspace.addSchema(languageAmericaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesAsia") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesAsia");
				logger.debug("languageEconomyReligionOfCountriesAsiaUrl: " + languageEconomyReligionOfCountriesAsiaUrl);
				DataSource languageAsiaDS = dataSourceService
						.addDataSource("LanguageEconomyReligionOfCountriesAsia", languageEconomyReligionOfCountriesAsiaDescription, driverClass,
								languageEconomyReligionOfCountriesAsiaUrl, userName, password);
				logger.debug("added LanguageEconomyReligionOfCountriesAsia");
				dataspace.addDataSource(languageAsiaDS);
				dataspace.addSchema(languageAsiaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesAustralia") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesAustralia");
				logger.debug("languageEconomyReligionOfCountriesAustraliaUrl: " + languageEconomyReligionOfCountriesAustraliaUrl);
				DataSource languageAustraliaDS = dataSourceService.addDataSource("LanguageEconomyReligionOfCountriesAustralia",
						languageEconomyReligionOfCountriesAustraliaDescription, driverClass, languageEconomyReligionOfCountriesAustraliaUrl,
						userName, password);
				logger.debug("added LanguageEconomyReligionOfCountriesAustralia");
				dataspace.addDataSource(languageAustraliaDS);
				dataspace.addSchema(languageAustraliaDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesEurope") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesEurope");
				logger.debug("languageEconomyReligionOfCountriesEuropeUrl: " + languageEconomyReligionOfCountriesEuropeUrl);
				DataSource languageEuropeDS = dataSourceService.addDataSource("LanguageEconomyReligionOfCountriesEurope",
						languageEconomyReligionOfCountriesEuropeDescription, driverClass, languageEconomyReligionOfCountriesEuropeUrl, userName,
						password);
				logger.debug("added LanguageEconomyReligionOfCountriesEurope");
				dataspace.addDataSource(languageEuropeDS);
				dataspace.addSchema(languageEuropeDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("LanguageEconomyReligionOfCountriesMix") == null) {
				logger.debug("adding LanguageEconomyReligionOfCountriesMix");
				logger.debug("languageEconomyReligionOfCountriesMixUrl: " + languageEconomyReligionOfCountriesMixUrl);
				DataSource languageMixDS = dataSourceService.addDataSource("LanguageEconomyReligionOfCountriesMix",
						languageEconomyReligionOfCountriesMixDescription, driverClass, languageEconomyReligionOfCountriesMixUrl, userName, password);
				logger.debug("added LanguageEconomyReligionOfCountriesMix");
				dataspace.addDataSource(languageMixDS);
				dataspace.addSchema(languageMixDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesEurope") == null) {
				logger.debug("adding MondialLanguageEconomyReligionOfCountriesEurope");
				logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeUrl: " + mondialLanguageEconomyReligionOfCountriesEuropeUrl);
				DataSource mondialLanguageEurope = dataSourceService.addDataSource("MondialLanguageEconomyReligionOfCountriesEurope",
						mondialLanguageEconomyReligionOfCountriesEuropeDescription, driverClass, mondialLanguageEconomyReligionOfCountriesEuropeUrl,
						userName, password);
				logger.debug("added MondialLanguageEconomyReligionOfCountriesEurope");
				dataspace.addDataSource(mondialLanguageEurope);
				dataspace.addSchema(mondialLanguageEurope.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesEuropeWR") == null) {
				logger.debug("adding MondialLanguageEconomyReligionOfCountriesEuropeWR");
				logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWRUrl: " + mondialLanguageEconomyReligionOfCountriesEuropeWRUrl);
				DataSource mondialLanguageEuropeWRDS = dataSourceService.addDataSource("MondialLanguageEconomyReligionOfCountriesEuropeWR",
						mondialLanguageEconomyReligionOfCountriesEuropeWRDescription, driverClass,
						mondialLanguageEconomyReligionOfCountriesEuropeWRUrl, userName, password);
				logger.debug("added MondialLanguageEconomyReligionOfCountriesEuropeWR");
				dataspace.addDataSource(mondialLanguageEuropeWRDS);
				dataspace.addSchema(mondialLanguageEuropeWRDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesAfrica") == null) {
				logger.debug("adding MondialLanguageEconomyReligionOfCountriesAfrica");
				logger.debug("mondialLanguageEconomyReligionOfCountriesAfricaUrl: " + mondialLanguageEconomyReligionOfCountriesAfricaUrl);
				DataSource mondialLanguageAfricaDS = dataSourceService.addDataSource("MondialLanguageEconomyReligionOfCountriesAfrica",
						mondialLanguageEconomyReligionOfCountriesAfricaDescription, driverClass, mondialLanguageEconomyReligionOfCountriesAfricaUrl,
						userName, password);
				logger.debug("added MondialLanguageEconomyReligionOfCountriesAfrica");
				dataspace.addDataSource(mondialLanguageAfricaDS);
				dataspace.addSchema(mondialLanguageAfricaDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialLanguageEconomyReligionOfCountriesAfricaWR") == null) {
				logger.debug("adding MondialLanguageEconomyReligionOfCountriesAfricaWR");
				logger.debug("mondialLanguageEconomyReligionOfCountriesAfricaWRUrl: " + mondialLanguageEconomyReligionOfCountriesAfricaWRUrl);
				DataSource mondialLanguageAfricaWRDS = dataSourceService.addDataSource("MondialLanguageEconomyReligionOfCountriesAfricaWR",
						mondialLanguageEconomyReligionOfCountriesAfricaWRDescription, driverClass,
						mondialLanguageEconomyReligionOfCountriesAfricaWRUrl, userName, password);
				logger.debug("added MondialLanguageEconomyReligionOfCountriesAfricaWR");
				dataspace.addDataSource(mondialLanguageAfricaWRDS);
				dataspace.addSchema(mondialLanguageAfricaWRDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("MondialIslandLakeMountain") == null) {
				logger.debug("adding MondialIslandLakeMountain");
				logger.debug("mondialIslandLakeMountainUrl: " + mondialIslandLakeMountainUrl);
				DataSource mondialIslandDS = dataSourceService.addDataSource("MondialIslandLakeMountain", mondialIslandLakeMountainDescription,
						driverClass, mondialIslandLakeMountainUrl, userName, password);
				logger.debug("added MondialIslandLakeMountain");
				dataspace.addDataSource(mondialIslandDS);
				dataspace.addSchema(mondialIslandDS.getSchema());
			}
			*/

			/*
			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("IslandLakeMountain") == null) {
				logger.debug("adding IslandLakeMountain");
				logger.debug("islandLakeMountainUrl: " + islandLakeMountainUrl);
				DataSource islandDS = dataSourceService.addDataSource("IslandLakeMountain", islandLakeMountainDescription, driverClass,
						islandLakeMountainUrl, userName, password);
				logger.debug("added IslandLakeMountain");
				dataspace.addDataSource(islandDS);
				dataspace.addSchema(islandDS.getSchema());
			}

			if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("IslandLakeMountainMix") == null) {
				logger.debug("adding IslandLakeMountainMix");
				logger.debug("islandLakeMountainMixUrl: " + islandLakeMountainMixUrl);
				DataSource islandMixDS = dataSourceService.addDataSource("IslandLakeMountainMix", islandLakeMountainMixDescription, driverClass,
						islandLakeMountainMixUrl, userName, password);
				logger.debug("added IslandLakeMountainMix");
				dataspace.addDataSource(islandMixDS);
				dataspace.addSchema(islandMixDS.getSchema());
			}
			*/

			if (!loadedMappings) {
				predefinedMappingsLoaderService.loadMappingsForDemo(dataspace);
				loadedMappings = true;
			}

			//add queries
			if (!loadedQueries) {
				//DataSource mondialIntegrDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");
				//DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("Mondial");

				//CityProvinceCountryContinentEurope

				//DataSource cityProvinceCountryContinentEuropeDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("CityProvinceCountryContinentEurope");
				//DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");

				//LanguageEconomyReligionOfCountriesEurope

				//DataSource mondialLanguageEconomyReligionOfCountriesEuropeNoRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEurope");
				//DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

				//IslandLakeMountain

				//DataSource mondialIslandLakeMountainDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIslandLakeMountain");

				//CityProvinceCountryContinentAfrica

				//DataSource cityProvinceCountryContinentAfricaDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("CityProvinceCountryContinentAfrica");
				//DataSource mondialCityProvinceCountryContinentAfricaWithRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfricaWR");

				//LanguageEconomyReligionOfCountriesAfrica

				//DataSource mondialLanguageEconomyReligionOfCountriesAfricaNoRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfrica");
				//DataSource mondialLanguageEconomyReligionOfCountriesAfricaWithRenameDS = dataSourceRepository
				//		.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfricaWR");

				Schema mondialSchema = schemaRepository.getSchemaByName("MondialIntegration");

				/*
				String queryString = "SELECT * FROM country c, city t WHERE c.code = t.country";
				String description = "Get all countries with their cities";

				String queryName = "JoinCountryCity";
				logger.debug("queryString: " + queryString);
				Query query1 = new Query(queryName, queryString);
				query1.setDescription(description);
				query1.addSchema(mondialSchema);
				logger.debug("query1.getSchemas: " + query1.getSchemas());
				//query1.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				//query1.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
				//logger.debug("query1.getDataSource: " + query1.getDataSources());

				dataspace.addQuery(query1);
				queryService.addQuery(query1);
				logger.debug("query1.string: " + query1.getQueryString());
				//logger.debug("query1.dataSources: " + query1.getDataSources());
				*/

				/*
				queryName = "JoinCountryCityEurope";
				Query query2 = new Query(queryName, queryString);
				query2.setDescription(description);
				query2.addSchema(mondialSchema);
				logger.debug("query2.getSchemas: " + query2.getSchemas());
				//query2.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				//logger.debug("query2.getDataSource: " + query2.getDataSources());

				dataspace.addQuery(query2);
				queryService.addQuery(query2);
				logger.debug("query2.string: " + query2.getQueryString());
				logger.debug("query2.dataSources: " + query2.getDataSources());

				queryName = "JoinCountryCityAfrica";
				Query query3 = new Query(queryName, queryString);
				query3.setDescription(description);
				query3.addSchema(mondialSchema);
				logger.debug("query3.getSchemas: " + query3.getSchemas());
				//query3.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
				//logger.debug("query3.getDataSource: " + query3.getDataSources());

				dataspace.addQuery(query3);
				queryService.addQuery(query3);
				logger.debug("query3.string: " + query3.getQueryString());
				logger.debug("query3.dataSources: " + query3.getDataSources());

				queryName = "JoinCountryCityMondialIntegr";
				Query query4 = new Query(queryName, queryString);
				query4.setDescription(description);
				query4.addSchema(mondialSchema);
				logger.debug("query4.getSchemas: " + query4.getSchemas());
				//query4.addDataSource(mondialIntegrDS);
				//logger.debug("query4.getDataSource: " + query4.getDataSources());

				dataspace.addQuery(query4);
				queryService.addQuery(query4);
				logger.debug("query4.string: " + query4.getQueryString());
				logger.debug("query4.dataSources: " + query4.getDataSources());
				*/

				/*
				queryName = "JoinCountryCityMondial";
				Query query5 = new Query(queryName, queryString);
				query5.setDescription(description);
				query5.addSchema(mondialSchema);
				logger.debug("query5.getSchemas: " + query5.getSchemas());
				query5.addDataSource(mondialDS);
				logger.debug("query5.getDataSource: " + query5.getDataSources());

				dataspace.addQuery(query5);
				queryService.addQuery(query5);
				logger.debug("query5.string: " + query5.getQueryString());
				logger.debug("query5.dataSources: " + query5.getDataSources());
				*/

				//TODO: this one doesn't work - fails at making result instances persistent

				/*
				Query query6 = new Query("JoinCountriesEthnicGroups", "SELECT * FROM country c, ethnicgroup e WHERE c.code = e.country;");
				query6.setDescription("Get all countries with their ethnic groups");
				query6.addSchema(mondialSchema);
				logger.debug("query6.getSchemas: " + query6.getSchemas());
				//query6.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				//query6.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeNoRenameDS);
				logger.debug("query6.getDataSource: " + query6.getDataSources());

				dataspace.addQuery(query6);
				queryService.addQuery(query6);
				logger.debug("query6.string: " + query6.getQueryString());
				logger.debug("query6.dataSources: " + query6.getDataSources());
				*/

				Query query8 = new Query("AllCountries", "SELECT * FROM country c");
				query8.setDescription("Get all countries");
				query8.addSchema(mondialSchema);
				dataspace.addQuery(query8);
				queryService.addQuery(query8);

				Query query6 = new Query("AllProvinces", "SELECT * FROM province p");
				query6.setDescription("Get all provinces");
				query6.addSchema(mondialSchema);
				dataspace.addQuery(query6);
				queryService.addQuery(query6);

				/*
				Query query7 = new Query("AllCities", "SELECT * FROM city c");
				query7.setDescription("Get all cities");
				query7.addSchema(mondialSchema);
				logger.debug("query7.getSchemas: " + query7.getSchemas());
				//query7.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				//query7.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
				//logger.debug("query7.getDataSources: " + query7.getDataSources());
				dataspace.addQuery(query7);
				queryService.addQuery(query7);
				logger.debug("query7.string: " + query7.getQueryString());
				logger.debug("query7.dataSources: " + query7.getDataSources());
				*/

				Query query2 = new Query("AllReligionsAndCountries", "SELECT * FROM religionandcountry r");
				query2.setDescription("Get all religions");
				query2.addSchema(mondialSchema);
				dataspace.addQuery(query2);
				queryService.addQuery(query2);

				Query query14 = new Query("AllLanguages", "SELECT * FROM language l");
				query14.setDescription("Get all languages");
				query14.addSchema(mondialSchema);
				dataspace.addQuery(query14);
				queryService.addQuery(query14);

				Query query15 = new Query("AllEthnicGroups", "SELECT * FROM ethnicgroup e");
				query15.setDescription("Get all ethnic groups");
				query15.addSchema(mondialSchema);
				dataspace.addQuery(query15);
				queryService.addQuery(query15);

				Query query16 = new Query("AllIslands", "SELECT * FROM island i");
				query16.setDescription("Get all islands");
				query16.addSchema(mondialSchema);
				dataspace.addQuery(query16);
				queryService.addQuery(query16);

				Query query17 = new Query("AllMountains", "SELECT * FROM mountain m");
				query17.setDescription("Get all mountains");
				query17.addSchema(mondialSchema);
				dataspace.addQuery(query17);
				queryService.addQuery(query17);

				/*
				Query query18 = new Query("AllLakes", "SELECT * FROM lake l");
				query18.setDescription("Get all lakes");
				query18.addSchema(mondialSchema);
				dataspace.addQuery(query18);
				queryService.addQuery(query18);
				*/

				/*
				Query query1 = new Query("AllSeasAndCountry", "SELECT * FROM seaandcountry s");
				query1.setDescription("Get all seas and their country");
				query1.addSchema(mondialSchema);
				dataspace.addQuery(query1);
				queryService.addQuery(query1);
				*/

				Query query4 = new Query("AllMountainsAndCountry", "SELECT * FROM mountainandcountry m");
				query4.setDescription("Get all mountains and their country");
				query4.addSchema(mondialSchema);
				dataspace.addQuery(query4);
				queryService.addQuery(query4);

				Query query5 = new Query("AllLakesAndCountry", "SELECT * FROM lakeandcountry l");
				query5.setDescription("Get all lakes and their country");
				query5.addSchema(mondialSchema);
				dataspace.addQuery(query5);
				queryService.addQuery(query5);

				Query query11 = new Query("AllIslandsAndCountry", "SELECT * FROM islandandcountry i");
				query11.setDescription("Get all islands and their country");
				query11.addSchema(mondialSchema);
				dataspace.addQuery(query11);
				queryService.addQuery(query11);

				/*
				Query query12 = new Query("AllRiversAndCountry", "SELECT * FROM riverandcountry r");
				query12.setDescription("Get all rivers and their country");
				query12.addSchema(mondialSchema);
				dataspace.addQuery(query12);
				queryService.addQuery(query12);
				*/

				Query query13 = new Query("AllDesertsAndCountry", "SELECT * FROM desertandcountry d");
				query13.setDescription("Get all deserts and their country");
				query13.addSchema(mondialSchema);
				dataspace.addQuery(query13);
				queryService.addQuery(query13);

				/*
				Query query3 = new Query("AllMembersOfOrganizations", "SELECT * FROM membersoforganizations m");
				query3.setDescription("Get all organisations with their members");
				query3.addSchema(mondialSchema);
				dataspace.addQuery(query3);
				queryService.addQuery(query3);
				*/

				/*
				Query query9 = new Query("AllCountriesWithReligionAndPolitics", "SELECT * FROM politicsreligioncountry p");
				query9.setDescription("Get all countries with their religion and politics");
				query9.addSchema(mondialSchema);
				dataspace.addQuery(query9);
				queryService.addQuery(query9);

				Query query10 = new Query("AllCountriesWithEconomyAndPopulationStats", "SELECT * FROM economypopulationcountry e");
				query10.setDescription("Get all countries with their economy and population stats");
				query10.addSchema(mondialSchema);
				dataspace.addQuery(query10);
				queryService.addQuery(query10);
				*/

				/*
				Query query8 = new Query("AllCitiesEuropeAfricaLanguageEuropeAfrica", "SELECT * FROM city c");
				query8.setDescription("Get all cities");
				query8.addSchema(mondialSchema);
				logger.debug("query8.getSchemas: " + query8.getSchemas());
				query8.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				query8.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
				query8.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeNoRenameDS);
				query8.addDataSource(mondialLanguageEconomyReligionOfCountriesAfricaNoRenameDS);
				logger.debug("query8.getDataSources: " + query8.getDataSources());

				queryService.addQuery(query8);
				logger.debug("query8.string: " + query8.getQueryString());
				logger.debug("query8.dataSources: " + query8.getDataSources());
				dataspace.addQuery(query8);

				Query query9 = new Query("AllCitiesEuropeAfricaGeo", "SELECT * FROM city c");
				query9.setDescription("Get all cities");
				query9.addSchema(mondialSchema);
				logger.debug("query9.getSchemas: " + query9.getSchemas());
				query9.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
				query9.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
				query9.addDataSource(mondialIslandLakeMountainDS);
				logger.debug("query9.getDataSources: " + query9.getDataSources());

				queryService.addQuery(query9);
				logger.debug("query9.string: " + query9.getQueryString());
				logger.debug("query9.dataSources: " + query9.getDataSources());
				dataspace.addQuery(query9);
				*/

				//dataspaceRepository.update(dataspace);
				loadedQueries = true;

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

				OntologyTerm annotationStatsNumberOfAnnotatedInstances = new OntologyTerm("annotationStatsNumberOfAnnotatedInstances",
						DataType.INTEGER);
				OntologyTerm annotationStatsNumberOfUnannotatedInstances = new OntologyTerm("annotationStatsNumberOfUnannotatedInstances",
						DataType.INTEGER);
				OntologyTerm annotationStatsNumberOfInstances = new OntologyTerm("annotationStatsNumberOfInstances", DataType.INTEGER);
				OntologyTerm annotationStatsNumberOfTruePositives = new OntologyTerm("annotationStatsNumberOfTruePositives", DataType.DOUBLE);
				OntologyTerm annotationStatsNumberOfFalsePositives = new OntologyTerm("annotationStatsNumberOfFalsePositives", DataType.DOUBLE);
				OntologyTerm annotationStatsNumberOfFalseNegatives = new OntologyTerm("annotationStatsNumberOfFalseNegatives", DataType.DOUBLE);

				ontologyTermService.addOntologyTerm(expectancyOT);
				ontologyTermService.addOntologyTerm(precisionOT);
				ontologyTermService.addOntologyTerm(recallOT);
				ontologyTermService.addOntologyTerm(fmeasureOT);
				ontologyTermService.addOntologyTerm(fractionOfAnnotatedResultsOT);
				ontologyTermService.addOntologyTerm(statisticalErrorOT);

				ontologyTermService.addOntologyTerm(annotationStatsNumberOfAnnotatedInstances);
				ontologyTermService.addOntologyTerm(annotationStatsNumberOfUnannotatedInstances);
				ontologyTermService.addOntologyTerm(annotationStatsNumberOfInstances);
				ontologyTermService.addOntologyTerm(annotationStatsNumberOfTruePositives);
				ontologyTermService.addOntologyTerm(annotationStatsNumberOfFalsePositives);
				ontologyTermService.addOntologyTerm(annotationStatsNumberOfFalseNegatives);

			}
			isInitialised = true;
			//QueryResult queryResult = queryService.evaluateQuery(query);
			//logger.debug("queryResult.instances.size: " + queryResult.getResultInstances().size());
		}

	}

	/**
	 * @return the numberOfSources
	 */
	/*
	public int getNumberOfSources() {
		return this.numberOfSources;
	} */

	/**
	 * @param numberOfSources the numberOfSources to set
	 */
	/*
	public void setNumberOfSources(int numberOfSources) {
		this.numberOfSources = numberOfSources;
	}*/

	/**
	 * @return the mondialIntegrationUrl
	 */
	public String getMondialIntegrationUrl() {
		return mondialIntegrationUrl;
	}

	/**
	 * @param mondialIntegrationUrl the mondialIntegrationUrl to set
	 */
	public void setMondialIntegrationUrl(String mondialIntegrationUrl) {
		this.mondialIntegrationUrl = mondialIntegrationUrl;
	}

	/**
	 * @return the mondialIntegrationDescription
	 */
	public String getMondialIntegrationDescription() {
		return this.mondialIntegrationDescription;
	}

	/**
	 * @param mondialIntegrationDescription the mondialIntegrationDescription to set
	 */
	public void setMondialIntegrationDescription(String mondialIntegrationDescription) {
		this.mondialIntegrationDescription = mondialIntegrationDescription;
	}

	/**
	 * @return the mondialIntegrationDriverClass
	 */
	public String getMondialIntegrationDriverClass() {
		return this.mondialIntegrationDriverClass;
	}

	/**
	 * @param mondialIntegrationDriverClass the mondialIntegrationDriverClass to set
	 */
	public void setMondialIntegrationDriverClass(String mondialIntegrationDriverClass) {
		this.mondialIntegrationDriverClass = mondialIntegrationDriverClass;
	}

	/**
	 * @return the mondialIntegrationUserName
	 */
	public String getMondialIntegrationUserName() {
		return this.mondialIntegrationUserName;
	}

	/**
	 * @param mondialIntegrationUserName the mondialIntegrationUserName to set
	 */
	public void setMondialIntegrationUserName(String mondialIntegrationUserName) {
		this.mondialIntegrationUserName = mondialIntegrationUserName;
	}

	/**
	 * @return the mondialIntegrationPassword
	 */
	public String getMondialIntegrationPassword() {
		return this.mondialIntegrationPassword;
	}

	/**
	 * @param mondialIntegrationPassword the mondialIntegrationPassword to set
	 */
	public void setMondialIntegrationPassword(String mondialIntegrationPassword) {
		this.mondialIntegrationPassword = mondialIntegrationPassword;
	}

	/**
	 * @return the macGraphvizLocation
	 *//*
	public String getMacGraphvizLocation() {
		return this.macGraphvizLocation;
	}

	/**
	 * @param macGraphvizLocation the macGraphvizLocation to set
	 *//*
	public void setMacGraphvizLocation(String macGraphvizLocation) {
		//this.macGraphvizLocation = macGraphvizLocation;
	}

	/**
	 * @return the macGraphvizParameters
	 *//*
	public String getMacGraphvizParameters() {
		//return this.macGraphvizParameters;
	}

	/**
	 * @param macGraphvizParameters the macGraphvizParameters to set
	 *//*
	public void setMacGraphvizParameters(String macGraphvizParameters) {
		this.macGraphvizParameters = macGraphvizParameters;
	}

	/**
	 * @return the macQueryDotFile
	 *//*
	public String getMacQueryDotFile() {
		return this.macQueryDotFile;
	}

	/**
	 * @param macQueryDotFile the macQueryDotFile to set
	 *//*
	public void setMacQueryDotFile(String macQueryDotFile) {
		this.macQueryDotFile = macQueryDotFile;
	}

	/**
	 * @return the macMappingDotFile
	 *//*
	public String getMacMappingDotFile() {
		return this.macMappingDotFile;
	}

	/**
	 * @param macMappingDotFile the macMappingDotFile to set
	 *//*
	public void setMacMappingDotFile(String macMappingDotFile) {
		this.macMappingDotFile = macMappingDotFile;
	}

	/**
	 * @return the icebergGraphvizLocation
	 *//*
	public String getIcebergGraphvizLocation() {
		return this.icebergGraphvizLocation;
	}

	/**
	 * @param icebergGraphvizLocation the icebergGraphvizLocation to set
	 *//*
	public void setIcebergGraphvizLocation(String icebergGraphvizLocation) {
		this.icebergGraphvizLocation = icebergGraphvizLocation;
	}

	/**
	 * @return the icebergGraphvizParameters
	 *//*
	public String getIcebergGraphvizParameters() {
		return this.icebergGraphvizParameters;
	}

	/**
	 * @param icebergGraphvizParameters the icebergGraphvizParameters to set
	 *//*
	public void setIcebergGraphvizParameters(String icebergGraphvizParameters) {
		this.icebergGraphvizParameters = icebergGraphvizParameters;
	}

	/**
	 * @return the icebergQueryDotFile
	 *//*
	public String getIcebergQueryDotFile() {
		return this.icebergQueryDotFile;
	}

	/**
	 * @param icebergQueryDotFile the icebergQueryDotFile to set
	 *//*
	public void setIcebergQueryDotFile(String icebergQueryDotFile) {
		this.icebergQueryDotFile = icebergQueryDotFile;
	}

	/**
	 * @return the icebergMappingDotFile
	 *//*
	public String getIcebergMappingDotFile() {
		return this.icebergMappingDotFile;
	}

	/**
	 * @param icebergMappingDotFile the icebergMappingDotFile to set
	 *//*
	public void setIcebergMappingDotFile(String icebergMappingDotFile) {
		this.icebergMappingDotFile = icebergMappingDotFile;
	}*/
}
