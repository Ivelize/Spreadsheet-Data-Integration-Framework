package uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelDenstityEstimator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelSupportType;

/***
 * This class encapsulates:
 * 	- a table that pre-computes the integrals using one step Simpson's rule
 *  - a kernel density estimator object (KDE) which is the actual probability density function f(x)
 * 
 * Then each matcher will have an single instance of this class attached to it
 * 
 * 
 * @author klitos
 */
public class ProbabilityDensityFunction extends AbstractProbabilityDistribution {

	static Logger logger = Logger.getLogger(ProbabilityDensityFunction.class);

	//Declare variables for this object
	private ArrayList<Double> integralResultVector = null;
	private KernelDenstityEstimator kde = null;
	private KernelCaseType caseType = null;
	private int 	lowerLimit;
	private int 	upperLimit;	
	private int 	range = 0;
	private int 	iMax  = 1000;
	private double 	h  	  = 0.0;
	
	/**
	 * Constructor of PDF with integral approximation using one step Simpson's rule
	 * 
	 * When KDE is with bounded support them lowerLimit = L , upperLimit = U of the support [L, U]
	 * 
	 */
	public ProbabilityDensityFunction(int lowerLimit, int upperLimit, KernelDenstityEstimator kde, KernelCaseType caseType) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;		
		this.range = upperLimit - lowerLimit;
		this.iMax = iMax * range;
		this.h = range / (double) iMax; //0.001 
		this.kde = kde;
		this.caseType = caseType;
		integralResultVector = new ArrayList<Double>();
	}//end constructor

	/***
	 * Create the integral pre-computed vector. The f(x) attached to this PDF is a non-parametric
	 * function that is approximated using a Kernel Density Estimation using a Kernel K(*). The 
	 * f(x) is called to approximate the integral using one step Simpson's rule, every time f(x)
	 * is called all the data points that are attached to the KDE are used to approximate the 
	 * PDF, therefore after the calculation of the approximate integral and the creation of the
	 * vector that holds the approximations we force gc to remove the data points attached to 
	 * each KDE to release memory. After performing this step only the vector that holds the 
	 * integrals is used to calculate the Pr that we need therefore the data points are not 
	 * needed any more. This is my optimisation technique. 
	 * 
	 * //TODO: consider making this vector persistent by storing it into a relational array
	 * 
	 * @return
	 * @throws IOException 
	 */
	public ArrayList<Double> createIntegralVector() throws IOException {
		logger.info("Pre-computing integral table...");
		
		//return the vector if exists
		if (integralResultVector != null && (!integralResultVector.isEmpty())) {			
			return integralResultVector;
		}		
		
		for (int i=0; i<=iMax; i++) {
			//case 0
			if (i==0) {
				integralResultVector.add(0.0);
			} else {
				double lower_x = lowerLimit + ((i-1) * h);
				double upper_x = lowerLimit + (i * h);	
				
				//this is the same as: h/2
				double step = (upper_x - lower_x) / 2;
				
				double nextPoint = integralResultVector.get(i-1) + (step / 3) * 
								 ( kde.fx(lower_x) + 4 * kde.fx(lower_x + step) +  kde.fx(lower_x + 2 * step) );
				
				//Add result to the next position in the array list
				integralResultVector.add(nextPoint);				
			}//end else
		}//end for	
		
		//**CLEAR** the datapoints vectors of the KDE and force a call to the garbage collector
		this.releaseMem();
		
		return integralResultVector;
	}//end approximateIntegral()
	
	/***
	 * The calculation of the integral uses the *smoothing factor* (h). This is the correct 
	 * approach as this is described in:
	 *    - Applied smoothing techniques by Walter Zucchini
	 * 		
	 * @param simScore - is the similarity score that I would like to find the integral for
	 * @return approximation of the integral using Simpson's Rule with one step
	 */
	public double approximateIntegralWithH(double simScore) {
		double kde_h = this.getKde().getH();
		
		double a = simScore - kde_h;
	    double b = simScore + kde_h;	
	    try {
		    int indexOfA = (int) Math.abs(Math.round( (a - this.lowerLimit) / h ));
		    int indexOfB = (int) Math.abs(Math.round( (b - this.lowerLimit) / h ));
		    
		    if (indexOfA == indexOfB) {
		    	return Double.NaN;	
		    } else {
		    	return integralResultVector.get(indexOfB) - integralResultVector.get(indexOfA);
		    }
	    } catch (ArrayIndexOutOfBoundsException e) {
	    	return Double.NaN;
	    } catch (IndexOutOfBoundsException e) {
	    	return Double.NaN;
	    }
	}//end approximateIntegral()	
	
	/**
	 * To find the Pr(x) of some x I need to find the correct index
	 * that I have already pre-computed.
	 * 
	 * x = index * step => x = i * h
	 * 
	 * therefore to find the index of x we solve by i = x / h
	 * 
	 * Note: see approximateIntegralWithH() - for the proper way of approximating the integral
	 * using the smoothing factor (h)
	 * 
	 * @param simScore - is the similarity score that I would like to find the integral for
	 * @param width - is the width of the area under the curve for the integral
	 * 
	 * @return approximation of the integral using Simpson's Rule with one step
	 */
	public double approximateIntegralWithWidth(double simScore, double width) {
		//double a = s - (h*width);
	    //double b = s + (h*width);
		double a = simScore - width;
	    double b = simScore + width;	
	    try {
		    int indexOfA = (int) Math.abs(Math.round( (a - this.lowerLimit) / h ));
		    int indexOfB = (int) Math.abs(Math.round( (b - this.lowerLimit) / h ));
		    
	    	logger.info("indexOfA : " + indexOfA);
	    	logger.info("indexOfB : " + indexOfB);

		    if (indexOfA == indexOfB) {
		    	return Double.NaN;	
		    } 
		    
		    return integralResultVector.get(indexOfB) - integralResultVector.get(indexOfA);		    
	    } catch (ArrayIndexOutOfBoundsException e) {
	    	return Double.NaN;
	    } catch (IndexOutOfBoundsException e) {
	    	return Double.NaN;
	    }
	}//end approximateIntegral()
	
	
	/**
	 * This method is the same as the approximateIntegralWithWidth() however this method
	 * instead of returning Double.NaN in several cases that the integral cannot be computed
	 * it calls the normal 3 step Simpson rule to approximate it instead of using the 
	 * pre-computed integrals.
	 * @throws IOException 
	 *
	 */
	public double approximateIntegralWithWidthSmart(double simScore, double width) throws IOException {
		
		double a = simScore - width;
	    double b = simScore + width;
	    double tempWidth = width - 0.01;
	 	    
	    
	    try {	    	
		    int indexOfA = (int) Math.abs(Math.round( (a - this.lowerLimit) / h ));
		    int indexOfB = (int) Math.abs(Math.round( (b - this.lowerLimit) / h ));
	    	
		    //logger.info("indexOfA : " + indexOfA);
	    	//logger.info("indexOfB : " + indexOfB);
	    
		    if (indexOfA == indexOfB) {
				a = simScore - tempWidth;
			    b = simScore + tempWidth;
		    	return kde.calcIntegral(a, b);
		    } 
		    
		    /* if everything is ok then it will get the integral from the pre-computed integrals otherwise
		       it will try to approximate the integral using the normal 3 step Simpson rule */
		    return integralResultVector.get(indexOfB) - integralResultVector.get(indexOfA);
		    
	    } catch (ArrayIndexOutOfBoundsException e) {
			a = simScore - tempWidth;
		    b = simScore + tempWidth;
	    	return kde.calcIntegral(a, b);
	    } catch (IndexOutOfBoundsException e) {
			a = simScore - tempWidth;
		    b = simScore + tempWidth;
	    	return kde.calcIntegral(a, b);
	    }
	}//end approximateIntegralWithWidthSmart()

	/**
	 * x = index * step => x = i * h
	 * 
	 * therefore to find the index of x we solve by i = x / h
	 * 
	 * @param a - lower limit
	 * @param b - upper limit
	 * 
	 * This method is the same as approximateIntegralSmart however it returns Double.NaN
	 * when an exception arises, whereas the smart method tries to approximate the 
	 * integral using the normal 3 step Simpson rule. 
	 * 
	 * @return approximation of the integral using Simpson's Rule with one step
	 */
	public double approximateIntegral(double a, double b) {
	    int indexOfA = (int) Math.abs(Math.round( (a - this.lowerLimit) / h ));
	    int indexOfB = (int) Math.abs(Math.round( (b - this.lowerLimit) / h ));	    
	    try {
	    	//logger.info("indexOfA : " + indexOfA);
	    	//logger.info("indexOfB : " + indexOfB);
	    
		    if (indexOfA == indexOfB) {
		    	return Double.NaN;	
		    } else {
		    	return integralResultVector.get(indexOfB) - integralResultVector.get(indexOfA);
		    }
	    } catch (ArrayIndexOutOfBoundsException e) {
	    	return Double.NaN;
	    } catch (IndexOutOfBoundsException e) {
	    	return Double.NaN;
	    }
	}//end approximateIntegral()
	
	/**
	 * x = index * step => x = i * h
	 * 
	 * therefore to find the index of x we solve by i = x / h
	 * 
	 * @param a - lower limit
	 * @param b - upper limit
	 * 
	 * This method is the same as approximateIntegralDummy however instead of returning Double.NaN
	 * when an exception arises, the smart method tries to approximate the integral using the
	 * normal 3 step Simpson rule. 
	 * 
	 * @return approximation of the integral using Simpson's Rule with one step
	 * @throws IOException 
	 */
	public double approximateIntegralSmart(double a, double b) throws IOException {
	    int indexOfA = (int) Math.abs(Math.round( (a - this.lowerLimit) / h ));
	    int indexOfB = (int) Math.abs(Math.round( (b - this.lowerLimit) / h ));	    
	    try {
	    	//logger.info("indexOfA : " + indexOfA);
	    	//logger.info("indexOfB : " + indexOfB);
	    
		    if (indexOfA == indexOfB) {
		    	return kde.calcIntegral(a, b);
		    } 
		    
		    /* if everything is ok then it will get the integral from the pre-computed integrals otherwise
		       it will try to approximate the integral using the normal 3 step Simpson rule */
		    return integralResultVector.get(indexOfB) - integralResultVector.get(indexOfA);
		    
	    } catch (ArrayIndexOutOfBoundsException e) {
	    	return kde.calcIntegral(a, b);
	    } catch (IndexOutOfBoundsException e) {
	    	return kde.calcIntegral(a, b);
	    }
	}//end approximateIntegral()
	
	/**
	 * Method that prints the pre-computed table. If I decide to store it in a relational db
	 * I will store this vector
	 */
	public void printVector() {		
		double sum = 0.0;
		logger.info("Output Integral Vector: ");		
		for (double i : integralResultVector) {
			sum = sum + i;
			logger.info(""+ i);	
		}//end for		
		
		logger.info("Sum is: " + sum);	
	}//end printTransVector()	
	
	/***
	 * Method that send the ArrayLists that hold the data points of a KDE to the 
	 * garbage collector to release memory.
	 */
	public void releaseMem() {
		logger.info("Releasing memory...");	
		
		//clear the ArrayLists that hold the data points for KDE,
		//this is done after the calculation of the approximate integral table
		this.kde.deleteSampleDataX();
		this.kde.deleteTransDataY();		
		
		//Force the garbage collector
		System.gc();		
	}//end releaseMem()	
	
	/***
	 * Call KDE's calcIntegral to calculate the integral using the simpsons 3 step rule
	 * instead of the one step rule optimisation strategy.
	 * @param a
	 * @param b
	 * @return - integral of f(x) using Simpson's rule
	 * @throws IOException 
	 */
	public float calcIntegral(double a, double b) throws IOException {
		if (this.kde != null) {
			return this.kde.calcIntegral(a, b);
		}
		return Float.NaN;
	}//end calcIntegral()	
	
	/*******
	 * Getter/ Setter methods
	 */
	
	public KernelSupportType getKDESupportType() {
		return this.kde.getSupportType();
	}

	public KernelDenstityEstimator getKde() {
		return kde;
	}

	public void setKde(KernelDenstityEstimator kde) {
		this.kde = kde;
	}

	public ArrayList<Double> getIntegralResultVector() {
		return integralResultVector;
	}

	public void setIntegralResultVector(ArrayList<Double> integralResultVector) {
		this.integralResultVector = integralResultVector;
	}

	public KernelCaseType getCaseType() {
		return caseType;
	}

	public void setCaseType(KernelCaseType caseType) {
		this.caseType = caseType;
	}	
}//end class
