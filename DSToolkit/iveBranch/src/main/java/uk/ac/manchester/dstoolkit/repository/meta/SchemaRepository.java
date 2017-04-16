package uk.ac.manchester.dstoolkit.repository.meta;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

public interface SchemaRepository extends GenericRepository<Schema, Long> {

	/**
	 * @param dataSource
	 * @return schema
	 */
	public Schema getSchemaForDataSource(DataSource dataSource);

	/**
	 * @param schemaName
	 * @return schema
	 */
	public Schema getSchemaByName(String schemaName);
	
	
	/**
	 * @return List Schemas
	 */
	public List<Schema> getAllSchemaExtracted();

	/**
	 * @param schema
	 * @return canonicalModelConstructs
	 */
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInSchema(Schema schema);

	/**
	 * @param constructType
	 * @param schema
	 * @return canonicalModelConstructs
	 */
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsOfTypeInSchema(ConstructType constructType, Schema schema);

	/**
	 * @param schema
	 * @return superAbstracts
	 */
	public List<SuperAbstract> getAllSuperAbstractsInSchema(Schema schema);

	/**
	 * @param schema
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsInSchema(Schema schema);

	/**
	 * @param schema
	 * @return superRelationships
	 */
	public List<SuperRelationship> getAllSuperRelationshipsInSchema(Schema schema);
}
