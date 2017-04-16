package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

/**
 * 
 * @author klitos
 */
public class PerformanceSummary {

	private float rmse   = 0.0F;
	private float n_rmse = 0.0F;
	private float sumOfObservedValues = 0.0F;
	private float avgOfObservedMatrix = 0.0F;
	private float sumOfPredictedValues = 0.0F;
	private float sumOfResiduals = 0.0F;
	
	/*Constructor*/
	public PerformanceSummary(float rmse, float n_rmse) {
		this.rmse = rmse;
		this.n_rmse = n_rmse;
	}	
	
	public float getRMSE() {
		return this.rmse;
	}
	
	public float getNormRMSE() {
		return this.n_rmse;
	}	
	
	public float getSumOfResiduals() {
		return this.sumOfResiduals;
	}

	public void setSumOfResiduals(float r_sum) {
		this.sumOfResiduals = r_sum;
	}
	
	//Predicted values
	public void setSumOfPredictedValues(float p_sum) {
		this.sumOfPredictedValues = p_sum;
	}	
	
	//Observed values
	public void setSumOfObservedValues(float o_sum) {
		this.sumOfObservedValues = o_sum;
	}
	
	public void setAverageOfObsMatrix(float o_avg) {
		this.avgOfObservedMatrix = o_avg;
	}
		
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n-------------------------------------------");
		stringBuilder.append("\nAvg of Observed matrix: ").append(this.avgOfObservedMatrix).append("\n");
		stringBuilder.append("Sum of Observed values: ").append(this.sumOfObservedValues).append("\n");
		stringBuilder.append("Sum of Predicted values: ").append(this.sumOfPredictedValues).append("\n");	
		stringBuilder.append("Sum of Residuals: ").append(this.sumOfResiduals).append("\n");
		stringBuilder.append("\n");
		stringBuilder.append("RMSE: ").append(this.rmse).append("\n");
		stringBuilder.append("N-RMSE (%): ").append(this.n_rmse);
		stringBuilder.append("-------------------------------------------\n"); 
		return stringBuilder.toString();		
	}//end toString();	
}//end class