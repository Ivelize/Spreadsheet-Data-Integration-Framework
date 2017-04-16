package uk.ac.manchester.dstoolkit.service.impl.util.statistics;


public class SquaredError extends ErrorMeasures  {

	public SquaredError() {
		this.mType = ErrorMeasuresTypes.SQUARED_ERROR;
	}
	
	/***
	 * @param p - predicted values
	 * @param a - actual values
	 * @return the squared difference between p-a
	 */
	@Override
	public float calc(float p, float a) {
		return (float) Math.pow((p - a),2);
	}	
	

}//end class