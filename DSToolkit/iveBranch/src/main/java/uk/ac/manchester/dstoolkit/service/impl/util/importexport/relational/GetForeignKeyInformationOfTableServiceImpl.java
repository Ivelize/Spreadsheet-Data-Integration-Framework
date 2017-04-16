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

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "getForeignKeyInformationOfTableService")
public class GetForeignKeyInformationOfTableServiceImpl extends GetSchemaInformationServiceImpl {

	static Logger logger = Logger.getLogger(GetForeignKeyInformationOfTableServiceImpl.class);

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
		ResultSet rs = dbmd.getImportedKeys(null, schemaName, tableName);
		logger.debug("rs: " + rs);
		List<ForeignKeyInformation> l = new ArrayList<ForeignKeyInformation>();
		while (rs.next()) {
			logger.debug("rs: " + rs);

			String pkTableSchema = rs.getString("PKTABLE_SCHEM");
			String pkTableName = rs.getString("PKTABLE_NAME");
			String pkColumnName = rs.getString("PKCOLUMN_NAME");
			String fkTableSchema = rs.getString("FKTABLE_SCHEM");
			String fkTableName = rs.getString("FKTABLE_NAME");
			String fkColumnName = rs.getString("FKCOLUMN_NAME");
			String fkName = rs.getString("FK_NAME");
			String pkName = rs.getString("PK_NAME");

			logger.debug("pkTableSchema: " + pkTableSchema);
			logger.debug("pkTableName: " + pkTableName);
			logger.debug("pkColumnName: " + pkColumnName);
			logger.debug("fkTableSchema: " + fkTableSchema);
			logger.debug("fkTableName: " + fkTableName);
			logger.debug("fkColumnName: " + fkColumnName);
			logger.debug("fkName: " + fkName);
			logger.debug("pkName: " + pkName);

			l.add(new ForeignKeyInformation(pkTableSchema, pkTableName, pkColumnName, fkTableSchema, fkTableName, fkColumnName, fkName, pkName));
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

	public class ForeignKeyInformation {

		String pkTableSchema;
		String pkTableName;
		String pkColumnName;
		String fkTableSchema;
		String fkTableName;
		String fkColumnName;
		String fkName;
		String pkName;

		ForeignKeyInformation(String pkTableSchema, String pkTableName, String pkColumnName, String fkTableSchema, String fkTableName,
				String fkColumnName, String fkName, String pkName) {
			this.pkTableSchema = pkTableSchema;
			this.pkTableName = pkTableName;
			this.pkColumnName = pkColumnName;
			this.fkTableSchema = fkTableSchema;
			this.fkTableName = fkTableName;
			this.fkColumnName = fkColumnName;
			this.fkName = fkName;
			this.pkName = pkName;
		}

		public String getPkTableSchema() {
			return pkTableSchema;
		}

		/**
		 * @return the pkTableName
		 */
		public String getPkTableName() {
			return pkTableName;
		}

		/**
		 * @return the pkColumnName
		 */
		public String getPkColumnName() {
			return pkColumnName;
		}

		public String getFkTableSchema() {
			return fkTableSchema;
		}

		/**
		 * @return the fkTableName
		 */
		public String getFkTableName() {
			return fkTableName;
		}

		/**
		 * @return the fkColumnName
		 */
		public String getFkColumnName() {
			return fkColumnName;
		}

		/**
		 * @return the fkName
		 */
		public String getFkName() {
			return fkName;
		}

		/**
		 * @return the pkName
		 */
		public String getPkName() {
			return pkName;
		}

	}
}
