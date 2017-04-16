/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.mapping.operators;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.operators.MappingOperatorRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.operators.MappingOperatorService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "mappingOperatorService")
public class MappingOperatorServiceImpl extends GenericEntityServiceImpl<MappingOperator, Long> implements MappingOperatorService {

	private static Logger logger = Logger.getLogger(MappingOperatorServiceImpl.class);

	@Autowired
	@Qualifier("mappingOperatorRepository")
	private MappingOperatorRepository mappingOperatorRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.operators.MappingOperatorService#addMappingOperator(uk.ac.manchester.dataspaces.domain.models.mapping.operators.MappingOperator)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addMappingOperator(MappingOperator mappingOperator) {
		mappingOperatorRepository.save(mappingOperator);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.operators.MappingOperatorService#deleteMappingOperator(uk.ac.manchester.dataspaces.domain.models.mapping.operators.MappingOperator)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteMappingOperator(Long mappingOperatorId) {
		// TODO
		mappingOperatorRepository.delete(mappingOperatorRepository.find(mappingOperatorId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.operators.MappingOperatorService#findMappingOperator(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public MappingOperator findMappingOperator(Long mappingOperatorId) {
		return mappingOperatorRepository.find(mappingOperatorId);
	}

	/**
	 * @param mappingOperatorRepository the mappingOperatorRepository to set
	 */
	public void setMappingOperatorRepository(MappingOperatorRepository mappingOperatorRepository) {
		this.mappingOperatorRepository = mappingOperatorRepository;
	}

	/**
	 * @return the mappingOperatorRepository
	 */
	public MappingOperatorRepository getMappingOperatorRepository() {
		return mappingOperatorRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<MappingOperator, Long> getRepository() {
		return mappingOperatorRepository;
	}

}
