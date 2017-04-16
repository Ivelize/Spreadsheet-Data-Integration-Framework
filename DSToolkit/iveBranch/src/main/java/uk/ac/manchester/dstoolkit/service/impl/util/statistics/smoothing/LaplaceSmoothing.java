package uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing;

/***
 * Laplace smoothing: a smoothing technique to prevent over fitting due to the fact that we have some zero probabilities
 * in some evidences that do not occur in the training set.
 * 
 *  - k > 0 is the smoothing parameter, k = 0 corresponds to no smoothing
 *  - |classes| = is the number of classes, this is different for calculating Laplace for conditionals in the  
 *  area of text recongintion, where the |classes| number of classes is the size of the all distinct words in 
 *  the dictionary. 
 * 
 * @author klitos
 */
public class LaplaceSmoothing extends SmoothingMethod {
	
	//Smoothing parameter k
	private int k;
	
	private int noOfClasses;	
	
	/*Construct*/
	public LaplaceSmoothing(int k, int noOfClasses) {
		this.k = k;
		this.noOfClasses = noOfClasses;
	}
	
	
	/***
	 * Formula for Laplace smoothing is:
	 * 
	 * 	( count + k ) / ( N +  ( k * |classes|) )
	 * 
	 */
	public double calc(double count, int n) {
		return ( count + this.k ) / (double) ( n +  ( this.k * this.noOfClasses ) );
	}
	
	

}//end class
