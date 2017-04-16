package uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing;

/***
 * In the standard naive Bayes approach, smoothing methods are commonly used to avoid zero probability estimates.
 * 
 * At the moment we only consider the Laplace smoothing method.
 * 
 * 	
 * @author klitos
 *
 */
public abstract class SmoothingMethod {

	public abstract double calc(double d, int n);
}
