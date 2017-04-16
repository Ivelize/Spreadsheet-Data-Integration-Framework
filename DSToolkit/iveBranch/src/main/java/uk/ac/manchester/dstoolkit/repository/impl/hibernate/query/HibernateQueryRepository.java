package uk.ac.manchester.dstoolkit.repository.impl.hibernate.query;

import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "queryRepository")
public class HibernateQueryRepository extends HibernateGenericRepository<Query, Long> implements QueryRepository {

	static Logger logger = Logger.getLogger(HibernateQueryRepository.class);

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Transactional(readOnly = true)
	public List<ResultInstance> getResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(Query query, String ontologyTermName,
			String annotationValue) {
		logger.debug("in getResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm");
		logger.debug("Query: " + query);
		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("annotationValue: " + annotationValue);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select r from QueryResult qr join qr.query q join qr.resultInstances r join r.annotations a join a.ontologyTerm o "
		//				+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select distinct r from ResultInstance r join fetch r.query q join fetch r.annotations a join fetch a.ontologyTerm o "
						+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue");
		hibernateQuery.setParameter("queryId", query.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		hibernateQuery.setParameter("annotationValue", annotationValue);
		try {
			List<ResultInstance> result = hibernateQuery.getResultList();
			logger.debug("got result: " + result);
			return result;
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<ResultInstance> getResultInstancesOfQueryWithoutAnnotationForGivenOntologyTerm(Query query, String ontologyTermName) {
		logger.debug("in getResultInstancesOfQueryWithoutAnnotationForGivenOntologyTerm");
		logger.debug("Query: " + query);
		logger.debug("ontologyTermName: " + ontologyTermName);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select r from QueryResult qr join qr.query q join qr.resultInstances r join r.annotations a join a.ontologyTerm o "
		//				+ "where q.id = :queryId and o.name <> :ontologyTermName");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select distinct r from ResultInstance r join fetch r.query q join fetch r.annotations a join fetch a.ontologyTerm o "
						+ "where q.id = :queryId and o.name <> :ontologyTermName");
		hibernateQuery.setParameter("queryId", query.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		try {
			List<ResultInstance> result = hibernateQuery.getResultList();
			logger.debug("got result: " + result);
			return result;
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public long getNumberOfResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(Query query, String ontologyTermName, String annotationValue) {
		logger.debug("in getNumberOfResultInstancesOfQueryWithGivenAnnotation");
		logger.debug("Query: " + query);
		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("annotationValue: " + annotationValue);
		//javax.persistence.Query hibernateQuery = em
		//		.createQuery("select count(distinct r.id) from QueryResult qr join qr.query q join qr.resultInstances r join r.annotations a join a.ontologyTerm o "
		//				+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue");
		javax.persistence.Query hibernateQuery = em
				.createQuery("select distinct r from ResultInstance r join fetch r.query q join fetch r.annotations a join fetch a.ontologyTerm o "
						+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue");
		hibernateQuery.setParameter("queryId", query.getId());
		hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
		hibernateQuery.setParameter("annotationValue", annotationValue);
		//hibernateQuery.setFlushMode(FlushModeType.COMMIT);
		try {
			List<ResultInstance> resultInstances = hibernateQuery.getResultList();
			logger.debug("resultInstances: " + resultInstances);
			long numberOfResultInstances = resultInstances.size();
			//long numberOfTps = ((Long) hibernateQuery.getSingleResult()).longValue();
			logger.debug("got numberOfResultInstances: " + numberOfResultInstances);
			return numberOfResultInstances;
		} catch (NoResultException ex) {
			return -1;
		}
	}

	@Transactional(readOnly = true)
	public long getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(Query query, Set<Mapping> mappings) {
		logger.debug("in getNumberOfResultInstancesOfQueryRetrievedByGivenMappings");
		logger.debug("Query: " + query);
		logger.debug("mappings: " + mappings);
		StringBuilder querySB = new StringBuilder();
		//querySB.append("select count(distinct r.id) from QueryResult qr join qr.query q join qr.resultInstances r join r.mappings m "
		//		+ "where q.id = :queryId and (");
		if (mappings.size() > 0) {
			querySB.append("select distinct r from ResultInstance r join fetch r.query q join fetch r.mappings m " + "where q.id = :queryId and (");
			for (int i = 0; i < mappings.size(); i++) {
				querySB.append("m.id = :mappingId");
				querySB.append(i);
				if (i < mappings.size() - 1)
					querySB.append(" or ");
			}
			querySB.append(")");
			logger.debug("queryString: " + querySB.toString());
			javax.persistence.Query hibernateQuery = em.createQuery(querySB.toString());
			hibernateQuery.setParameter("queryId", query.getId());
			int i = 0;
			for (Mapping mapping : mappings) {
				logger.debug("i: " + i);
				hibernateQuery.setParameter("mappingId" + i, mapping.getId());
				i++;
				logger.debug("mapping.getId(): " + mapping.getId());
			}
			//hibernateQuery.setFlushMode(FlushModeType.COMMIT);
			try {
				List<ResultInstance> resultInstances = hibernateQuery.getResultList();
				//logger.debug("resultInstances: " + resultInstances);
				long numberOfResultInstances = resultInstances.size();
				//long numberOfResultInstances = ((Long) hibernateQuery.getSingleResult()).longValue();
				logger.debug("got numberOfResultInstances: " + numberOfResultInstances);
				return numberOfResultInstances;
			} catch (NoResultException ex) {
				return -1l;
			}
		}
		return -1l;
	}

	@Transactional(readOnly = true)
	public long getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(Query query,
			String ontologyTermName, String annotationValue, Set<Mapping> mappings) {
		logger.debug("in getNumberOfResultInstancesOfQueryWithGivenAnnotationValueRetrievedByGivenMappings");
		logger.debug("query: " + query);
		logger.debug("ontologyTermName: " + ontologyTermName);
		logger.debug("annotationValue: " + annotationValue);
		logger.debug("mappings: " + mappings);
		if (mappings.size() > 0) {
			StringBuilder querySB = new StringBuilder();
			//querySB.append("select count(distinct r.id) from QueryResult qr join qr.query q join fetch qr.resultInstances r join r.annotations a join r.mappings m join a.ontologyTerm o "
			//		+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue");
			//querySB.append("select count(distinct r.id) from QueryResult qr join qr.query q join fetch qr.resultInstances r join r.annotations a join r.mappings m join a.ontologyTerm o "
			//		+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue and (");
			//querySB.append("select count(distinct r.id) from ResultInstance r join r.queryResults qr join qr.query q join r.annotations a join r.mappings m join a.ontologyTerm o "
			//		+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue and (");
			//querySB.append("select count(distinct r.id) from ResultInstance r join r.query q join r.annotations a join r.mappings m join a.ontologyTerm o "
			//		+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue"); //and (");
			//querySB.append("select distinct r from ResultInstance r join fetch r.query q join fetch r.annotations a join r.mappings m join fetch a.ontologyTerm o "
			//		+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue and (");
			querySB.append("select distinct r from ResultInstance r join fetch r.query q join fetch r.annotations a join a.constrainingModelManagementConstructs m join fetch a.ontologyTerm o "
					+ "where q.id = :queryId and o.name = :ontologyTermName and a.value = :annotationValue and (");

			for (int i = 0; i < mappings.size(); i++) {
				querySB.append("m.id = :mappingId");
				querySB.append(i);
				if (i < mappings.size() - 1)
					querySB.append(" or ");
			}
			querySB.append(")");

			logger.debug("queryString: " + querySB.toString());
			javax.persistence.Query hibernateQuery = em.createQuery(querySB.toString());
			hibernateQuery.setParameter("queryId", query.getId());
			logger.debug("queryId: " + query.getId());
			hibernateQuery.setParameter("ontologyTermName", ontologyTermName);
			logger.debug("ontologyTermName: " + ontologyTermName);
			hibernateQuery.setParameter("annotationValue", annotationValue);
			logger.debug("annotationValue: " + annotationValue);

			int i = 0;
			for (Mapping mapping : mappings) {
				logger.debug("i: " + i);
				hibernateQuery.setParameter("mappingId" + i, mapping.getId());
				logger.debug("mapping.getId(): " + mapping.getId());
				i++;
			}

			//hibernateQuery.setFlushMode(FlushModeType.COMMIT);
			try {
				List<ResultInstance> resultInstances = hibernateQuery.getResultList();
				//logger.debug("resultInstances: " + resultInstances);
				long numberOfResultInstances = resultInstances.size();
				logger.debug("got numberOfResultInstances: " + numberOfResultInstances);

				if (annotationValue.equals("fn")) {
					logger.debug("annotationValue is fn - sort out resultInstances that are tps for some mappings but fns for others");
					for (ResultInstance resultInstance : resultInstances) {
						logger.debug("resultInstance: " + resultInstance);
						//List<Annotation> annotations = resultInstance.getAnnotations();
						//logger.debug("annotations.size: " + annotations.size());
						//logger.debug("annotations: " + annotations);

						List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstruct(resultInstance);
						logger.debug("allAnnotations.size(): " + annotations.size());
						logger.debug("allAnnotations: " + annotations);

						boolean foundMappingInTpConstrainingConstructs = false;
						boolean foundMappingInFnConstrainingConstructs = false;
						Annotation fnAnnotation = null;
						Annotation tpAnnotation = null;
						for (Annotation annotation : annotations) {
							if (annotation.getValue().equals("fn")) {
								logger.debug("got fn annotation for resultInstance");
								fnAnnotation = annotation;
							}
							if (annotation.getValue().equals("tp")) {
								logger.debug("got tp annotation for resultInstance");
								tpAnnotation = annotation;
							}
						}
						if (fnAnnotation != null && tpAnnotation != null) {
							logger.debug("resultInstance has fn and tp annotation");
							logger.debug("compare mappings with constraining mappings for each annotation");
							Set<ModelManagementConstruct> tpConstrainingConstructs = tpAnnotation.getConstrainingModelManagementConstructs();
							Set<ModelManagementConstruct> fnConstrainingConstructs = fnAnnotation.getConstrainingModelManagementConstructs();
							for (Mapping mapping : mappings) {
								logger.debug("mapping: " + mapping);
								if (tpConstrainingConstructs.contains(mapping)) {
									foundMappingInTpConstrainingConstructs = true;
								}
								if (fnConstrainingConstructs.contains(mapping)) {
									foundMappingInFnConstrainingConstructs = true;
								}
								logger.debug("foundMappingInTpConstrainingConstructs: " + foundMappingInTpConstrainingConstructs);
								logger.debug("foundMappingInFnConstrainingConstructs: " + foundMappingInFnConstrainingConstructs);
							}
							if (foundMappingInTpConstrainingConstructs) {
								logger.debug("foundMappingInTpConstrainingConstructs is true - remove number of resultInstances ");
								numberOfResultInstances--;
								logger.debug("numberOfResultInstances: " + numberOfResultInstances);
							}
						}
					}
				}
				logger.debug("numberOfResultInstances: " + numberOfResultInstances);

				return numberOfResultInstances;
			} catch (NoResultException ex) {
				return -1l;
			}
		}
		return -1l;
	}

	@Transactional(readOnly = true)
	public List<Query> getAllQueriesWithSchemasOrderedByQueryId() {
		javax.persistence.Query query = em.createQuery("select q from Query q join fetch q.schemas where q.queryName is not null order by q.id");
		try {
			List<Query> list = query.getResultList();
			return list;
		} catch (NoResultException ex) {
			//log.error("ex: " + ex);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Query getQueryWithIdWithSchemasDataSources(Long id) {
		javax.persistence.Query query = em.createQuery("select q from Query q join fetch q.schemas join fetch q.dataSources where q.id = :queryId");
		query.setParameter("queryId", id);
		try {
			Query queryResult = (Query) query.getSingleResult();
			return queryResult;
		} catch (NoResultException ex) {
			//log.error("ex: " + ex);
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Query getQueryWithName(String queryName) {
		javax.persistence.Query query = em
				.createQuery("select q from Query q join fetch q.schemas join fetch q.dataSources where q.queryName = :queryName");
		query.setParameter("queryName", queryName);
		try {
			Query queryResult = (Query) query.getSingleResult();
			return queryResult;
		} catch (NoResultException ex) {
			//log.error("ex: " + ex);
			return null;
		}
	}

	/*
	public Set<Mapping> getAllMappingsForQueryWithId(Long id) {
		//javax.persistence.Query query = em.createQuery("select q.mappings from Query q where q.id = :queryId");
		javax.persistence.Query query = em.createQuery("select q from Query q join fetch q.mappings where q.id = :queryId");
		query.setParameter("queryId", id);
		try {
			Query queryResult = (Query) query.getSingleResult();
			//List<Mapping> list = query.getResultList();
			return queryResult.getMappings();
			//return list;
		} catch (NoResultException ex) {
			//log.error("ex: " + ex);
			return null;
		}
	}
	*/

	@Transactional(readOnly = true)
	public Set<DataSource> getAllDataSourcesForQueryWithId(Long id) {
		javax.persistence.Query query = em.createQuery("select q from Query q join fetch q.dataSources where q.id = :queryId");
		query.setParameter("queryId", id);
		try {
			Query queryResult = (Query) query.getSingleResult();
			//List<Mapping> list = query.getResultList();
			return queryResult.getDataSources();
			//return list;
		} catch (NoResultException ex) {
			//log.error("ex: " + ex);
			return null;
		}
	}
}
