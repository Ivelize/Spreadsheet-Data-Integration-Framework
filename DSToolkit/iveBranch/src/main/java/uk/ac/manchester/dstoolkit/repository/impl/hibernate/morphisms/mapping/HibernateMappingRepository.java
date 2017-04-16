package uk.ac.manchester.dstoolkit.repository.impl.hibernate.morphisms.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "mappingRepository")
public class HibernateMappingRepository extends HibernateGenericRepository<Mapping, Long> implements MappingRepository {

	static Logger logger = Logger.getLogger(HibernateMappingRepository.class);

	/*
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
				fetchParametersApplicationsOfSchematicCorrespondencesToConstructAndCanonicalModelConstructsForCollectionOfSchematicCorrespondences(list);
			return list;
		} catch (NoResultException ex) {
			return null;
		}
	}
	*/

	@Transactional(readOnly = true)
	public Mapping fetchConstructs(Mapping mapping) {
		//Set<CanonicalModelConstruct> query1Constructs = 
		//mapping.getConstructs1().size();
		//query1Constructs.size();
		//Set<CanonicalModelConstruct> query2Constructs = 
		//mapping.getConstructs2().size();
		//query2Constructs.size();
		mapping = this.update(mapping);
		Hibernate.initialize(mapping.getConstructs1());
		Hibernate.initialize(mapping.getConstructs2());
		return mapping;
	}

	@Transactional(readOnly = true)
	public Mapping fetchAnnotations(Mapping mapping) {
		//this.update(mapping);
		//List<Annotation> annotations = 
		//mapping.getAnnotations().size();
		//annotations.size();
		mapping = this.update(mapping);
		Hibernate.initialize(mapping.getAnnotations());
		return mapping;
	}

	/*
	public String getAnnotationValueForMappingAndGivenOntologyTermName(Mapping mapping, String ontologyTermName) {
		log.debug("in getAnnotationValueForMappingAndGivenOntologyTermName");
		log.debug("Mapping: " + mapping);
		log.debug("ontologyTermName: " + ontologyTermName);
		javax.persistence.Query hibernateQuery = em.createQuery("select a.value from Mapping m join m.annotations a join a.ontologyTerm o "
						+ "where m.id = :mappingId and o.name = :ontologyTermName");
		//javax.persistence.Query hibernateQuery = em.createQuery("select a.value from Mapping m join m.annotations a join a.ontologyTerm o "
		//		+ "where m.id = :mappingId and o.name = :ontologyTermName");
		hibernateQuery.setParameter("mappingId", mapping.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		try {
			String annotationValue = (String) hibernateQuery.getSingleResult();
			log.debug("got annotationValue: " + annotationValue);
			return annotationValue;
		} catch (NoResultException ex) {
			return null;
		}
	}
	*/

	@Transactional(readOnly = true)
	public Set<Mapping> getAllMappingsBetweenSourceSchemaAndTargetSchema(Schema sourceSchema, Schema targetSchema) {
		Set<Mapping> mappingsBetweenSourceAndTargetSchemas = new HashSet<Mapping>();

		//TODO this doesn't seem to work properly - check this

		Query query = em
				.createQuery("select m from Mapping m join fetch m.constructs1 join fetch m.constructs2 join fetch m.query1 q1 join fetch m.query2 q2 join fetch q1.schemas s1 join fetch q2.schemas s2 where :sourceSchemaId in s1 and :targetSchemaId in s2 or :sourceSchemaId in s2 and :targetSchemaId in s1");
		query.setParameter("sourceSchemaId", sourceSchema.getId());
		query.setParameter("targetSchemaId", targetSchema.getId());

		try {
			List<Mapping> mappingList = query.getResultList();
			for (Mapping mapping : mappingList) {
				//Hibernate.initialize(mapping.getAnnotations());
				Hibernate.initialize(mapping.getConstructs1());
				Hibernate.initialize(mapping.getConstructs2());
			}
			mappingsBetweenSourceAndTargetSchemas.addAll(mappingList);
		} catch (NoResultException ex) {
			return null;
		}

		return mappingsBetweenSourceAndTargetSchemas;
	}

	@Transactional(readOnly = true)
	public Set<Mapping> getAllMappingsBetweenConstructQueriedAndTargetSchema(CanonicalModelConstruct constructQueried, Schema targetSchema) {
		Set<Mapping> mappingsBetweenSourceAndTargetSchemas = new HashSet<Mapping>();

		Query query = em
				.createQuery("select m from Mapping m join fetch m.constructs1 join fetch m.constructs2 join fetch m.query1 q1 join fetch m.query2 q2 join q1.schemas s1 join q2.schemas s2 join m.constructs1 q1c join m.constructs2 q2c where :queriedConstructId in q1c and :targetSchemaId in s2 or :queriedConstructId in q2c and :targetSchemaId in s1");
		query.setParameter("queriedConstructId", constructQueried.getId());
		query.setParameter("targetSchemaId", targetSchema.getId());

		try {
			List<Mapping> mappingList = query.getResultList();
			for (Mapping mapping : mappingList) {
				//Hibernate.initialize(mapping.getAnnotations());
				Hibernate.initialize(mapping.getConstructs1());
				Hibernate.initialize(mapping.getConstructs2());
				logger.debug("mapping.constructs1.size(): " + mapping.getConstructs1().size());
				logger.debug("mapping.constructs2.size(): " + mapping.getConstructs2().size());
			}
			mappingsBetweenSourceAndTargetSchemas.addAll(mappingList);
		} catch (NoResultException ex) {
			return null;
		}

		return mappingsBetweenSourceAndTargetSchemas;
	}
	
}
