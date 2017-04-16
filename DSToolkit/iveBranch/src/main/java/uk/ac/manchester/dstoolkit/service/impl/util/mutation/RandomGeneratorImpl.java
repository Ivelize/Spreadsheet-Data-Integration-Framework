package uk.ac.manchester.dstoolkit.service.impl.util.mutation;

import java.util.Random;

import org.apache.log4j.Logger;


/***
 * This class is responsible for generating random choices. The class holds a single Random object.
 * 
 * @author klitos
 *
 */
public class RandomGeneratorImpl {

	private static Logger logger = Logger.getLogger(RandomGeneratorImpl.class);
	
	Random randomObj = null;
	
	/**
	 * Constructor
	 */
	public RandomGeneratorImpl(){
		randomObj = new Random();
	}//end contructor
	
	/**
	 * Uniform random choice on whether we are mutation or not
	 * @return true  - 50%
	 * @return false - 50%
 	 */
	public boolean randomBoolean() {
		return randomObj.nextBoolean();
	}//end randomBoolean()
	
	/**
	 * Mutation with threshold
	 * @return true  - if result (r >= 0) & (r <= t) 
	 * @return false - otherwise
	 */
	public boolean randomWithBias(double threshold) {
		Double pickedNumber = this.randomObj.nextDouble();

		if ( (pickedNumber >= 0) && (pickedNumber <= threshold)) {
			return true;
		} else {
			return false;
		}		
	}//end doMutation()
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public int randInt(int min, int max) {		
		
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = this.randomObj.nextInt((max - min) + 1) + min;

	    return randomNum;
	}//end randInt()
	
	
    public static void main( String[] args ) {
    	RandomGeneratorImpl rg = new RandomGeneratorImpl();
    	//30% of the times we get true
    	for (int i=0; i<10; i ++) {
    	
    		logger.info("result [" + i + "] " + rg.randomWithBias(0.5));
    	}
    }	
}//end class