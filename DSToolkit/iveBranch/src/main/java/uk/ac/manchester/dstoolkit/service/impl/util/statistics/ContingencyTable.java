package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.exceptions.DataIsEmptyException;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.LaplaceSmoothing;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.SmoothingMethod;

/***
 *
 * This Class implements a contingency table which is a matrix that holds frequency distributions
 * of variables. 
 *  
 * @author klitos christodoulou
 *
 */
public class ContingencyTable {
	private static Logger logger = Logger.getLogger(ContingencyTable.class);
	
	private SmoothingMethod smoothingMethod = null; 
	
	private int grandTotal;

	private String[] rowName;
	private String[] colName;

	private int[] rowSumObserved;
	private int[] colSumObserved;

	private int numberRow;
	private int numberCol;

	private double[][] observed; // first index is for row, second index for column
	
	/*Constructor: No variables names*/
	public ContingencyTable(double[][] observed) {
		try {
			this.observed = observed;
			this.numberRow = observed.length;
			this.numberCol = observed[0].length;
			this.rowSumObserved = new int[this.numberRow];
			this.colSumObserved = new int[this.numberCol];
			/*Reverse column and row index for summing up by using AnalysisUtility.sum*/
			double[][] currentColumnArray = new double[this.numberCol][this.numberRow];

			for (int i = 0; i < this.numberRow; i++) {
				rowSumObserved[i] = (int)AnalysisUtility.sum(observed[i]);
				for (int j = 0; j < this.numberCol; j++) {
					currentColumnArray[j][i] = observed[i][j]; // switch index.
					this.grandTotal += observed[i][j];
				}
			}//end for

			for (int j = 0; j < this.numberCol; j++) {
				colSumObserved[j] = (int)AnalysisUtility.sum(currentColumnArray[j]);
			}

		} catch (DataIsEmptyException exe) {
			logger.error("Excepetion: " + exe );
		} catch (NullPointerException exe) {
			logger.error("Exception: " + exe);
		}
	}//end constructor
	
	/*Constructor: Set names for the variables at rows and columns*/
	public ContingencyTable(double[][] observed, String[] rowName, String[] colName) {
		try {
			this.observed = observed;
			this.numberRow = observed.length;
			this.numberCol = observed[0].length;
			this.rowName = rowName;// new String[this.numberRow];
			this.colName = colName;//new String[this.numberCol];
			this.rowSumObserved = new int[this.numberRow];
			this.colSumObserved = new int[this.numberCol];
			
			/*Reverse column and row index for summing up by using AnalysisUtility.sum*/
			double[][] currentColumnArray = new double[this.numberCol][this.numberRow]; 

			for (int i = 0; i < this.numberRow; i++) {
				rowSumObserved[i] = (int)AnalysisUtility.sum(observed[i]);
				for (int j = 0; j < this.numberCol; j++) {
					currentColumnArray[j][i] = observed[i][j]; // switch index.
					this.grandTotal += observed[i][j];
				}
			}//end for

			for (int j = 0; j < this.numberCol; j++) {
				colSumObserved[j] = (int)AnalysisUtility.sum(currentColumnArray[j]);
			}

		} catch (DataIsEmptyException exe) {
			logger.error("Excepetion: " + exe );
		} catch (NullPointerException exe) {
			logger.error("Exception: " + exe);
		}
	}//end constructor
	
	
	/**
	 * Add a smoothing method to prevent zero probabilities when there is not any 
	 * evidence available.
	 */
	public void useSmoothing(SmoothingMethod sm) {
		this.smoothingMethod = sm;
	}	
	
	/***
	 * Supporting methods
	 */
	public int getGrandTotal() {
		return this.grandTotal;
	}
	
	/***
	 * Method that prints the sum of each row and returns only the sum of the row specified
	 * @return
	 */
	public int[] getRowSumObserved() {
		 for (int i = 0; i < this.numberRow; i++) {
			 System.out.println("rowSum["+i+"] = " + this.rowSumObserved[i]);

	    }
		System.out.println("");
		return this.rowSumObserved;
	}

	public int[] getColSumObserved() {
		return this.colSumObserved;
	}
	
	/*Get Row names*/
	public String[] getRowNames() {
		return this.rowName;
	}
	
	/*Get Column names*/
	public String[] getColNames() {
		return this.colName;
	}
	
	/*Get the number of rows*/
	public int getNumberOfRows() {
		return this.numberRow;
	}
	
	/*Get the number of columns*/
	public int getNumberOfCols() {
		return this.numberCol;
	}
	
	public int[] getRowSum() {
		return this.rowSumObserved;
	}

	public int[] getColSum() {
		return this.colSumObserved;
	}
	
	
	public double getObserved(int i, int j) {
		if (0<=i && i< this.numberRow && 0<=j && j<this.numberCol)
			return this.observed[i][j];
		else return 0;
	}

	/**
	 * @param rowVariable - the row variable name
	 * 
	 * @return Pr(Equiv) = Pr(c1 = c2)
	 * @return Pr(NoEqui) = Pr(c1 ¬ c2) 
	 * 
	 * Return -1 if something is wrong
	 */
	public double Pr(String rowVariable) {			
		
		for (int i = 0; i < this.numberRow; i++) {
			if (rowName[i].equals(rowVariable)){
				if (this.smoothingMethod != null) {
					return smoothingMethod.calc(this.getRowSum()[i], this.getGrandTotal());	
				}
				else {
					return this.getRowSum()[i] / (double) this.getGrandTotal();
				}
			}
		}//end for
		
		return -1.0;
	}//end Pr()	

	/**
	 * Calculate the likelihoods as joint probabilities
	 *  - for laplace the |classes| might be different in this case, according to text classification
	 * 
	 * @param rowVariable - the row variable name
	 * @param colVariable - the col variable name
	 * 
	 * @return Pr(evidence | c1 = c2)
	 * @return Pr(evidence | c1 ¬ c2) 
	 * 
	 * Pr(colVariable | rowVariable) - It calculates the probability specified
	 * by the name of the column variable given that we know the row variable
	 * 
	 * Return -1 if something is wrong
	 */
	public double Pr(String colVariable, String rowVariable) {			
		
		for (int i = 0; i < this.numberRow; i++) {
			for (int j = 0; j < this.numberCol; j++) {
				if (rowName[i].equals(rowVariable) && colName[j].equals(colVariable)) {
					if (this.smoothingMethod != null) {
						return smoothingMethod.calc(this.getObserved(i, j), this.getRowSum()[i]);						
					} else {
						return this.getObserved(i, j) / (double) this.getRowSum()[i]; 
					}
				}//end if		
			}//end for
		}//end for		
		
		return -1.0;		
	}//end Pr()	
	
	/***
	 * Create a String representation of this Contingency Table
	 */
	@Override
	public String toString() {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		//Print the first row
		if (colName.length !=0) {
			stringBuilder.append("\t | \t");
			for (int j = 0; j < this.numberCol; j++) {
				stringBuilder.append(colName[j] + "\t | \t");
			}
			stringBuilder.append("Total \t |").append("\n");
		}//end if

		//Print the rest of the table
		for (int i = 0; i < this.numberRow; i++) {
			
			if (rowName.length !=0) {
				stringBuilder.append(rowName[i]);
			}
			
			for (int j = 0; j < this.numberCol; j++) {

				stringBuilder.append("\t | \t" + observed[i][j]);
				
			 if (j == numberCol-1)
				 stringBuilder.append("\t |");									
		}//end for
		
			stringBuilder.append(" \t" + this.getRowSum()[i] + "\t |").append("\n");
		}//end for
		
		//Print the last row
		if (colName.length !=0) {
			stringBuilder.append("Total \t | \t");
			for (int j = 0; j < this.numberCol; j++) {
				stringBuilder.append( this.getColSum()[j] + "\t | \t");
			}
			
			stringBuilder.append( this.getGrandTotal() + "\t |").append("\n");
		}//end if
		
		return stringBuilder.toString();
	}//end toString()	


	/*For Test Create a Main Class*/
	public static void main(String [] args) {		
		//Assume that the following table construct this contingency table
		double[][] test = {{52, 344}, {0, 117}};
		
		//Specify row names for this table
		String[] rname = {"Equiv", "NoEquiv"};
		
		//Specify column names for this table
		String[] cname = {"CSN", "NoCSN"};

		ContingencyTable ct = new ContingencyTable(test, rname, cname);		
		
		//Print the contingency table
		logger.debug("\n" + ct);		
		
		logger.debug("No. of rows: " + ct.getNumberOfRows());		
		

		logger.debug("Calc the Pr: " + ct.Pr("Equiv"));
		logger.debug("Calc the ¬Pr: " + ct.Pr("NoEquiv"));
		
		//Joint Probabilities	
		logger.debug("\nJoint Probabilities: \n");
		logger.debug("Pr(CSN | Equiv): " + ct.Pr("CSN", "Equiv"));		
		logger.debug("Pr(CSN | NoEquiv): " + ct.Pr("CSN", "NoEquiv"));
		logger.debug("");
		logger.debug("Pr(NoCSN | Equiv): " + ct.Pr("NoCSN", "Equiv"));		
		logger.debug("Pr(NoCSN | NoEquiv): " + ct.Pr("NoCSN", "NoEquiv"));	
		
		logger.debug("");
		
		
		//Create a Contingency Table and attach Laplace smoothing
		LaplaceSmoothing ls = new LaplaceSmoothing(1, 2);
		ContingencyTable ct_ls = new ContingencyTable(test, rname, cname);	
		ct_ls.useSmoothing(ls);
		
		logger.debug("With Laplace smoothing");	
		logger.debug("Calc the Pr: " + ct_ls.Pr("Equiv"));
		logger.debug("Pr(CSN | Equiv): " + ct_ls.Pr("CSN", "Equiv"));	
		logger.debug("Pr(CSN | NoEquiv): " + ct_ls.Pr("CSN", "NoEquiv"));
		
		
	}	
}//end ContingencyTable
