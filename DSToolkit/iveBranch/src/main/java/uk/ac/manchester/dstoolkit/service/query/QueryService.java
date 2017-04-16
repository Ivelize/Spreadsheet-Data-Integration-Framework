/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.query;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface QueryService extends GenericEntityService<Query, Long> {

	public Set<Mapping> selectMappings(Query query, Set<Mapping> mappings, ControlParameterType thresholdType, double thresholdValue);

	public QueryResult evaluateQuery(String queryString, String queryName, Set<Schema> schemasToEvaluateQueryOver,
			Set<DataSource> dataSourcesToEvaluateQueryOver, Set<Mapping> mappingsToUtilise, User user,
			Map<ControlParameterType, ControlParameter> controlParameters);

	/**
	 * @param query
	 * @return
	 */
	public QueryResult evaluateQuery(Query query, Set<Mapping> mappingsToUtilise, User user,
			Map<ControlParameterType, ControlParameter> controlParameters);

	/**
	 * @param queryId
	 * @return query
	 */
	public Query findQuery(Long queryId);

	/**
	 * @param query
	 */
	public void addQuery(Query query);

	/**
	 * @param queryId
	 */
	public void deleteQuery(Long queryId);

}
