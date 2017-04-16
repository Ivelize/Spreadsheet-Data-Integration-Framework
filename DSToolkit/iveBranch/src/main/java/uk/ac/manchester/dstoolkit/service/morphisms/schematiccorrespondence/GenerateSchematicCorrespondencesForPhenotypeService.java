/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence;

import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;

/**
 * @author chedeler
 *
 */
public interface GenerateSchematicCorrespondencesForPhenotypeService {

	public abstract Set<SchematicCorrespondence> generateSchematicCorrespondencesForBestPhenotype(ELRPhenotype bestPhenotype);

}