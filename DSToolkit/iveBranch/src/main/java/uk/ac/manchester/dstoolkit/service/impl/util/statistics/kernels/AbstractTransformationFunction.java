package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/***
 * A transformation (t) is selected form a parametric family of functions so that the density of
 * Yi = t(Xi) has a first derivative that is approximatelly equal to 0 at the boundaries of the 
 * support [L, U]
 * 
 * All transformation functions should extend this abstract class
 * 
 * @author klitos
 */
public abstract class AbstractTransformationFunction {
	
	private double lower_bound;
	private double upper_bound;

	public abstract double t(double x);
	
	public abstract double derivativeOf(double x);
	
	public double getLower_bound() {
		return lower_bound;
	}

	public void setLower_bound(double lower_bound) {
		this.lower_bound = lower_bound;
	}

	public double getUpper_bound() {
		return upper_bound;
	}

	public void setUpper_bound(double upper_bound) {
		this.upper_bound = upper_bound;
	}		
}//end class