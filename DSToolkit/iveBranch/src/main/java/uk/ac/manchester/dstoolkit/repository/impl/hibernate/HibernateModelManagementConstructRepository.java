package uk.ac.manchester.dstoolkit.repository.impl.hibernate;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.repository.ModelManagementConstructRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "modelManagementConstructRepository")
public class HibernateModelManagementConstructRepository extends HibernateGenericRepository<ModelManagementConstruct, Long> implements
		ModelManagementConstructRepository {

	@Transactional(readOnly = true)
	public ModelManagementConstruct fetchAnnotations(ModelManagementConstruct construct) {
		//this.update(mapping);
		//List<Annotation> annotations = 
		//mapping.getAnnotations().size();
		//annotations.size();
		construct = this.update(construct);
		Hibernate.initialize(construct.getAnnotations());
		return construct;
	}
}
