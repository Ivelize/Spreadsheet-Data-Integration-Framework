package uk.ac.manchester.dstoolkit.service.impl.util.importexport.mappings;

import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.RenameOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.mappings.PredefinedMappingsLoaderService;

//@Transactional(readOnly = true)
@Service(value = "predefinedMappingsLoaderService")
public class PredefinedMappingsLoaderServiceImpl implements PredefinedMappingsLoaderService {

	//TODO check out loading of mappings ... doesn't seem to create persistent mappings

	static Logger logger = Logger.getLogger(PredefinedMappingsLoaderServiceImpl.class);

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("globalQueryTranslatorService")
	private GlobalQueryTranslatorService globalTranslator;

	@Autowired
	@Qualifier("sqlQueryParserService")
	private SQLQueryParserService parser;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	private Dataspace dataspace;

	public void loadMappingsForDemo(Dataspace dataspace) {
		//loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "Mondial", "Mondial");
		//TODO loadDodgyMappingsBetweenMondialIntegrAndMondialIslandLakeMountain();

		//TODO mapping between organization, ismember and MembersOfOrganizations	

		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentEurope();
		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAfrica();
		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAmerica();
		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAsia();
		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAustralia();
		loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentMix();

		/*
		loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentEurope();
		loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAfrica();
		loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAmerica();
		loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAsia();
		loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAustralia();
		*/

		//loadMappingsBetweenProvinceCountryContinentAfricaAndProvinceCountryContinentEurope();

		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAfrica();
		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAmerica();
		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAsia();
		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAustralia();
		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesEurope();
		loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesMix();

		//loadMappingsBetweenLanguageEconomyReligionOfCountriesAfricaAndLanguageEconomyReligionOfCountriesEurope();

		loadMappingsBetweenMondialIntegrationAndIslandLakeMountain();
		//loadMappingsBetweenMondialIntegrationAndIslandLakeMountainMix();

		//loadDodgyMappingsBetweenLanguageReligionAndEthnicGroup("MondialIntegration", "Mondial");

		//loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "Mondial");
		//loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "Mondial");
		//loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "Mondial");
		//loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "Mondial");
		//loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "Mondial");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "Mondial");

		//loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAfrica", "Mondial");
		//loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesEurope", "Mondial");

		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS",
				"ProvinceCountryContinentEuropeS");

		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS",
				"ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS",
				"ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS",
				"ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS",
				"ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentEuropeS");

		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS",
				"ProvinceCountryContinentMix");

		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix", "ProvinceCountryContinentMix");

		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS",
				"ProvinceCountryContinentMix");

		loadDodgyManyToOneMappingBetweenReligionAndProvince("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentMix");

		/*
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAfrica",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAmerica",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsia",
				"CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAustralia",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenReligionAndCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesEurope",
				"CountryProvinceNullContinentEurope");
		*/

		/*
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfricaS", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmericaS", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustraliaS", "ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEuropeS", "ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfricaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmericaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustraliaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEuropeS", "ProvinceCountryContinentMix");

		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentMix");
		*/

		/*
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfrica", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmerica", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAsia",
				"CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustralia", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEurope", "CountryProvinceNullContinentEurope");
		*/

		//loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesAfrica", "Mondial");
		//loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesEurope", "Mondial");

		/*
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfricaS", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmericaS", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAsiaS", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustraliaS", "ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEuropeS", "ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfricaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmericaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAsiaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustraliaS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEuropeS", "ProvinceCountryContinentMix");

		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration", "LanguageEconomyReligionOfCountriesMix",
				"ProvinceCountryContinentMix");
		*/

		/*
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAfrica", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAmerica", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAsia", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesAustralia", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry("MondialIntegration",
				"LanguageEconomyReligionOfCountriesEurope", "CountryProvinceNullContinentEurope");
		*/
	}

	public void loadMappingsForTests(Dataspace dataspace, boolean loadMappingsForDatasourcesWithRename) {
		this.dataspace = dataspace;
		/*
		Schema mondialIntegrSchema = schemaRepository.getSchemaByName("MondialIntegr");
		Schema mondialSchema = schemaRepository.getSchemaByName("Mondial");
		Schema mondialCityProvinceCountryContinentEuropeSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEurope");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeSchema = schemaRepository.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEurope");
		Schema mondialIslandLakeMountainSchema = schemaRepository.getSchemaByName("MondialIslandLakeMountain");
		Schema mondialCityProvinceCountryContinentAfricaSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentAfrica");
		Schema mondialLanguageEconomyReligionOfCountriesAfricaSchema = schemaRepository.getSchemaByName("MondialLanguageEconomyReligionOfCountriesAfrica");
		
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		Schema mondialCityProvinceCountryContinentAfricaWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentAfricaWR");
		Schema mondialLanguageEconomyReligionOfCountriesAfricaWRSchema = schemaRepository.getSchemaByName("MondialLanguageEconomyReligionOfCountriesAfricaWR");		
		*/

		/*
		DataSource mondialIntegrDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");
		DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("Mondial");
		DataSource mondialCityProvinceCountryContinentEuropeDS = dataSourceRepository.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeDS = dataSourceRepository.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEurope");
		DataSource mondialIslandLakeMountainDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIslandLakeMountain");
		DataSource mondialCityProvinceCountryContinentAfricaDS = dataSourceRepository.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");
		DataSource mondialLanguageEconomyReligionOfCountriesAfricaDS = dataSourceRepository.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfrica");
		
		DataSource mondialCityProvinceCountryContinentEuropeWRDS = dataSourceRepository.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWRDS = dataSourceRepository.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		DataSource mondialCityProvinceCountryContinentAfricaWRDS = dataSourceRepository.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfricaWR");
		DataSource mondialLanguageEconomyReligionOfCountriesAfricaWRDS = dataSourceRepository.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesAfricaWR");
		
		*/

		loadMappingsBetweenMondialIntegrAndMondial();
		loadMappingsBetweenMondialIntegrAndMondialIslandLakeMountain();

		loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentEurope();
		loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentAfrica();

		loadMappingsBetweenMondialCityProvinceCountryContinentAfricaAndMondialCityProvinceCountryContinentEurope();

		loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentEurope();
		loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentAfrica();

		loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesEuropeNoRename();
		loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesAfricaNoRename();

		loadMappingsBetweenMondialLanguageEconomyReligionOfCountriesAfricaNoRenameAndMondialLanguageEconomyReligionOfCountriesEuropeNoRename();

		if (loadMappingsForDatasourcesWithRename) {
			loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentEuropeWR();
			loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentAfricaWR();

			loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentEuropeWR();
			loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentAfricaWR();

			loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesEuropeWR();
			loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesAfricaWR();
		}

	}

	//************************************** Mappings for demo ****************************************

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentEurope() {
		//1-1 mappings between tables in MondialIntegration and ProvinceCountryContinentEurope; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentEuropeS", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentEuropeS", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentEuropeS");
	}

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAfrica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAfrica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAfricaS", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAfricaS", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentAfricaS");
	}

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAsia() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAsia; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAsiaS", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAsiaS", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentAsiaS");
	}

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAmerica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAmerica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAmericaS", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAmericaS", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentAmericaS");
	}

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentAustralia() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAustralia; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAustraliaS", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentAustraliaS", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentAustraliaS");
	}

	private void loadMappingsBetweenMondialIntegrationAndProvinceCountryContinentMix() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAustralia; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentMix", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "ProvinceCountryContinentMix", "Province");

		loadDodgyMappingsBetweenCountryAndProvince("MondialIntegration", "ProvinceCountryContinentMix");
	}

	/*
	private void loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentEurope() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentEurope; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "CountryProvinceNullContinentEurope", "Country");
	}

	private void loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAfrica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAfrica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "CountryProvinceNullContinentAfrica", "Country");
	}

	private void loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAsia() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAsia; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "CountryProvinceNullContinentAsia", "Country");
	}

	private void loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAmerica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAmerica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "CountryProvinceNullContinentAmerica", "Country");
	}

	private void loadMappingsBetweenMondialIntegrationAndCountryProvinceNullContinentAustralia() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAustralia; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "CountryProvinceNullContinentAustralia", "Country");
	}
	*/

	/*
	private void loadMappingsBetweenProvinceCountryContinentAfricaAndProvinceCountryContinentEurope() {
		load1t1SNSCMappingBetweenTwoSchemas("ProvinceCountryContinentAfrica", "ProvinceCountryContinentEurope", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("ProvinceCountryContinentAfrica", "ProvinceCountryContinentEurope", "Province");
	}
	*/

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAfrica() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAfrica; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAfrica", "Religion");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS", "EthnicGroup");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesAfricaS");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAmerica() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAmerica; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAmerica", "Religion");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS", "EthnicGroup");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesAmericaS");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAsia() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAsia; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAsia", "Religion");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS", "EthnicGroup");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesAsiaS");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesAustralia() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAfrica; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAustralia", "Religion");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS", "EthnicGroup");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesAustraliaS");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesEurope() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEurope; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS", "EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesEurope", "Religion");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesEuropeS");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	private void loadMappingsBetweenMondialIntegrationAndLanguageEconomyReligionOfCountriesMix() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEurope; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesMix", "EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesMix", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "LanguageEconomyReligionOfCountriesEurope", "Religion");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("MondialIntegration", "LanguageEconomyReligionOfCountriesMix");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}

	/*
	private void loadMappingsBetweenLanguageEconomyReligionOfCountriesAfricaAndLanguageEconomyReligionOfCountriesEurope() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEurope; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("LanguageEconomyReligionOfCountriesAfrica", "LanguageEconomyReligionOfCountriesEurope", "EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("LanguageEconomyReligionOfCountriesAfrica", "LanguageEconomyReligionOfCountriesEurope", "Language");
		//load1t1SNSCMappingBetweenTwoSchemas("LanguageEconomyReligionOfCountriesAfrica", "LanguageEconomyReligionOfCountriesEurope", "Religion");

		loadDodgyMappingsBetweenLanguageAndEthnicGroup("LanguageEconomyReligionOfCountriesAfrica", "LanguageEconomyReligionOfCountriesEurope");

		//TODO mapping between organization, ismember and MembersOfOrganizations		
	}
	*/

	private void loadMappingsBetweenMondialIntegrationAndIslandLakeMountain() {
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainS", "Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainS", "Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainS", "Island");

		//TODO add dodgy manyToOneMappings with Province instead of country
		//TODO add dodgy manyToOneMappings with Island instead of mountain and vice versa

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentAfrica");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAfricaS");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentAmerica");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAmericaS");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentAsia");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAsiaS");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAustraliaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentAustralia");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentAustraliaS");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentAustraliaS");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentEurope");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentEuropeS");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentEuropeS");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentEuropeS");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentEuropeS");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "ProvinceCountryContinentMix");

		loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince("MondialIntegration", "IslandLakeMountainS",
				"ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");
		loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince("MondialIntegration", "IslandLakeMountainS", "ProvinceCountryContinentMix");

		loadDodgyMappingsBetweenIslandAndMountain("MondialIntegration", "IslandLakeMountainS");

		/*
		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		*/
	}

	private void loadMappingsBetweenMondialIntegrationAndIslandLakeMountainMix() {
		//load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainMix", "Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainMix", "Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegration", "IslandLakeMountainMix", "Island");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAfricaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAfricaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAfrica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAmericaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAmericaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAmerica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAsiaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAsiaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAsia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAustraliaS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAustraliaS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentAustralia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentEuropeS");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentEuropeS");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentEurope");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountainMix",
				"ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentMix");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentMix");
		//loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountainMix", "ProvinceCountryContinentMix");

		loadDodgyMappingsBetweenIslandAndMountain("MondialIntegration", "IslandLakeMountainMix");

		/*
		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAfrica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAmerica");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAsia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentAustralia");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentAustralia");

		loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry("MondialIntegration", "IslandLakeMountain",
				"CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry("MondialIntegration", "IslandLakeMountain", "CountryProvinceNullContinentEurope");
		*/
	}

	private void loadManyToOneMappingBetweenEconomyPopulationCountryAndEconomyPopulationCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM EconomyPopulationCountry";

		String targetQueryString = "SELECT c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population, "
				+ "e.GDP, e.Agriculture, e.Service, e.Industry, e.Inflation, " + "p.Population_Growth, p.Infant_Mortality "
				+ "FROM Country c, Economy e, Population p " + "WHERE p.CountryCode = e.Country " + "AND c.CountryCode = p.CountryCode";

		//String targetQueryString = "SELECT * FROM Country c, Economy e, Population p " + "WHERE c.Code = e.Country " + "AND c.Code = p.Country";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenPoliticsReligionCountryAndPoliticsReligionCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM PoliticsReligionCountry";

		String targetQueryString = "SELECT c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population,"
				+ " p.Independence, p.Dependent, p.Government, r.ReligionName, r.Percentage " + "FROM Country c, Politics p, Religion r "
				+ "WHERE r.Country = p.CountryCode " + "AND  c.CountryCode = r.Country";

		//String targetQueryString = "SELECT * FROM Country c, Politics p, Religion r " + "WHERE c.Code = p.Country " + "AND c.Code = r.Country";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenReligionAndCountry(String sourceSchemaName, String targetSchemaName1, String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM ReligionAndCountry";
		String targetQueryString = "SELECT c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population, "
				+ "r.ReligionName, r.Percentage " + "FROM Country c, Religion r " + "WHERE c.CountryCode = r.Country";
		//String targetQueryString = "SELECT * FROM Mountain m, Geo_Mountain g WHERE m.MountainName = g.Mountain";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenReligionAndProvince(String sourceSchemaName, String targetSchemaName1, String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM ReligionAndCountry";
		String targetQueryString = "SELECT p.Name, p.CountryCode, p.Capital, p.CapitalProvince, p.Area, p.Population, "
				+ "r.ReligionName, r.Percentage " + "FROM Province p, Religion r " + "WHERE p.CountryCode = r.Country";
		//String targetQueryString = "SELECT * FROM Mountain m, Geo_Mountain g WHERE m.MountainName = g.Mountain";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenMountainGeoMountainAndMountainAndCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM MountainAndCountry";
		String targetQueryString = "SELECT m.gName, m.Height, m.Type, m.Longitude, m.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Mountain m, geo_Mountain g, Country c "
				+ "WHERE m.gName = g.Mountain AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Mountain m, Geo_Mountain g WHERE m.MountainName = g.Mountain";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenMountainGeoMountainAndMountainAndProvince(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM MountainAndCountry";
		String targetQueryString = "SELECT m.gName, m.Height, m.Type, m.Longitude, m.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Mountain m, geo_Mountain g, Province c "
				+ "WHERE m.gName = g.Mountain AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Mountain m, Geo_Mountain g WHERE m.MountainName = g.Mountain";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenDesertGeoDesertAndDesertAndCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM DesertAndCountry";
		String targetQueryString = "SELECT d.DesertName, d.DesertArea, d.DesertLongitude, d.DesertLatitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Desert d, geo_Desert g, Country c "
				+ "WHERE d.DesertName = g.Desert AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Desert d, Geo_Desert g WHERE d.DesertName = g.Desert";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenDesertGeoDesertAndDesertAndProvince(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM DesertAndCountry";
		String targetQueryString = "SELECT d.DesertName, d.DesertArea, d.DesertLongitude, d.DesertLatitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Desert d, geo_Desert g, Province c "
				+ "WHERE d.DesertName = g.Desert AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Desert d, Geo_Desert g WHERE d.DesertName = g.Desert";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenIslandGeoIslandAndIslandAndCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM IslandAndCountry";
		String targetQueryString = "SELECT i.gName, i.Height, i.Type, i.Longitude, i.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Island i, geo_Island g, Country c "
				+ "WHERE i.gName = g.Island AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Island i, Geo_Island g WHERE i.IslandName = g.Island";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenIslandGeoIslandAndIslandAndProvince(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM IslandAndCountry";
		String targetQueryString = "SELECT i.gName, i.Height, i.Type, i.Longitude, i.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Island i, geo_Island g, Province c "
				+ "WHERE i.gName = g.Island AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Island i, Geo_Island g WHERE i.IslandName = g.Island";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenLakeGeoLakeAndLakeAndCountry(String sourceSchemaName, String targetSchemaName1, String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM LakeAndCountry";
		String targetQueryString = "SELECT l.gName, l.Depth, l.Type, l.Longitude, l.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Lake l, geo_Lake g, Country c "
				+ "WHERE l.gName = g.Lake AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Lake l, Geo_Lake g WHERE l.LakeName = g.Lake";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenLakeGeoLakeAndLakeAndProvince(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM LakeAndCountry";
		String targetQueryString = "SELECT l.gName, l.Depth, l.Type, l.Longitude, l.Latitude, "
				+ "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population " + "FROM Lake l, geo_Lake g, Province c "
				+ "WHERE l.gName = g.Lake AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Lake l, Geo_Lake g WHERE l.LakeName = g.Lake";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadManyToOneMappingBetweenSeaGeoSeaAndSeaAndCountry(String sourceSchemaName, String targetSchemaName1, String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM SeaAndCountry";
		String targetQueryString = "SELECT s.SeaName, s.SeaDepth, " + "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population "
				+ "FROM Sea s, geo_Sea g, Country c " + "WHERE s.SeaName = g.Sea AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Sea s, Geo_Sea g WHERE s.SeaName = g.Sea";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void loadDodgyManyToOneMappingBetweenSeaGeoSeaAndSeaAndProvince(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM SeaAndCountry";
		String targetQueryString = "SELECT s.SeaName, s.SeaDepth, " + "c.Name, c.CountryCode, c.Capital, c.CapitalProvince, c.Area, c.Population "
				+ "FROM Sea s, geo_Sea g, Province c " + "WHERE s.SeaName = g.Sea AND g.Country = c.CountryCode";
		//String targetQueryString = "SELECT * FROM Sea s, Geo_Sea g WHERE s.SeaName = g.Sea";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	/*
	private void loadManyToOneMappingBetweenRiverGeoRiverAndRiverAndCountry(String sourceSchemaName, String targetSchemaName1,
			String targetSchemaName2) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema1 = schemaRepository.getSchemaByName(targetSchemaName1);
		Schema targetSchema2 = schemaRepository.getSchemaByName(targetSchemaName2);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS1 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName1);
		DataSource targetDS2 = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName2);

		String sourceQueryString = "SELECT * FROM RiverAndCountry";
		String targetQueryString = "SELECT r.RiverName, r.River, r.Lake, r.Sea, r.RiverLength, r.SourceLongitude, r.SourceLatitude, "
				+ "r.Mountains, r.SourceAltitude, r.EstuaryLongitude, r.EstuaryLatitude, "
				+ "c.CountryName, c.Code, c.Capital, c.Province, c.CountryArea, c.CountryPopulation " + "FROM River r, Geo_River g, Country c "
				+ "WHERE r.RiverName = g.River AND g.Country = c.Code";
		//String targetQueryString = "SELECT * FROM River r, Geo_River g WHERE r.RiverName = g.River";

		Set<Schema> schemas = new HashSet<Schema>();
		schemas.add(targetSchema1);
		schemas.add(targetSchema2);

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, schemas, targetDS1, targetDS2);

		Mapping mapping = new Mapping(sourceQueryString, targetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}
	*/

	private void loadDodgyMappingsBetweenIslandAndMountain(String sourceSchemaName, String targetSchemaName) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema = schemaRepository.getSchemaByName(targetSchemaName);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName);

		/*
		String sourceQueryString = "SELECT * FROM Island";
		String targetQueryString = "SELECT * FROM Lake";

		Query sourceQuery1 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery1 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping1 = new Mapping(sourceQueryString, targetQueryString);
		mapping1.setQuery1(sourceQuery1);
		mapping1.setQuery2(targetQuery1);
		makeMappingPersistent(mapping1);
		*/

		String sourceQueryString = "SELECT * FROM Island";
		String targetQueryString = "SELECT * FROM Mountain";

		Query sourceQuery2 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery2 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping2 = new Mapping(sourceQueryString, targetQueryString);
		mapping2.setQuery1(sourceQuery2);
		mapping2.setQuery2(targetQuery2);
		makeMappingPersistent(mapping2);

		/*
		sourceQueryString = "SELECT * FROM Lake";
		targetQueryString = "SELECT * FROM Island";

		Query sourceQuery3 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery3 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping3 = new Mapping(sourceQueryString, targetQueryString);
		mapping3.setQuery1(sourceQuery3);
		mapping3.setQuery2(targetQuery3);
		makeMappingPersistent(mapping3);

		sourceQueryString = "SELECT * FROM Lake";
		targetQueryString = "SELECT * FROM Mountain";

		Query sourceQuery4 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery4 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping4 = new Mapping(sourceQueryString, targetQueryString);
		mapping4.setQuery1(sourceQuery4);
		mapping4.setQuery2(targetQuery4);
		makeMappingPersistent(mapping4);
		*/

		sourceQueryString = "SELECT * FROM Mountain";
		targetQueryString = "SELECT * FROM Island";

		Query sourceQuery5 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery5 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping5 = new Mapping(sourceQueryString, targetQueryString);
		mapping5.setQuery1(sourceQuery5);
		mapping5.setQuery2(targetQuery5);
		makeMappingPersistent(mapping5);

		/*
		sourceQueryString = "SELECT * FROM Mountain";
		targetQueryString = "SELECT * FROM Lake";

		Query sourceQuery6 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery6 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping6 = new Mapping(sourceQueryString, targetQueryString);
		mapping6.setQuery1(sourceQuery6);
		mapping6.setQuery2(targetQuery6);
		makeMappingPersistent(mapping6);
		*/
	}

	private void loadDodgyMappingsBetweenLanguageAndEthnicGroup(String sourceSchemaName, String targetSchemaName) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema = schemaRepository.getSchemaByName(targetSchemaName);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName);

		//String sourceQueryString = "SELECT * FROM Language";
		//String targetQueryString = "SELECT * FROM Religion";

		//Query sourceQuery1 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery1 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping1 = new Mapping(sourceQueryString, targetQueryString);
		//mapping1.setQuery1(sourceQuery1);
		//mapping1.setQuery2(targetQuery1);
		//makeMappingPersistent(mapping1);

		String sourceQueryString = "SELECT * FROM Language";
		String targetQueryString = "SELECT * FROM EthnicGroup";

		Query sourceQuery2 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery2 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping2 = new Mapping(sourceQueryString, targetQueryString);
		mapping2.setQuery1(sourceQuery2);
		mapping2.setQuery2(targetQuery2);
		makeMappingPersistent(mapping2);

		//sourceQueryString = "SELECT * FROM Religion";
		//targetQueryString = "SELECT * FROM EthnicGroup";

		//Query sourceQuery3 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery3 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping3 = new Mapping(sourceQueryString, targetQueryString);
		//mapping3.setQuery1(sourceQuery3);
		//mapping3.setQuery2(targetQuery3);
		//makeMappingPersistent(mapping3);

		//sourceQueryString = "SELECT * FROM Religion";
		//targetQueryString = "SELECT * FROM Language";

		//Query sourceQuery4 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery4 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping4 = new Mapping(sourceQueryString, targetQueryString);
		//mapping4.setQuery1(sourceQuery4);
		//mapping4.setQuery2(targetQuery4);
		//makeMappingPersistent(mapping4);

		sourceQueryString = "SELECT * FROM EthnicGroup";
		targetQueryString = "SELECT * FROM Language";

		Query sourceQuery5 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery5 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping5 = new Mapping(sourceQueryString, targetQueryString);
		mapping5.setQuery1(sourceQuery5);
		mapping5.setQuery2(targetQuery5);
		makeMappingPersistent(mapping5);

		//sourceQueryString = "SELECT * FROM EthnicGroup";
		//targetQueryString = "SELECT * FROM Religion";

		//Query sourceQuery6 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery6 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping6 = new Mapping(sourceQueryString, targetQueryString);
		//mapping6.setQuery1(sourceQuery6);
		//mapping6.setQuery2(targetQuery6);
		//makeMappingPersistent(mapping6);
	}

	private void loadDodgyMappingsBetweenCountryAndProvince(String sourceSchemaName, String targetSchemaName) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema = schemaRepository.getSchemaByName(targetSchemaName);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName);

		//String sourceQueryString = "SELECT * FROM Language";
		//String targetQueryString = "SELECT * FROM Religion";

		//Query sourceQuery1 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery1 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping1 = new Mapping(sourceQueryString, targetQueryString);
		//mapping1.setQuery1(sourceQuery1);
		//mapping1.setQuery2(targetQuery1);
		//makeMappingPersistent(mapping1);

		String sourceQueryString = "SELECT * FROM Country";
		String targetQueryString = "SELECT * FROM Province";

		Query sourceQuery2 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery2 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping2 = new Mapping(sourceQueryString, targetQueryString);
		mapping2.setQuery1(sourceQuery2);
		mapping2.setQuery2(targetQuery2);
		makeMappingPersistent(mapping2);

		//sourceQueryString = "SELECT * FROM Religion";
		//targetQueryString = "SELECT * FROM EthnicGroup";

		//Query sourceQuery3 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery3 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping3 = new Mapping(sourceQueryString, targetQueryString);
		//mapping3.setQuery1(sourceQuery3);
		//mapping3.setQuery2(targetQuery3);
		//makeMappingPersistent(mapping3);

		//sourceQueryString = "SELECT * FROM Religion";
		//targetQueryString = "SELECT * FROM Language";

		//Query sourceQuery4 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery4 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping4 = new Mapping(sourceQueryString, targetQueryString);
		//mapping4.setQuery1(sourceQuery4);
		//mapping4.setQuery2(targetQuery4);
		//makeMappingPersistent(mapping4);

		sourceQueryString = "SELECT * FROM Province";
		targetQueryString = "SELECT * FROM Country";

		Query sourceQuery5 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery5 = generateQuery(targetQueryString, targetSchema, targetDS);

		Mapping mapping5 = new Mapping(sourceQueryString, targetQueryString);
		mapping5.setQuery1(sourceQuery5);
		mapping5.setQuery2(targetQuery5);
		makeMappingPersistent(mapping5);

		//sourceQueryString = "SELECT * FROM EthnicGroup";
		//targetQueryString = "SELECT * FROM Religion";

		//Query sourceQuery6 = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		//Query targetQuery6 = generateQuery(targetQueryString, targetSchema, targetDS);

		//Mapping mapping6 = new Mapping(sourceQueryString, targetQueryString);
		//mapping6.setQuery1(sourceQuery6);
		//mapping6.setQuery2(targetQuery6);
		//makeMappingPersistent(mapping6);
	}

	//************************************** Mappings for tests ****************************************

	private void loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesEuropeWR() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEuropeWR; are DNSC

		String economyFinalTargetQueryString = "SELECT CountryE as Country, GDPE as GDP, AgricultureE as Agriculture, ServiceE as Service, IndustryE as Industry, InflationE as Inflation FROM Economye Economy";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Economy", "EconomyE",
				economyFinalTargetQueryString, 1);

		String ethnicGroupFinalTargetQueryString = "SELECT CountryE as Country, NameE as Name, PercentageE as Percentage FROM EthnicGroupE EthnicGroup";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "EthnicGroup", "EthnicGroupE",
				ethnicGroupFinalTargetQueryString, 1);

		String isMemberFinalTargetQueryString = "SELECT CountryE as Country, OrganizationE as Organization, TypeE as Type FROM isMemberE isMember";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "IsMember", "IsMemberE",
				isMemberFinalTargetQueryString, 1);

		String languageFinalTargetQueryString = "SELECT CountryE as Country, NameE as Name, PercentageE as Percentage FROM LanguageE Language";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Language", "LanguageE",
				languageFinalTargetQueryString, 1);

		String organizationFinalTargetQueryString = "SELECT AbbreviationE as Abbreviation, NameE as Name, CityE as City, CountryE as Country, ProvinceE as Province, EstablishedE as Established FROM OrganizationE Organization";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Organization", "OrganizationE",
				organizationFinalTargetQueryString, 1);

		String politicsFinalTargetQueryString = "SELECT CountryE as Country, IndependenceE as Independence, DependentE as Dependent, GovernmentE as Government FROM PoliticsE Politics";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Politics", "PoliticsE",
				politicsFinalTargetQueryString, 1);

		String populationFinalTargetQueryString = "SELECT CountryE as Country, Population_GrowthE as Population_Growth, Infant_MortalityE as Infant_Mortality FROM PopulationE Population";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Population", "PopulationE",
				populationFinalTargetQueryString, 1);

		String religionFinalTargetQueryString = "SELECT CountryE as Country, NameE as Name, PercentageE as Percentage FROM ReligionE Religion";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEuropeWR", "Religion", "ReligionE",
				religionFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesAfricaWR() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAfricaWR; are DNSC

		String economyFinalTargetQueryString = "SELECT CountryA as Country, GDPA as GDP, AgricultureA as Agriculture, ServiceA as Service, IndustryA as Industry, InflationA as Inflation FROM EconomyA Economy";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Economy", "EconomyA",
				economyFinalTargetQueryString, 1);

		String ethnicGroupFinalTargetQueryString = "SELECT CountryA as Country, NameA as Name, PercentageA as Percentage FROM EthnicGroupA EthnicGroup";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "EthnicGroup", "EthnicGroupA",
				ethnicGroupFinalTargetQueryString, 1);

		String isMemberFinalTargetQueryString = "SELECT CountryA as Country, OrganizationA as Organization, TypeA as Type FROM isMemberA isMember";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "IsMember", "IsMemberA",
				isMemberFinalTargetQueryString, 1);

		String languageFinalTargetQueryString = "SELECT CountryA as Country, NameA as Name, PercentageA as Percentage FROM LanguageA Language";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Language", "LanguageA",
				languageFinalTargetQueryString, 1);

		String organizationFinalTargetQueryString = "SELECT AbbreviationA as Abbreviation, NameA as Name, CityA as City, CountryA as Country, ProvinceA as Province, EstablishedA as Established FROM OrganizationA Organization";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Organization", "OrganizationA",
				organizationFinalTargetQueryString, 1);

		String politicsFinalTargetQueryString = "SELECT CountryA as Country, IndependenceA as Independence, DependentA as Dependent, GovernmentA as Government FROM PoliticsA Politics";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Politics", "PoliticsA",
				politicsFinalTargetQueryString, 1);

		String populationFinalTargetQueryString = "SELECT CountryA as Country, Population_GrowthA as Population_Growth, Infant_MortalityA as Infant_Mortality FROM PopulationA Population";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Population", "PopulationA",
				populationFinalTargetQueryString, 1);

		String religionFinalTargetQueryString = "SELECT CountryA as Country, NameA as Name, PercentageA as Percentage FROM ReligionA Religion";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfricaWR", "Religion", "ReligionA",
				religionFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentEuropeWR() {
		//1-1 mappings between tables in MondialIntegr and MondialCityProvinceCountryContinentEuropeWR; are DNSC

		String bordersFinalTargetQueryString = "SELECT Country1E as Country1, Country2E as Country2, LengthE as Length FROM BordersE Borders";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "Borders", "BordersE",
				bordersFinalTargetQueryString, 1);

		String cityFinalTargetQueryString = "SELECT NameE as Name, CountryE as Country, ProvinceE as Province, PopulationE as Population, LongitudeE as Longitude, LatitudeE as Latitude FROM CityE City";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "City", "CityE",
				cityFinalTargetQueryString, 1);

		String continentFinalTargetQueryString = "SELECT NameE as Name, AreaE as Area FROM ContinentE Continent";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "Continent", "ContinentE",
				continentFinalTargetQueryString, 1);

		String countryFinalTargetQueryString = "SELECT NameE as Name, CodeE as Code, CapitalE as Capital, ProvinceE as Province, AreaE as Area, PopulationE as Population FROM CountryE Country";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "Country", "CountryE",
				countryFinalTargetQueryString, 1);

		String encompassesFinalTargetQueryString = "SELECT CountryE as Country, ContinentE as Continent, PercentageE as Percentage FROM EncompassesE Encompasses";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "Encompasses", "EncompassesE",
				encompassesFinalTargetQueryString, 1);

		String provinceFinalTargetQueryString = "SELECT NameE as Name, CountryE as Country, PopulationE as Population, AreaE as Area, CapitalE as Capital, CapProvE as CapProv FROM ProvinceE Province";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEuropeWR", "Province", "ProvinceE",
				provinceFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentEuropeWR() {
		//1-1 mappings between tables in MondialIntegr and MondialCityProvinceCountryContinentEuropeWR; are DNSC

		String bordersFinalTargetQueryString = "SELECT Country1E as Country1, Country2E as Country2, LengthE as Length FROM BordersE Borders";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEuropeWR", "Borders", "BordersE",
				bordersFinalTargetQueryString, 1);

		String cityFinalTargetQueryString = "SELECT NameE as Name, CountryE as Country, ProvinceE as Province, PopulationE as Population, LongitudeE as Longitude, LatitudeE as Latitude FROM CityE City";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEuropeWR", "City", "CityE",
				cityFinalTargetQueryString, 1);

		String continentFinalTargetQueryString = "SELECT NameE as Name, AreaE as Area FROM ContinentE Continent";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEuropeWR", "Continent", "ContinentE",
				continentFinalTargetQueryString, 1);

		String countryFinalTargetQueryString = "SELECT NameE as Name, CodeE as Code, CapitalE as Capital, ProvinceE as Province, AreaE as Area, PopulationE as Population FROM CountryE Country";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEuropeWR", "Country", "CountryE",
				countryFinalTargetQueryString, 1);

		String encompassesFinalTargetQueryString = "SELECT CountryE as Country, ContinentE as Continent, PercentageE as Percentage FROM EncompassesE Encompasses";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEuropeWR", "Encompasses", "EncompassesE",
				encompassesFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentAfricaWR() {
		//1-1 mappings between tables in MondialIntegr and MondialCityProvinceCountryContinentAfricaWR; are DNSC

		String bordersFinalTargetQueryString = "SELECT Country1A as Country1, Country2A as Country2, LengthA as Length FROM BordersA Borders";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "Borders", "BordersA",
				bordersFinalTargetQueryString, 1);

		String cityFinalTargetQueryString = "SELECT NameA as Name, CountryA as Country, ProvinceA as Province, PopulationA as Population, LongitudeA as Longitude, LatitudeA as Latitude FROM CityA City";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "City", "CityA",
				cityFinalTargetQueryString, 1);

		String continentFinalTargetQueryString = "SELECT NameA as Name, AreaA as Area FROM ContinentA Continent";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "Continent", "ContinentA",
				continentFinalTargetQueryString, 1);

		String countryFinalTargetQueryString = "SELECT NameA as Name, CodeA as Code, CapitalA as Capital, ProvinceA as Province, AreaA as Area, PopulationA as Population FROM CountryA Country";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "Country", "CountryA",
				countryFinalTargetQueryString, 1);

		String encompassesFinalTargetQueryString = "SELECT CountryA as Country, ContinentA as Continent, PercentageA as Percentage FROM EncompassesA Encompasses";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "Encompasses", "EncompassesA",
				encompassesFinalTargetQueryString, 1);

		String provinceFinalTargetQueryString = "SELECT NameA as Name, CountryA as Country, PopulationA as Population, AreaA as Area, CapitalA as Capital, CapProvA as CapProv FROM ProvinceA Province";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfricaWR", "Province", "ProvinceA",
				provinceFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentAfricaWR() {
		//1-1 mappings between tables in MondialIntegr and MondialCityProvinceCountryContinentAfricaWR; are DNSC

		String bordersFinalTargetQueryString = "SELECT Country1A as Country1, Country2A as Country2, LengthA as Length FROM BordersA Borders";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfricaWR", "Borders", "BordersA",
				bordersFinalTargetQueryString, 1);

		String cityFinalTargetQueryString = "SELECT NameA as Name, CountryA as Country, ProvinceA as Province, PopulationA as Population, LongitudeA as Longitude, LatitudeA as Latitude FROM CityA City";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfricaWR", "City", "CityA",
				cityFinalTargetQueryString, 1);

		String continentFinalTargetQueryString = "SELECT NameA as Name, AreaA as Area FROM ContinentA Continent";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfricaWR", "Continent", "ContinentA",
				continentFinalTargetQueryString, 1);

		String countryFinalTargetQueryString = "SELECT NameA as Name, CodeA as Code, CapitalA as Capital, ProvinceA as Province, AreaA as Area, PopulationA as Population FROM CountryA Country";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfricaWR", "Country", "CountryA",
				countryFinalTargetQueryString, 1);

		String encompassesFinalTargetQueryString = "SELECT CountryA as Country, ContinentA as Continent, PercentageA as Percentage FROM EncompassesA Encompasses";
		load1t1DNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfricaWR", "Encompasses", "EncompassesA",
				encompassesFinalTargetQueryString, 1);
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentEurope() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentEurope; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "Encompasses");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentEurope", "Province");
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentEurope() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentEurope; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEurope", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEurope", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEurope", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEurope", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentEurope", "Encompasses");
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceCountryContinentAfrica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAfrica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "Encompasses");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceCountryContinentAfrica", "Province");
	}

	private void loadMappingsBetweenMondialIntegrAndMondialCityProvinceNACountryContinentAfrica() {
		//1-1 mappings between tables in MondialIntegr and CityProvinceCountryContinentAfrica; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfrica", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfrica", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfrica", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfrica", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialCityProvinceNACountryContinentAfrica", "Encompasses");
	}

	private void loadMappingsBetweenMondialCityProvinceCountryContinentAfricaAndMondialCityProvinceCountryContinentEurope() {
		//1-1 mappings between tables in CityProvinceCountryContinentAfrica and MondialCityProvinceCountryContinentEurope; are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "Encompasses");
		load1t1SNSCMappingBetweenTwoSchemas("MondialCityProvinceCountryContinentAfrica", "MondialCityProvinceCountryContinentEurope", "Province");
	}

	private void loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesAfricaNoRename() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesAfrica; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Organization");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "IsMember");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Economy");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Population");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Politics");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Language");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "Religion");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesAfrica", "EthnicGroup");

	}

	private void loadMappingsBetweenMondialIntegrAndMondialLanguageEconomyReligionOfCountriesEuropeNoRename() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEurope; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Economy");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "IsMember");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Language");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Organization");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Politics");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Population");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialLanguageEconomyReligionOfCountriesEurope", "Religion");

	}

	private void loadMappingsBetweenMondialLanguageEconomyReligionOfCountriesAfricaNoRenameAndMondialLanguageEconomyReligionOfCountriesEuropeNoRename() {
		//1-1 mappings between tables in MondialIntegr and MondialLanguageEconomyReligionOfCountriesEurope; are SNSC	
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Economy");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"IsMember");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Language");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Organization");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Politics");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Population");
		load1t1SNSCMappingBetweenTwoSchemas("MondialLanguageEconomyReligionOfCountriesAfrica", "MondialLanguageEconomyReligionOfCountriesEurope",
				"Religion");
	}

	private void loadMappingsBetweenMondialIntegrAndMondial() {

		//TODO mapping between organization, ismember and MembersOfOrganizations

		//1-1 mappings between all tables in MondialIntegr and Mondial; all are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Borders");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "City");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Continent");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Country");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Province");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Encompasses");

		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Economy");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "EthnicGroup");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "IsMember");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Language");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Organization");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Politics");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Population");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Religion");

		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Desert");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Desert");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Island");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Island");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "River");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_River");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Sea");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Sea");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Estuary");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "Geo_Source");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "IslandIn");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "LocatedOn");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "MergesWith");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "MountainOnIsland");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "Mondial", "located");
	}

	private void loadMappingsBetweenMondialIntegrAndMondialIslandLakeMountain() {
		//1-1 mappings between all tables in MondialIntegr and MondialIslandLakeMountain; all are SNSC
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Desert");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Desert");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Estuary");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Island");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_River");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Sea");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Geo_Source");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Island");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "IslandIn");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Lake");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "LocatedOn");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "MergesWith");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Mountain");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "MountainOnIsland");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "River");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "Sea");
		load1t1SNSCMappingBetweenTwoSchemas("MondialIntegr", "MondialIslandLakeMountain", "located");
	}

	/***
	 * Comments, this method is used only for 1-to-1 mappings. That is why it uses the SELECT * to get all the attributes
	 * 
	 * @param sourceSchemaName
	 * @param targetSchemaName
	 * @param saName
	 */
	private void load1t1SNSCMappingBetweenTwoSchemas(String sourceSchemaName, String targetSchemaName, String saName) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema = schemaRepository.getSchemaByName(targetSchemaName);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName);

		String queryString = "SELECT * FROM " + saName;

		Query sourceQuery = generateQuery(queryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(queryString, targetSchema, targetDS);

		Mapping mapping = new Mapping(queryString, queryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private void load1t1DNSCMappingBetweenTwoSchemas(String sourceSchemaName, String targetSchemaName, String sourceSaName, String targetSaName,
			String finalTargetQueryString, int numberOfLettersToRemove) {
		Schema sourceSchema = schemaRepository.getSchemaByName(sourceSchemaName);
		Schema targetSchema = schemaRepository.getSchemaByName(targetSchemaName);

		DataSource sourceDS = dataSourceRepository.getDataSourceWithSchemaName(sourceSchemaName);
		DataSource targetDS = dataSourceRepository.getDataSourceWithSchemaName(targetSchemaName);

		String sourceQueryString = "SELECT * FROM " + sourceSaName;
		String targetQueryString = "SELECT * FROM " + targetSaName;

		Query sourceQuery = generateQuery(sourceQueryString, sourceSchema, sourceDS);
		Query targetQuery = generateQuery(targetQueryString, targetSchema, targetDS);

		targetQuery = addRenameOperatorsForRemovalOfLastLetters(numberOfLettersToRemove, targetQuery);

		Mapping mapping = new Mapping(sourceQueryString, finalTargetQueryString);
		mapping.setQuery1(sourceQuery);
		mapping.setQuery2(targetQuery);
		makeMappingPersistent(mapping);
	}

	private Query generateQuery(String queryString, Schema schema, DataSource dataSource) {
		logger.debug("in generateQuery");
		logger.debug("queryString: " + queryString);
		logger.debug("schema: " + schema);
		logger.debug("dataSource: " + dataSource);
		CommonTree queryAst = parser.parseSQL(queryString);
		logger.debug("queryAst: " + queryAst.toStringTree());

		Query query = new Query();
		query = globalTranslator.translateAstIntoQuery(query, queryString, queryAst, schema);
		query.addDataSource(dataSource);
		logger.debug("query: " + query);
		logger.debug("query.getDataSources(): " + query.getDataSources());

		//TODO get scanOperators in query - should be somewhere in queryExpander

		MappingOperator rootOperator = query.getRootOperator();
		logger.debug("query.rootOperator: " + rootOperator);
		if (rootOperator instanceof ReduceOperator) {
			MappingOperator input = rootOperator.getInput();
			logger.debug("input: " + input);
			if (!(input instanceof ReduceOperator))
				query.setRootOperator(input);
			else
				logger.error("input is reduceOperator too - TODO sort this");
		}

		return query;
	}

	private Query generateQuery(String queryString, Set<Schema> schemas, DataSource dataSource1, DataSource dataSource2) {
		logger.debug("in generateQuery");
		logger.debug("queryString: " + queryString);
		logger.debug("dataSource1: " + dataSource1);
		logger.debug("dataSource2: " + dataSource2);
		CommonTree queryAst = parser.parseSQL(queryString);
		logger.debug("queryAst: " + queryAst.toStringTree());

		Query query = new Query();
		query = globalTranslator.translateAstIntoQuery(query, queryString, queryAst, schemas);
		query.addDataSource(dataSource1);
		query.addDataSource(dataSource2);
		logger.debug("query: " + query);

		//TODO get scanOperators in query - should be somewhere in queryExpander

		MappingOperator rootOperator = query.getRootOperator();
		logger.debug("query.rootOperator: " + rootOperator);
		if (rootOperator instanceof ReduceOperator) {
			MappingOperator input = rootOperator.getInput();
			logger.debug("input: " + input);
			if (!(input instanceof ReduceOperator))
				query.setRootOperator(input);
			else
				logger.error("input is reduceOperator too - TODO sort this");
		}

		return query;
	}

	/*
	private Set<ScanOperator> getScanOperatorsOfMapping(MappingOperator mappingOperator) {
		//logger.debug("in getScanOperatorsOfMapping");
		Set<ScanOperator> scanOperators = new HashSet<ScanOperator>();
		//logger.debug("mappingOperator: " + mappingOperator);
		mappingOperator.addMappingUsedForExpansion(mappingOperator.getMapping());
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getRhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ReduceOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			scanOperators.add(scanOperator);
			return scanOperators;
		} else
			logger.error("unexpected operator");
		return scanOperators;
	}
	*/

	/*
	private void setMappingOfAllMappingOperators(Mapping mapping, MappingOperator mappingOperator) {
		mappingOperator.setMapping(mapping);
		if (mappingOperator.getLhsInput() != null)
			setMappingOfAllMappingOperators(mapping, mappingOperator.getLhsInput());
		if (mappingOperator.getRhsInput() != null)
			setMappingOfAllMappingOperators(mapping, mappingOperator.getRhsInput());
	}
	*/

	private Query addRenameOperatorsForRemovalOfLastLetters(int numberOfLettersToRemove, Query query) {
		logger.debug("in addRenameOperatorsForRemovalOfLastLetters");
		logger.debug("numberOfLettersToRemove: " + numberOfLettersToRemove);
		MappingOperator rootOperator = query.getRootOperator();
		MappingOperator currentRootOperator = rootOperator;
		if (rootOperator instanceof ScanOperator) {
			logger.debug("rootOperator is scanOperator");
			ScanOperator scanOperator = (ScanOperator) rootOperator;

			SuperAbstract superAbstract = scanOperator.getSuperAbstract();
			logger.debug("superAbstract: " + superAbstract);
			Set<SuperLexical> superLexicals = superAbstract.getSuperLexicals();
			for (SuperLexical superLexical : superLexicals) {
				String superLexicalName = superLexical.getName();
				logger.debug("superLexicalName: " + superLexicalName);
				String newSuperLexicalName = superLexicalName.substring(0, superLexicalName.length() - numberOfLettersToRemove);
				logger.debug("newSuperLexicalName: " + newSuperLexicalName);
				RenameOperator superLexicalRenameOperator = new RenameOperator(superLexical, newSuperLexicalName);
				superLexicalRenameOperator.setInput(currentRootOperator);
				superLexicalRenameOperator.setDataSource(currentRootOperator.getDataSource());
				superLexicalRenameOperator.setResultType(currentRootOperator.getResultType());
				currentRootOperator = superLexicalRenameOperator;
			}

			String superAbstractName = superAbstract.getName();
			logger.debug("superAbstractName: " + superAbstractName);
			String newSuperAbstractName = superAbstractName.substring(0, superAbstractName.length() - numberOfLettersToRemove);
			logger.debug("newSuperAbstractName: " + newSuperAbstractName);
			RenameOperator superAbstractRenameOperator = new RenameOperator(superAbstract, newSuperAbstractName);
			superAbstractRenameOperator.setInput(currentRootOperator);
			superAbstractRenameOperator.setDataSource(currentRootOperator.getDataSource());
			superAbstractRenameOperator.setResultType(currentRootOperator.getResultType());
			currentRootOperator = superAbstractRenameOperator;
		} else {
			logger.error("unexpected operator: " + rootOperator);
		}
		query.setRootOperator(currentRootOperator);
		return query;
	}

	//TODO; add these to mappingRepository or service, add resultType too, check where that's in

	@Transactional
	//(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	private void makeMappingPersistent(Mapping mapping) {
		/*
		MappingOperator rootOperatorQuery1 = mapping.getQuery1().getRootOperator();
		Set<ScanOperator> scanOperatorsQuery1 = this.getScanOperatorsOfMapping(rootOperatorQuery1);
		Set<CanonicalModelConstruct> query1Populates = new LinkedHashSet<CanonicalModelConstruct>();
		for (ScanOperator scanOp : scanOperatorsQuery1) {
			CanonicalModelConstruct construct = scanOp.getSuperAbstract();
			query1Populates.add(construct);
		}
		mapping.setConstructs1(query1Populates);
		this.setMappingOfAllMappingOperators(mapping, rootOperatorQuery1);
		MappingOperator rootOperatorQuery2 = mapping.getQuery2().getRootOperator();
		Set<ScanOperator> scanOperatorsQuery2 = this.getScanOperatorsOfMapping(rootOperatorQuery2);
		Set<CanonicalModelConstruct> query2Populates = new LinkedHashSet<CanonicalModelConstruct>();
		for (ScanOperator scanOp : scanOperatorsQuery2) {
			CanonicalModelConstruct construct = scanOp.getSuperAbstract();
			query2Populates.add(construct);
		}
		mapping.setConstructs1(query2Populates);
		*/
		//this.setMappingOfAllMappingOperators(mapping, rootOperatorQuery2);
		mapping.setDataspace(dataspace);
		mapping.getQuery1().setDataspace(dataspace);
		mapping.getQuery2().setDataspace(dataspace);
		mappingService.addMapping(mapping);
		//mappingRepository.flush();
		//mappingRepository.update(mapping);
	}
}
