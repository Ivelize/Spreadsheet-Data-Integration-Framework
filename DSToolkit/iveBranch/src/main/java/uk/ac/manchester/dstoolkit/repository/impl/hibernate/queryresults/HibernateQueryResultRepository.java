package uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.query.HibernateQueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "queryResultRepository")
public class HibernateQueryResultRepository extends HibernateGenericRepository<QueryResult, Long> implements QueryResultRepository {

	static Logger logger = Logger.getLogger(HibernateQueryRepository.class);

	public QueryResult getQueryResultForQuery(Query query) {
		logger.debug("in getQueryResultForQuery");
		javax.persistence.Query hibernateQuery = em.createQuery("select qr from QueryResult qr where qr.query.id = :queryId");
		hibernateQuery.setParameter("queryId", query.getId());
		try {
			return (QueryResult) hibernateQuery.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Set<QueryResult> getQueryResultsThatResultInstancesBelongTo(Set<ResultInstance> resultInstances) {
		logger.debug("in getQueryResultThatResultInstancesBelongTo");
		StringBuilder querySB = new StringBuilder();
		Set<Long> resultInstanceIds = new HashSet<Long>();
		for (ResultInstance resultInstance : resultInstances) {
			resultInstanceIds.add(resultInstance.getId());
		}
		logger.debug("resultInstanceIds: " + resultInstanceIds);
		if (!resultInstanceIds.isEmpty()) {
			querySB.append("select distinct qr from QueryResult qr join qr.resultInstances r where r.id IN (:resultInstanceIds)");
			javax.persistence.Query query = em.createQuery(querySB.toString());
			query.setParameter("resultInstanceIds", resultInstanceIds);
			try {
				List<QueryResult> queryResults = query.getResultList();
				logger.debug("queryResults: " + queryResults);
				for (QueryResult queryResult : queryResults) {
					queryResult.getMappings().size();
					queryResult.getResultInstances().size();
					queryResult.getQuery().getDataspace();
				}
				Set<QueryResult> queryResultSet = new HashSet<QueryResult>(queryResults);
				return queryResultSet;
			} catch (NoResultException ex) {
				return null;
			}
		}
		return null;
	}

}
