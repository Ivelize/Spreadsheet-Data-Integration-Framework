package uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence;

import java.util.Collection;
import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface SchematicCorrespondenceRepository extends GenericRepository<SchematicCorrespondence, Long> {

	/**
	 * Get all canonicalModelConstructs that are involved in given schematic correspondence.
	 * @param schematicCorrespondence
	 * @return canonicalModelConstructs
	 */
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsThatAreInvolvedInSchematicCorrespondence(
			SchematicCorrespondence schematicCorrespondence);

	/**
	 * Get all canonicalModelConstructs of a given group that are involved in given schematic correspondence	
	 * @param groupLabel
	 * @param schematicCorrespondence
	 * @return canonicalModelConstructs
	 */

	/*
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInGroupWithGroupLabelThatAreInvolvedInSchematicCorrespondence(
			GroupLabel groupLabel, SchematicCorrespondence schematicCorrespondence);
	*/

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) in which a given (set of) CanonicalModelConstruct(s) 
	 * (in given schema) is/are involved.
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) in which a given (set of) CanonicalModelConstruct(s) 
	 * (in given schema) is/are involved.
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) in which a given (set of) CanonicalModelConstruct(s) 
	 * (in given schema) is/are involved.
	 * @param schema
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) in which a given (set of) CanonicalModelConstruct(s) (in a given schema) is/are involved.
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) in which a given (set of) CanonicalModelConstruct(s) (in a given schema) is/are involved.
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstruct);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) in which a given (set of) CanonicalModelConstruct(s) (in a given schema) is/are involved.
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param schema
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, Schema schema);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) between two given (set of) CanonicalModelConstruct(s) (in given schema) 
	 * is/are involved. some combinations of constructs not made explicit, use method which takes arbitrary constructs
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) between two given (set of) CanonicalModelConstruct(s) (in given schema) 
	 * is/are involved. some combinations of constructs not made explicit, use method which takes arbitrary constructs
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) between two given (set of) CanonicalModelConstruct(s) (in given schema) 
	 * is/are involved. some combinations of constructs not made explicit, use method which takes arbitrary constructs
	 * @param schema1
	 * @param schema2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			Schema schema1, Schema schema2);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) between two given sets of CanonicalModelConstructs (of two given Schemas)
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) between two given sets of CanonicalModelConstructs (of two given Schemas)
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2);

	/**
	 * Get all Schematic Correspondences (with their parameters and canonicalModelConstructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperAbstract correspondences) between two given sets of CanonicalModelConstructs (of two given Schemas)
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param schema1
	 * @param schema2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, Schema schema1, Schema schema2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param schema
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param schema
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstruct
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) canonicalModelConstruct(s) is involved
	 * @param schematicCorrespondenceType
	 * @param schema
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, Schema schema);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param schema1
	 * @param schema2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			Schema schema1, Schema schema2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param schema1
	 * @param schema2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenConstructsInTwoSchemas(
			Schema schema1, Schema schema2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct1,
			CanonicalModelConstruct canonicalModelConstruct2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs1,
			Collection<CanonicalModelConstruct> canonicalModelConstructs2);

	/**
	 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) canonicalModelConstruct(s)
	 * @param schematicCorrespondenceType
	 * @param schema1
	 * @param schema2
	 * @return schematicCorrespondences
	 */
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			SchematicCorrespondenceType schematicCorrespondenceType, Schema schema1, Schema schema2);
	
	public void addSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence);
	
	public SchematicCorrespondence findSchematicCorrespondenceByName(String schematicCorrespondenceName);
	
	public List<Long> findAllSchematicCorrespondences();
	
	/*
	 * the following two groups of queries aren't provided explicitly (yet), but can be implemented using already provided queries, e.g., by getting all superlexicals of the superabstracts first and then getting the appropriate schematic correspondences for those superLexicals
	 */

	/*
	 * Get all Schematic Correspondences (with their parameters and constructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperLexical correspondences) in which Constructs (e.g., SuperLexicals) that belong to a given (set of) CanonicalModelConstruct (e.g., SuperAbstract) are involved. 
	 */

	/*
	 * Get all Schematic Correspondences (with their parameters and constructs) of a particular canonicalModelConstruct related kind (e.g., 
	 * SuperLexical correspondences) between Constructs (e.g., SuperLexicals) that belong to two given sets of CanonicalModelConstruct (e.g., SuperAbstracts) (in two given Schemas).
	 */
}
