package uk.ac.manchester.dstoolkit.service.canonical;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface CanonicalModelConstructService extends GenericEntityService<CanonicalModelConstruct, Long> {

	/**
	 * @param canonicalModelConstructId
	 * @return canonicalModelConstruct
	 */
	public CanonicalModelConstruct findCanonicalModelConstruct(Long canonicalModelConstructId);

	/**
	 * @param canonicalModelConstruct
	 */
	public void addCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct);

	/**
	 * @param canonicalModelConstructId
	 */
	public void deleteCanonicalModelConstruct(Long canonicalModelConstructId);
}
