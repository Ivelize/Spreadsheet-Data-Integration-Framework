package uk.ac.manchester.dstoolkit.service.impl.util.benchmark;

/***
 * 
 * This Enum type allows us to choose the policy for selecting individual cells from the 
 * matrices for dealing with the dampening effort that is caused due to the insafficient
 * occurrences of semantic evidence.
 * 
 * 
 * Policy:
 * 	- SOME_EVIDENCE: it will select all the individual cells from the matrix that have assimilated some 
 * 					 evidence, for example given the list CSN, CSP it will select the cells that have
 * 					 assimilated some evidence from the list either: CSN or CSP or (CSN and CSP).
 *                   This is the wrong behaviour however this option allows us to choose this behaviour.
 * 
 *  - COMBINATION_OF_EVIDENCE: example case (CSN and CSP). This policy will select only the cells from the
 *  						   matrix that have assimilated the combination of evidence (CSN and CSP). The
 *  						   policy uses a method that keeps track of which semantic evidence each cell 
 *  						   from the matrix has assimilated so far. (This is the correct behaviour). 
 * 
 * @author christk6
 *
 */
public enum DampeningEffectPolicy {

	SOME_EVIDENCE("SOME_EVIDENCE"),
	COMBINATION_OF_EVIDENCE("COMBINATION_OF_EVIDENCE");
	
	private final String value;

	DampeningEffectPolicy (String v) {
		value = v;
    }
	
    public static DampeningEffectPolicy fromValue(String v) {
        for (DampeningEffectPolicy c: DampeningEffectPolicy.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }	
	
}//end DampeningEffectPolicy
