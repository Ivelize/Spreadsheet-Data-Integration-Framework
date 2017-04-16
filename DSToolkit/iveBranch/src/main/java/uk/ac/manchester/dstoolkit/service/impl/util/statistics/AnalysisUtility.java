package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

import uk.ac.manchester.dstoolkit.exceptions.DataIsEmptyException;

/***
 * This class is responsible for creating various statistical calculations
 * 
 * 
 * @author klitos
 */
public class AnalysisUtility {
	
	public static double sum(double[] data) throws DataIsEmptyException {
		int sampleSize = data.length;
		if (sampleSize <= 0) throw new DataIsEmptyException("There is no data.");
		double total = 0;
		for (int i = 0; i < sampleSize; i++) {

			total = total + data[i];
		}

		return total;
	}

}//end Class
