/*
 * (c) Copyright 2005-2011 JAXIO, www.jaxio.com
 * Source code generated by Celerio, a Jaxio product
 * Want to use Celerio within your company? email us at info@jaxio.com
 * Follow us on twitter: @springfuse
 * Template pack-backend:src/main/java/project/manager/support/GenericEntityServiceImpl.p.vm.java
 */
package uk.ac.manchester.dstoolkit.service.impl;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * Default implementation of the {@link GenericEntityService}
 * @see GenericEntityService
 */
public abstract class GenericEntityServiceImpl<T extends DomainEntity, ID extends Serializable> implements GenericEntityService<T, ID> {

	private final Logger logger;

	public GenericEntityServiceImpl() {
		this.logger = Logger.getLogger(getClass());
	}

	abstract public GenericRepository<T, ID> getRepository();

	/**
	 * {@inheritDoc}
	 */
	//public abstract T getNew();

	/**
	 * {@inheritDoc}
	 */
	//public abstract T getNewWithDefaults();

	//-------------------------------------------------------------
	//  Save methods
	//-------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void save(T model) {
		getRepository().save(model);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void save(Iterable<T> models) {
		getRepository().save(models);
	}

	//-------------------------------------------------------------
	//  Get and Delete methods (primary key or unique constraints)
	//-------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T getByPrimaryKey(PK primaryKey) {
		T entity = getNew();
		entity.setPrimaryKey(primaryKey);
		return get(entity);
	}
	*/
	public T getById(Long id) {
		return getRepository().find(id);
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional
	public void deleteByPrimaryKey(PK primaryKey) {
		delete(getByPrimaryKey(primaryKey));
	}
	*/
	@Transactional
	public void deleteById(Long id) {
		getRepository().delete(getById(id));
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T get(T model) {
		return getRepository().get(model);
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void delete(T model) {
		if (model == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping deletion for null model!");
			}

			return;
		}

		getRepository().delete(model);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void delete(Iterable<T> models) {
		getRepository().delete(models);
	}

	//-------------------------------------------------------------
	//  Refresh
	//-------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	public void refresh(T entity) {
		getRepository().refresh(entity);
	}

	//-------------------------------------------------------------
	//  Finders methods
	//-------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T findUnique(T model) {
		return findUnique(model, new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T findUnique(T model, SearchTemplate searchTemplate) {
		T result = findUniqueOrNone(model, searchTemplate);

		if (result == null) {
			throw new InvalidDataAccessApiUsageException("Developper: You expected 1 result but we found none ! sample: " + model);
		}

		return result;
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T findUniqueOrNone(T model) {
		return findUniqueOrNone(model, new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public T findUniqueOrNone(T model, SearchTemplate searchTemplate) {
		// this code is an optimisation to prevent using a count
		// we request at most 2, if there's more than one then we throw an InvalidDataAccessApiUsageException
		SearchTemplate searchTemplateBounded = new SearchTemplate(searchTemplate);
		searchTemplateBounded.setFirstResult(0);
		searchTemplateBounded.setMaxResults(2);
		List<T> results = find(model, searchTemplateBounded);

		if (results == null || results.isEmpty()) {
			return null;
		}

		if (results.size() > 1) {
			throw new InvalidDataAccessApiUsageException("Developer: You expected 1 result but we found more ! sample: " + model);
		}

		return results.iterator().next();
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public List<T> find() {
		return find(getNew(), new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public List<T> find(T model) {
		return find(model, new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public List<T> find(SearchTemplate searchTemplate) {
		return find(getNew(), searchTemplate);
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public List<T> find(T model, SearchTemplate searchTemplate) {
		return getDao().find(model, searchTemplate);
	}
	*/

	//-------------------------------------------------------------
	//  Count methods
	//-------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public int findCount() {
		return findCount(getNew(), new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public int findCount(T model) {
		return findCount(model, new SearchTemplate());
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public int findCount(SearchTemplate searchTemplate) {
		return findCount(getNew(), searchTemplate);
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	/*
	@Transactional(readOnly = true)
	public int findCount(T model, SearchTemplate searchTemplate) {
		return getDao().findCount(model, searchTemplate);
	}
	*/
}