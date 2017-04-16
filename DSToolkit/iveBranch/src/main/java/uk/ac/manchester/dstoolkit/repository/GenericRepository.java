package uk.ac.manchester.dstoolkit.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;

/**
 * @author chedeler
 *
 * @param <T>
 * @param <ID>
 */

public interface GenericRepository<T extends DomainEntity, ID extends Serializable> {

	/*
	 * JPA methods
	 */

	public void refresh(T entity);

	/**
	 * @return entities
	 */
	public List<T> findAll();

	public void fetch(List<T> collection);

	public void fetch(Set<T> collection);

	/**
	 * @param id
	 * @return entity
	 */
	public T find(Long id);

	public Long getNextId();

	/**
	 * @param entity
	 */
	public T update(T entity);

	//public T merge(T entity);

	/**
	 * @param entity
	 */
	public void save(T entity);

	public void save(Iterable<T> entities);

	/**
	 * @param entity
	 */
	public void delete(T entity);

	public void delete(Iterable<T> entities);

	/*
	 * Hibernate specific methods
	 */

	/**
	 * @param exampleInstance
	 * @param excludeProperty
	 * @return entities
	 */
	//public List<T> findByExample(T exampleInstance, String... excludeProperty);

	/**
	 * @param entity
	 * @return entity
	 */
	//public T makePersistent(T entity);

	/**
	 * @param entity
	 */
	//public void makeTransient(T entity);

	/**
	 * Affects every managed instance in the current persistence context!
	 */
	public void flush();

	/**
	 * Affects every managed instance in the current persistence context!
	 */
	public void clear();

}
