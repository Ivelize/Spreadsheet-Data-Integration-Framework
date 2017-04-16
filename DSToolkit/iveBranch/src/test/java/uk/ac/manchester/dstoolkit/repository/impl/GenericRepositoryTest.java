package uk.ac.manchester.dstoolkit.repository.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

@Transactional
public abstract class GenericRepositoryTest<T extends DomainEntity, ID extends Serializable> extends AbstractIntegrationTest {

	@PersistenceContext
	EntityManager em;

	public abstract GenericRepository<T, ID> getRepository();

	@Test
	public void load() {
		// nothing to do here
	}

	public T verifyFindExisting(Long id) {
		T entity = getRepository().find(id);
		assertNotNull(entity);
		assertTrue(id.equals(entity.getId()));
		return entity;
	}

	public T verifySave(T newEntity) {
		getRepository().save(newEntity);
		Long id = newEntity.getId();

		flush();

		T savedEntity = getRepository().find(id);
		assertNotNull(savedEntity);
		return savedEntity;
	}

	public T verifyUpdate(T newEntity) {
		getRepository().update(newEntity);
		Long id = newEntity.getId();

		flush();

		T savedEntity = getRepository().find(id);
		assertNotNull(savedEntity);
		return savedEntity;
	}

	public void verifyDelete(Long id) {
		getRepository().delete(getRepository().find(id));

		flush();

		T deletedEntity = getRepository().find(id);
		assertNull(deletedEntity);
	}

	public void flush() {
		em.flush();
		em.clear();
	}
}