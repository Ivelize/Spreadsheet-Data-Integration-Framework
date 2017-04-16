/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Service;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "getPrimaryKeysOfTableService")
public class GetPrimaryKeysOfTableServiceImpl extends GetSchemaInformationServiceImpl {

	static Logger logger = Logger.getLogger(GetPrimaryKeysOfTableServiceImpl.class);

	private String tableName;

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.support.DatabaseMetaDataCallback#processMetaData(java.sql.DatabaseMetaData)
	 */
	@Override
	public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
		logger.debug("in GetPrimaryKeysOfTableServiceImpl");
		logger.debug("tableName: " + tableName);
		logger.debug("this.getSchemaName: " + this.getSchemaName());
		String schemaName = "";
		if (this.getSchemaName() == null)
			schemaName = "public";
		else
			schemaName = this.getSchemaName();
		logger.debug("schemaName: " + schemaName);
		ResultSet rs = dbmd.getPrimaryKeys(null, schemaName, tableName);
		logger.debug("rs: " + rs);
		ArrayList<String> l = new ArrayList<String>();
		while (rs.next()) {
			logger.debug("rs: " + rs);
			String columnName = rs.getString("COLUMN_NAME");
			String pkName = rs.getString("PK_NAME"); //TODO name of primary key is ignored for now
			logger.debug("primaryKeyName: " + columnName);
			logger.debug("pkName: " + pkName);
			l.add(columnName);
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
}
