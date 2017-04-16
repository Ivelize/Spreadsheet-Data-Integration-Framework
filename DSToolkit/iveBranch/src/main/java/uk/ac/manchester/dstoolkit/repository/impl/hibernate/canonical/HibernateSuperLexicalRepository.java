package uk.ac.manchester.dstoolkit.repository.impl.hibernate.canonical;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "superLexicalRepository")
public class HibernateSuperLexicalRepository extends HibernateGenericRepository<SuperLexical, Long> implements SuperLexicalRepository {

	static Logger logger = Logger.getLogger(HibernateSuperLexicalRepository.class);

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperLexicalRepository#getAllSuperLexicalsWithNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsWithNameInSchemaWithName(String superLexicalName, String schemaName) {
		logger.debug("in getAllSuperLexicalsWithNameInSchemaWithName");
		logger.debug("superLexicalName: " + superLexicalName);
		logger.debug("schemaName: " + schemaName);
		Query query = em.createQuery("select sl from SuperLexical sl where sl.name = :name and sl.schema.name = :schemaName");
		query.setParameter("name", superLexicalName).setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperLexicalRepository#getAllSuperLexicalsInSchemaWithName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsInSchemaWithName(String schemaName) {
		logger.debug("in getAllSuperLexicalsInSchemaWithName");
		logger.debug("schemaName: " + schemaName);
		Query query = em.createQuery("select sl from SuperLexical sl where sl.schema.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public SuperLexical getSuperLexicalWithNameOfSuperAbstract(String superLexicalName, SuperAbstract superAbstract) {
		logger.debug("in getSuperLexicalWithNameOfSuperAbstract");
		logger.debug("superLexicalName: " + superLexicalName);
		logger.debug("superAbstract: " + superAbstract);
		SuperLexical superLexical = null;
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.parentSuperAbstract.id = :superAbstractId and sl.name =: superLexicalName");
		query.setParameter("superAbstractId", superAbstract.getId()).setParameter("superLexicalName", superLexicalName);
		try {
			superLexical = (SuperLexical) query.getSingleResult();
			logger.debug("superLexical: " + superLexical);
		} catch (NoResultException ex) {
			logger.debug("didn't find superLexical, check nesting");
			query = em
					.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where sl.schema.id = :schemaId and "
							+ "sl.name = :superLexicalName and psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
							+ "sa.id = :superAbstractId)");
			query.setParameter("schemaId", superAbstract.getSchema().getId()).setParameter("superAbstractId", superAbstract.getId())
					.setParameter("superLexicalName", superLexicalName);
			try {
				superLexical = (SuperLexical) query.getSingleResult();
				logger.debug("superLexical: " + superLexical);
			} catch (NoResultException ex1) {
				logger.debug("didn't find superLexical with nesting");
			}
		}
		return superLexical;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperLexicalRepository#getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional(readOnly = true)
	public SuperLexical getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName(String superLexicalName, String superAbstractName,
			String schemaName) {
		logger.debug("in getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName");
		logger.debug("superLexicalName: " + superLexicalName);
		logger.debug("superAbstractName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);
		SuperLexical superLexical = null;
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.name = :slName and sl.parentSuperAbstract.name = :saName and sl.schema.name = :schemaName");
		query.setParameter("slName", superLexicalName).setParameter("saName", superAbstractName).setParameter("schemaName", schemaName);

		logger.debug("query: " + query.toString());
		try {
			superLexical = (SuperLexical) query.getSingleResult();
			logger.debug("superLexical: " + superLexical);
		} catch (NoResultException ex) {
			logger.debug("didn't find superLexical, check nesting");

			query = em
					.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where sl.schema.name = :schemaName and "
							+ "sl.name = :slName and psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
							+ "sa.name = :saName and sa.schema.name = :schemaName)");
			query.setParameter("slName", superLexicalName).setParameter("saName", superAbstractName).setParameter("schemaName", schemaName);
			try {
				superLexical = (SuperLexical) query.getSingleResult();
				logger.debug("superLexical: " + superLexical);
			} catch (NoResultException ex1) {
				logger.debug("didn't find superLexical with nesting");
			}
		}
		return superLexical;
	}

	@Transactional(readOnly = true)
	public SuperLexical getSuperLexicalWithNameOfParentSuperLexicalWithNameInSchemaWithName(String superLexicalName, String parentSuperLexicalName,
			String schemaName) {
		logger.debug("in getSuperLexicalWithNameOfParentSuperLexicalWithNameInSchemaWithName");
		logger.debug("superLexicalName: " + superLexicalName);
		logger.debug("parentSuperLexicalName: " + parentSuperLexicalName);
		logger.debug("schemaName: " + schemaName);
		SuperLexical superLexical = null;
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.name = :slName and sl.parentSuperLexical = :pslName and sl.schema.name = :schemaName");
		query.setParameter("slName", superLexicalName).setParameter("pslName", parentSuperLexicalName).setParameter("schemaName", schemaName);

		logger.debug("query: " + query.toString());
		try {
			superLexical = (SuperLexical) query.getSingleResult();
		} catch (NoResultException ex) {
			logger.debug("didn't find superLexical, check nesting");
			query = em
					.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where sl.schema.name = :schemaName and "
							+ "sl.name = :slName and psrsl.superRelationship srsl in (select psrpsl.superRelationship from SuperLexical psl join psl.participationInSuperRelationships psrpsl where "
							+ "psl.name = :pslName and psl.schema.name = :schemaName)");
			query.setParameter("slName", superLexicalName).setParameter("pslName", parentSuperLexicalName).setParameter("schemaName", schemaName);
			try {
				superLexical = (SuperLexical) query.getSingleResult();
				logger.debug("superLexical: " + superLexical);
			} catch (NoResultException ex1) {
				logger.debug("didn't find superLexical with nesting");
			}
		}
		return superLexical;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllSuperLexicalsOfSuperAbstract(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstract(Long superAbstractId) {
		logger.debug("in getAllSuperLexicalsOfSuperAbstract");
		logger.debug("superAbstractId: " + superAbstractId);
		List<SuperLexical> superLexicals = new ArrayList<SuperLexical>();
		Query query = em.createQuery("select sl from SuperLexical sl where sl.parentSuperAbstract.id = :superAbstractId");
		query.setParameter("superAbstractId", superAbstractId);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex) {
			logger.error("didn't find any superLexicals");
		}
		logger.debug("superLexicals.size(): " + superLexicals.size());
		logger.debug("check nesting");
		query = em
				.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where "
						+ "psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
						+ "sa.is = :superAbstractId)");
		query.setParameter("superAbstractId", superAbstractId);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex1) {
			logger.debug("didn't find superLexical with nesting");
		}
		if (superLexicals.size() > 0)
			return superLexicals;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllSuperLexicalsOfSuperAbstractWithNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstractWithNameInSchemaWithName(String superAbstractName, String schemaName) {
		logger.debug("in getAllSuperLexicalsOfSuperAbstractWithNameInSchemaWithName");
		logger.debug("superAbstractName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);
		List<SuperLexical> superLexicals = new ArrayList<SuperLexical>();
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.parentSuperAbstract.name = :superAbstractName and sl.schema.name = :schemaName");
		query.setParameter("superAbstractName", superAbstractName).setParameter("schemaName", schemaName);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex) {
			logger.error("didn't find any superLexicals");
		}
		logger.debug("superLexicals.size(): " + superLexicals.size());
		logger.debug("check nesting");
		query = em
				.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where sl.schema.name = :schemaName and "
						+ "psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
						+ "sa.name = :superAbstractName and sa.schema.name =: schemaName)");
		query.setParameter("superAbstractName", superAbstractName).setParameter("schemaName", schemaName);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex1) {
			logger.debug("didn't find superLexical with nesting");
		}
		if (superLexicals.size() > 0)
			return superLexicals;
		else
			return null;
	}

	//TODO not checking for and getting all nested childElements of a superAbstract, e.g.  city.located_at.watertype & city.located_at.lake is missing

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllSuperLexicalsOfSuperAbstractOrderedById(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstractOrderedById(Long superAbstractId) {
		logger.debug("in getAllSuperLexicalsOfSuperAbstractOrderedById");
		logger.debug("superAbstractId: " + superAbstractId);
		List<SuperLexical> superLexicals = new ArrayList<SuperLexical>();
		Query query = em.createQuery("select sl from SuperLexical sl where sl.parentSuperAbstract.id = :superAbstractId order by sl.id");
		query.setParameter("superAbstractId", superAbstractId);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex) {
			logger.error("didn't find any superLexicals");
		}
		logger.debug("superLexicals.size(): " + superLexicals.size());
		logger.debug("check nesting");
		query = em
				.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where "
						+ "psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
						+ "sa.id = :superAbstractId) order by sl.id");
		query.setParameter("superAbstractId", superAbstractId);
		List<SuperLexical> sls = new ArrayList<SuperLexical>();
		try {
			sls.addAll(query.getResultList());
		} catch (NoResultException ex1) {
			logger.debug("didn't find superLexical with nesting");
		}

		List<SuperLexical> resultList = new ArrayList<SuperLexical>();

		int totalSize = sls.size() + superLexicals.size();

		while (resultList.size() < totalSize) {
			logger.debug("resultList.size(): " + resultList.size());
			if (sls.size() > 0 && superLexicals.size() > 0) {
				if (sls.get(0).getId() < superLexicals.get(0).getId()) {
					resultList.add(sls.get(0));
					sls.remove(0);
				} else {
					resultList.add(superLexicals.get(0));
					superLexicals.remove(0);
				}
			} else if (sls.size() == 0 && superLexicals.size() > 0) {
				resultList.addAll(superLexicals);
			} else if (sls.size() > 0 && superLexicals.size() == 0) {
				resultList.addAll(sls);
			}
		}
		if (resultList.size() > 0)
			return resultList;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllPrimaryKeySuperLexicalsOfSuperAbstractWithNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllPrimaryKeySuperLexicalsOfSuperAbstractWithNameInSchemaWithName(String superAbstractName, String schemaName) {
		logger.debug("in getAllPrimaryKeySuperLexicalsOfSuperAbstractWithNameInSchemaWithName");
		logger.debug("superAbstractName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);
		List<SuperLexical> superLexicals = new ArrayList<SuperLexical>();
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.parentSuperAbstract.name = :superAbstractName and sl.schema.name = :schemaName and sl.isIdentifier = true");
		query.setParameter("superAbstractName", superAbstractName).setParameter("schemaName", schemaName);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex) {
			logger.error("didn't find any superLexicals");
		}
		logger.debug("superLexicals.size(): " + superLexicals.size());
		logger.debug("check nesting");
		query = em
				.createQuery("select sl from SuperLexical sl join sl.participationInSuperRelationships psrsl where sl.schema.name = :schemaName and sl.isIdentifier = true and "
						+ "psrsl.superRelationship in (select psrsa.superRelationship from SuperAbstract sa join sa.participationInSuperRelationships psrsa where "
						+ "sa.name = :superAbstractName and sa.schema.name =: schemaName)");
		query.setParameter("superAbstractName", superAbstractName).setParameter("schemaName", schemaName);
		try {
			superLexicals.addAll(query.getResultList());
		} catch (NoResultException ex1) {
			logger.debug("didn't find superLexical with nesting");
		}
		if (superLexicals.size() > 0)
			return superLexicals;
		else
			return null;
	}

}
