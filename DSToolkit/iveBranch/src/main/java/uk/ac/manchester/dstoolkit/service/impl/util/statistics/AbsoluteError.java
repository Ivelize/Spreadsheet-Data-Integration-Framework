package uk.ac.manchester.dstoolkit.service.impl.util.statistics;


/**
 * Calculation of Absolute Error
 * 
 * @author klitos
 *
 */
public class AbsoluteError extends ErrorMeasures  {

	public AbsoluteError() {
		this.mType = ErrorMeasuresTypes.ABSOLUTE_ERROR;
	}
	
	/***
	 * @param p - predicted values
	 * @param a - actual values
	 * @return the absolute different between p-a
	 */
	@Override
	public float calc(float p, float a) {
		return Math.abs(p-a);
	}	
}//end class