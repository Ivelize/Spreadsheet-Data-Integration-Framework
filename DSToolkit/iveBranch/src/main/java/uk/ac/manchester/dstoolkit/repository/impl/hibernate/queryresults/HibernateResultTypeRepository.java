package uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultTypeRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "resultTypeRepository")
public class HibernateResultTypeRepository
        extends     HibernateGenericRepository<ResultType, Long>
        implements  ResultTypeRepository {
}
