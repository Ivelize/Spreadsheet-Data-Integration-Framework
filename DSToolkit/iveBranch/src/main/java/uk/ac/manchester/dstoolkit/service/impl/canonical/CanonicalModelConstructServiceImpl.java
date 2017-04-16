/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.canonical;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.CanonicalModelConstructRepository;
import uk.ac.manchester.dstoolkit.service.canonical.CanonicalModelConstructService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "canonicalModelConstructService")
public class CanonicalModelConstructServiceImpl extends GenericEntityServiceImpl<CanonicalModelConstruct, Long> implements
		CanonicalModelConstructService {

	//need to get the instance tuples for each canonical model construct that have been annotated

	@Autowired
	@Qualifier("canonicalModelConstructRepository")
	private CanonicalModelConstructRepository canonicalModelConstructRepository;

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void propagateAnnotationFromQueryResultInstancesToCanonicalModelConstructs(QueryResult queryResult, User user) {
		Set<Mapping> mappings = queryResult.getMappings();
		Query query = queryResult.getQuery();
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		MappingOperator rootOperator = query.getRootOperator();
		//TODO finish this
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.CanonicalModelConstructService#addCanonicalModelConstruct(uk.ac.manchester.dataspaces.domain.models.canonical.CanonicalModelConstruct)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		canonicalModelConstructRepository.save(canonicalModelConstruct);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.CanonicalModelConstructService#deleteCanonicalModelConstruct(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteCanonicalModelConstruct(Long canonicalModelConstructId) {
		//TODO doesn't take into account any associations with anything
		canonicalModelConstructRepository.delete(canonicalModelConstructRepository.find(canonicalModelConstructId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.CanonicalModelConstructService#findCanonicalModelConstruct(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public CanonicalModelConstruct findCanonicalModelConstruct(Long canonicalModelConstructId) {
		return canonicalModelConstructRepository.find(canonicalModelConstructId);
	}

	/**
	 * @param canonicalModelConstructRepository the canonicalModelConstructRepository to set
	 */
	public void setCanonicalModelConstructRepository(CanonicalModelConstructRepository canonicalModelConstructRepository) {
		this.canonicalModelConstructRepository = canonicalModelConstructRepository;
	}

	/**
	 * @return the canonicalModelConstructRepository
	 */
	public CanonicalModelConstructRepository getCanonicalModelConstructRepository() {
		return canonicalModelConstructRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<CanonicalModelConstruct, Long> getRepository() {
		return canonicalModelConstructRepository;
	}

}
