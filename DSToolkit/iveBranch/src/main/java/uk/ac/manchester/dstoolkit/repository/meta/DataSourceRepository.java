package uk.ac.manchester.dstoolkit.repository.meta;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

public interface DataSourceRepository extends GenericRepository<DataSource, Long> {

	/**
	 * @param dataSourceId
	 * @return modelType
	 */
	public ModelType getModelTypeOfDataSourceWithId(Long dataSourceId);

	/**
	 * @param dataSourceId
	 * @return dataSource
	 */
	public DataSource getDataSourceWithId(Long dataSourceId);

	/**
	 * @param schemaName
	 * @return dataSource
	 */
	public DataSource getDataSourceWithSchemaName(String schemaName);

	public DataSource getDataSourceWithName(String dataSourceName);
}
