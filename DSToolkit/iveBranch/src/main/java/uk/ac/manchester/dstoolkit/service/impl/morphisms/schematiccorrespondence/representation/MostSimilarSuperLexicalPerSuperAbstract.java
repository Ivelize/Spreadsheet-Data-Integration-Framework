/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

/**
 * @author chedeler
 *
 */
public class MostSimilarSuperLexicalPerSuperAbstract {

	private SuperLexical superLexical;
	private SuperAbstract superAbstract;
	private DerivedOneToOneMatching derivedOneToOneMatching;
	private double d = -1;

	public MostSimilarSuperLexicalPerSuperAbstract(SuperAbstract superAbstract, SuperLexical superLexical,
			DerivedOneToOneMatching derivedOneToOneMatching) {
		this.superAbstract = superAbstract;
		this.superLexical = superLexical;
		this.derivedOneToOneMatching = derivedOneToOneMatching;
	}

	/**
	 * @return the superLexical
	 */
	public SuperLexical getSuperLexical() {
		return superLexical;
	}

	/**
	 * @param superLexical the superLexical to set
	 */
	public void setSuperLexical(SuperLexical superLexical) {
		this.superLexical = superLexical;
	}

	/**
	 * @return the superAbstract
	 */
	public SuperAbstract getSuperAbstract() {
		return superAbstract;
	}

	/**
	 * @param superAbstract the superAbstract to set
	 */
	public void setSuperAbstract(SuperAbstract superAbstract) {
		this.superAbstract = superAbstract;
	}

	/**
	 * @return the derivedOneToOneMatching
	 */
	public DerivedOneToOneMatching getDerivedOneToOneMatching() {
		return derivedOneToOneMatching;
	}

	/**
	 * @param derivedOneToOneMatching the derivedOneToOneMatching to set
	 */
	public void setDerivedOneToOneMatching(DerivedOneToOneMatching derivedOneToOneMatching) {
		this.derivedOneToOneMatching = derivedOneToOneMatching;
	}

	/**
	 * @return the d
	 */
	public double getD() {
		return d;
	}

	/**
	 * @param d the d to set
	 */
	public void setD(double d) {
		this.d = d;
	}

}
