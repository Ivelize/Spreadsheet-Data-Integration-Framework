package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexicalModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema.ImportRDFSchemaFromXMLServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFModel.ImportRDFModelService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.ImportRDFSchemaFromXMLService;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/*****************************************************************************************
 * Class: Information will be used to populate a simple RDF Model consisting of          *
 * (Class, Property, Predicate, BNode). Then the RDF Model will populate the SUPERMODEL  *
 * with SuperAbstracts, SuperLexicals, SuperRelationships                                *
 *                                                                                       *
 * @author Klitos Christodoulou                                                          *
 *                                       
 * Changes: I have removed toLowerCase()                                                 *
 ****************************************************************************************/

@Service(value = "importRDFModelService")
public class ImportRDFModelServiceImpl implements ImportRDFModelService {

	//TODO: Need to add more support to BNodes.	
	
	//TODO: autowire the repositories 
	
	private SuperRelationshipRepository superRelationshipRepository;
	private ParticipationOfCMCInSuperRelationshipRepository participationRepository;
	
	/*Declaration of Data Structures*/
	private Map<String, RDFClass> nameRDFClassMap;

	static Logger logger = Logger.getLogger(ImportRDFSchemaFromXMLServiceImpl.class);
	
	/*Constructor*/
	public ImportRDFModelServiceImpl() {
		logger.debug("ImportRDFModelServiceImpl");
		nameRDFClassMap = new HashMap<String, RDFClass>();		
	}//end constructor
	
	
	/*Init*/
	public void init(SuperRelationshipRepository superRel,
              		ParticipationOfCMCInSuperRelationshipRepository partRepo) {
		superRelationshipRepository = superRel;
		participationRepository     = partRepo;
	}
	
	
	/**
	 * Method: Populate simple RDFModel to the CanonicalModel
	 */
	public void populateRDFModelToCanonicalModel(ImportRDFSchemaFromXMLService importRDFSchemaFromXMLService, Schema schema) {
		logger.debug("in RDF_populateRDFModelToCanonicalModel");
		
		/*Get the Map <String, RDFClass>*/
		nameRDFClassMap = importRDFSchemaFromXMLService.getNameRDFClassMapMap();
		
		/*Get the list of RDFClass*/
		List<RDFClass> listRDFClass = importRDFSchemaFromXMLService.getRDFClassList();
		
		if (!listRDFClass.isEmpty()) {
			logger.debug("in RDF_listRDFClass");
			
			/*Create SuperAbstracts*/
			Map<String, SuperAbstract> nameSuperAbstractsMap = createSuperAbstractsForRDFClassAndAddToSchema(listRDFClass, schema);
			logger.debug("RDF_nameSuperAbstractsMap: " + nameSuperAbstractsMap);
		
			/*Create SuperLexicals for SuperAbstract*/
			createAndAddSuperLexicalsForSuperAbstracts(nameSuperAbstractsMap, schema);
			
			logger.debug("schema.name: " + schema.getName());
		
			/*Create SuperRelationships for SuperAbstract*/
			createAndAddSuperRelationshipsForSuperAbstracts(nameSuperAbstractsMap, schema);
		}//end if
	}//end populateRDFModelToCanonicalModel()	
	
	
	/***
	 * Method: Create SuperAbstracts for each RDFClass and add them to the schema.
	 * 
	 * Extra property : rdfType, if rdfType = null then this is a BNode
	 * 
	 * @param listRDFClass
	 * @param schema
	 * @return Map<String, SuperAbstract>
	 */
	private Map<String, SuperAbstract> createSuperAbstractsForRDFClassAndAddToSchema(List<RDFClass> listRDFClass, Schema schema) {
		logger.debug("in RDF_createSuperAbstractsForTablesAndAddToSchema");
		
		Map<String, SuperAbstract> nameSuperAbstractsMap = new HashMap<String, SuperAbstract>();
		
		for (RDFClass rdfClass : listRDFClass) {
			/*Get the name of this RDFClass*/
		    String rdfClassName = rdfClass.getClassName();
			logger.debug("RDF_CLASS Name: " + rdfClassName);
			
			/*Create a Map <String,RDFClass>*/
			//nameRDFClassMap.put(rdfClassName, rdfClass);
			
			/*Get the rdf:type URI and store it as an extra namespace property*/
			String rdfs_class_uri = rdfClass.getClassNS();
			logger.debug("RDF_URI of rdf:type " + rdfs_class_uri); 
			
			/*Set of extra properties for this Construct*/
			Set<CanonicalModelProperty> propSet = new LinkedHashSet<CanonicalModelProperty>();
		
			/*Add constructURI (here is called rdfTypeValue) property*/
			boolean a = propSet.add(new CanonicalModelProperty("rdfTypeValue",rdfs_class_uri));

			/*Add namespaceURI of this RDFS Class*/
			String namespaceURI = this.getNamespaceURI(rdfs_class_uri);
			logger.debug("RDF_namespaceURI of this construct: " + namespaceURI); 
			boolean b = propSet.add(new CanonicalModelProperty("namespaceURI",namespaceURI));	
						
			/*Create a new SuperAbstract for that RDFClass (with Properties)*/
			SuperAbstract superAbstract	=  new SuperAbstract(rdfClassName, propSet, schema, SuperAbstractModelSpecificType.RDF_CLASS);
			
			//Add SuperAbstract to Property and make it persistent
			logger.debug("RDF_size of CanonicalModelProperty set: " + propSet.size() + " | boolean A: " + a +" | boolean B: " + b); 
			for (CanonicalModelProperty cmp : propSet) {
				cmp.setPropertyOf(superAbstract);
			}//end for
		
			//Add SuperAbstract to a Map
			nameSuperAbstractsMap.put(rdfClassName, superAbstract);
			
			//TODO: Do I need something like that?
			/*Finally add this SuperAbstract to schema*/
			schema.addCanonicalModelConstruct(superAbstract);			
		}//end for
		
		return nameSuperAbstractsMap;
	}//end createSuperAbstractsForRDFClassAndAddToSchema()

	
	/***
	 * Method: Create SuperAbstracts for each RDFClass and add them to the schema.
	 * 
	 * Extra property : rdfType, if rdfType = null then this is a BNode
	 * 
	 * @param nameSuperAbstractsMap
	 * @param schema
	 */
	private void createAndAddSuperLexicalsForSuperAbstracts(Map<String, SuperAbstract> nameSuperAbstractsMap, Schema schema) {
		logger.debug("in createAndAddSuperLexicalsForSuperAbstract");
		
		/*Get a Set of SuperAbstract names*/
		Set<String> superAbstractNames = nameSuperAbstractsMap.keySet();
		
		/*Loop map of SuperAbstractNames*/
		for (String superAbstractName : superAbstractNames) {
			logger.debug("RDF_superAbstractName: " + superAbstractName);
			SuperAbstract superAbstract = nameSuperAbstractsMap.get(superAbstractName);
			
			//TODO: importSchemaElementsToIncludeFromXmlService, importSchemaElementsToExcludeFromXmlService
			
			/*Get the list of RDF_PROPERTY*/
			List<RDFProperty> listRDFProperty = getPropertyListOfRDFClass(superAbstractName);
			logger.debug("RDF_listRDFProperty.size(): " + listRDFProperty.size());
			
			/*Check if there are any RDF_PROPERTY*/
			if (!listRDFProperty.isEmpty()) {
				
				for (RDFProperty property : listRDFProperty) {
					
					String propName =property.getPropertyName();
					String propNamespaceURI = property.getPropertyNS();
					
					//TODO: Information from schema inference about this property might not be in a human readable form
					// and it might be the case that the property name is the content of rdfs:label. In this case the
					// combination URI+name may result to a URI that does not exist.
					logger.debug("RDF_RDFProperty Name: " + propName);
					logger.debug("RDF_RDFProperty NamespaceURI: " + propNamespaceURI);
					
			        /*Set of extra properties for this Construct*/
					Set<CanonicalModelProperty> propSet = new LinkedHashSet<CanonicalModelProperty>();
								
					/*Add constructURI property of this SuperLexical*/
					String constructURI = createConstructURI(propNamespaceURI, propName);
					propSet.add(new CanonicalModelProperty("constructURI",constructURI));
					
					/*Add namespaceURI property of this SuperLexical*/
					propSet.add(new CanonicalModelProperty("namespaceURI",propNamespaceURI));					
					
					//TODO: Read data type from RDF Literal if exists.
					SuperLexical superLexical = null;
					DataType dataType = DataType.STRING;
					boolean isNullable = true;
					
					/*Create a new SuperLexical*/
					superLexical = new SuperLexical(propName, propSet, schema, dataType, isNullable, SuperLexicalModelSpecificType.RDF_PROPERTY);
					
					//TODO: This might not be the best way to check for unique identifier (ID)
					/*Check whether PropertyName is a key*/
					if (propName.endsWith("ID") || propName.endsWith("_ID") || propName.endsWith("_id")) {
						logger.debug("RDF_found ID");
						superLexical.setIsIdentifier(true);
					} else {
						superLexical.setIsIdentifier(false);
					}//end inner if
					
					//Add SuperLexical to Property and make it persistent
					logger.debug("RDF_size of CanonicalModelProperty set: " + propSet.size()); 
					for (CanonicalModelProperty cmp : propSet) {
						cmp.setPropertyOf(superLexical);
					}//end for
					
					//Add it to SuperAbstract
					superAbstract.addSuperLexical(superLexical);	
					
					//TODO: Do I need something like that?
					//Add it to schema
					schema.addCanonicalModelConstruct(superLexical);
				}//end for				
			}//end RDF_PROPERTY		
		}//end for
	}//end createSuperAbstractsForRDFClassAndAddToSchema()
	
	
	/***
	 * 
	 * @param nameSuperAbstractsMap
	 * @param schema
	 */
	private void createAndAddSuperRelationshipsForSuperAbstracts(Map<String, SuperAbstract> nameSuperAbstractsMap, Schema schema) {
		logger.debug("in createAndAddSuperRelationshipsForSuperAbstracts");
		Set<String> superAbstractNames = nameSuperAbstractsMap.keySet();
		
		for (String superAbstractName : superAbstractNames) {
			SuperAbstract referencingSuperAbstract = nameSuperAbstractsMap.get(superAbstractName);
			logger.debug("RDF_referencingSuperAbstract Name: " + superAbstractName);
			logger.debug("RDF_referencingSuperAbstract: " + referencingSuperAbstract);

			
			/*Check whether referencing SuperAbstract has Predicates*/
			/*Get the list of RDF_PROPERTY*/
			List<RDFPredicate> listRDFPredicate = getPredicateListOfRDFClass(superAbstractName);
			logger.debug("RDF_listRDFPredicate.size(): " + listRDFPredicate.size());
			
			/*Check if there are any RDF_PREDICATE*/
			if (!listRDFPredicate.isEmpty()) {			
				
				for (RDFPredicate predicate : listRDFPredicate) {
				
					/*Check whether Object of Predicate has an RDFClass which is a also a SuperAbstract*/
					if ((predicate.getReferencedObject() != null) && predicate.getReferencedObject() != null) {
						String nameOfReferencedSuperAbstract = predicate.getReferencedObject();
						logger.debug("RDF_referencedSuperAbstract Name: " + nameOfReferencedSuperAbstract);
						
						SuperAbstract referencedSuperAbstract = nameSuperAbstractsMap.get(nameOfReferencedSuperAbstract);
						
						if (referencedSuperAbstract != null) {
							String predName = predicate.getPredicateName();
							String predNamespaceURI = predicate.getPredicateNS();
							
							logger.debug("RDF_referencedSuperAbstract Name:" + predName);
							logger.debug("RDF_referencedSuperAbstract NamespaceURI: " + predNamespaceURI);
							
					        /*Set of extra properties for this Construct*/
							Set<CanonicalModelProperty> propSet = new LinkedHashSet<CanonicalModelProperty>();							
							
							/*Add constructURI property of this SuperLexical*/
							String constructURI = createConstructURI(predNamespaceURI, predName);
							propSet.add(new CanonicalModelProperty("constructURI",constructURI));
							
							/*Add namespaceURI property of this SuperLexical*/
							propSet.add(new CanonicalModelProperty("namespaceURI",predNamespaceURI));
														
							/*Create a new SuperRelationship*/
							SuperRelationship superRelationship = new SuperRelationship(predName, propSet, schema, SuperRelationshipModelSpecificType.RDF_PREDICATE);
		
							//Add SuperLexical to Property and make it persistent
							logger.debug("RDF_size of CanonicalModelProperty set: " + propSet.size()); 
							for (CanonicalModelProperty cmp : propSet) {
								cmp.setPropertyOf(superRelationship);
							}//end for
							
							//TODO: Do I need something like that?
							schema.addCanonicalModelConstruct(superRelationship);

							/*Save SuperRelationship to repository*/
							saveSuperRelationship(superRelationship);
							
							ParticipationOfCMCInSuperRelationship participationOfReferencingSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
									SuperRelationshipRoleType.REFERENCING, superRelationship, referencingSuperAbstract);

							ParticipationOfCMCInSuperRelationship participationOfReferencedSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
									SuperRelationshipRoleType.REFERENCED, superRelationship, referencedSuperAbstract);

							/*Update Repository*/
							saveParticipationOfCMCInSuperRelationship(participationOfReferencingSuperAbstractInSuperRelationship);
							saveParticipationOfCMCInSuperRelationship(participationOfReferencedSuperAbstractInSuperRelationship);							
						}//end if						
					}//end if
				}//end for
			}//end RDF_PREDICATE
		}//end for
	}//end createAndAddSuperRelationshipsForSuperAbstracts()

	
	/***
	 * This method gets a full Resource URI and returns the namespace part.
	 * @param String resourceURI.
	 * return a string representation of the namespace URI that describes the Resource.
	 */
	private String getNamespaceURI(String resourceURI) {
		logger.debug("in RDF_getPropertyListOfRDFClass()");			
		Model tempModel = ModelFactory.createDefaultModel();
        Resource tempRes = tempModel.createResource(resourceURI);			
        return tempRes.getNameSpace();		
	}//end getNamespaceURI()
	
	/***
	 * Return a URI that describes this construct, potentially in a namespace.
	 * @param namespaceURI
	 * @param constructName
	 * @return a String representation of the URI that describes this construct.
	 */
	private String createConstructURI(String namespaceURI, String constructName) {
		constructName = constructName;
		if (namespaceURI.endsWith("/")) {
			  return new String(namespaceURI+constructName);	
			} else if (namespaceURI.endsWith("#")) {
			  return new String(namespaceURI+constructName);	
			} 		
		return new String(namespaceURI+"/"+constructName);
	}//end createConstructURI()
	
	/***
	 * RDF_PROPERTY
	 * @param nameOfRDFClass
	 * @return List<RDFProperty>
	 */
	private List<RDFProperty> getPropertyListOfRDFClass(String nameOfRDFClass) {
		logger.debug("in RDF_getPropertyListOfRDFClass()");		
		return nameRDFClassMap.get(nameOfRDFClass).getRDFProperty();
	}//end getPropertyListOfRDFClass()	
	
	
	/***
	 * RDF_PREDICATE
	 * @param nameOfRDFClass
	 * @return List<RDFPredicate>
	 */
	private List<RDFPredicate> getPredicateListOfRDFClass(String nameOfRDFClass) {
		logger.debug("in RDF_getPropertyListOfRDFClass()");		
		return nameRDFClassMap.get(nameOfRDFClass).getRDFPredicate();
	}//end getPropertyListOfRDFClass()	
	
	
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveSuperRelationship(SuperRelationship superRelationship) {
		logger.debug("in saveSuperRelationship");
		superRelationshipRepository.save(superRelationship);
		superRelationshipRepository.flush();
	}//end saveSuperRelationship()
	
	
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveParticipationOfCMCInSuperRelationship(ParticipationOfCMCInSuperRelationship participation) {
		logger.debug("in saveParticipationOfCMCInSuperRelationship");
		participationRepository.save(participation);
		participationRepository.flush();
	}//end saveSuperRelationship()
}//end class
