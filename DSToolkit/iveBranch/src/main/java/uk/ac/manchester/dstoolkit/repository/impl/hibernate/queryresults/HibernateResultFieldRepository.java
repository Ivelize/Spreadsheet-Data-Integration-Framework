package uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultFieldRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "resultFieldRepository")
public class HibernateResultFieldRepository
        extends     HibernateGenericRepository<ResultField, Long>
        implements  ResultFieldRepository {

}
