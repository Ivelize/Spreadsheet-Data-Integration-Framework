/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;

/**
 * @author chedeler
 *
 */
public abstract class GetSchemaInformationServiceImpl implements DatabaseMetaDataCallback {

	//TODO this doesn't look like the best way of doing this ... think about it
	private String schemaName = null;

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.support.DatabaseMetaDataCallback#processMetaData(java.sql.DatabaseMetaData)
	 */
	public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSchemaName() {
		return schemaName;
	}

}
