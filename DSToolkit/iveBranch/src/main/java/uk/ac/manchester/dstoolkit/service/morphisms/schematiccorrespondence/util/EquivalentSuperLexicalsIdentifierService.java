/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

/**
 * @author chedeler
 *
 */
public interface EquivalentSuperLexicalsIdentifierService {

	public abstract Map<SuperLexical, Set<SuperLexical>> identifyEquivalentSuperLexicals(
			DerivedOneToOneMatchingsGeneratorService derivedMatchingsGenerator, double maxMatchingScore, boolean isSourceToTarget);

	public abstract Set<SuperLexical> getEquivalentSuperLexicalsForSuperLexical(SuperLexical superLexical, boolean isSource);
}