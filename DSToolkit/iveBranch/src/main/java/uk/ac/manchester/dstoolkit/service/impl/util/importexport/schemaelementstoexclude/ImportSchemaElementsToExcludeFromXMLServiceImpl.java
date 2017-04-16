/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoexclude;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoexclude.ImportSchemaElementsToExcludeFromXMLService;

/**
 * @author chedeler
 *
 */
@Service(value = "importSchemaElementsToExcludeFromXMLService")
public class ImportSchemaElementsToExcludeFromXMLServiceImpl implements ImportSchemaElementsToExcludeFromXMLService {

	//TODO currently only checking two levels in XML, i.e., complexElement - simpleElement, complexElement - attribute, complexElement - complexElement
	//TODO for this to work, the complexElement within a complexElement will have to be listed separately as a complexElement
	//TODO this may be ok for global complexElements, but I'm not sure about local complexElements if they have same name as global complexElement
	//TODO still need to think about this a bit more

	static Logger logger = Logger.getLogger(ImportSchemaElementsToExcludeFromXMLServiceImpl.class);

	private final Map<String, ElementsToExclude> schemaNameElementsToExcludeMap = new HashMap<String, ElementsToExclude>();

	private final Map<String, Map<String, Table>> schemaNameTableNameToExcludeMap = new HashMap<String, Map<String, Table>>();
	private final Map<String, Map<String, Map<String, Column>>> schemaNameTableNameColumnNameToExcludeMap = new HashMap<String, Map<String, Map<String, Column>>>();

	private final Map<String, Map<String, ComplexElement>> schemaNameComplexElementNameToExcludeMap = new HashMap<String, Map<String, ComplexElement>>();
	private final Map<String, Map<String, Map<String, SimpleElement>>> schemaNameComplexElementNameSimpleElementNameToExcludeMap = new HashMap<String, Map<String, Map<String, SimpleElement>>>();
	private final Map<String, Map<String, Map<String, Attribute>>> schemaNameComplexElementNameAttributeNameToExcludeMap = new HashMap<String, Map<String, Map<String, Attribute>>>();
	private final Map<String, Map<String, Map<String, ComplexElement>>> schemaNameComplexElementNameComplexElementNameToExcludeMap = new HashMap<String, Map<String, Map<String, ComplexElement>>>();

	public void readSchemaElementsToExcludeFromXml(String fileLocation) {
		logger.debug("in readSchemaElementsToExcludeFromXml");
		logger.debug("fileLocation: " + fileLocation);
		Schemas schemas = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoexclude");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			schemas = (Schemas) unmarshaller.unmarshal(new FileReader(fileLocation));
		} catch (JAXBException e) {
			logger.error("something wrong with xml file containing schema elements to exclude");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("didn't find xml file containing schema elements to exclude");
			e.printStackTrace();
		}
		if (schemas != null) {
			List<Schema> schemaList = schemas.getSchema();
			for (Schema schema : schemaList) {
				String schemaName = schema.getName();
				ElementsToExclude elementsToExclude = schema.getElementsToExclude();
				schemaNameElementsToExcludeMap.put(schemaName, elementsToExclude);
				processSchemaElementsToExcludeFromXml(schemaName, elementsToExclude);
			}
		}
	}

	private void processSchemaElementsToExcludeFromXml(String schemaName, ElementsToExclude elementsToExclude) {
		logger.debug("in processSchemaElementsToExcludeFromXml");
		logger.debug("schemaName: " + schemaName);
		logger.debug("elementsToExclude: " + elementsToExclude);
		List<Table> tables = elementsToExclude.getTable();
		logger.debug("tables: " + tables);
		if (tables != null && tables.size() > 0) {
			logger.debug("found tables to exclude");
			processRelationalSchemaElementsToExclude(schemaName, tables);
		}
		List<ComplexElement> complexElements = elementsToExclude.getComplexElement();
		logger.debug("complexElements: " + complexElements);
		if (complexElements != null & complexElements.size() > 0) {
			logger.debug("found complexElements to exclude");
			processXmlSchemaElementsToExclude(schemaName, complexElements);
		}
	}

	private void processRelationalSchemaElementsToExclude(String schemaName, List<Table> tables) {
		logger.debug("in processRelationalSchemaElementsToExclude");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tables: " + tables);
		Map<String, Table> tablesToCheckForExclusion = new HashMap<String, Table>();
		Map<String, Map<String, Column>> columnsInTableToCheckForExclusion = new HashMap<String, Map<String, Column>>();
		for (Table table : tables) {
			logger.debug("table: " + table);
			Map<String, Column> columnsToCheckForExclusion = new HashMap<String, Column>();
			String tableName = table.getName();
			logger.debug("tableName: " + tableName);
			logger.debug("table.isExclude: " + table.isExclude());
			if (table.isExclude()) {
				logger.debug("table to be excluded, tableName: " + tableName);
				logger.debug("no need to check for columns, as all will be excluded automatically - ignore columns");
				tablesToCheckForExclusion.put(tableName, table);
			} else {
				logger.debug("table not to be excluded, check columns, tableName: " + tableName);
				List<Column> columns = table.getColumn();
				if (columns != null && columns.size() > 0) {
					for (Column column : columns) {
						logger.debug("column.getName(): " + column.getName());
						logger.debug("column.isExclude: " + column.isExclude());
						if (column.isExclude()) {
							logger.debug("found column to exclude, columnName: " + column.getName());
							logger.debug("add column for table to check for exclusion");
							columnsToCheckForExclusion.put(column.getName(), column);
						} else {
							logger.debug("column not to be exluded, ignore, columnName: " + column.getName());
						}
					}
				} else {
					logger.debug("tableName: " + tableName);
					logger.debug("table not to be excluded and no columns listed that are to be excluded, ignore");
				}
			}
			if (!columnsToCheckForExclusion.isEmpty()) {
				logger.debug("found columns to exclude for table, tableName: " + tableName);
				columnsInTableToCheckForExclusion.put(tableName, columnsToCheckForExclusion);
			}
		}
		if (!tablesToCheckForExclusion.isEmpty()) {
			logger.debug("found tables to exclude for schema, schemaName: " + schemaName);
			schemaNameTableNameToExcludeMap.put(schemaName, tablesToCheckForExclusion);
		}
		if (!columnsInTableToCheckForExclusion.isEmpty()) {
			logger.debug("found columns in tables of schema to exclude, schemaName: " + schemaName);
			schemaNameTableNameColumnNameToExcludeMap.put(schemaName, columnsInTableToCheckForExclusion);
		}
	}

	/*
	public Map<String, Table> getTablesToExcludeForRelationalSchema(String schemaName) {
		logger.debug("in getTablesToExcludeForRelationalSchema");
		logger.debug("schemaName: " + schemaName);
		return schemaNameTableNameToExcludeMap.get(schemaName);
	}
	*/

	/*
	public Set<String> getColumnNamesToExcludeForTableNotToExcludeInRelationalSchema(String schemaName, String tableName) {
		logger.debug("in getColumnNamesToExcludeForTableInRelationalSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		if (schemaNameTableNameColumnNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found tables with columns to exclude for schemaName");
			Map<String, Map<String, Column>> columnsInTableInSchema = schemaNameTableNameColumnNameToExcludeMap.get(schemaName);
			if (columnsInTableInSchema.containsKey(tableName)) {
				logger.debug("found columns to exclude for tableName not to exclude");
				return columnsInTableInSchema.get(tableName).keySet();
			} else
				logger.debug("didn't find columns to exclude for tableName not to exclude");
		} else
			logger.debug("didn't find tables to exclude for schemaName");
		return null;
	}
	*/

	public boolean excludeTableInSchema(String schemaName, String tableName) {
		logger.debug("in excludeTableInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		if (schemaNameTableNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found tables to exclude for schemaName");
			Map<String, Table> tablesInSchema = schemaNameTableNameToExcludeMap.get(schemaName);
			return tablesInSchema.containsKey(tableName);
		} else
			logger.debug("didn't find tables to exclude for schemaName");
		return false;
	}

	public boolean excludeColumnInTableInSchema(String schemaName, String tableName, String columnName) {
		logger.debug("in excludeColumnInTableInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		logger.debug("columnName: " + columnName);
		if (excludeTableInSchema(schemaName, tableName)) {
			logger.debug("whole table to be excluded, excluding all columns automatically");
			return true;
		} else {
			logger.debug("not found whole table to be excluded, check columns to be excluded");
			if (schemaNameTableNameColumnNameToExcludeMap.containsKey(schemaName)) {
				logger.debug("found columns in tables to exclude for schemaName");
				Map<String, Map<String, Column>> columnsInTableInSchema = schemaNameTableNameColumnNameToExcludeMap.get(schemaName);
				if (columnsInTableInSchema.containsKey(tableName)) {
					logger.debug("found columns to exclude for tableName");
					Map<String, Column> columnsInTable = columnsInTableInSchema.get(tableName);
					return columnsInTable.containsKey(columnName);
				} else {
					logger.debug("didn't find columns to exclude for tableName, include all as default");
					return false;
				}
			} else {
				logger.debug("didn't find tables to exclude for schemaName, include all as default");
				return false;
			}
		}
	}

	private void processXmlSchemaElementsToExclude(String schemaName, List<ComplexElement> complexElements) {
		logger.debug("in processXmlSchemaElementsToExclude");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElements: " + complexElements);
		Map<String, ComplexElement> complexElementsToCheckForExclusion = new HashMap<String, ComplexElement>();
		Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementToCheckForExclusion = new HashMap<String, Map<String, SimpleElement>>();
		Map<String, Map<String, Attribute>> attributesInComplexElementToCheckForExclusion = new HashMap<String, Map<String, Attribute>>();
		Map<String, Map<String, ComplexElement>> complexElementsInComplexElementToCheckForExclusion = new HashMap<String, Map<String, ComplexElement>>();

		for (ComplexElement complexElement : complexElements) {
			logger.debug("complexElement: " + complexElement);
			Map<String, SimpleElement> simpleElementsToCheckForExclusion = new HashMap<String, SimpleElement>();
			Map<String, Attribute> attributesToCheckForExclusion = new HashMap<String, Attribute>();
			Map<String, ComplexElement> complexElementsToCheckForExclusionInComplexElement = new HashMap<String, ComplexElement>();
			String complexElementName = complexElement.getName();
			logger.debug("complexElementName: " + complexElementName);
			logger.debug("complexElement.isExclude(): " + complexElement.isExclude());
			if (complexElement.isExclude()) {
				logger.debug("complexElement to be excluded, complexElementName: " + complexElementName);
				logger.debug("not checking for elements within complexElement, as should all be excluded automatically");//TODO think about this, does and should this apply for all elements in XML?
				complexElementsToCheckForExclusion.put(complexElementName, complexElement);
			} else {
				logger.debug("complexElement not to be excluded, check its child elements, complexElementName: " + complexElementName);
				List<SimpleElement> simpleElements = complexElement.getSimpleElement();
				logger.debug("simpleElements: " + simpleElements);
				if (simpleElements != null && simpleElements.size() > 0) {
					for (SimpleElement simpleElement : simpleElements) {
						logger.debug("simpleElement.getName(): " + simpleElement.getName());
						logger.debug("simpleElement.isExclude(): " + simpleElement.isExclude());
						if (simpleElement.isExclude()) {
							logger.debug("found simpleElement to exclude, simpleElement.getName(): " + simpleElement.getName());
							logger.debug("add simpleElement for complexElement to exclude");
							simpleElementsToCheckForExclusion.put(simpleElement.getName(), simpleElement);
						} else {
							logger.debug("simpleElement not to be excluded, ignore, simpleElement.getName: " + simpleElement.getName());
						}
					}
				} else {
					logger.debug("complexElement not to be excluded and no simpleElements found");
				}
				if (!simpleElementsToCheckForExclusion.isEmpty())
					logger.debug("found simpleElements to exclude for complexElement, complexElementName: " + complexElementName);
				simpleElementsInComplexElementToCheckForExclusion.put(complexElementName, simpleElementsToCheckForExclusion);

				List<Attribute> attributes = complexElement.getAttribute();
				logger.debug("attributes: " + attributes);
				if (attributes != null & attributes.size() > 0) {
					for (Attribute attribute : attributes) {
						logger.debug("attribute.getName(): " + attribute.getName());
						logger.debug("attribute.isExclude(): " + attribute.isExclude());
						if (attribute.isExclude()) {
							logger.debug("found attribute to exclude, attribute.getName(): " + attribute.getName());
							logger.debug("add attribute for complexElement to exclude");
							attributesToCheckForExclusion.put(attribute.getName(), attribute);
						} else {
							logger.debug("attribute not to be excluded, ignore, attribute.getName(): " + attribute.getName());
						}
					}
				} else {
					logger.debug("complexElement not to be excluded and no attributes found");
				}
				if (!attributesToCheckForExclusion.isEmpty()) {
					logger.debug("found attributes to exclude for complexElement, complexElementName: " + complexElementName);
					attributesInComplexElementToCheckForExclusion.put(complexElementName, attributesToCheckForExclusion);
				}

				List<ComplexElement> complexElementsInComplexElement = complexElement.getComplexElement();
				logger.debug("complexElementsInComplexElement: " + complexElementsInComplexElement);
				if (complexElementsInComplexElement != null && complexElementsInComplexElement.size() > 0) {
					for (ComplexElement complexElementInComplexElement : complexElementsInComplexElement) {
						logger.debug("complexElementInComplexelement.getName(): " + complexElementInComplexElement.getName());
						logger.debug("complexElementInComplexElement.isExclude(): " + complexElementInComplexElement.isExclude());
						if (complexElementInComplexElement.isExclude()) {
							logger.debug("found complexElementInComplexElement to exclude, complexElementInComplexElement.getName(): "
									+ complexElementInComplexElement);
							logger.debug("add complexElementInComplexElement for complexElement to exclude");
							complexElementsToCheckForExclusionInComplexElement.put(complexElementInComplexElement.getName(),
									complexElementInComplexElement);
						} else {
							logger.debug("complexElementInComplexElement not to be excluded, ignore, complexElementInComplexElement.getName(): "
									+ complexElementInComplexElement.getName());
						}
					}
				} else {
					logger.debug("complexElement not to be excluded and no child complexElements found");
				}
				if (!complexElementsToCheckForExclusionInComplexElement.isEmpty()) {
					logger.debug("found complexElements in complexElement to exclude, complexElementName: " + complexElementName);
					complexElementsInComplexElementToCheckForExclusion.put(complexElementName, complexElementsToCheckForExclusionInComplexElement);
				}
			}
		}
		if (!complexElementsToCheckForExclusion.isEmpty()) {
			logger.debug("found complexElements to exclude in schema, schemaName: " + schemaName);
			schemaNameComplexElementNameToExcludeMap.put(schemaName, complexElementsToCheckForExclusion);
		}
		if (!simpleElementsInComplexElementToCheckForExclusion.isEmpty()) {
			logger.debug("found simpleElements in complexElements of schema to exclude, schemaName: " + schemaName);
			schemaNameComplexElementNameSimpleElementNameToExcludeMap.put(schemaName, simpleElementsInComplexElementToCheckForExclusion);
		}
		if (!attributesInComplexElementToCheckForExclusion.isEmpty()) {
			logger.debug("found attributes in complexElements of schema to exclude, schemaName: " + schemaName);
			schemaNameComplexElementNameAttributeNameToExcludeMap.put(schemaName, attributesInComplexElementToCheckForExclusion);
		}
		if (!complexElementsInComplexElementToCheckForExclusion.isEmpty()) {
			logger.debug("found complexElements in complexElements of schema to exclude, schemaName: " + schemaName);
			schemaNameComplexElementNameComplexElementNameToExcludeMap.put(schemaName, complexElementsInComplexElementToCheckForExclusion);
		}
	}

	/*
	public Map<String, ComplexElement> getComplexElementsToExcludeForXmlSchema(String schemaName) {
		logger.debug("in getComplexElementsToExcludeForXmlSchema");
		logger.debug("schemaName: " + schemaName);
		return schemaNameComplexElementNameToExcludeMap.get(schemaName);
	}
	*/

	/*
	public Set<String> getSimpleElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName) {
		logger.debug("in getSimpleElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameSimpleElementNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found simpleElements to exclude for complexElements not to exclude in schemaName");
			Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementsInSchema = schemaNameComplexElementNameSimpleElementNameToExcludeMap
					.get(schemaName);
			if (simpleElementsInComplexElementsInSchema.containsKey(complexElementName)) {
				logger.debug("found simpleElements to exclude for complexElement not to exclude");
				return simpleElementsInComplexElementsInSchema.get(complexElementName).keySet();
			} else
				logger.debug("didn't find simpleElements to exclude for complexElement not to exclude");
		} else
			logger.debug("didn't find simpleElements to exclude for complexElements not to exclude in schemaName");
		return null;
	}
	*/

	/*
	public Set<String> getAttributeNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName) {
		logger.debug("in getAttributeNamesToExcludeForComplexElementNotToExcludeInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameAttributeNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found attributes to exclude for complexElement not to exclude in schemaName");
			Map<String, Map<String, Attribute>> attributesInComplexElementsInSchema = schemaNameComplexElementNameAttributeNameToExcludeMap
					.get(schemaName);
			if (attributesInComplexElementsInSchema.containsKey(complexElementName)) {
				logger.debug("found attributes to exclude for complexElement not to exclude");
				return attributesInComplexElementsInSchema.get(complexElementName).keySet();
			} else
				logger.debug("didn't find attributes to exclude for complexElement not to exclude");
		} else
			logger.debug("didn't find attributes to exclude for complexElement not to exclude in schemaName");
		return null;
	}
	*/

	/*
	public Set<String> getComplexElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema(String schemaName, String complexElementName) {
		logger.debug("in getComplexElementNamesToExcludeForComplexElementNotToExcludeInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameComplexElementNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found complexElements to exclude for complexElements not to exclude in schemaName");
			Map<String, Map<String, ComplexElement>> complexElementsInComplexElementsInSchema = schemaNameComplexElementNameComplexElementNameToExcludeMap
					.get(schemaName);
			if (complexElementsInComplexElementsInSchema.containsKey(schemaName)) {
				logger.debug("found complexElements to exclude for complexElement");
				return complexElementsInComplexElementsInSchema.get(schemaName).keySet();
			} else
				logger.debug("didn't find complexElements to exclude for complexElement");
		} else
			logger.debug("didn't find complexElements to exclude for complexElements not to exclude in schemaName");
		return null;
	}
	*/

	public boolean excludeComplexElementInSchema(String schemaName, String complexElementName) {
		logger.debug("in excludeComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameToExcludeMap.containsKey(schemaName)) {
			logger.debug("found complexElements to exclude for schemaName");
			Map<String, ComplexElement> complexElementsInSchema = schemaNameComplexElementNameToExcludeMap.get(schemaName);
			return complexElementsInSchema.containsKey(complexElementName);
		} else {
			logger.debug("didn't find complexElements to exclude for schemaName, include all as default");
			return false;
		}
	}

	public boolean excludeSimpleElementInComplexElementInSchema(String schemaName, String complexElementName, String simpleElementName) {
		logger.debug("in excludeSimpleElementInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("simpleElementName: " + simpleElementName);
		if (excludeComplexElementInSchema(schemaName, complexElementName)) {
			logger.debug("complexElement to be excluded ... exclude all of its simpleElements too");
			return true;
		} else {
			logger.debug("complexElement not to be excluded ... check its simpleElements");
			if (schemaNameComplexElementNameSimpleElementNameToExcludeMap.containsKey(schemaName)) {
				logger.debug("found simpleElements to exclude for complexElements not to be excluded for schemaName");
				Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementInSchema = schemaNameComplexElementNameSimpleElementNameToExcludeMap
						.get(schemaName);
				if (simpleElementsInComplexElementInSchema.containsKey(complexElementName)) {
					logger.debug("found simpleElements to be excluded for complexElement not to be excluded");
					Map<String, SimpleElement> simpleElementsInComplexElement = simpleElementsInComplexElementInSchema.get(complexElementName);
					return simpleElementsInComplexElement.containsKey(simpleElementName);
				} else {
					logger.debug("didn't find simpleElements to be excluded for complexElement, include all as default");
					return false;
				}
			} else {
				logger.debug("didn't find simpleElements to exclude for complexElements for schemaName, include all as default");
				return false;
			}
		}
	}

	public boolean excludeAttributeInComplexElementInSchema(String schemaName, String complexElementName, String attributeName) {
		logger.debug("in excludeAttributeInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("attributeName: " + attributeName);
		if (excludeComplexElementInSchema(schemaName, complexElementName)) {
			logger.debug("complexElement to be excluded ... exclude all of its attributes");
			return true;
		} else {
			logger.debug("complexElement not to be excluded ... check its attributes");
			if (schemaNameComplexElementNameAttributeNameToExcludeMap.containsKey(schemaName)) {
				logger.debug("found attributes to be excluded for complexElements in schemaName");
				Map<String, Map<String, Attribute>> attributesInComplexElementInSchema = schemaNameComplexElementNameAttributeNameToExcludeMap
						.get(schemaName);
				if (attributesInComplexElementInSchema.containsKey(complexElementName)) {
					logger.debug("found attributes to exclude for complexElement");
					Map<String, Attribute> attributesInComplexElement = attributesInComplexElementInSchema.get(complexElementName);
					return attributesInComplexElement.containsKey(attributeName);
				} else {
					logger.debug("didn't find attributes to exclude for complexelement, include all as default");
					return false;
				}
			} else {
				logger.debug("didn't find attributes to be excluded for complexElements for schemaName, include all as default");
				return false;
			}
		}
	}

	public boolean excludeComplexElementInComplexElementInSchema(String schemaName, String complexElementName,
			String nameOfComplexElementToCheckForExclusion) {
		logger.debug("in excludeComplexElementInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("nameOfComplexElementToCheckForExclusion: " + nameOfComplexElementToCheckForExclusion);
		if (excludeComplexElementInSchema(schemaName, complexElementName)) {
			logger.debug("complexElement to be excluded ... including all of its child complexelements");
			return true;
		} else {
			logger.debug("complexElement not to be excluded ... check its child complexElements");
			if (schemaNameComplexElementNameComplexElementNameToExcludeMap.containsKey(schemaName)) {
				logger.debug("found complexElements to exclude for complexelement");
				Map<String, Map<String, ComplexElement>> complexElementsInComplexElementInSchema = schemaNameComplexElementNameComplexElementNameToExcludeMap
						.get(schemaName);
				if (complexElementsInComplexElementInSchema.containsKey(complexElementName)) {
					logger.debug("found complexelement to exclude for complexElement");
					Map<String, ComplexElement> complexElementsInComplexElement = complexElementsInComplexElementInSchema.get(complexElementName);
					return complexElementsInComplexElement.containsKey(nameOfComplexElementToCheckForExclusion);
				} else {
					logger.debug("didn't find complexElement to exclude for complexElement, include all as default");
					return false;
				}
			} else {
				logger.debug("didn't find complexelement to exclude for complexelement not to exclude, return all as default");
				return false;
			}
		}
	}

	/*
	public Map<String, ElementsToExclude> getSchemaElementsToExclude() {
		return schemaNameElementsToExcludeMap;
	}
	*/

}
