/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryService;

/**
 * @author chedeler
 *
 */
@Service(value = "vectorSpaceVectorFactoryService")
public class VectorSpaceVectorFactoryServiceImpl implements VectorSpaceVectorFactoryService {

	public List<VectorSpaceVector> generateVectorSpaceVectorsForEntitySet(ELRChromosome chromosome, ELRPhenotype phenotype, boolean isSource,
			LinkedHashSet<SuperAbstract> entitySet) {
		List<VectorSpaceVector> vectorSpaceVectors = new ArrayList<VectorSpaceVector>();
		if (entitySet.size() == 1) {
			SingleVector singleVector = new SingleVector(chromosome, phenotype, isSource);
			singleVector.setSource(isSource);
			singleVector.generateVector(entitySet);
			vectorSpaceVectors.add(singleVector);
		} else if (entitySet.size() > 1) {
			HorizontalPartitioningVector horizontalPartitioningVector = new HorizontalPartitioningVector(chromosome, phenotype, isSource);
			horizontalPartitioningVector.setSource(isSource);
			horizontalPartitioningVector.generateVector(entitySet);
			vectorSpaceVectors.add(horizontalPartitioningVector);
			VerticalPartitioningVector verticalPartitioningVector = new VerticalPartitioningVector(chromosome, phenotype, isSource);
			verticalPartitioningVector.setSource(isSource);
			verticalPartitioningVector.generateVector(entitySet);
			vectorSpaceVectors.add(verticalPartitioningVector);
		}
		return vectorSpaceVectors;
	}
}
