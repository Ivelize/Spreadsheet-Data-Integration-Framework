/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataTypeMapper;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "getColumnInformationOfTableService")
public class GetColumnInformationOfTableServiceImpl extends GetSchemaInformationServiceImpl {

	static Logger logger = Logger.getLogger(GetColumnInformationOfTableServiceImpl.class);

	private String tableName;

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.support.DatabaseMetaDataCallback#processMetaData(java.sql.DatabaseMetaData)
	 */
	@Override
	public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
		logger.debug("in GetColumnInformationOfTableServiceImpl");
		logger.debug("tableName: " + tableName);
		logger.debug("this.getSchemaName: " + this.getSchemaName());
		String schemaName = "";
		if (this.getSchemaName() == null)
			schemaName = "public";
		else
			schemaName = this.getSchemaName();
		logger.debug("schemaName: " + schemaName);
		ResultSet rs = dbmd.getColumns(null, schemaName, tableName, null);
		logger.debug("rs: " + rs);
		List<ColumnInformation> l = new ArrayList<ColumnInformation>();
		while (rs.next()) {
			logger.debug("rs: " + rs);
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			int maxValueSize = rs.getInt("COLUMN_SIZE");
			int isNullableInt = rs.getInt("NULLABLE");
			logger.debug("name: " + name);
			logger.debug("type: " + type);
			logger.debug("isNullableInt: " + isNullableInt);
			DataType dataType = DataTypeMapper.mapSQLTypeToDataType(type);
			logger.debug("dataType: " + dataType);
			boolean isNullable = false;
			if (isNullableInt == 1)
				isNullable = true;
			logger.debug("isNullable: " + isNullable);
			l.add(new ColumnInformation(name, dataType, maxValueSize, isNullable));
		}
		rs.close();
		return l;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public class ColumnInformation {

		String columnName;
		DataType columnType;
		int columnSize;
		boolean isNullable;

		ColumnInformation(String columnName, DataType columnType, int columnSize, boolean isNullable) {
			this.columnName = columnName;
			this.columnType = columnType;
			this.columnSize = columnSize;
			this.isNullable = isNullable;
		}

		public String getColumnName() {
			return columnName;
		}

		public DataType getColumnType() {
			return columnType;
		}

		public boolean getIsNullable() {
			return isNullable;
		}

		public int getColumnSize() {
			return columnSize;
		}
	}
}
