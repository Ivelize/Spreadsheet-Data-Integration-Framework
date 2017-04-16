package uk.ac.manchester.dstoolkit.repository.impl.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 * @param <T>
 * @param <ID>
 */

public abstract class HibernateGenericRepository<T extends DomainEntity, ID extends Serializable> implements GenericRepository<T, ID> {

	private static Logger logger = Logger.getLogger(HibernateGenericRepository.class);
	private final Class<T> type;

	/*
	 * JPA
	 */

	@PersistenceContext
	protected EntityManager em;


	/*
	 * Hibernate
	 */

	//@PersistenceContext
	//private SessionFactory sessionFactory;

	//private Session session;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public HibernateGenericRepository() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
	
	}

	/*
	 * JPA methods
	 */

	@Transactional(readOnly = true)
	public void refresh(T entity) {
		if (em.contains(entity)) {
			em.refresh(entity);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<T> findAll() {
		System.out.println("genericrepository");
		return em.createQuery("select o from " + type.getName() + " o ").getResultList();
	}

	@Transactional(readOnly = true)
	public Long getNextId() {
		List resultList = em.createNativeQuery("select nextval ('hibernate_sequence')").getResultList();
		Long id = ((BigInteger) resultList.get(0)).longValue();
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#find(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public T find(Long id) {
		logger.debug("in find");
		logger.debug("type: " + type);
		return em.find(type, id);
		/*
		Query query = em.createQuery("select o from " + entityType.getName() + " o where o.id = :id");
		query.setParameter("id", id);
		return (T) query.getSingleResult();
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#update(java.lang.Object)
	 */
	@Transactional
	public T update(T entity) {
		logger.debug("in update, entity: " + entity);
		return em.merge(entity);
	}

	@Transactional(readOnly = true)
	public void fetch(List<T> collection) {
		logger.debug("in fetch collection");
		Hibernate.initialize(collection);
	}

	@Transactional(readOnly = true)
	public void fetch(Set<T> collection) {
		logger.debug("in fetch collection");
		Hibernate.initialize(collection);
	}

	@Transactional
	public void save(T entity) {
		logger.debug("in save, entity: " + entity);
		Validate.notNull(entity, "The entity to save cannot be null element");
		em.persist(entity);
	}

	@Transactional
	public void save(Iterable<T> entities) {
		Validate.notNull(entities, "The entities to save cannot be null");

		for (T entity : entities) {
			save(entity);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Save List done");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#delete(java.lang.Object)
	 */
	@Transactional
	public void delete(T entity) {
		//em.remove(entity);
		if (getEm().contains(entity)) {
			getEm().remove(entity);
		} else {
			// could be a delete on a transient instance
			T entityRef = getEm().getReference(type, entity.getId());

			if (entityRef != null) {
				getEm().remove(entityRef);
			} else {
				logger.warn("Attempt to delete an instance that is not present in the database: " + entity.toString());
			}
		}
	}

	@Transactional
	public void delete(Iterable<T> entities) {
		Validate.notNull(entities, "Cannot delete null collection");
		for (T entity : entities) {
			delete(entity);
		}
	}

	/*
	 * Hibernate methods
	 */

	/**
	 * @return class
	 */
	public Class<T> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#findByExample(java.lang.Object, java.lang.String...)
	 */
	/*
	@SuppressWarnings("unchecked")
	public List<T> findByExample(T exampleInstance, String... excludeProperty) {
		Criteria crit = getSession().createCriteria(getEntityType());
		Example example = Example.create(exampleInstance);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit.list();
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#makePersistent(java.lang.Object)
	 */
	/*
	public T makePersistent(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#makeTransient(java.lang.Object)
	 */
	/*
	public void makeTransient(T entity) {
		getSession().delete(entity);
	}
	*/

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#flush()
	 */
	public void flush() {
		em.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.GenericRepository#clear()
	 */
	public void clear() {
		em.clear();
	}

	/**
	 * Use this inside subclasses as a convenience method.
	 * @param criterion...
	 * @return entities
	 */
	/*
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(Criterion... criterion) {
		Criteria crit = getSession().createCriteria(getEntityType());
		for (Criterion c : criterion) {
			crit.add(c);
		}
		return crit.list();
	}
	*/

	/**
	 * @return Session
	 */
	/*
	protected Session getSession() {
		if (session == null)
			session = sessionFactory.getCurrentSession();
		//session = HibernateUtil.getSessionFactory().getCurrentSession();
		return session;
	}
	*/

	/**
	 * @return the em
	 */
	public EntityManager getEm() {
		return em;
	}

	/**
	 * @param em the em to set
	 */
	public void setEm(EntityManager em) {
		this.em = em;
	}

	protected Session getCurrentSession() {
		return (Session) getEm().getDelegate();
	}

	/**
	 * @return the sessionFactory
	 */
	/*
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	*/

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	/*
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	*/

	/**
	 * @param session the session to set
	 */
	/*
	public void setSession(Session session) {
		this.session = session;
	}
	*/

}
