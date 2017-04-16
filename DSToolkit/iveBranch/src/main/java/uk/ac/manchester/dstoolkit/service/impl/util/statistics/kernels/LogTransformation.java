package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/***
 * We have chosen the same log transformation function as Matlab2012:
 * 	log ((X - L) / (U - X))
 * 
 * @author klitos
 */
public class LogTransformation extends AbstractTransformationFunction {
	
	public LogTransformation() { }
	
	public LogTransformation(double l, double u) {
		this.setLower_bound(l);
		this.setUpper_bound(u);
	}	
	
	/**
	 * This is the transformation method : log ((X - L) / (U - X))
	 */
	@Override
	public double t(double x) {
		return Math.log( (x - this.getLower_bound() ) / (this.getUpper_bound() - x) );
	}
	
	/**
	 * Method that returns the derivative of log ((X - L) / (U - X))
	 * which is: (1 / (x - l)) + (1 / (u - x))
	 * 
	 */
	@Override
	public double derivativeOf(double x) {
		return ( 1 / (x - this.getLower_bound()) ) + ( 1 / (this.getUpper_bound() - x) );  
	}
}//end class