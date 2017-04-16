package uk.ac.manchester.dstoolkit.repository.impl.hibernate.morphisms.schematiccorrespondence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.Parameter;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;


/**
 * @author chedeler
 *
 */

@Repository(value = "schematicCorrespondenceRepository")
public class HibernateSchematicCorrespondenceRepository extends HibernateGenericRepository<SchematicCorrespondence, Long> implements
		SchematicCorrespondenceRepository {

	protected static Logger logger = Logger.getLogger(HibernateSchematicCorrespondenceRepository.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	private final List<SchematicCorrespondenceType> primary = new ArrayList<SchematicCorrespondenceType>();
	private final List<SchematicCorrespondenceType> secondary = new ArrayList<SchematicCorrespondenceType>();

	/**
	 * 
	 */
	private void initPrimarySchematicCorrespondences() {
		primary.add(SchematicCorrespondenceType.DIFFERENT_NAME_DIFFERENT_CONSTRUCT);
		primary.add(SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
		primary.add(SchematicCorrespondenceType.SAME_NAME_DIFFERENT_CONSTRUCT);
		primary.add(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
	}

	/**
	 * 
	 */
	private void initSecondarySchematicCorrespondences() {
		secondary.add(SchematicCorrespondenceType.DIFFERENT_TYPE);
		secondary.add(SchematicCorrespondenceType.MISSING_SUPER_LEXICAL);
	}

	/**
	 * Helper method: fetch parameters and constructs for collection of schematic correspondences
	 * @param schematicCorrespondences
	 */
	@Transactional(readOnly = true)
	private void fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(
			Collection<SchematicCorrespondence> schematicCorrespondences) {
		logger.debug("in fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences");
		for (SchematicCorrespondence schematicCorrespondence : schematicCorrespondences) {
			logger.debug("schematicCorrespondence: " + schematicCorrespondence);

			Set<Parameter> parameters = schematicCorrespondence.getParameters();
			for (Parameter parameter : parameters)
				logger.debug("Parameter: " + parameter);

			Set<CanonicalModelConstruct> constructs1 = schematicCorrespondence.getConstructs1();
			constructs1.size();
			for (CanonicalModelConstruct construct : constructs1) {
				logger.debug("constructs1");
				if (construct instanceof SuperAbstract)
					logger.debug("construct is SuperAbstract");
				else if (construct instanceof SuperLexical)
					logger.debug("construct is SuperLexical");
				else if (construct instanceof SuperRelationship)
					logger.debug("construct is SuperRelationship");
				logger.debug("construct: " + construct);
			}
			Set<CanonicalModelConstruct> constructs2 = schematicCorrespondence.getConstructs2();
			constructs2.size();
			for (CanonicalModelConstruct construct : constructs2) {
				logger.debug("constructs2");
				if (construct instanceof SuperAbstract)
					logger.debug("construct is SuperAbstract");
				else if (construct instanceof SuperLexical)
					logger.debug("construct is SuperLexical");
				else if (construct instanceof SuperRelationship)
					logger.debug("construct is SuperRelationship");
				logger.debug("construct: " + construct);
			}
		}
	}

	/**
	 * @param schematicCorrespondence
	 * @return canonicalModelConstructs
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<CanonicalModelConstruct> getAllCanonicalModelConstructsThatAreInvolvedInSchematicCorrespondenceQuery(
			SchematicCorrespondence schematicCorrespondence) {
		Query query = em.createQuery("select c from Construct c " + "join fetch c.applicationOfSchematicCorrespondenceToConstructs ap "
				+ "join fetch ap.schematicCorrespondence sc " + "where sc.id = :schematicCorrespondenceId");
		query.setParameter("schematicCorrespondenceId", schematicCorrespondence.getId());
		try {
			List<CanonicalModelConstruct> list = query.getResultList();
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllCanonicalModelConstructsThatAreInvolvedInSchematicCorrespondence(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondence)
	 */
	@Transactional(readOnly = true)
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsThatAreInvolvedInSchematicCorrespondence(
			SchematicCorrespondence schematicCorrespondence) {
		List<CanonicalModelConstruct> list = getAllCanonicalModelConstructsThatAreInvolvedInSchematicCorrespondenceQuery(schematicCorrespondence);
		return list;
	}

	/**
	 * @param groupLabel
	 * @param schematicCorrespondence
	 * @return canonicalModelConstructs
	 */
	/*
	@SuppressWarnings("unchecked")
	private List<CanonicalModelConstruct> getAllCanonicalModelConstructsInGroupWithGroupLabelThatAreInvolvedInSchematicCorrespondenceQuery(
			GroupLabel groupLabel, SchematicCorrespondence schematicCorrespondence) {
		Query query = em.createQuery("select c from Construct c " + "join fetch c.applicationOfSchematicCorrespondenceToConstructs ap "
				+ "join fetch ap.schematicCorrespondence sc " + "where sc.id = :schematicCorrespondenceId " + "and ap.groupLabel = :groupLabel");
		query.setParameter("schematicCorrespondenceId", schematicCorrespondence.getId()).setParameter("groupLabel", groupLabel);
		try {
			List<CanonicalModelConstruct> list = query.getResultList();
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllCanonicalModelConstructsInGroupWithGroupLabelThatAreInvolvedInSchematicCorrespondence(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.GroupLabel, uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondence)
	 */
	/*
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInGroupWithGroupLabelThatAreInvolvedInSchematicCorrespondence(
			GroupLabel groupLabel, SchematicCorrespondence schematicCorrespondence) {
		List<CanonicalModelConstruct> list = getAllCanonicalModelConstructsInGroupWithGroupLabelThatAreInvolvedInSchematicCorrespondenceQuery(
				groupLabel, schematicCorrespondence);
		return list;
	}
	*/

	/**
	 * @param canonicalModelConstruct
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct, boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c " + "where c.id = :constructId");
		query.setParameter("constructId", canonicalModelConstruct.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesInWhichCanonicalModelConstructIsInvolved(canonicalModelConstruct, true);
		return list;
	}

	/**
	 * @param canonicalModelConstructs
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesInWhichACollectionOfCanonicalModelConstructIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs, boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c "
				+ "where c in (:constructCollection)");
		query.setParameter("constructCollection", canonicalModelConstructs);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesInWhichACollectionOfCanonicalModelConstructIsInvolved(
				canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema) {
		List<CanonicalModelConstruct> canonicalModelConstructs = schemaRepository.getAllCanonicalModelConstructsInSchema(schema);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesInWhichACollectionOfCanonicalModelConstructIsInvolved(
				canonicalModelConstructs, true);
		return list;
	}

	/**
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstruct
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfConstructRelatedTypeInWhichCanonicalModelConstructIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c " + "where c.id = :constructId "
				+ "and sc.constructRelatedSchematicCorrespondenceType = :constructRelatedSchematicCorrespondenceType");
		query.setParameter("constructRelatedSchematicCorrespondenceType", constructRelatedSchematicCorrespondenceType).setParameter("constructId",
				canonicalModelConstruct.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeInWhichCanonicalModelConstructIsInvolved(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstruct, true);
		return list;
	}

	/**
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstructs
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfConstructRelatedTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstructs, boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c "
				+ "where sc.constructRelatedSchematicCorrespondenceType = :constructRelatedSchematicCorrespondenceType "
				+ "and c in (:constructCollection)");
		query.setParameter("constructRelatedSchematicCorrespondenceType", constructRelatedSchematicCorrespondenceType).setParameter(
				"constructCollection", canonicalModelConstructs);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstructs) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, Schema schema) {
		List<CanonicalModelConstruct> canonicalModelConstructs = schemaRepository.getAllCanonicalModelConstructsInSchema(schema);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstructs, true);
		return list;
	}

	/**
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2, boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.construct c1 "
				+ "where c1.id = :constructId1 " + "and sc1 in ( " + "select sc2 from SchematicCorrespondence sc2 "
				+ "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 " + "join ap2.construct c2 " + "where c2.id = :constructId2)");
		query.setParameter("constructId1", canonicalModelConstruct1.getId()).setParameter("constructId2", canonicalModelConstruct2.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesBetweenTwoCanonicalModelConstructs(canonicalModelConstruct1,
				canonicalModelConstruct2, true);
		return list;
	}

	/**
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.construct c1 "
				+ "where c1 in (:constructCollection1) " + "and sc1 in ( " + "select sc2 from SchematicCorrespondence sc2 "
				+ "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 " + "join ap2.construct c2 "
				+ "where c2 in (:constructCollection2))");
		query.setParameter("constructCollection1", canonicalModelConstructs1).setParameter("constructCollection2", canonicalModelConstructs2);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(java.util.Collection, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesBetweenTwoCollectionsOfCanonicalModelConstructs(canonicalModelConstructs1,
				canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(uk.ac.manchester.dataspaces.domain.models.meta.Schema, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			Schema schema1, Schema schema2) {
		List<CanonicalModelConstruct> canonicalModelConstructs1 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema1);
		List<CanonicalModelConstruct> canonicalModelConstructs2 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema2);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesBetweenTwoCollectionsOfCanonicalModelConstructs(canonicalModelConstructs1,
				canonicalModelConstructs2, true);
		return list;
	}

	/**
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfConstructRelatedTypeBetweenTwoCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2, boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.construct c1 "
				+ "where c1.id = :constructId1 "
				+ "and sc1.constructRelatedSchematicCorrespondenceType = :constructRelatedSchematicCorrespondenceType " + "and sc1 in ( "
				+ "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.construct c2 " + "where c2.id = :constructId2)");
		query.setParameter("constructRelatedSchematicCorrespondenceType", constructRelatedSchematicCorrespondenceType)
				.setParameter("constructId1", canonicalModelConstruct1.getId()).setParameter("constructId2", canonicalModelConstruct2.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeBetweenTwoCanonicalModelConstructs(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstruct1, canonicalModelConstruct2, true);
		return list;
	}

	/**
	 * @param constructRelatedSchematicCorrespondenceType
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfConstructRelatedTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.construct c1 "
				+ "where c1 in (:constructCollection1) "
				+ "and sc1.constructRelatedSchematicCorrespondenceType = :constructRelatedSchematicCorrespondenceType " + "and sc1 in ( "
				+ "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.construct c2 " + "where c2 in (:constructCollection2))");
		query.setParameter("constructRelatedSchematicCorrespondenceType", constructRelatedSchematicCorrespondenceType)
				.setParameter("constructCollection1", canonicalModelConstructs1).setParameter("constructCollection2", canonicalModelConstructs2);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, java.util.Collection, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType,
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.meta.Schema, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfAParticularConstructRelatedTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType, Schema schema1, Schema schema2) {
		List<CanonicalModelConstruct> canonicalModelConstructs1 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema1);
		List<CanonicalModelConstruct> canonicalModelConstructs2 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema2);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfConstructRelatedTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
				constructRelatedSchematicCorrespondenceType, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfTypeInWhichCanonicalModelConstructIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct, boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c " + "where c.id = :constructId "
				+ "and sc.schematicCorrespondenceType = :schematicCorrespondenceType");
		query.setParameter("schematicCorrespondenceType", schematicCorrespondenceType).setParameter("constructId", canonicalModelConstruct.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeInWhichCanonicalModelConstructIsInvolved(
				schematicCorrespondenceType, canonicalModelConstruct, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstructs
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.construct c "
				+ "where sc.schematicCorrespondenceType = :schematicCorrespondenceType " + "and c in (:constructCollection)");
		query.setParameter("schematicCorrespondenceType", schematicCorrespondenceType).setParameter("constructCollection", canonicalModelConstructs);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
				schematicCorrespondenceType, canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			SchematicCorrespondenceType schematicCorrespondenceType, Schema schema) {
		List<CanonicalModelConstruct> canonicalModelConstructs = schemaRepository.getAllCanonicalModelConstructsInSchema(schema);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeInWhichCollectionOfCanonicalModelConstructIsInvolved(
				schematicCorrespondenceType, canonicalModelConstructs, true);
		return list;
	}

	/**
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfTypeBetweenTwoCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct1,
			CanonicalModelConstruct canonicalModelConstruct2, boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.construct c1 "
				+ "where c1.id = :constructId1 " + "and sc1.schematicCorrespondenceType = :schematicCorrespondenceType " + "and sc1 in ( "
				+ "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.construct c2 " + "where c2.id = :constructId2)");
		query.setParameter("schematicCorrespondenceType", schematicCorrespondenceType).setParameter("constructId1", canonicalModelConstruct1.getId())
				.setParameter("constructId2", canonicalModelConstruct2.getId());
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, CanonicalModelConstruct canonicalModelConstruct1,
			CanonicalModelConstruct canonicalModelConstruct2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeBetweenTwoCanonicalModelConstructs(schematicCorrespondenceType,
				canonicalModelConstruct1, canonicalModelConstruct2, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceType
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs1,
			Collection<CanonicalModelConstruct> canonicalModelConstructs2, boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.canonicalModelConstruct c1 "
				+ "where c1 in (:constructCollection1) " + "and sc1.schematicCorrespondenceType = :schematicCorrespondenceType " + "and sc1 in ( "
				+ "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.canonicalModelConstruct c2 " + "where c2 in (:constructCollection2))");
		query.setParameter("constructCollection1", canonicalModelConstructs1).setParameter("constructCollection2", canonicalModelConstructs2)
				.setParameter("schematicCorrespondenceType", schematicCorrespondenceType);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, java.util.Collection, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			SchematicCorrespondenceType schematicCorrespondenceType, Collection<CanonicalModelConstruct> canonicalModelConstructs1,
			Collection<CanonicalModelConstruct> canonicalModelConstructs2) {
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
				schematicCorrespondenceType, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(uk.ac.manchester.dataspaces.domain.models.schematiccorrespondence.SchematicCorrespondenceType, uk.ac.manchester.dataspaces.domain.models.meta.Schema, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSchematicCorrespondencesOfParticularTypeWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			SchematicCorrespondenceType schematicCorrespondenceType, Schema schema1, Schema schema2) {
		List<CanonicalModelConstruct> canonicalModelConstruct1 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema1);
		List<CanonicalModelConstruct> canonicalModelConstructs2 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema2);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfTypeBetweenTwoCollectionsOfCanonicalModelConstructs(
				schematicCorrespondenceType, canonicalModelConstruct1, canonicalModelConstructs2, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceTypeCollection
	 * @param canonicalModelConstruct
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCanonicalModelConstructIsInvolved(
			Collection<SchematicCorrespondenceType> schematicCorrespondenceTypeCollection, CanonicalModelConstruct canonicalModelConstruct,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.canonicalModelConstruct c "
				+ "where c.id = :constructId " + "and sc.schematicCorrespondenceType in (:schematicCorrespondenceTypeCollection)");
		query.setParameter("constructId", canonicalModelConstruct.getId()).setParameter("schematicCorrespondenceTypeCollection",
				schematicCorrespondenceTypeCollection);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct) {
		initPrimarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCanonicalModelConstructIsInvolved(primary,
				canonicalModelConstruct, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructIsInvolved(
			CanonicalModelConstruct canonicalModelConstruct) {
		initSecondarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCanonicalModelConstructIsInvolved(secondary,
				canonicalModelConstruct, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceTypeCollection
	 * @param canonicalModelConstructs
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<SchematicCorrespondenceType> schematicCorrespondenceTypeCollection,
			Collection<CanonicalModelConstruct> canonicalModelConstructs, boolean fetchParameters) {
		Query query = em.createQuery("select sc from SchematicCorrespondence sc "
				+ "join fetch sc.applicationOfSchematicCorrespondenceToConstructs ap " + "join fetch ap.canonicalModelConstruct c "
				+ "where sc.schematicCorrespondenceType in (:schematicCorrespondenceTypeCollection) " + "and c in (:constructCollection)");
		query.setParameter("schematicCorrespondenceTypeCollection", schematicCorrespondenceTypeCollection).setParameter("constructCollection",
				canonicalModelConstructs);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs) {
		initPrimarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCollectionOfCanonicalModelConstructsIsInvolved(
				primary, canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema) {
		initPrimarySchematicCorrespondences();
		List<CanonicalModelConstruct> canonicalModelConstructs = schemaRepository.getAllCanonicalModelConstructsInSchema(schema);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCollectionOfCanonicalModelConstructsIsInvolved(
				primary, canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCollectionOfCanonicalModelConstructsIsInvolved(
			Collection<CanonicalModelConstruct> canonicalModelConstructs) {
		initSecondarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCollectionOfCanonicalModelConstructsIsInvolved(
				secondary, canonicalModelConstructs, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsInWhichCanonicalModelConstructsInSchemaAreInvolved(
			Schema schema) {
		initSecondarySchematicCorrespondences();
		List<CanonicalModelConstruct> canonicalModelConstructs = schemaRepository.getAllCanonicalModelConstructsInSchema(schema);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesInWhichCollectionOfCanonicalModelConstructsIsInvolved(
				secondary, canonicalModelConstructs, true);
		return list;
	}

	/**
	 * @param canonicalModelConstruct1
	 * @param canonicalModelConstruct2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCanonicalModelConstructs(
			Collection<SchematicCorrespondenceType> schematicCorrespondenceTypeCollection, CanonicalModelConstruct canonicalModelConstruct1,
			CanonicalModelConstruct canonicalModelConstruct2, boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.canonicalModelConstruct c1 "
				+ "where c1.id = :constructId1 " + "and sc1.schematicCorrespondenceType in (:schematicCorrespondenceTypeCollection) "
				+ "and sc1 in ( " + "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.canonicalModelConstruct c2 " + "where c2.id = :constructId2)");
		query.setParameter("constructId1", canonicalModelConstruct1.getId()).setParameter("constructId2", canonicalModelConstruct2.getId())
				.setParameter("schematicCorrespondenceTypeCollection", schematicCorrespondenceTypeCollection);
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2) {
		initPrimarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCanonicalModelConstructs(primary,
				canonicalModelConstruct1, canonicalModelConstruct2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCanonicalModelConstructs(
			CanonicalModelConstruct canonicalModelConstruct1, CanonicalModelConstruct canonicalModelConstruct2) {
		initSecondarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCanonicalModelConstructs(secondary,
				canonicalModelConstruct1, canonicalModelConstruct2, true);
		return list;
	}

	/**
	 * @param schematicCorrespondenceTypeCollection
	 * @param canonicalModelConstructs1
	 * @param canonicalModelConstructs2
	 * @param fetchParameters
	 * @return schematicCorrespondences
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private List<SchematicCorrespondence> getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<SchematicCorrespondenceType> schematicCorrespondenceTypeCollection,
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2,
			boolean fetchParameters) {
		Query query = em.createQuery("select sc1 from SchematicCorrespondence sc1 "
				+ "join fetch sc1.applicationOfSchematicCorrespondenceToConstructs ap1 " + "join fetch ap1.canonicalModelConstruct c1 "
				+ "where c1 in (:constructCollection1) " + "and sc1.schematicCorrespondenceType in (:schematicCorrespondenceTypeCollection) "
				+ "and sc1 in ( " + "select sc2 from SchematicCorrespondence sc2 " + "join sc2.applicationOfSchematicCorrespondenceToConstructs ap2 "
				+ "join ap2.canonicalModelConstruct c2 " + "where c2 in (:constructCollection2))");
		query.setParameter("constructCollection1", canonicalModelConstructs1).setParameter("constructCollection2", canonicalModelConstructs2)
				.setParameter("schematicCorrespondenceTypeCollection", schematicCorrespondenceTypeCollection);
		//TODO not sure whether giving it a list as input works for em.query
		try {
			List<SchematicCorrespondence> list = query.getResultList();
			if (list != null && fetchParameters)
				fetchParametersAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(java.util.Collection, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfCanonicalModelConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2) {
		initPrimarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCollectionsOfCanonicalModelConstructs(
				primary, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(uk.ac.manchester.dataspaces.domain.models.meta.Schema, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllPrimarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenCanonicalModelConstructsInTwoSchemas(
			Schema schema1, Schema schema2) {
		initPrimarySchematicCorrespondences();
		List<CanonicalModelConstruct> canonicalModelConstructs1 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema1);
		List<CanonicalModelConstruct> canonicalModelConstructs2 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema2);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCollectionsOfCanonicalModelConstructs(
				primary, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfConstructs(java.util.Collection, java.util.Collection)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenTwoCollectionsOfConstructs(
			Collection<CanonicalModelConstruct> canonicalModelConstructs1, Collection<CanonicalModelConstruct> canonicalModelConstructs2) {
		initSecondarySchematicCorrespondences();
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCollectionsOfCanonicalModelConstructs(
				secondary, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.schematiccorrespondence.SchematicCorrespondenceRepository#getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenConstructsInTwoSchemas(uk.ac.manchester.dataspaces.domain.models.meta.Schema, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@Transactional(readOnly = true)
	public List<SchematicCorrespondence> getAllSecondarySchematicCorrespondencesWithParametersAndCanonicalModelConstructsBetweenConstructsInTwoSchemas(
			Schema schema1, Schema schema2) {
		initSecondarySchematicCorrespondences();
		List<CanonicalModelConstruct> canonicalModelConstructs1 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema1);
		List<CanonicalModelConstruct> canonicalModelConstructs2 = schemaRepository.getAllCanonicalModelConstructsInSchema(schema2);
		List<SchematicCorrespondence> list = getAllSchematicCorrespondencesOfCollectionOfTypesBetweenTwoCollectionsOfCanonicalModelConstructs(
				secondary, canonicalModelConstructs1, canonicalModelConstructs2, true);
		return list;
	}
	
	@Transactional
	public void addSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence){
		this.save(schematicCorrespondence);
		this.flush();
	}
	
	
	public SchematicCorrespondence findSchematicCorrespondenceByName(String schematicCorrespondenceName){
		Query query = em.createQuery("select sc from SchematicCorrespondence sc where sc.name = :schematicCorrespondenceName");
		query.setParameter("schematicCorrespondenceName", schematicCorrespondenceName);
		try {
			return (SchematicCorrespondence) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
	

	public List<Long> findAllSchematicCorrespondences(){
		Query query = em.createQuery("select sc.id from SchematicCorrespondence sc");
		this.flush();
		this.clear();
		try {
			return (List<Long> ) query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
