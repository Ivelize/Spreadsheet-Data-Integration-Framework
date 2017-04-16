package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;

public interface BayesianMatcherService {

	public List<Matching> produceAndSaveMatches(Schema sourceSchema, Schema targetSchema);
	
	public List<Matching> produceMatches(Schema sourceSchema, Schema targetSchema);
	
	public List<Matching> produceMatchesForSpecificCells(Schema sourceSchema, Schema targetSchema, Set<SemanticMatrixCellIndex> cellsSet);
	
	public List<Matching> produceAndSaveMatchesForSpecificCellsWithError(Schema sourceSchema, Schema targetSchema,
			float[][] errMatrix, Set<SemanticMatrixCellIndex> cellsSet);
	
	
}
