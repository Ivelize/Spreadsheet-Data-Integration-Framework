package uk.ac.manchester.dstoolkit.repository.impl.hibernate;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.repository.RepositoryFactory;

/**
 * Returns Hibernate-specific instances of DAOs.
 * <p/>
 * If for a particular DAO there is no additional non-CRUD functionality, we use
 * a nested static class to implement the interface in a generic way. This allows clean
 * refactoring later on, should the interface implement business data access
 * methods at some later time. Then, we would externalize the implementation into
 * its own first-level class.
 *
 * @author Christian Bauer
 * @author chedeler
 */
public class HibernateRepositoryFactory extends RepositoryFactory {

    private static Logger log = Logger.getLogger(HibernateRepositoryFactory.class);

    //this probably won't be needed as repositoryClasses are managed by Spring
    
    @SuppressWarnings({ "unused", "unchecked" })
	private HibernateGenericRepository instantiateRepository(Class repositoryClass) {
        try {
            log.debug("Instantiating Repository: " + repositoryClass);
            return (HibernateGenericRepository)repositoryClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Can not instantiate DAO: " + repositoryClass, ex);
        }
    }

    // Inline concrete DAO implementations with no business-related data access methods.
    // If we use public static nested classes, we can centralize all of them in one source file.

    /*
    public static class CategorizedItemDAOHibernate
            extends GenericHibernateDAO<CategorizedItem, CategorizedItem.Id>
            implements CategorizedItemDAO {}

    public static class CommentDAOHibernate
            extends GenericHibernateDAO<Comment, Long>
            implements CommentDAO {}

    public static class ShipmentDAOHibernate
            extends GenericHibernateDAO<Shipment, Long>
            implements ShipmentDAO {}
	*/
}
