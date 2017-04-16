package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.MatchingProfilerServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.QueryService;

public interface InstanceBasedMatcherService extends MatcherService {

	// doesn't make sense to call it for a single CanonicalModelConstruct, unless it's a SuperAbstract,
	// but need all canonicalModelConstructs to be able to assign scores to the superLexicals of a superAbstract based on the instances matched
	/*
	public float match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2, DataSource dataSource1, DataSource dataSource2);

	public float[] runChildMatchers(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2, DataSource dataSource1,
			DataSource dataSource2);
	*/

	public List<Matching> match(Schema schema1, Schema schema2, DataSource dataSource1, DataSource dataSource2,
			Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException;

	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2, DataSource dataSource1,
			DataSource dataSource2, Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException;
	/*
	public float[][][] runChildMatchers(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2, DataSource dataSource1,
			DataSource dataSource2);
	*/

	public void setQueryService(QueryService queryService);

	public void setQueryRepository(QueryRepository queryRepository);

	public void setQueryResultRepository(QueryResultRepository queryResultRepository);

	public void setMatchingProfilerServiceImpl(MatchingProfilerServiceImpl matchingProfilerServiceImpl);
}