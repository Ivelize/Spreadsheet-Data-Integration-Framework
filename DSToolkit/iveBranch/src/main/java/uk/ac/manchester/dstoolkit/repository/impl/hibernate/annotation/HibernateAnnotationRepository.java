package uk.ac.manchester.dstoolkit.repository.impl.hibernate.annotation;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "annotationRepository")
public class HibernateAnnotationRepository extends HibernateGenericRepository<Annotation, Long> implements AnnotationRepository {

	static Logger log = Logger.getLogger(HibernateAnnotationRepository.class);

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstructProvidedByUser(ModelManagementConstruct construct, User user) {
		log.debug("in getAnnotationsForModelManagementConstructProvidedByUser");
		log.debug("ModelManagmentConstruct: " + construct);
		log.debug("user: " + user);
		javax.persistence.Query hibernateQuery = em
				.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m join a.user u "
						+ "where m.id = :constructId and u.id = :userId");
		//javax.persistence.Query hibernateQuery = em.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join a.user u "
		//		+ "where m.id = :constructId and u.id = :userId");
		hibernateQuery.setParameter("constructId", construct.getId());
		hibernateQuery.setParameter("userId", user.getId());
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			/*
			List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			if (mmConstructs != null && mmConstructs.size() > 0) {
				ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
				List<Annotation> annotations = construct.getAnnotations();
				annotations.size();//forces fetch
				List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
				for (Annotation annotation : annotations) {
					annotationsToReturn.add(annotation);
					annotation.getConstrainingModelManagementConstructs().size();//forces fetch
				}
				//List<Annotation> annotations = hibernateQuery.getResultList();
				log.debug("got annotationsToReturn: " + annotationsToReturn);
				return annotationsToReturn;
			}
			return null;
			*/
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstruct(ModelManagementConstruct construct) {
		log.debug("in getAnnotationsForModelManagementConstructProvidedByUser");
		log.debug("ModelManagmentConstruct: " + construct);
		javax.persistence.Query hibernateQuery = em.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m "
				+ "where m.id = :constructId");
		//javax.persistence.Query hibernateQuery = em.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join a.user u "
		//		+ "where m.id = :constructId and u.id = :userId");
		hibernateQuery.setParameter("constructId", construct.getId());
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			/*
			List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			if (mmConstructs != null && mmConstructs.size() > 0) {
				ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
				List<Annotation> annotations = construct.getAnnotations();
				annotations.size();//forces fetch
				List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
				for (Annotation annotation : annotations) {
					annotationsToReturn.add(annotation);
					annotation.getConstrainingModelManagementConstructs().size();//forces fetch
				}
				//List<Annotation> annotations = hibernateQuery.getResultList();
				log.debug("got annotationsToReturn: " + annotationsToReturn);
				return annotationsToReturn;
			}
			return null;
			*/
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTerm(ModelManagementConstruct construct, OntologyTerm ontologyTerm) {
		log.debug("in getAnnotationsForModelManagementConstructAndOntologyTerm");
		log.debug("ModelManagmentConstruct: " + construct);
		log.debug("ontologyTerm: " + ontologyTerm);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join a.ontologyTerm o "
		//				+ "where m.id = :constructId and o.id = :ontologyTermId");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m join a.ontologyTerm o "
						+ "where m.id = :constructId and o.id = :ontologyTermId");
		hibernateQuery.setParameter("constructId", construct.getId());
		hibernateQuery.setParameter("ontologyTermId", ontologyTerm.getId());
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			/*
			List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			if (mmConstructs != null && mmConstructs.size() > 0) {
				ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
				List<Annotation> annotations = construct.getAnnotations();
				annotations.size();//forces fetch
				List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
				for (Annotation annotation : annotations) {
					if (annotation.getOntologyTerm().equals(ontologyTerm)) {
						annotationsToReturn.add(annotation);
						annotation.getConstrainingModelManagementConstructs().size();//forces fetch
					}
				}
				//List<Annotation> annotations = hibernateQuery.getResultList();
				log.debug("got annotationsToReturn: " + annotationsToReturn);
				return annotationsToReturn;
			}
			return null;
			*/
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermName(ModelManagementConstruct construct, String ontologyTermName) {
		log.debug("in getAnnotationsForModelManagementConstructAndOntologyTermName");
		log.debug("ModelManagmentConstruct: " + construct);
		log.debug("ontologyTermName: " + ontologyTermName);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join a.ontologyTerm o "
		//				+ "where m.id = :constructId and o.name = :ontologyTermName");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m join a.ontologyTerm o "
						+ "where m.id = :constructId and o.name = :ontologyTermName");
		hibernateQuery.setParameter("constructId", construct.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			/*
			List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			if (mmConstructs != null && mmConstructs.size() > 0) {
				ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
				List<Annotation> annotations = construct.getAnnotations();
				annotations.size();//forces fetch
				List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
				for (Annotation annotation : annotations) {
					if (annotation.getOntologyTerm().getName().equals(ontologyTermName)) {
						annotationsToReturn.add(annotation);
						annotation.getConstrainingModelManagementConstructs().size();//forces fetch
					}
				}
				//List<Annotation> annotations = hibernateQuery.getResultList();
				log.debug("got annotationsToReturn: " + annotationsToReturn);
				return annotationsToReturn;
			}
			return null;
			*/
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermProvidedByUser(ModelManagementConstruct construct,
			OntologyTerm ontologyTerm, User user) {
		log.debug("in getAnnotationForModelManagementConstructAndOntologyTermProvidedByUser");
		log.debug("ModelManagmentConstruct: " + construct);
		log.debug("ontologyTerm: " + ontologyTerm);
		log.debug("user: " + user);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join a.ontologyTerm o join a.user u "
		//				+ "where m.id = :constructId and o.id = :ontologyTermId and u.id = :userId");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m join a.ontologyTerm o join a.user u "
						+ "where m.id = :constructId and o.id = :ontologyTermId and u.id = :userId");

		hibernateQuery.setParameter("constructId", construct.getId());
		hibernateQuery.setParameter("ontologyTermId", ontologyTerm.getId());
		hibernateQuery.setParameter("userId", user.getId());
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			/*
			List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			if (mmConstructs != null && mmConstructs.size() > 0) {
				ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
				List<Annotation> annotations = construct.getAnnotations();
				annotations.size();//forces fetch
				List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
				for (Annotation annotation : annotations) {
					if (annotation.getOntologyTerm().equals(ontologyTerm)) {
						annotationsToReturn.add(annotation);
						annotation.getConstrainingModelManagementConstructs().size();//forces fetch
					}
				}
				//List<Annotation> annotations = hibernateQuery.getResultList();
				log.debug("got annotationsToReturn: " + annotationsToReturn);
				return annotationsToReturn;
			}
			return null;
			*/
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(ModelManagementConstruct construct,
			String ontologyTermName, User user) {
		log.debug("in getAnnotationForModelManagementConstructAndOntologyTermNameProvidedByUser");
		log.debug("ModelManagmentConstruct: " + construct);
		log.debug("ontologyTermName: " + ontologyTermName);
		log.debug("user: " + user);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select m from ModelManagementConstruct m join fetch m.annotations a join fetch a.ontologyTerm o join a.user u "
		//				+ "where m.id = :constructId and o.name = :ontologyTermName and u.id = :userId");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select a from Annotation a join fetch a.annotatedModelManagementConstructs m join a.ontologyTerm o join a.user u "
						+ "where m.id = :constructId and o.name = :ontologyTermName and u.id = :userId");
		hibernateQuery.setParameter("constructId", construct.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		hibernateQuery.setParameter("userId", user.getId());
		try {
			//ModelManagementConstruct modelManagementConstruct = (ModelManagementConstruct) hibernateQuery.getSingleResult();
			return hibernateQuery.getResultList();
			//List<ModelManagementConstruct> mmConstructs = hibernateQuery.getResultList();
			//if (mmConstructs != null && mmConstructs.size() > 0) {
			//ModelManagementConstruct modelManagementConstruct = mmConstructs.get(0);
			/*
			List<Annotation> annotations = modelManagementConstruct.getAnnotations();
			annotations.size();//forces fetch
			List<Annotation> annotationsToReturn = new ArrayList<Annotation>();
			for (Annotation annotation : annotations) {
				if (annotation.getOntologyTerm().getName().equals(ontologyTermName)) {
					annotationsToReturn.add(annotation);
					annotation.getConstrainingModelManagementConstructs().size();//forces fetch
				}
			}
			//List<Annotation> annotations = hibernateQuery.getResultList();
			log.debug("got annotationsToReturn: " + annotationsToReturn);
			return annotationsToReturn;
			*/
			//}
			//return null;
		} catch (NoResultException ex) {
			return null;
		}
	}
}
