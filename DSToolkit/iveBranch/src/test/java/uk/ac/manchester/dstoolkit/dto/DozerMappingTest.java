package uk.ac.manchester.dstoolkit.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.dto.models.canonical.AttributeDTO;
import uk.ac.manchester.dstoolkit.dto.models.canonical.EntityDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.DataSourceDTO;
import uk.ac.manchester.dstoolkit.dto.models.meta.SchemaDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class DozerMappingTest {

	static Logger logger = Logger.getLogger(DozerMappingTest.class);

	@Autowired
	private Mapper dozerMapper;
	private DataSource dataSource;
	private SuperAbstract sa1;
	private SuperLexical sl1;
	private Schema schema;

	@Before
	public void setUp() {
		dataSource = new DataSource();
		dataSource.setConnectionURL("connectionURL");
		dataSource.setSchemaURL("schemaURL");
		dataSource.setDescription("description");
		dataSource.setDriverClass("driverClass");
		dataSource.setPassword("password");
		dataSource.setUserName("userName");

		schema = new Schema();
		schema.setName("schemaName");

		sa1 = new SuperAbstract();
		sa1.setName("sa1Name");
		SuperAbstract sa2 = new SuperAbstract();
		sa2.setName("sa2Name");

		sl1 = new SuperLexical();
		sl1.setName("sl1Name");
		sl1.setDataType(DataType.STRING);
		sl1.setIsNullable(false);

		SuperLexical sl2 = new SuperLexical();
		sl2.setName("sl2Name");
		sl2.setDataType(DataType.INTEGER);
		sl2.setIsNullable(true);

		SuperLexical sl3 = new SuperLexical();
		sl3.setName("sl3Name");
		sl3.setDataType(DataType.BOOLEAN);
		sl3.setIsNullable(false);

		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);
		sa2.addSuperLexical(sl3);

		schema.addCanonicalModelConstruct(sa1);
		schema.addCanonicalModelConstruct(sa2);
		schema.addCanonicalModelConstruct(sl1);
		schema.addCanonicalModelConstruct(sl2);
		schema.addCanonicalModelConstruct(sl3);

		schema.setDataSource(dataSource);
	}

	@Test
	public void testMappingDataSource() {
		DataSourceDTO dataSourceDTO = this.dozerMapper.map(dataSource, DataSourceDTO.class);
		assertEquals(dataSource.getConnectionURL(), dataSourceDTO.getConnectionURL());
		assertEquals(dataSource.getDescription(), dataSourceDTO.getDescription());
		assertEquals(dataSource.getPassword(), dataSourceDTO.getPassword());
		assertEquals(dataSource.getDriverClass(), dataSourceDTO.getDriverClass());
		assertEquals(dataSource.getUserName(), dataSourceDTO.getUserName());
		assertEquals(dataSource.getSchemaURL(), dataSourceDTO.getSchemaURL());
	}

	@Test
	public void testMappingSuperLexical() {
		AttributeDTO attributeDTO = this.dozerMapper.map(sl1, AttributeDTO.class);
		assertEquals(sl1.getName(), attributeDTO.getName());
		assertEquals(sl1.getDataType().toString(), attributeDTO.getDataType());
		assertEquals(sl1.getIsNullable(), attributeDTO.getIsNullable());
	}

	@Test
	public void testMappingSchemaSuperAbstractsAndSuperLexicals() {
		SchemaDTO schemaDTO = this.dozerMapper.map(schema, SchemaDTO.class);
		assertEquals(schema.getName(), schemaDTO.getName());
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
		assertEquals(2, schemaDTO.getEntities().size());
		for (EntityDTO entity : schemaDTO.getEntities()) {
			logger.debug("entity: " + entity.getName());
			assertTrue(entity.getAttributes().size() > 0);
			for (AttributeDTO attribute : entity.getAttributes()) {
				assertTrue(attribute.getName() != null);
				logger.debug("attribute.getName: " + attribute.getName());
				assertTrue(attribute.getDataType() != null);
				logger.debug("attribute.getDataType: " + attribute.getDataType());
			}
		}
	}
}
