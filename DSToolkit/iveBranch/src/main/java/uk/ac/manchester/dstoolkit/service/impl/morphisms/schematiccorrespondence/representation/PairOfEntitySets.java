/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation;

import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

/**
 * @author chedeler
 *
 */
public class PairOfEntitySets {

	private final Set<SuperAbstract> sourceEntitySet = new HashSet<SuperAbstract>();
	private final Set<SuperAbstract> targetEntitySet = new HashSet<SuperAbstract>();

	public PairOfEntitySets(SuperAbstract sourceSuperAbstract, SuperAbstract targetSuperAbstract) {
		this.addSourceEntity(sourceSuperAbstract);
		this.addTargetEntity(targetSuperAbstract);
	}

	public PairOfEntitySets(Set<SuperAbstract> sourceSuperAbstracts, Set<SuperAbstract> targetSuperAbstracts) {
		this.addSourceEntities(sourceSuperAbstracts);
		this.addTargetEntities(targetSuperAbstracts);
	}

	public void addSourceEntity(SuperAbstract sourceSuperAbstract) {
		this.sourceEntitySet.add(sourceSuperAbstract);
	}

	public void addTargetEntity(SuperAbstract targetSuperAbstract) {
		this.targetEntitySet.add(targetSuperAbstract);
	}

	public void addSourceEntities(Set<SuperAbstract> sourceSuperAbstracts) {
		this.sourceEntitySet.addAll(sourceSuperAbstracts);
	}

	public void addTargetEntities(Set<SuperAbstract> targetSuperAbstracts) {
		this.targetEntitySet.addAll(targetSuperAbstracts);
	}

	/**
	 * @return the sourceEntitySet
	 */
	public Set<SuperAbstract> getSourceEntitySet() {
		return sourceEntitySet;
	}

	/**
	 * @return the targetEntitySet
	 */
	public Set<SuperAbstract> getTargetEntitySet() {
		return targetEntitySet;
	}
}
