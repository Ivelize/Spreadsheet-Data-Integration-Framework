/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;

/**
 * @author chedeler
 *
 */
public interface InferCorrespondenceService {

	public Set<SchematicCorrespondence> inferCorrespondences(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			double maxMatchingScore, Map<ControlParameterType, ControlParameter> controlParameters);
}
