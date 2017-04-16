/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import java.util.LinkedHashSet;
import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;

/**
 * @author chedeler
 *
 */
public interface VectorSpaceVectorFactoryService {

	public abstract List<VectorSpaceVector> generateVectorSpaceVectorsForEntitySet(ELRChromosome chromosome, ELRPhenotype phenotype,
			boolean isSource, LinkedHashSet<SuperAbstract> entitySet);

}