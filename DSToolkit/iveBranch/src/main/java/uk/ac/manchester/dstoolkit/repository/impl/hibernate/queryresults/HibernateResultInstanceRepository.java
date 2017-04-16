package uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "resultInstanceRepository")
public class HibernateResultInstanceRepository extends HibernateGenericRepository<ResultInstance, Long> implements ResultInstanceRepository {

	static Logger logger = Logger.getLogger(HibernateResultInstanceRepository.class);

	//TODO could this be more than one??? yes, a FN from a previous execution, added parameter specifying whether to get userSpecified instances or not
	@Transactional(readOnly = true)
	public ResultInstance getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance(Query query, ResultInstance resultInstance,
			boolean getUserSpecifiedInstances) {
		logger.debug("in getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance");
		logger.debug("query: " + query);
		logger.debug("resultInstance: " + resultInstance);

		javax.persistence.Query hibernateQuery = em
				.createQuery(
						"select distinct qr from QueryResult qr join fetch qr.resultInstances ri join qr.query q where q.id = :queryId and ri.isUserSpecified = :isUserSpecified")
				.setFlushMode(FlushModeType.COMMIT);
		hibernateQuery.setParameter("queryId", query.getId());
		hibernateQuery.setParameter("isUserSpecified", getUserSpecifiedInstances);

		try {
			List<QueryResult> queryResults = hibernateQuery.getResultList();
			logger.debug("got queryResults: " + queryResults);
			logger.debug("queryResults.size(): " + queryResults.size());

			Set<String> resultFieldNames = resultInstance.getResultFieldNameResultValueMap().keySet();
			logger.debug("resultFieldNames: " + resultFieldNames);
			logger.debug("resultFieldNames.size(): " + resultFieldNames.size());

			StringBuilder sb = new StringBuilder();
			for (String fieldName : resultFieldNames) {
				logger.debug("fieldName: " + fieldName);
				if (resultInstance.getResultValue(fieldName) != null) {
					logger.debug("found value for fieldName, append to string");
					sb.append(resultInstance.getResultValue(fieldName).getValue());
				} else {
					logger.debug("didn't find value for fieldName, append null to string");
					sb.append("null");
				}
			}
			logger.debug("resultInstance - valueString: " + sb.toString());

			Set<ResultInstance> resultInstances = new LinkedHashSet<ResultInstance>();
			for (QueryResult queryResult : queryResults) {
				logger.debug("queryResult: " + queryResult);

				List<ResultInstance> instances = queryResult.getResultInstances();
				logger.debug("instances.size(): " + instances.size());

				for (ResultInstance instance : instances) {
					logger.debug("instance: " + instance);

					StringBuilder isb = new StringBuilder();
					//boolean sameValues = true;
					for (String fieldName : resultFieldNames) {
						logger.debug("fieldName: " + fieldName);
						if (instance.getResultValue(fieldName) != null) {
							logger.debug("found value for fieldName, append to string");
							isb.append(instance.getResultValue(fieldName).getValue());
						} else {
							logger.debug("didn't find value for fieldName, append null to string");
							isb.append("null");
						}

						/*
						logger.debug("resultInstance.getResultValue(fieldName): " + resultInstance.getResultValue(fieldName));
						logger.debug("instance.getResultValue(fieldName): " + instance.getResultValue(fieldName));
						if (resultInstance.getResultValue(fieldName) != null && instance.getResultValue(fieldName) != null) {
							logger.debug("resultInstance.getResultValue(fieldName).getResultValue(): "
									+ resultInstance.getResultValue(fieldName).getValue());
							logger.debug("instance.getResultValue(fieldName).getResultValue(): " + instance.getResultValue(fieldName).getValue());
							if (!resultInstance.getResultValue(fieldName).getValue().equals(instance.getResultValue(fieldName).getValue())) {
								sameValues = false;
								logger.debug("found different value, break for loop");
								break;
							}
						} else if (resultInstance.getResultValue(fieldName) == null && instance.getResultValue(fieldName) != null) {
							sameValues = false;
							logger.debug("one value is null, the other one isn't");
							break;
						} else if (instance.getResultValue(fieldName) == null && resultInstance.getResultValue(fieldName) != null) {
							sameValues = false;
							logger.debug("one value is null, the other one isn't");
							break;
						} else if (instance.getResultValue(fieldName) == null && resultInstance.getResultValue(fieldName) == null) {
							logger.debug("both values are null");
						}
						logger.debug("sameValues: " + sameValues);
						*/
					}
					//logger.debug("sameValues: " + sameValues);
					logger.debug("sb.toString: " + sb.toString());
					logger.debug("isb.toString: " + isb.toString());
					if (sb.toString().equals(isb.toString())) {
						logger.debug("instance has same resultValues as given resultInstance");
						logger.debug("instance: " + instance);
						if (!resultInstance.getResultType().equals(instance.getResultType()))
							logger.error("resultInstance and instance have different kinds of resultTypes though, shouldn't happen - TODO");
						else
							resultInstances.add(instance);
					} else
						logger.debug("resultInstance and instance have different values");
				}
			}
			if (resultInstances.size() > 0) {
				if (resultInstances.size() == 1) {
					logger.debug("found single instance with same values as given resultInstance");
					return resultInstances.iterator().next();
				} else
					logger.error("found more than one instance with same values - TODO, size: " + resultInstances.size());
			} else {
				logger.debug("didn't find instance with same values as given resultInstance");
				return null;
			}
		} catch (NoResultException ex) {
			return null;
		}
		return null;
	}
}
