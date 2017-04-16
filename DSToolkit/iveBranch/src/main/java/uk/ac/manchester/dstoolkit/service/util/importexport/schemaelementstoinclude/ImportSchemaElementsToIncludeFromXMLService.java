/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoinclude;

/**
 * @author chedeler
 *
 */
public interface ImportSchemaElementsToIncludeFromXMLService {

	public abstract void readSchemaElementsToIncludeFromXml(String fileLocation);

	//public abstract boolean hasSchemaElementsToIncludeForSchema(String schemaName);

	//public abstract Map<String, ElementsToInclude> getSchemaElementsToInclude();

	//public abstract Map<String, Table> getTablesToIncludeForRelationalSchema(String schemaName);

	public abstract boolean includeTableInSchema(String schemaName, String tableName);

	//public abstract Set<String> getColumnNamesToIncludeForTableToIncludeInRelationalSchema(String schemaName, String tableName);

	public abstract boolean includeColumnInTableInSchema(String schemaName, String tableName, String columnName);

	//public abstract Map<String, ComplexElement> getComplexElementsToIncludeForXmlSchema(String schemaName);

	public abstract boolean includeComplexElementInSchema(String schemaName, String complexElementName);

	//public abstract Set<String> getSimpleElementNamesToIncludeForComplexElementToIncludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean includeSimpleElementInComplexElementInSchema(String schemaName, String complexElementName, String simpleElementName);

	//public abstract Set<String> getAttributeNamesToIncludeForComplexElementToIncludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean includeAttributeInComplexElementInSchema(String schemaName, String complexElement, String attributeName);

	//public abstract Set<String> getComplexElementNamesToIncludeForComplexElementToIncludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean includeComplexElementInComplexElementInSchema(String schemaName, String complexElementName,
			String nameOfComplexElementToCheckForInclusion);
}
