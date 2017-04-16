/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

/**
 * @author chedeler
 *
 */
public interface MatchingProducerService {

	public abstract List<Matching> produceMatches(float[][] simMatrix, List<CanonicalModelConstruct> constructs1,
			List<CanonicalModelConstruct> constructs2, Map<ControlParameterType, ControlParameter> controlParameters, MatcherService matcherService);

	public abstract Matching produceSingleMatching(float simScore, CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters, MatcherService matcherService);
	
	public void writeMatchingsToFile(final float[][] simMatrix, final List<CanonicalModelConstruct> sourceConstructs,
			final List<CanonicalModelConstruct> targetConstructs, final Map<ControlParameterType, ControlParameter> controlParameters);
	
	public List<Matching> produceFinalMatches(final float[][] simMatrix, final List<CanonicalModelConstruct> constructs1,
			final List<CanonicalModelConstruct> constructs2, final Map<ControlParameterType, ControlParameter> controlParameters,
			final MatcherService matcherService);
	
	public List<Matching> produceMatchesForSpecificCells(final List<CanonicalModelConstruct> sourceConstructs, final List<CanonicalModelConstruct> targetConstructs,
			 final float[][] simMatrix, final Map<ControlParameterType, ControlParameter> controlParameters,
			 final Set<SemanticMatrixCellIndex> cellsSet, final MatcherService matcherService);	
	
	public List<Matching> produceFinalMatchesWithError(final float[][] simMatrix, final float[][] errMatrix, final List<CanonicalModelConstruct> sourceConstructs,
			final List<CanonicalModelConstruct> targetConstructs, final Map<ControlParameterType, ControlParameter> controlParameters,
			final MatcherService matcherService);

}