/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoexclude;

/**
 * @author chedeler
 *
 */
public interface ImportSchemaElementsToExcludeFromXMLService {

	public abstract void readSchemaElementsToExcludeFromXml(String fileLocation);

	//public abstract Map<String, ElementsToExclude> getSchemaElementsToExclude();

	//public abstract boolean hasSchemaElementsToExcludeForSchema(String schemaName);

	//public abstract Map<String, Table> getTablesToExcludeForRelationalSchema(String schemaName);

	public abstract boolean excludeTableInSchema(String schemaName, String tableName);

	//public abstract Set<String> getColumnNamesToExcludeForTableNotToExcludeInRelationalSchema(String schemaName, String tableName);

	public abstract boolean excludeColumnInTableInSchema(String schemaName, String tableName, String columnName);

	//public abstract Map<String, ComplexElement> getComplexElementsToExcludeForXmlSchema(String schemaName);

	public abstract boolean excludeComplexElementInSchema(String schemaName, String complexElementName);

	//public abstract Set<String> getSimpleElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean excludeSimpleElementInComplexElementInSchema(String schemaName, String complexElementName, String simpleElementName);

	//public abstract Set<String> getAttributeNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean excludeAttributeInComplexElementInSchema(String schemaName, String complexElement, String attributeName);

	//public abstract Set<String> getComplexElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName);

	public abstract boolean excludeComplexElementInComplexElementInSchema(String schemaName, String complexElementName,
			String nameOfComplexElementToCheckForExclusion);
}
