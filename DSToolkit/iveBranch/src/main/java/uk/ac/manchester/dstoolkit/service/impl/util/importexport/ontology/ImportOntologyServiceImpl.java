package uk.ac.manchester.dstoolkit.service.impl.util.importexport.ontology;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipMIDSTSuperModelType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.service.util.importexport.ontology.ImportOntologyService;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/***
 * This class is responsible for mappings an Ontology class hierarchy to DSToolkit's internal representation of a super model. 
 * 
 * Note: At the moment only the Class hierarchy of the ontology is mappes to the super model
 * 
 * @author klitos
 *
 */
@Service(value = "importOntologyService")
public class ImportOntologyServiceImpl implements ImportOntologyService {

	static Logger logger = Logger.getLogger(ImportOntologyServiceImpl.class);
	
	private SuperRelationshipRepository superRelationshipRepository;
	private ParticipationOfCMCInSuperRelationshipRepository participationRepository;
	
	private Schema schema = null;
	private OntModel ontoModel = null;
	
	/*Constructor*/
	public ImportOntologyServiceImpl() {}

	//Initialise the service
	public void init(OntModel ontoModel, Schema schema,
				SuperRelationshipRepository superRel, ParticipationOfCMCInSuperRelationshipRepository partRepo) {
		this.setOntoModel(ontoModel);
		this.setSchema(schema);
		superRelationshipRepository = superRel;
		participationRepository     = partRepo;
	}
	
	//TODO: This method attemps to Map the entire TBox to the supermodel
	public void mapOntologyToSuperModel() {
		
		//Map a TBox class hierarchy to the super model
		this.mapOntologyClassesToSuperModel();

		//TODO: Map ontology roles to the supermodel.		
	}	
	
	/***
	 * This method attempts to map classes from a TBox to the super model as SuperAbstracts
	 */
	public void mapOntologyClassesToSuperModel() {
		logger.debug("in mapOntologyClassesToSuperModel");
				
		//Retrieve the list of Classes that have owl:Thing as their direct parent.
		ExtendedIterator ontClassItr = ontoModel.listHierarchyRootClasses();
		
		while(ontClassItr.hasNext()) {
			OntClass ontoClass = (OntClass) ontClassItr.next();
			
			if (ontoClass != null) {				
				//Filter all Classes that are not anonymous
				if (!ontoClass.isAnon()) {				
					//logger.info("create superAbstract for superClass: " + ontoClass.getLocalName());
					//Create a SuperAbstract and attach it to a DSToolkit Schema object
					SuperAbstract parentSuperAbstract = createSuperAbstract(ontoClass);
					
					//logger.info("created superAbstract for superClass: " + parentSuperAbstract);
					
					//Call to the recursive method for creating SuperAbstract from subClasses
					discoverAndCreateSuperAbstractFromSubClasses(ontoClass, parentSuperAbstract);
					
				}//end if
			}//end if
		}//end while		
	}//end mapOntologyClassesToSuperModel()	
	
	/***
	 * Recursive method, for creating SuperAbstracts and their generalisation relationships
	 * @param superClass
	 */
	public void discoverAndCreateSuperAbstractFromSubClasses(OntClass parentClass, SuperAbstract parentSuperAbstract) {
		logger.info("start recursion");
						
		//Get the direct subclasses of this ontoClass
		ExtendedIterator ontClassItr = parentClass.listSubClasses(true);
		
		//This is the base case of the recursive method
		if (!ontClassItr.hasNext()) {
			return;
		}//end if
						
		while(ontClassItr.hasNext()) {
			OntClass subClass = (OntClass) ontClassItr.next();
			
			if (!subClass.isAnon()) {
				logger.info("create SuperAbstract for subClass: " + subClass.getLocalName());
				SuperAbstract subClassSuperAbstract = createSuperAbstract(subClass);

				SuperRelationship superRelationship = createGeneralisationRelationship(parentClass, subClass);
				
				//save SuperRelationship to repository
				saveSuperRelationship(superRelationship);
				
				//Add which SuperAbstract participate in the SuperRelationship for the specialisation hierarchy
				ParticipationOfCMCInSuperRelationship participationOfParentSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
						SuperRelationshipRoleType.SUPER_CLASS, superRelationship, parentSuperAbstract);
		
				//logger.debug("participationOfParentSuperAbstractInSuperRelationship: " + participationOfParentSuperAbstractInSuperRelationship);
				
				ParticipationOfCMCInSuperRelationship participationOfChildSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
						SuperRelationshipRoleType.SUB_CLASS, superRelationship, subClassSuperAbstract);				

				//logger.debug("participationOfChildSuperAbstractInSuperRelationship: " + participationOfChildSuperAbstractInSuperRelationship);
				
				//update repository with roles fot the SuperRelationship
				saveParticipationOfCMCInSuperRelationship(participationOfParentSuperAbstractInSuperRelationship);
				saveParticipationOfCMCInSuperRelationship(participationOfChildSuperAbstractInSuperRelationship);				
				
				//make a recursive call
				discoverAndCreateSuperAbstractFromSubClasses(subClass, subClassSuperAbstract);
				
			} else {
				return;
			}			
		}//end if	
	}//end method
	
	
	
	/*********************
	 * Supportive methods
	 ********************/
	
	/***
	 * Create a DSToolkit SuperAbstract object
	 */
	public SuperAbstract createSuperAbstract(OntClass ontoClass) {		
		//Hold a reference to the SuperAbstract
		SuperAbstract superAbstract	= null;
		
		//Get the localname of this OWL Class object
		String localName = ontoClass.getLocalName();
		//logger.debug("OWL_CLASS localname: " + localName);
		
		//Get the namespace URI of this OWL Class object
		String namespaceURI = ontoClass.getNameSpace();
		//logger.debug("OWL_CLASS namespace URI: " + namespaceURI);
		
		//Get the Resource of the rdf:type of this. This is will be most of the times owl:Class or rdfs:Class
		com.hp.hpl.jena.rdf.model.Resource res = ontoClass.getRDFType(true);
		
		//Set of extra annotations for this Construct, such as rdf:type information etc.
		Set<CanonicalModelProperty> propSet = new LinkedHashSet<CanonicalModelProperty>();
		
		propSet.add(new CanonicalModelProperty("rdfTypeValue",res.getURI()));			
		propSet.add(new CanonicalModelProperty("namespaceURI",namespaceURI));		
		
		//Create a new SuperAbstract for that OWL_CLASS
		superAbstract	= new SuperAbstract(localName, propSet, schema, SuperAbstractModelSpecificType.OWL_CLASS);
		
		//Attach the annotation properties, namespaceURI and rdfTypeValue to the newlly created SuperAbstract
		for (CanonicalModelProperty cmp : propSet) {
			cmp.setPropertyOf(superAbstract);
		}//end for
			
		return superAbstract;		
	}//end createSuperAsbtract()
	
	/***
	 * Create DSToolkit SuperRelationship for Generalisation
	 */
	public SuperRelationship createGeneralisationRelationship(OntClass parentClass, OntClass subClass) {
		//Hold a reference to the SuperLexical
		SuperRelationship superRelationship	= null;
		
		try {		
			String superRelationshipName = parentClass.getLocalName() + "_" + subClass.getLocalName();
			//logger.info("create SuperRelationship for the hierrarchy: " + superRelationshipName);
				
			superRelationship = new SuperRelationship(superRelationshipName, schema,
        							SuperRelationshipMIDSTSuperModelType.GENERALISATION, SuperRelationshipModelSpecificType.SPECIALISATION_HIERARCHY);
		
			//add the specialisation relationship to the DSToolkit supermodel		
			schema.addCanonicalModelConstruct(superRelationship);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return superRelationship;
	}//end createGeneralisationRelationship()	
		

	/***
	 * Setters / Getters 
	 */
	public OntModel getOntoModel() {
		return ontoModel;
	}

	public void setOntoModel(OntModel ontoModel) {
		this.ontoModel = ontoModel;
	}
	
	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
	/***
	 * Add DSToolkit SuperRelationship to the DSToolkit's Repository
	 * @param superRelationship
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveSuperRelationship(SuperRelationship superRelationship) {
		logger.debug("in saveSuperRelationship");		
		superRelationshipRepository.save(superRelationship);
		//superRelationshipRepository.flush();
	}//end saveSuperRelationship()	
	
	/***
	 * Given a Generalisation SuperRelationship we add the participants i.e., the superClass and subClass 
	 * @param participation
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveParticipationOfCMCInSuperRelationship(ParticipationOfCMCInSuperRelationship participation) {
		logger.debug("in saveParticipationOfCMCInSuperRelationship");
		participationRepository.save(participation);
		//participationRepository.flush();
	}//end saveSuperRelationship()	
}//end class
