package uk.ac.manchester.dstoolkit.repository.impl.hibernate.morphisms.mapping.operators;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.operators.MappingOperatorRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "mappingOperatorRepository")
public class HibernateMappingOperatorRepository extends HibernateGenericRepository<MappingOperator, Long> implements MappingOperatorRepository {

	static Logger logger = Logger.getLogger(HibernateMappingOperatorRepository.class);

	@Override
	@Transactional
	public void save(MappingOperator entity) {
		logger.debug("in save, mappingOperator: " + entity);
		if (entity.getReconcilingExpression() != null) {
			logger.debug("got reconcilingExpression");
			ReconcilingExpression reconcilingExpression = entity.getReconcilingExpression();
			if (reconcilingExpression.getId() == null) {
				logger.debug("reconcilingExpression doesn't have id, persist it");
				em.persist(reconcilingExpression);
				logger.debug("persisted reconcilingExpression");
			} else {
				logger.debug("reconcilingExpression has id, merge it");
				em.merge(reconcilingExpression);
				logger.debug("merged reconcilingExpression");
			}
		}
		em.persist(entity);
		logger.debug("persisted mappingOperator");
	}
}
