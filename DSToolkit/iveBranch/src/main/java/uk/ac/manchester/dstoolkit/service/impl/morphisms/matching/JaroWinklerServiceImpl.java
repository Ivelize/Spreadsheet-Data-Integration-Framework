package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;

/****
 * This is an implementation of the Jaro–Winkler distance which is a measure of similarity between two strings.
 * It is a variant of the Jaro distance metric, a type of string edit distance, and mainly used in the area of
 * record linkage (duplicate detection). See Wikipedia article for more information.
 * 
 * This class constructs:
 * (1) the orginigal Jaro string distance without the Winkler modifications.
 * (2) Winkler-modified Jaro string distance with the specified weight threshold for refinement and an initial
 * number of characters over which to reweight.
 * 
 * 
 * Inspired by: http://en.wikipedia.org/wiki/Jaro-Winkler_distance
 * 
 * @author klitos
 *
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class JaroWinklerServiceImpl extends StringBasedMatcherServiceImpl {

	private static Logger logger = Logger.getLogger(JaroWinklerServiceImpl.class);
	
	@Autowired
	private MatchingProducerService matchingProducerService;
	
	private final double mWeightThreshold;
	private final int mNumChars;
	
	//A constant for Jaro distance - This is the same as using the nullary constructo (this is constructor 1)	
	public static final JaroWinklerServiceImpl JARO_DISTANCE = new JaroWinklerServiceImpl();	
	
	//A constant for the Jaro-Winkler distance with defaults set as in Winkler's papers (this is constructor 2) 
	public static final JaroWinklerServiceImpl JARO_WINKLER_DISTANCE = new JaroWinklerServiceImpl(0.70,4);
	
	/***
	 * Constructor 1: Construct a basic Jaro string distance without the Winkler modifications.
	 */
	public JaroWinklerServiceImpl() {
	    this(Double.POSITIVE_INFINITY,0);
	}
	
	/***
	 * Constructor 2: Construct a Winkler-modified Jaro string distance with the specified weight threshold for refinement
	 * and an initial number of characters over which to reweight.
	 */
	public JaroWinklerServiceImpl(double weightThreshold, int numChars) {
		this.setMatcherType(MatcherType.JARO_WINKLER);
	    mNumChars = numChars;
	    mWeightThreshold = weightThreshold;
	}
	
	/***
	 * Method that returns the Jaro-Winkler between the specified strings 
	 */
	@Override
	protected float match(String string1, String string2) {
		CharSequence charSeq1 = string1;
		CharSequence charSeq2 = string2;
		
		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0) {
			logger.error("JaroWinkler matcher has child matcher - shoudn't be the case - TODO sort this");
		}//end if

		/*Return 0 if strings are null*/
		if ((string1 == null) || (string2 == null) || (string1.length() == 0) || (string2.length() == 0)) {
			return 0.0F;
		}//end if
		
		//Call method to calculate Jaro distance
		float sim = (float) this.jaroWinklerComparison(charSeq1, charSeq2);
		logger.debug("JaroWinkler sim: " + sim);
				
		return sim;
	}

	/***
	 * This method runs the matcher over the names of constructs from the schemata.
	 */
	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {

		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0) {
			logger.error("This is a Primitive matcher cannot have child matchers.");
			logger.error("JaroWinkler matcher has child matcher - shouldn't be the case - TODO sort this");
			return null;
		}	
		
		/*Construct a similarity matrix that will hold the match similarity scores
		 *   - contructs1 (source constructs at the rows)
		 *   - contructs2 (target constructs at the columns)
		 */
		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		logger.info("constructs1.size(): " + constructs1.size());
		logger.info("constructs2.size(): " + constructs2.size());
		/*Monitor the time to start the construction of the similarity matrix*/
		logger.info("Matching the schemata using : JaroWinkler");
		long startTime = System.nanoTime();
		logger.info("start matching all constructs with JaroWinkler: " + startTime);
		for (CanonicalModelConstruct construct1 : constructs1) {		
			for (CanonicalModelConstruct construct2 : constructs2) {	
				logger.debug("construct1.getName: " + construct1.getName());
				logger.debug("construct2.getName: " + construct2.getName());
				long startTimeSingleMatch = System.nanoTime();
				logger.info("start matching two constructs with JaroWinkler: " + startTimeSingleMatch);
				simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = this.match(construct1.getName(), construct2.getName());
				long endTimeSingleMatch = System.nanoTime();
				logger.info("finished matching two constructs with StringBasedMatcher: " + endTimeSingleMatch);
				logger.info("duration for matching two constructs (in seconds): " + (endTimeSingleMatch - startTimeSingleMatch) / 1.0e9);				
			}
		}
		long endTime = System.nanoTime();
		logger.info("finished matching all constructs with JaroWinkler: " + endTime);
		logger.info("duration for matching all constructs (in seconds): " + (endTime - startTime) / 1.0e9);
		return simMatrix;
	}	
	
	
	/**
	 * Return a normalised Jaro-Winkler distance, this will return a similarity score in the 
	 * range of [0, 1] where 0 means no match and 1 perfect match.
	 * 
	 * @param charSeq1
	 * @param charSeq2
	 * @return 
	 */
	private double jaroWinklerComparison(CharSequence charSeq1, CharSequence charSeq2) {
	    int len1 = charSeq1.length();
	    int len2 = charSeq2.length();
	    if (len1 == 0)
	        return len2 == 0 ? 1.0 : 0.0;

	    int searchRange = Math.max(0,Math.max(len1,len2)/2 - 1);

	    boolean[] matched1 = new boolean[len1];
	    Arrays.fill(matched1,false);
	    boolean[] matched2 = new boolean[len2];
	    Arrays.fill(matched2,false);

	    int numCommon = 0;
	    for (int i = 0; i < len1; ++i) {
	        int start = Math.max(0,i-searchRange);
	        int end = Math.min(i+searchRange+1,len2);
	        for (int j = start; j < end; ++j) {
	            if (matched2[j]) continue;
	            if (charSeq1.charAt(i) != charSeq2.charAt(j))
	                continue;
	            matched1[i] = true;
	            matched2[j] = true;
	            ++numCommon;
	            break;
	        }
	    }
	    if (numCommon == 0) return 0.0;

	    int numHalfTransposed = 0;
	    int j = 0;
	    for (int i = 0; i < len1; ++i) {
	        if (!matched1[i]) continue;
	        while (!matched2[j]) ++j;
	        if (charSeq1.charAt(i) != charSeq2.charAt(j))
	            ++numHalfTransposed;
	        ++j;
	    }

	    int numTransposed = numHalfTransposed/2;

	    double numCommonD = numCommon;
	    double weight = (numCommonD/len1
	                     + numCommonD/len2
	                     + (numCommon - numTransposed)/numCommonD)/3.0;

	    if (weight <= mWeightThreshold) return weight;
	    int max = Math.min(mNumChars,Math.min(charSeq1.length(),charSeq2.length()));
	    int pos = 0;
	    while (pos < max && charSeq1.charAt(pos) == charSeq2.charAt(pos))
	        ++pos;
	    if (pos == 0) return weight;
	    return weight + 0.1 * pos * (1.0 - weight);

	}//end jaroWinklerComparison()	
}//end class