package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel.RDFClass;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel.RDFPredicate;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel.RDFProperty;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.ImportRDFSchemaFromXMLService;

/*****************************************************************************************
 * Class: Responsible for reading the RDF schema which is in the Data Structures created *
 * from an XML schema representation. Later this information will be used to populate    *
 * the simple RDF Model (Class, Property, Predicate, BNode). Then the RDF Model will     *
 * populate the SUPERMODEL with SuperAbstracts, SuperLexicals, SuperRelationships        *
 *                                                                                       *
 * @author Klitos Christodoulou                                                          *
 *                                                                                       *
 ****************************************************************************************/

//TODO: Add support for BNodes - WeakEntities and Composite attributes.

@Service(value = "importRDFSchemaFromXMLService")
public class ImportRDFSchemaFromXMLServiceImpl implements ImportRDFSchemaFromXMLService {
	
	static Logger logger = Logger.getLogger(ImportRDFSchemaFromXMLServiceImpl.class);
	
	private Map<String, RDFClass> nameRDFClassMap;
	private List<RDFProperty> nameRDFPropertyList;
	private List<RDFPredicate> nameRDFPredicateList;
	
	/*Constructor*/
	public ImportRDFSchemaFromXMLServiceImpl() {
		nameRDFClassMap       = new HashMap<String, RDFClass>();
		nameRDFPropertyList   = new ArrayList<RDFProperty>();
		nameRDFPredicateList  = new ArrayList<RDFPredicate>();
	}//end constructor
	
	
	/**
	 * 
	 */
	public void readRDFSchemaElementsFromXml(String fileLocation) {
		logger.debug("in readRDFSchemaElementsFromXml");
		logger.debug("RDF_Schema file path: " + fileLocation);
	
		/*Root XML element*/
		RDFEntityTypes rdfEntityTypes = null;
		
		/*Transform data between XML elements and Java objects*/		
		JAXBContext context;
		
		try {
			context = JAXBContext.newInstance("uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema");
			
			/*convert XML data into a tree of Java content objects*/
			Unmarshaller unmarshaller = context.createUnmarshaller();
			rdfEntityTypes = (RDFEntityTypes) unmarshaller.unmarshal(new FileReader(fileLocation));
		} catch (JAXBException e) {
			logger.error("something wrong with xml file containing RDF schema");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("didn't find xml file containing RDF schema");
			e.printStackTrace();
		}
		
		/*Check whether the Root element (RDFEntityType) is not empty*/
		if (rdfEntityTypes != null) {
			
			//CASE 1: RDFEntityType			
			List<RDFEntityType> rdfEntityTypeList = rdfEntityTypes.getRDFEntityType();
		
			/*For each RDFEntityType from the list*/
			for (RDFEntityType rdfEntityType : rdfEntityTypeList) {
				/*Get the name of this RDFEntityType*/
				String rdfEntityTypeName = rdfEntityType.getRDFEntityTypeName();
				
                /*Store pair (entityTypeName, RDFEntityType)*/ 
				//entityTypeNameRDFEntityTypeMap.put(rdfEntityTypeName,rdfEntityType);
				
				/*Process RDFEntityType*/
				processRDFEntityTypeFromXml(rdfEntityTypeName, rdfEntityType);
			}//end for
			
			//CASE 2: RDFWeakEntityType
			//TODO: This is for BNodes, need to implement this later
		}//end if
	}//end readRDFSchemaElementsFromXml()	
	
	
	/***
	 * 
	 * @param rdfEntityTypeName
	 * @param rdfEntityType
	 */
	private void processRDFEntityTypeFromXml(String rdfEntityTypeName, RDFEntityType rdfEntityType) {

		/*Create an empty RDFClass*/
		RDFClass rdfClass = null;
		
		rdfClass = createRDFClassForRDFEntityType(rdfEntityTypeName, rdfEntityType);

		if (rdfClass != null) {
				
			/*Check if there are any RDF_PROPERTY*/
			if (!rdfEntityType.getSimpleAttrOfRDFEntityType().isEmpty()) {
				
				List<SimpleAttrOfRDFEntityType> simpleAttrOfRDFEntityType = rdfEntityType.getSimpleAttrOfRDFEntityType();
				
				for (SimpleAttrOfRDFEntityType simpleAttr : simpleAttrOfRDFEntityType) {
					rdfClass = this.createAndAddPropertiesForRDFClass(rdfClass, simpleAttr.getValue(), simpleAttr.getNamespace());
				}
			}//end if
			
			//TODO: Multi-valued attributes as a BNode Container not having an rdf:type
			
			/*Check if there are any Multi-Valued Attributes & add them as RDF_SIMPLE_ATTRIBUTE/
			if (!rdfEntityType.multiAttrOfRDFEntityType.isEmpty()){
				//TODO: Just add them as simple attributes 
				List<MultiAttrOfRDFEntityType> multiAttrOfRDFEntityType = rdfEntityType.getMultiAttrOfRDFEntityType();
				
				for (MultiAttrOfRDFEntityType attribute : multiAttrOfRDFEntityType) {
					//superAbstract = this.createAndAddSuperLexicalsForSuperAbstract(superAbstract, attribute.getValue(), attribute.getNamespace(), rdfEntityType);
				}
			}

			//TODO: Composite attributes as a BNode Container not having an rdf:type			  			
			 
			/*Check if there are any RDF_COMPO_ATTRIBUTE (From Blank nodes)/
			if (!rdfEntityType.getCompositeAttrOfRDFEntityType().isEmpty()){
				List<CompositeAttrOfRDFEntityType> compositeAttrOfRDFEntityType = rdfEntityType.getCompositeAttrOfRDFEntityType();
			
				for (CompositeAttrOfRDFEntityType attribute : compositeAttrOfRDFEntityType) {
					//createSuperAbstractsForCompositeAttrOfRDFEntityType(attribute);
				}				
			}*/
			
			/*Check if there are any RDF_PREDICATES*/
			if (!rdfEntityType.getRDFBinaryRelationshipType().isEmpty()){
				List<RDFBinaryRelationshipType> binaryRelationshipType = rdfEntityType.getRDFBinaryRelationshipType();				
			
				for (RDFBinaryRelationshipType binaryRel : binaryRelationshipType) {
					rdfClass = this.createAndAddPredicatesForRDFClass(rdfClass, binaryRel.getRDFRelationshipName(), binaryRel.getNamespace(),binaryRel.fromRDFEntityType, binaryRel.toRDFEntityType);
				}			
			}
			
		} else {
			logger.debug("RDF_superAbstract is NULL");
		}
	}//end processRDFEntityTypeFromXml()
	

	/***
	 * Method: Reads the schema <RDFEntityTypeName> element and produces an RDFClass, to be mapped 
	 * later as a SuperAbstract
	 * 
	 * @param rdfEntityTypeName
	 * @param rdfEntityType
	 * @return RDFClass
	 */
	private RDFClass createRDFClassForRDFEntityType(String rdfEntityTypeName, RDFEntityType rdfEntityType) {
		//logger.debug("RDF_CLASS/RDFEntityType to include in schema: " + rdfEntityTypeName);
		
		/*Get the name for the new RDFClass object*/
		String rdfClassName = rdfEntityTypeName;
		//logger.debug("RDFClass Name: " + rdfClassName); 
		
		/*Get the rdf:type URI to be stored as an extra property called namespace*/
		String rdfClassUri = rdfEntityType.getRdftype();
		//logger.debug("RDFClass, rdf:type: " + rdfClassUri); 
		
		/*Hold an RDFClass*/
		RDFClass rdfClass = null;
		
		/*If RDFClass already exists*/
		if (nameRDFClassMap.containsKey(rdfClassName)) {
			//logger.debug("RDF_found RDFClass with name: " + rdfClassName);
			rdfClass = nameRDFClassMap.get(rdfClassName);
			return rdfClass;   
		}	
		
		/*Create a new RDFClass*/
		rdfClass = new RDFClass();
		
		/*Add attributes of this RDFClass*/	
		rdfClass.setClassName(rdfClassName);
		rdfClass.setClassNS(rdfClassUri);
		
		/*Check whether this is a BNode RDFClass*/
		if (rdfClassUri.equals("") || rdfClassUri != null) {
			rdfClass.setBNode(true);
		}
		
		/*Add RDFClass to a Map*/
		nameRDFClassMap.put(rdfClassName, rdfClass);
	
		return rdfClass;
	}//end createRDFClassForRDFEntityType()
	
	
	/***
	 * Method: Reads the schema <SimpleAttrOfRDFEntityType> element and produces an RDFProperty which then add to an RDFClass.
	 * The RDFProperty is later mapped as a SuperLexical
	 * 
	 * @param superAbstract
	 * @param propertyName
	 * @param namespaceURI
	 * @param rdfEntityType
	 * @return RDFClass
	 */
	private RDFClass createAndAddPropertiesForRDFClass(RDFClass rdfClass, String attributeName, String namespaceURI) {
			logger.debug("in createAndAddPropertiesForRDFClass");
			logger.debug("RDF_SimpleAttrOfRDFEntityType: " + attributeName);
			logger.debug("RDF_Attribute Namespace: " + namespaceURI);
						
			/*Create a new RDF Property*/
			RDFProperty rdfProperty = new RDFProperty();
				
			/*Add attributes for this RDF Property*/
			rdfProperty.setPropertyName(attributeName);
			rdfProperty.setPropertyNS(namespaceURI);
				
			//TODO: If information from RDF could state otherwise change type from String to something else
			rdfProperty.setPropertyType("String");
				
			/*Add RDFProperty to a Map*/
			//nameRDFPropertyList.add(rdfProperty);	
				
			/*Add RDFProperty to the RDFClass*/
			rdfClass.getRDFProperty().add(rdfProperty);			
		
		return rdfClass;
	}//end createAndAddPropertiesForRDFClass()
	

	/***
	 * Method: Reads the schema <RDFBinaryRelationshipType> element
	 * 
	 * @param rdfClass
	 * @return RDFClass
	 */
	private RDFClass createAndAddPredicatesForRDFClass(RDFClass rdfClass, String relationshipName, String namespaceURI, String from, String to){
		//logger.debug("in createAndAddPredicatesForRDFClass");
		//logger.debug("RDF_BinaryRelationshipType: " + relationshipName);
		//logger.debug("RDF_Attribute Namespace: " + namespaceURI);		
		
		if ((!from.equals("") && !to.equals("")) || ((from != null) || (to != null))) {
			
			/*Create a new RDF Predicate*/
			RDFPredicate rdfPredicate = new RDFPredicate();
			
			/*Add attributes for this RDFPredicate*/
			rdfPredicate.setPredicateName(relationshipName);
			rdfPredicate.setPredicateNS(namespaceURI);
			
			/*Add the name of the referenced RDFClass*/
			rdfPredicate.setReferencedObject(to);
			
			/*Add RDFPredicate to a Map*/
			//nameRDFPredicateList.add(rdfPredicate);	
			
			/*Add RDFPredicate to the RDFClass*/
			rdfClass.getRDFPredicate().add(rdfPredicate);
		}//end if
	
		return rdfClass;
	}//end createAndAddPredicatesForRDFClass()

	
	/***
     * Return pair <name,RDFClass>
     * 
	 * @return Map<String, RDFClass>
	 */
	public Map<String, RDFClass> getNameRDFClassMapMap() {
		return this.nameRDFClassMap;		
	}

	/***
	 * Return a List representation of the Map.values()
	 *
	 * @return List<RDFClass>
	 */
	public List<RDFClass> getRDFClassList() {
		return new ArrayList<RDFClass>(nameRDFClassMap.values());	
	}
	
}//end class
