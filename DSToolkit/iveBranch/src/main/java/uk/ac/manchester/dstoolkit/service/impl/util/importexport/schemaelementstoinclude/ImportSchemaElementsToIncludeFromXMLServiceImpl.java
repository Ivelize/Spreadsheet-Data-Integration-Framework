/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoinclude;

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

import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoinclude.ImportSchemaElementsToIncludeFromXMLService;

/**
 * @author chedeler
 *
 */
@Service(value = "importSchemaElementsToIncludeFromXMLService")
public class ImportSchemaElementsToIncludeFromXMLServiceImpl implements ImportSchemaElementsToIncludeFromXMLService {

	//TODO currently only checking two levels in XML, i.e., complexElement - simpleElement, complexElement - attribute, complexElement - complezElement
	//TODO for this to work, the complexElement within a complexElement will have to be listed separately as a complexElement
	//TODO this may be ok for global complexElements, but I'm not sure about local complexElements if they have same name as global complexElement
	//TODO still need to think about this a bit more

	//TODO check the processing steps ... I'm including the whole table even if only some columns are included ... change this

	static Logger logger = Logger.getLogger(ImportSchemaElementsToIncludeFromXMLServiceImpl.class);

	private final Map<String, ElementsToInclude> schemaNameElementsToIncludeMap = new HashMap<String, ElementsToInclude>();

	private final Map<String, Map<String, Table>> schemaNameTableNameToIncludeMap = new HashMap<String, Map<String, Table>>();
	private final Map<String, Map<String, Map<String, Column>>> schemaNameTableNameColumnNameToIncludeMap = new HashMap<String, Map<String, Map<String, Column>>>();

	private final Map<String, Map<String, ComplexElement>> schemaNameComplexElementNameToIncludeMap = new HashMap<String, Map<String, ComplexElement>>();
	private final Map<String, Map<String, Map<String, SimpleElement>>> schemaNameComplexElementNameSimpleElementNameToIncludeMap = new HashMap<String, Map<String, Map<String, SimpleElement>>>();
	private final Map<String, Map<String, Map<String, Attribute>>> schemaNameComplexElementNameAttributeNameToIncludeMap = new HashMap<String, Map<String, Map<String, Attribute>>>();
	private final Map<String, Map<String, Map<String, ComplexElement>>> schemaNameComplexElementNameComplexElementNameToIncludeMap = new HashMap<String, Map<String, Map<String, ComplexElement>>>();

	public void readSchemaElementsToIncludeFromXml(String fileLocation) {
		logger.debug("in readSchemaElementsToIncludeFromXml");
		logger.debug("fileLocation: " + fileLocation);
		Schemas schemas = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoinclude");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			schemas = (Schemas) unmarshaller.unmarshal(new FileReader(fileLocation));
		} catch (JAXBException e) {
			logger.error("something wrong with xml file containing schema elements to include");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("didn't find xml file containing schema elements to include");
			e.printStackTrace();
		}
		if (schemas != null) {
			List<Schema> schemaList = schemas.getSchema();
			for (Schema schema : schemaList) {
				String schemaName = schema.getName();
				ElementsToInclude elementsToInclude = schema.getElementsToInclude();
				schemaNameElementsToIncludeMap.put(schemaName, elementsToInclude);
				processSchemaElementsToIncludeFromXml(schemaName, elementsToInclude);
			}
		}
	}

	private void processSchemaElementsToIncludeFromXml(String schemaName, ElementsToInclude elementsToInclude) {
		logger.debug("in processSchemaElementsToIncludeFromXml");
		logger.debug("schemaName: " + schemaName);
		logger.debug("elementsToInclude: " + elementsToInclude);
		List<Table> tables = elementsToInclude.getTable();
		logger.debug("tables: " + tables);
		if (tables != null && tables.size() > 0)
			processRelationalSchemaElementsToInclude(schemaName, tables);
		List<ComplexElement> complexElements = elementsToInclude.getComplexElement();
		logger.debug("complexElements: " + complexElements);
		if (complexElements != null & complexElements.size() > 0)
			processXmlSchemaElementsToInclude(schemaName, complexElements);
	}

	private void processRelationalSchemaElementsToInclude(String schemaName, List<Table> tables) {
		logger.debug("in processRelationalSchemaElementsToInclude");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tables: " + tables);
		Map<String, Table> tablesToCheckForInclusion = new HashMap<String, Table>();
		Map<String, Map<String, Column>> columnsInTableToCheckForInclusion = new HashMap<String, Map<String, Column>>();
		for (Table table : tables) {
			logger.debug("table: " + table);
			Map<String, Column> columnsToCheckForInclusion = new HashMap<String, Column>();
			String tableName = table.getName();
			logger.debug("tableName: " + tableName);
			logger.debug("table.isInclude: " + table.isInclude());
			if (table.isInclude()) {
				logger.debug("table to be included, tableName: " + tableName);

				logger.debug("check for columns, as not all of them might be included, if none listed, all will be included as default");
				List<Column> columns = table.getColumn();
				logger.debug("columns: " + columns);
				if (columns != null && columns.size() > 0) {
					for (Column column : columns) {
						logger.debug("column.getName(): " + column.getName());
						logger.debug("column.isInclude: " + column.isInclude());
						if (column.isInclude()) {
							logger.debug("found column to include, columnName: " + column.getName());
							logger.debug("add column for table to check for inclusion");
							columnsToCheckForInclusion.put(column.getName(), column);
							logger.debug("check whether table is already included, if not, add table");
							if (!tablesToCheckForInclusion.containsKey(tableName))
								tablesToCheckForInclusion.put(tableName, table);
						} else {
							logger.debug("column not to be inluded, ignore, columnName: " + column.getName());
						}
					}
				} else {
					logger.debug("tableName: " + tableName);
					logger.debug("table to be included and no columns listed, include all columns as default");
					tablesToCheckForInclusion.put(tableName, table);
				}
			} else {
				logger.debug("table with all its columns not to be included, ignore table and columns");
			}
			if (!columnsToCheckForInclusion.isEmpty()) {
				logger.debug("found columns to include for table, tableName: " + tableName);
				columnsInTableToCheckForInclusion.put(tableName, columnsToCheckForInclusion);
			}
		}
		if (!tablesToCheckForInclusion.isEmpty()) {
			logger.debug("found tables to include for schema, schemaName: " + schemaName);
			schemaNameTableNameToIncludeMap.put(schemaName, tablesToCheckForInclusion);
		}
		if (!columnsInTableToCheckForInclusion.isEmpty()) {
			logger.debug("found columns in tables of schema to include, schemaName: " + schemaName);
			schemaNameTableNameColumnNameToIncludeMap.put(schemaName, columnsInTableToCheckForInclusion);
		}
	}

	/*
	public Map<String, Table> getTablesToIncludeForRelationalSchema(String schemaName) {
		logger.debug("in getTablesToIncludeForRelationalSchema");
		logger.debug("schemaName: " + schemaName);
		return schemaNameTableNameToIncludeMap.get(schemaName);
	}
	*/

	/*
	public Set<String> getColumnNamesToIncludeForTableToIncludeInRelationalSchema(String schemaName, String tableName) {
		//TODO Warning: if columns aren't listed separately, i.e., all columns are to be included as default, this method here will return null
		//TODO better to use includeColumnInTableInSchema for each columnName
		logger.debug("in getColumnNamesToIncludeForTableInRelationalSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		if (schemaNameTableNameColumnNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found columns to include for tables to include in schemaName");
			Map<String, Map<String, Column>> columnsInTableInSchema = schemaNameTableNameColumnNameToIncludeMap.get(schemaName);
			if (columnsInTableInSchema.containsKey(tableName)) {
				logger.debug("found columns to include for tableName");
				return columnsInTableInSchema.get(tableName).keySet();
			} else
				logger.debug("didn't find columns to include for tableName");
		} else
			logger.debug("didn't find columns to include for tables to include in schemaName");
		return null;
	}
	*/

	public boolean includeTableInSchema(String schemaName, String tableName) {
		logger.debug("in includeTableInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		if (schemaNameTableNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found tables to include for schemaName");
			Map<String, Table> tablesInSchema = schemaNameTableNameToIncludeMap.get(schemaName);
			return tablesInSchema.containsKey(tableName);
		} else {
			logger.debug("didn't find tables to include for schemaName, include all as default");
			return true;
		}
	}

	//TODO test this properly
	public boolean includeColumnInTableInSchema(String schemaName, String tableName, String columnName) {
		logger.debug("in includeColumnInTableInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("tableName: " + tableName);
		logger.debug("columnName: " + columnName);

		if (schemaNameTableNameColumnNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found columnNames to include for tableNames in schemaName");
			Map<String, Map<String, Column>> columnsInTableInSchema = schemaNameTableNameColumnNameToIncludeMap.get(schemaName);
			logger.debug("columnsInTableInSchema: " + columnsInTableInSchema);
			if (columnsInTableInSchema.containsKey(tableName)) {
				logger.debug("found columnNames to include for tableName");
				Map<String, Column> columnsInTable = columnsInTableInSchema.get(tableName);
				logger.debug("columnsInTable: " + columnsInTable);
				return columnsInTable.containsKey(columnName);
			} else
				logger.debug("didn't find columnNames to include for tableName, check whether whole table is to be included");
		} else
			logger.debug("didn't find columnNames to include for tableName in schemaName, check whether whole table is to be included");

		logger.debug("checking whether whole table is to be included");
		return includeTableInSchema(schemaName, tableName);
	}

	private void processXmlSchemaElementsToInclude(String schemaName, List<ComplexElement> complexElements) {
		logger.debug("in processXmlSchemaElementsToInclude");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElements: " + complexElements);
		Map<String, ComplexElement> complexElementsToCheckForInclusion = new HashMap<String, ComplexElement>();
		Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementToCheckForInclusion = new HashMap<String, Map<String, SimpleElement>>();
		Map<String, Map<String, Attribute>> attributesInComplexElementToCheckForInclusion = new HashMap<String, Map<String, Attribute>>();
		Map<String, Map<String, ComplexElement>> complexElementsInComplexElementToCheckForInclusion = new HashMap<String, Map<String, ComplexElement>>();

		for (ComplexElement complexElement : complexElements) {
			logger.debug("complexElement: " + complexElement);
			Map<String, SimpleElement> simpleElementsToCheckForInclusion = new HashMap<String, SimpleElement>();
			Map<String, Attribute> attributesToCheckForInclusion = new HashMap<String, Attribute>();
			Map<String, ComplexElement> complexElementsToCheckForInclusionInComplexElement = new HashMap<String, ComplexElement>();
			String complexElementName = complexElement.getName();
			logger.debug("complexElementName: " + complexElementName);
			logger.debug("complexElement.isInclude(): " + complexElement.isInclude());
			if (complexElement.isInclude()) {
				logger.debug("complexElement to be included, complexElementName: " + complexElementName);
				logger.debug("check for elements within complexElement, as not all of them may be included");
				List<SimpleElement> simpleElements = complexElement.getSimpleElement();
				logger.debug("simpleElements: " + simpleElements);
				if (simpleElements != null && simpleElements.size() > 0) {
					for (SimpleElement simpleElement : simpleElements) {
						logger.debug("simpleElement.getName(): " + simpleElement.getName());
						logger.debug("simpleElement.isInclude(): " + simpleElement.isInclude());
						if (simpleElement.isInclude()) {
							logger.debug("found simpleElement to include, simpleElement.getName(): " + simpleElement.getName());
							logger.debug("add simpleElement for complexElement to include");
							simpleElementsToCheckForInclusion.put(simpleElement.getName(), simpleElement);
							logger.debug("check whether complexelement is already included, if not add it");
							if (complexElementsToCheckForInclusion.containsKey(complexElementName))
								complexElementsToCheckForInclusion.put(complexElementName, complexElement);
						} else {
							logger.debug("simpleElement not to be included, ignore, simpleElement.getName: " + simpleElement.getName());
						}
					}
				} else {
					logger.debug("complexElement to be included but no simpleElements found, all simpleElements to be included as default");
					logger.debug("check whether complexelement is already included, if not add it");
					if (complexElementsToCheckForInclusion.containsKey(complexElementName))
						complexElementsToCheckForInclusion.put(complexElementName, complexElement);
				}
				if (!simpleElementsToCheckForInclusion.isEmpty()) {
					logger.debug("found simpleElements to include for complexElement, complexElementName: " + complexElementName);
					simpleElementsInComplexElementToCheckForInclusion.put(complexElementName, simpleElementsToCheckForInclusion);
				}

				List<Attribute> attributes = complexElement.getAttribute();
				logger.debug("attributes: " + attributes);
				if (attributes != null & attributes.size() > 0) {
					for (Attribute attribute : attributes) {
						logger.debug("attribute.getName(): " + attribute.getName());
						logger.debug("attribute.isInclude(): " + attribute.isInclude());
						if (attribute.isInclude()) {
							logger.debug("found attribute to include, attribute.getName(): " + attribute.getName());
							logger.debug("add attribute for complexElement to include");
							attributesToCheckForInclusion.put(attribute.getName(), attribute);
							logger.debug("check whether complexelement is already included, if not add it");
							if (complexElementsToCheckForInclusion.containsKey(complexElementName))
								complexElementsToCheckForInclusion.put(complexElementName, complexElement);
						} else {
							logger.debug("attribute not to be included, ignore, attribute.getName(): " + attribute.getName());
						}
					}
				} else {
					logger.debug("complexElement to be included but no attributes found, all attributes to be included as default");
					logger.debug("check whether complexelement is already included, if not add it");
					if (complexElementsToCheckForInclusion.containsKey(complexElementName))
						complexElementsToCheckForInclusion.put(complexElementName, complexElement);
				}
				if (!attributesToCheckForInclusion.isEmpty()) {
					logger.debug("found attributes to include for complexElement, complexElementName: " + complexElementName);
					attributesInComplexElementToCheckForInclusion.put(complexElementName, attributesToCheckForInclusion);
				}

				List<ComplexElement> complexElementsInComplexElement = complexElement.getComplexElement();
				logger.debug("complexElementsInComplexElement: " + complexElementsInComplexElement);
				if (complexElementsInComplexElement != null && complexElementsInComplexElement.size() > 0) {
					for (ComplexElement complexElementInComplexElement : complexElementsInComplexElement) {
						logger.debug("complexElementInComplexelement.getName(): " + complexElementInComplexElement.getName());
						logger.debug("complexElementInComplexElement.isInclude(): " + complexElementInComplexElement.isInclude());
						if (complexElementInComplexElement.isInclude()) {
							logger.debug("found complexElementInComplexElement to include, complexElementInComplexElement.getName(): "
									+ complexElementInComplexElement);
							logger.debug("add complexElementInComplexElement for complexElement to include");
							complexElementsToCheckForInclusionInComplexElement.put(complexElementInComplexElement.getName(),
									complexElementInComplexElement);
							logger.debug("check whether complexelement is already included, if not add it");
							if (complexElementsToCheckForInclusion.containsKey(complexElementName))
								complexElementsToCheckForInclusion.put(complexElementName, complexElement);
						} else {
							logger.debug("complexElementInComplexElement not to be included, ignore, complexElementInComplexElement.getName(): "
									+ complexElementInComplexElement.getName());
						}
					}
				} else {
					logger.debug("complexElement to be excluded but no child complexElements found, all child complexElements to be included as default");
					logger.debug("check whether complexelement is already included, if not add it");
					if (complexElementsToCheckForInclusion.containsKey(complexElementName))
						complexElementsToCheckForInclusion.put(complexElementName, complexElement);
				}
				if (!complexElementsToCheckForInclusionInComplexElement.isEmpty()) {
					logger.debug("found complexElements in complexElement to include, complexElementName: " + complexElementName);
					complexElementsInComplexElementToCheckForInclusion.put(complexElementName, complexElementsToCheckForInclusionInComplexElement);
				}
			} else {
				logger.debug("complexElement not to be included, ignore its child elements,as all exluded by default, complexElementName: "
						+ complexElementName);

			}
		}
		if (!complexElementsToCheckForInclusion.isEmpty()) {
			logger.debug("found complexElements to include in schema, schemaName: " + schemaName);
			schemaNameComplexElementNameToIncludeMap.put(schemaName, complexElementsToCheckForInclusion);
		}
		if (!simpleElementsInComplexElementToCheckForInclusion.isEmpty()) {
			logger.debug("found simpleElements in complexElements of schema to include, schemaName: " + schemaName);
			schemaNameComplexElementNameSimpleElementNameToIncludeMap.put(schemaName, simpleElementsInComplexElementToCheckForInclusion);
		}
		if (!attributesInComplexElementToCheckForInclusion.isEmpty()) {
			logger.debug("found attributes in complexElements of schema to include, schemaName: " + schemaName);
			schemaNameComplexElementNameAttributeNameToIncludeMap.put(schemaName, attributesInComplexElementToCheckForInclusion);
		}
		if (!complexElementsInComplexElementToCheckForInclusion.isEmpty()) {
			logger.debug("found complexElements in complexElements of schema to include, schemaName: " + schemaName);
			schemaNameComplexElementNameComplexElementNameToIncludeMap.put(schemaName, complexElementsInComplexElementToCheckForInclusion);
		}
	}

	/*
	public Map<String, ComplexElement> getComplexElementsToIncludeForXmlSchema(String schemaName) {
		logger.debug("in getComplexElementsToIncludeForXmlSchema");
		logger.debug("schemaName: " + schemaName);
		return schemaNameComplexElementNameToIncludeMap.get(schemaName);
	}
	*/

	/*
	public Set<String> getSimpleElementNamesToIncludeForComplexElementInXmlSchema(String schemaName, String complexElementName) {
		//TODO Warning: if simpleElements aren't listed separately, i.e., all simpleElements are to be included as default, this method here will return null
		//TODO better to use includeSimpleElementInComplexElementInSchema for each simpleElement name
		logger.debug("in getSimpleElementNamesToIncludeForComplexElementInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameSimpleElementNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found simpleElements to include for complexelements in schemaName");
			Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementsInSchema = schemaNameComplexElementNameSimpleElementNameToIncludeMap
					.get(schemaName);
			if (simpleElementsInComplexElementsInSchema.containsKey(complexElementName)) {
				logger.debug("found simpleElements to include for complexelement");
				return simpleElementsInComplexElementsInSchema.get(complexElementName).keySet();
			} else
				logger.debug("didn't find simpleElements to include for complexelement");
		} else
			logger.debug("didn't find simpleElements to include for complexelements in schemaName");
		return null;
	}
	*/

	/*
	public Set<String> getAttributeNamesToIncludeForComplexElementInXmlSchema(String schemaName, String complexElementName) {
		//TODO Warning: if attributes aren't listed separately, i.e., all attributes are to be included as default, this method here will return null
		//TODO better to use includeAttributeInComplexElementInSchema for each attribute name
		logger.debug("in getSimpleElementNamesToIncludeForComplexElementInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameAttributeNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found attributes to included for complexelements in schemaName");
			Map<String, Map<String, Attribute>> attributesInComplexElementsInSchema = schemaNameComplexElementNameAttributeNameToIncludeMap
					.get(schemaName);
			if (attributesInComplexElementsInSchema.containsKey(complexElementName)) {
				logger.debug("found attributes to include for complexelement");
				return attributesInComplexElementsInSchema.get(complexElementName).keySet();
			} else
				logger.debug("didn't find attributes to include for complexelement");
		} else
			logger.debug("didn't find attributes to include for complexelements in schemaName");
		return null;
	}
	*/

	/*
	public Set<String> getComplexElementNamesToIncludeForComplexElementInXmlSchema(String schemaName, String complexElementName) {
		//TODO Warning: if complexElements aren't listed separately, i.e., all complexElements are to be included as default, this method here will return null
		//TODO better to use includeComplexElementInComplexElementInSchema for each complexElement name
		logger.debug("in getComplexElementNamesToIncludeForComplexElementInXmlSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameComplexElementNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found complexelements to include for complexelement in schemaName");
			Map<String, Map<String, ComplexElement>> complexElementsInComplexElementsInSchema = schemaNameComplexElementNameComplexElementNameToIncludeMap
					.get(schemaName);
			if (complexElementsInComplexElementsInSchema.containsKey(schemaName)) {
				logger.debug("found complexelements to include for complexelement");
				return complexElementsInComplexElementsInSchema.get(schemaName).keySet();
			} else
				logger.debug("didn't find complexelement to include for complexelement");
		} else
			logger.debug("didn't find complexelement to include for complexelement in schemaName");
		return null;
	}
	*/

	public boolean includeComplexElementInSchema(String schemaName, String complexElementName) {
		logger.debug("in includeComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		if (schemaNameComplexElementNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found complexelements to include for schemaName");
			Map<String, ComplexElement> complexElementsInSchema = schemaNameComplexElementNameToIncludeMap.get(schemaName);
			return complexElementsInSchema.containsKey(complexElementName);
		} else {
			logger.debug("didn't find complexElements to include for schemaName, include all complexelements as default");
			return true;
		}
	}

	public boolean includeSimpleElementInComplexElementInSchema(String schemaName, String complexElementName, String simpleElementName) {
		logger.debug("in includeSimpleElementInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("simpleElementName: " + simpleElementName);
		if (schemaNameComplexElementNameSimpleElementNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found simpleElements to include for complexelements in schemaName");
			Map<String, Map<String, SimpleElement>> simpleElementsInComplexElementInSchema = schemaNameComplexElementNameSimpleElementNameToIncludeMap
					.get(schemaName);
			logger.debug("simpleElementsInComplexElementInSchema: " + simpleElementsInComplexElementInSchema);
			if (simpleElementsInComplexElementInSchema.containsKey(complexElementName)) {
				logger.debug("found simpleelements to include for complexelement");
				Map<String, SimpleElement> simpleElementsInComplexElement = simpleElementsInComplexElementInSchema.get(complexElementName);
				return simpleElementsInComplexElement.containsKey(simpleElementName);
			} else
				logger.debug("didn't find simpleElements to include for complexelement");
		} else
			logger.debug("didn't find simpleElements to include for complexelements in schemaName");

		logger.debug("checking whether whole complexelement is to be included");
		return includeComplexElementInSchema(schemaName, complexElementName);
	}

	public boolean includeAttributeInComplexElementInSchema(String schemaName, String complexElementName, String attributeName) {
		logger.debug("in includeAttributeInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("attributeName: " + attributeName);
		if (schemaNameComplexElementNameAttributeNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found attributes to include for complexelements in schemaName");
			Map<String, Map<String, Attribute>> attributesInComplexElementInSchema = schemaNameComplexElementNameAttributeNameToIncludeMap
					.get(schemaName);
			logger.debug("attributesInComplexElementInSchema: " + attributesInComplexElementInSchema);
			if (attributesInComplexElementInSchema.containsKey(complexElementName)) {
				logger.debug("found attributes for complexElement");
				Map<String, Attribute> attributesInComplexElement = attributesInComplexElementInSchema.get(complexElementName);
				return attributesInComplexElement.containsKey(attributeName);
			} else
				logger.debug("didn't find attributes for complexelement");
		} else
			logger.debug("didn't find attributes to include for complexelements in schemaName");

		logger.debug("checking whether whole complexelement is to be included");
		return includeComplexElementInSchema(schemaName, complexElementName);
	}

	public boolean includeComplexElementInComplexElementInSchema(String schemaName, String complexElementName,
			String nameOfComplexElementToCheckForExclusion) {
		logger.debug("in includeComplexElementInComplexElementInSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("complexElementName: " + complexElementName);
		logger.debug("nameOfComplexElementToCheckForExclusion: " + nameOfComplexElementToCheckForExclusion);
		if (schemaNameComplexElementNameComplexElementNameToIncludeMap.containsKey(schemaName)) {
			logger.debug("found complexelements to include for complexelements in schemaName");
			Map<String, Map<String, ComplexElement>> complexElementsInComplexElementInSchema = schemaNameComplexElementNameComplexElementNameToIncludeMap
					.get(schemaName);
			logger.debug("complexElementsInComplexElementInSchema: " + complexElementsInComplexElementInSchema);
			if (complexElementsInComplexElementInSchema.containsKey(complexElementName)) {
				logger.debug("found complexelements to include for complexelement");
				Map<String, ComplexElement> complexElementsInComplexElement = complexElementsInComplexElementInSchema.get(complexElementName);
				return complexElementsInComplexElement.containsKey(nameOfComplexElementToCheckForExclusion);
			} else
				logger.debug("didn't find complexelements to include for complexelement");
		} else
			logger.debug("didn't find complexElements to include for complexElements in schemaName");

		logger.debug("checking whether whole complexelement is to be included");
		return includeComplexElementInSchema(schemaName, complexElementName);
	}

	/*
	public Map<String, ElementsToInclude> getSchemaElementsToInclude() {
		return schemaNameElementsToIncludeMap;
	}
	 */
}
