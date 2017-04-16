package uk.ac.manchester.dstoolkit.repository;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateRepositoryFactory;

/**
 * Defines all DAOs and the concrete factories to get the conrecte DAOs.
 * <p>
 * To get a concrete DAOFactory, call <tt>instance()</tt> with one of the
 * classes that extend this factory.
 * <p>
 * Implementation: If you write a new DAO, this class has to know about it.
 * If you add a new persistence mechanism, add an additional concrete factory
 * for it as a constant, like <tt>HIBERNATE</tt>.
 *
 * @author Christian Bauer
 * @author chedeler
 */
public abstract class RepositoryFactory {

    private static Logger log = Logger.getLogger(RepositoryFactory.class);

    /**
     * Creates a standalone DAOFactory that returns unmanaged DAO
     * beans for use in any environment Hibernate has been configured
     * for. Uses HibernateUtil/SessionFactory and Hibernate context
     * propagation (CurrentSessionContext), thread-bound or transaction-bound,
     * and transaction scoped.
     */
    public static final Class<HibernateRepositoryFactory> HIBERNATE = HibernateRepositoryFactory.class;

    /**
     * Factory method for instantiation of concrete factories.
     */
    public static RepositoryFactory instance(Class<?> factory) {
        try {
            log.debug("Creating concrete Repository factory: " + factory);
            return (RepositoryFactory)factory.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't create DAOFactory: " + factory);
        }
    }

    //TODO this is probably not gonna work anymore, as this is for unmanaged beans, whereas they will be managed by Spring now
    
    // Add your DAO interfaces here
  

}
