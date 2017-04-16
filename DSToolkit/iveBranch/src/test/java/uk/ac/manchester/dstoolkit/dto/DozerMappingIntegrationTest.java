package uk.ac.manchester.dstoolkit.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.dto.models.canonical.AttributeDTO;
import uk.ac.manchester.dstoolkit.dto.models.canonical.EntityDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.DataSourceDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.SchemaDTO;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;

public class DozerMappingIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(DozerMappingIntegrationTest.class);

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	private Mapper dozerMapper;

	@Test
	public void testMappingDataSource() {
		DataSource mondialCityCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityCountryContinentAfrica");

		logger.debug("mondialCityCountryContinentAfricaNoRenameDS: " + mondialCityCountryContinentAfricaNoRenameDS);

		DataSourceDTO dataSourceDTO = this.dozerMapper.map(mondialCityCountryContinentAfricaNoRenameDS, DataSourceDTO.class);
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getId(), dataSourceDTO.getId());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getConnectionURL(), dataSourceDTO.getConnectionURL());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getDescription(), dataSourceDTO.getDescription());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getPassword(), dataSourceDTO.getPassword());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getDriverClass(), dataSourceDTO.getDriverClass());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getUserName(), dataSourceDTO.getUserName());
		assertEquals(mondialCityCountryContinentAfricaNoRenameDS.getSchemaURL(), dataSourceDTO.getSchemaURL());
	}

	@Test
	public void testMappingSchemaSuperAbstractsAndSuperLexicals() {
		Schema mondialCityCountryContinentAfricaSchema = schemaRepository.getSchemaByName("MondialCityCountryContinentAfrica");

		logger.debug("mondialCityCountryContinentAfricaSchema: " + mondialCityCountryContinentAfricaSchema);

		SchemaDTO schemaDTO = this.dozerMapper.map(mondialCityCountryContinentAfricaSchema, SchemaDTO.class);
		assertEquals(mondialCityCountryContinentAfricaSchema.getId(), schemaDTO.getId());
		assertEquals(mondialCityCountryContinentAfricaSchema.getName(), schemaDTO.getName());
		for (CanonicalModelConstruct construct : mondialCityCountryContinentAfricaSchema.getCanonicalModelConstructs()) {
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
		assertEquals(5, schemaDTO.getEntities().size());
		for (EntityDTO tableSchema : schemaDTO.getEntities()) {
			logger.debug("tableSchema.getName: " + tableSchema.getName());
			assertTrue(tableSchema.getAttributes().size() > 0);
			for (AttributeDTO attribute : tableSchema.getAttributes()) {
				assertTrue(attribute.getId() != null);
				logger.debug("attribute.getId: " + attribute.getId());
				assertTrue(attribute.getName() != null);
				logger.debug("attribute.getName: " + attribute.getName());
				assertTrue(attribute.getDataType() != null);
				logger.debug("attribute.getDataType: " + attribute.getDataType());
				logger.debug("attribute.getIsNullable: " + attribute.getIsNullable());
			}
		}
	}
}
