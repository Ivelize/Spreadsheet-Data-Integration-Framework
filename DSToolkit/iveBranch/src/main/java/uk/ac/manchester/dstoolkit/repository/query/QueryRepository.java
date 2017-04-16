package uk.ac.manchester.dstoolkit.repository.query;

import java.util.List;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface QueryRepository extends GenericRepository<Query, Long> {

	public List<ResultInstance> getResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(Query query, String ontologyTermName,
			String annotationValue);

	public List<ResultInstance> getResultInstancesOfQueryWithoutAnnotationForGivenOntologyTerm(Query query, String ontologyTermName);

	public long getNumberOfResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(Query query, String ontologyTermName, String annotationValue);

	public long getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(Query query, Set<Mapping> mappings);

	public long getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(Query query,
			String ontologyTermName, String annotationValue, Set<Mapping> mappings);

	public List<Query> getAllQueriesWithSchemasOrderedByQueryId();

	public Query getQueryWithIdWithSchemasDataSources(Long id);

	public Query getQueryWithName(String queryName);

	//public Set<Mapping> getAllMappingsForQueryWithId(Long id);

	public Set<DataSource> getAllDataSourcesForQueryWithId(Long id);
}
