package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;

/**
 * @author chedeler
 * @author klitos
 */

@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class NGramMatcherServiceImpl extends StringBasedMatcherServiceImpl {

	static Logger logger = Logger.getLogger(NGramMatcherServiceImpl.class);

	@Autowired
	private MatchingProducerService matchingProducerService;

	private int lengthOfNGram;

	//TODO check for potential childMatchers, shouldn't have any though
	//TODO sort out MatcherType, is ignored here
	//TODO test
	public NGramMatcherServiceImpl(int lengthOfNGram) {
		logger.debug("in NGramMatcherServiceImpl");
		logger.debug("lengthOfNGram: " + lengthOfNGram);
		this.lengthOfNGram = lengthOfNGram;
		this.setName("NGramMatcher");
		this.setMatcherType(MatcherType.NGRAM);
	}//end constructor

	@Override
	public Matching match(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("call override match in [NGramMatcherServiceImpl]");
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		float similarity = this.match(construct1.getName(), construct2.getName());
		logger.debug("similarity: " + similarity);
		return matchingProducerService.produceSingleMatching(similarity, construct1, construct2, controlParameters, this);
	}
	
	/***
	 * This method overrides the match() from the ConstructBasedMatcherServiceImpl
	 */	
	@Override
	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("call override match in [NGramMatcherServiceImpl]");
		float[][] simMatrix = this.match(constructs1, constructs2); //[constructs1][constructs2]
		return matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, this);
	}

	/***
	 * This method overrides the match(String,String) from the Abstract Class StringBasedMatcherServiceImpl 
	 */	
	@Override
	public float match(String string1, String string2) {
		logger.debug("in match");
		logger.debug("string1: " + string1);
		logger.debug("string2: " + string2);
		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0)
			logger.error("nGram matcher has child matcher - shoudn't be the case - TODO sort this");

		if ((string1 == null) || (string2 == null) || (string1.length() == 0) || (string2.length() == 0))
			return 0.0F;

		ArrayList<String> grams1 = generateNGrams(string1.toLowerCase().trim());
		ArrayList<String> grams2 = generateNGrams(string2.toLowerCase().trim());

		/*Count the number of equal NGrams*/
		int count = this.countNumberOfEqualNGrams(grams1, grams2);

		/*Return a similarity (match score)*/
		float sim = this.calculateSimilarity(count, grams1.size(), grams2.size());
		logger.debug("count: " + count);
		logger.debug("sim: " + sim);
		return sim;
	}

	protected float calculateSimilarity(int count, int grams1Size, int grams2Size) {
		float sim = 2.0F * count / (grams1Size + grams2Size);
		return sim;
	}//calculateSimilarity()

	protected int countNumberOfEqualNGrams(ArrayList<String> grams1, ArrayList<String> grams2) {
		int count = 0;
		for (String gram1 : grams1) {
			for (String gram2 : grams2) {
				//logger.debug("gram1: " + gram1);
				//logger.debug("gram2: " + gram2);
				if (gram1.equals(gram2))
					count++;
				//logger.debug("count: " + count);
			}
		}
		return count;
	}//countNumberOfEqualNGrams()

	/***
	 * This methods overrides the method from the Abstract class (StringBasedMatcherServiceImpl) which then overrides the 
	 * method from the ConstuctBasedMatcherServiceImpl.
	 * 
	 * @return a similarity matrix.
	 */
	@Override
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2) {
		logger.debug("in match");
		
		if (this.getChildMatchers() != null && this.getChildMatchers().size() > 0) {
			logger.error("This is a Primitive matcher cannot have child matchers.");
			logger.error("NGram matcher has child matcher - shouldn't be the case - TODO sort this");
			return null;
		}
		
 		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		/*Monitor the time to start the construction of the similarity matrix*/
		logger.info("Primitive matcher is : NGram");
		long startTime = System.nanoTime();
		logger.info("start matching all constructs with StringBasedMatcher: " + startTime);
		for (CanonicalModelConstruct construct1 : constructs1) {
			for (CanonicalModelConstruct construct2 : constructs2) {				
				logger.debug("construct1: " + construct1);
				logger.debug("construct2: " + construct2);
				logger.debug("construct1.getName: " + construct1.getName());
				logger.debug("construct2.getName: " + construct2.getName());
				long startTimeSingleMatch = System.nanoTime();
				logger.info("start matching two constructs with StringBasedMatcher: " + startTimeSingleMatch);
				simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = this.match(construct1.getName(), construct2.getName());
				long endTimeSingleMatch = System.nanoTime();
				logger.info("finished matching two constructs with StringBasedMatcher: " + endTimeSingleMatch);
				logger.info("duration for matching two constructs (in seconds): " + (endTimeSingleMatch - startTimeSingleMatch) / 1.0e9);
				logger.info("simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]: "
						+ simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]);
				logger.info("construct1.getName: " + construct1.getName());
				logger.info("construct2.getName: " + construct2.getName());
				logger.info("construct1.getName().length(): " + construct1.getName().length());
				logger.info("construct2.getName().length(): " + construct2.getName().length());
			}
		}
		long endTime = System.nanoTime();
		logger.info("constructs1.size(): " + constructs1.size());
		logger.info("constructs2.size(): " + constructs2.size());
		logger.info("finished matching all constructs with StringBasedMatcher: " + endTime);
		logger.info("duration for matching all constructs (in seconds): " + (endTime - startTime) / 1.0e9);
		return simMatrix;
	}//end match()

	/**
	 * This methods returns the nGrams generated for that String. 
	 * @param str
	 * @return an ArrayList that holds the nGrams for the input String
	 */
	protected ArrayList<String> generateNGrams(String str) {
		//logger.debug("str: " + str);
		if ((str == null) || (str.length() == 0))
			return null;
		ArrayList<String> grams = new ArrayList<String>();
		int length = str.length();
		//logger.debug("length: " + length);

		if (length < this.lengthOfNGram) {
			//logger.debug("length < this.lengthOfNGram");
			for (int i = 1; i <= length; ++i) {
				String gram = str.substring(0, length);
				//logger.debug("gram: " + gram);
				if (grams.indexOf(gram) == -1)
					grams.add(gram);
			}//end for
		} else {
			//logger.debug("length >= this.lengthOfNGram");
			for (int i = 0; i < str.length() - lengthOfNGram + 1; i++) {
				String gram = str.substring(i, i + lengthOfNGram);
				//logger.debug("gram: " + gram);
				if (grams.indexOf(gram) == -1)
					grams.add(gram);
			}//end for
		}
		return grams;
	}//end generateNGrams()

	/**
	 * //TODO klitos: This could be a control parameter so change this if possible.
	 * 
	 * @param lengthOfNGram the lengthOfNGram to set
	 */
	public void setLengthOfNGram(int lengthOfNGram) {
		this.lengthOfNGram = lengthOfNGram;
	}

	/**
	 * @return the lengthOfNGram
	 */
	public int getLengthOfNGram() {
		return lengthOfNGram;
	}

	/***
	 * Test 
	 */
	public static void main(String [] args) {
				
		//String source_construct = "MusicGroup";
		//String target_sontruct  = "Group"; 
		
		String source_construct = "SoloMusicArtist";
		String target_sontruct  = "Person"; 		
		
		NGramMatcherServiceImpl triGram = new NGramMatcherServiceImpl(3);
		float simScore = triGram.match(source_construct, target_sontruct);
		
		logger.debug("simScore: " + simScore);
		
	}
	
}//end NGramMatcherServiceImpl
