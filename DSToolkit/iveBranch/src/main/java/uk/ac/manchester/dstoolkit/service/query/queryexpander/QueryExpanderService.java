package uk.ac.manchester.dstoolkit.service.query.queryexpander;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;

public interface QueryExpanderService {

	public Query expandQuery(Query queryToExpand, Set<Mapping> mappingsToUseForExpansion,
			Map<ControlParameterType, ControlParameter> controlParameters);

}