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
@Service(value = "getTableNamesService")
public class GetTableNamesServiceImpl extends GetSchemaInformationServiceImpl {

	static Logger logger = Logger.getLogger(GetTableNamesServiceImpl.class);

	//TODO these seem hard to test, work on it

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.support.DatabaseMetaDataCallback#processMetaData(java.sql.DatabaseMetaData)
	 */
	@Override
	public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
		logger.debug("in getTableNamesServiceImpl");
		logger.debug("this.getSchemaName: " + this.getSchemaName());
		String schemaName = "";
		if (this.getSchemaName() == null)
			schemaName = "public";
		else
			schemaName = this.getSchemaName();
		logger.debug("schemaName: " + schemaName);
		ResultSet rs = dbmd.getTables(null, schemaName, null, new String[] { "TABLE" });
		logger.debug("rs: " + rs);
		ArrayList<String> l = new ArrayList<String>();
		while (rs.next()) {
			logger.debug("rs: " + rs);
			String tableName = rs.getString("TABLE_NAME");
			logger.debug("tableName: " + tableName);
			l.add(tableName);
		}
		rs.close();
		return l;
	}
}
