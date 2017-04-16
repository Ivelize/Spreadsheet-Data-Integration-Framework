/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query.queryresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultFieldRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultTypeRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.queryresults.ResultTypeService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "resultTypeService")
public class ResultTypeServiceImpl extends GenericEntityServiceImpl<ResultType, Long> implements ResultTypeService {

	@Autowired
	@Qualifier("resultTypeRepository")
	private ResultTypeRepository resultTypeRepository;

	@Autowired
	@Qualifier("resultFieldRepository")
	private ResultFieldRepository resultFieldRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultTypeService#addResultFieldToResultType(uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultType, uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultField)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addResultFieldToResultType(ResultType resultType, ResultField resultField) {
		//resultType.getResultFields().add(resultField);
		resultType.addResultField(resultField.getFieldName(), resultField);
		resultTypeRepository.update(resultType);
		resultFieldRepository.update(resultField);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultTypeService#addResultType(uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultType)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addResultType(ResultType resultType) {
		resultTypeRepository.save(resultType);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultTypeService#deleteResultType(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteResultType(Long resultTypeId) {
		// TODO
		resultTypeRepository.delete(resultTypeRepository.find(resultTypeId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultTypeService#findResultType(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public ResultType findResultType(Long resultTypeId) {
		return resultTypeRepository.find(resultTypeId);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultTypeService#removeResultFieldToResultType(uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultType, uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultField)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void removeResultFieldFromResultType(ResultType resultType, ResultField resultField) {
		// TODO resultField is deleted from repository
		resultType.removeResultField(resultField.getFieldName());
		resultTypeRepository.update(resultType);
		resultFieldRepository.delete(resultField);
	}

	/**
	 * @param resultTypeRepository the resultTypeRepository to set
	 */
	public void setResultTypeRepository(ResultTypeRepository resultTypeRepository) {
		this.resultTypeRepository = resultTypeRepository;
	}

	/**
	 * @return the resultTypeRepository
	 */
	public ResultTypeRepository getResultTypeRepository() {
		return resultTypeRepository;
	}

	/**
	 * @param resultFieldRepository the resultFieldRepository to set
	 */
	public void setResultFieldRepository(ResultFieldRepository resultFieldRepository) {
		this.resultFieldRepository = resultFieldRepository;
	}

	/**
	 * @return the resultFieldRepository
	 */
	public ResultFieldRepository getResultFieldRepository() {
		return resultFieldRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<ResultType, Long> getRepository() {
		return resultTypeRepository;
	}

}
