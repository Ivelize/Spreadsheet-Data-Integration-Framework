package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**************
 * Kernel density estimators belong to a class of estimators called non-parametric density estimators. In comparison to parametric
 * estimators where the estimator has a fixed functional form (structure) and the parameters of this function are the only
 * information we need to store, Non-parametric estimators have no fixed structure and depend upon all the data points to reach an estimate.
 *
 * @author klitos
 *
 */
public class KernelDenstityEstimator {

	static Logger logger = Logger.getLogger(KernelDenstityEstimator.class);
	
	private String dataPointsLoc = null; //data points location
	private AbstractKernelFunction kernel = null; //type of kernel function
	private AbstractTransformationFunction t = null; //transformation function 
	private ArrayList<Double> sampleDataX  = null; //sample data
	private ArrayList<Double> transDataY   = null; //sample data transformed
	private double h = 0; //bandwidth, smoothing parameter
	private KernelSupportType supportType = null;
	private KernelEstimatorType kdeType = null; //the type of this KDE 
	
	/***
	 * Constuctor1: Construct a Kernel Denstity Estimator without boundary support
	 * 
	 * @param k - is a weight function known as the Kernel K(*) 
	 * @param listOfData - is a list of data points
	 * @param h - is a smoothing parameter known as the bandwidth 
	 */
	public KernelDenstityEstimator(KernelEstimatorType kdeType, AbstractKernelFunction k, double h, ArrayList<Double> listOfData) {
		this.setSupportType(KernelSupportType.KDE_WITHOUT_SUPPORT);
		this.setKernel(k);
		this.setH(h);
		this.sampleDataX = listOfData;
		this.setKdeType(kdeType);
	}

	public KernelDenstityEstimator(KernelEstimatorType kdeType, AbstractKernelFunction k, double h) {
		this.setSupportType(KernelSupportType.KDE_WITHOUT_SUPPORT);
		this.setKernel(k);
		this.setH(h);
		this.sampleDataX = new ArrayList<Double>();
		this.setKdeType(kdeType);
	}	
	
	/***
	 * Constuctor2: Construct a Kernel Denstity Estimator with boundary support: [L, U]
	 * 
	 * For creating a KDE with support we follow the transformation procedure:
	 *  1. transform X using a log function to vector Y : log((X-L)/(U-X))
	 *  2. estimate the density of the transformed value of some point t(x) using Ys
	 *  3. transform (x) back to the original scale
	 * 
	 * @param k - is a weight function known as the Kernel K(*) 
	 * @param listOfData - is a list of data points
	 * @param h - is a smoothing parameter known as the bandwidth 
	 */
	public KernelDenstityEstimator(KernelEstimatorType kdeType, AbstractKernelFunction k, double h, AbstractTransformationFunction t, ArrayList<Double> listOfData) {
		this.setKernel(k);
		this.setH(h);
		this.setT(t);		
		this.setSupportType(KernelSupportType.KDE_WITH_SUPPORT);
		//Step 1: Transform the sample data points using the transformation function
		transDataY = this.transform(listOfData);
		this.setKdeType(kdeType);
	}
	
	public KernelDenstityEstimator(KernelEstimatorType kdeType, AbstractKernelFunction k, double h, AbstractTransformationFunction t) {
		this.setKernel(k);
		this.setH(h);
		this.setT(t);		
		this.setSupportType(KernelSupportType.KDE_WITH_SUPPORT);
		//Step 1: Transform the sample data points using the transformation function
		transDataY = new ArrayList<Double>();
		this.setKdeType(kdeType);
	}
		
	/***
	 * This method transforms the vector of data points using the transformation function
	 * @return
	 */
	private ArrayList<Double> transform(ArrayList<Double> dataX) {
		//Step 1: transform all x sample points to y
		ArrayList<Double> t_x = new ArrayList<Double>();
		
		for (double e : dataX) {
			t_x.add(this.getT().t(e));		
		}//end for		
		return t_x;
	}//end transform()
	
	/***
	 * This is the method for calculating the density of point (x) using the standard
	 * Kernel Density Estimation formula f(x)
	 * 
	 * To calculate the density of some point (x) using a the Kernel Density Estimation 
	 * formula f(x) with 'support' [L, U] we follow the transformation procedure as follows: 
	 * 
	 *  1. transform X using a log function to vector Y : log((X-L)/(U-X))
	 *  2. estimate the density of the transformed value of some point t(x) using Ys
	 *  3. transform (x) back to the original scale
	 * 
	 * @param x
	 * @return
	 * @throws IOException 
	 */
	public double fx(double x) throws IOException {
		double kde = 0.0;
		double sum = 0.0;
		double n = 0.0;
		
		if (this.getSupportType().equals(KernelSupportType.KDE_WITHOUT_SUPPORT)) {
			
			if (sampleDataX == null) {
				this.sampleDataX = new ArrayList<Double>();
				this.readDataPointsFromLocation(dataPointsLoc);
			}	
			
			n = sampleDataX.size();
		
			//Loop
			for (double elm : sampleDataX) {
				sum = sum + kernel.k( (x - elm) / h );			
			}//end for
		
			kde = (double) sum / (n * h); 

		} else if (this.getSupportType().equals(KernelSupportType.KDE_WITH_SUPPORT)) {
		
			if (transDataY == null) {
				this.transDataY = new ArrayList<Double>();
				this.readDataPointsFromLocation(dataPointsLoc);
			}			
			
			n = transDataY.size();
			double w = 1.0 / (n * h);
			
			//transform x
			double g_x =  this.getT().t(x);
			
			for (double elm : transDataY) {
				sum = sum + kernel.k( (g_x - elm) / h );		
			}
			
			//translate back to the PDF of x
			kde = w * sum *  this.getT().derivativeOf(x);	
		}
		
		return kde;		
	}//end calc f(x)
	
	
	/***
	 * Method that calculates the definite integral of some f(x) given 
	 * x and limits (a, b)
	 * @param a - lower limit
	 * @param b - upper limit
	 * @return integral of f(x) using Simpson's rule
	 * @throws IOException 
	 */
	public float calcIntegral(double a, double b) throws IOException {
		int N = 1000;  // precision parameter
		double h2 = (b - a) / (N - 1);  // step size
	 
	    // 1/3 terms
	    double sum = 1.0 / 3.0 * (fx(a) + fx(b));

	    // 4/3 terms
	    for (int i = 1; i < N - 1; i += 2) {
	       double x = a + h2 * i;
	       sum += 4.0 / 3.0 * fx(x);
	    }

	    // 2/3 terms
	    for (int i = 2; i < N - 1; i += 2) {
	       double x = a + h2 * i;
	       sum += 2.0 / 3.0 * fx(x);
	    }

	    return (float) ((float) sum * h2);
	}//end integrate
	
	
	/**
	 * Method that reads data points directly to the sampleDataX List
	 * 
	 * @param loc - location of the file to read the points from
	 */
	public void readDataPointsFromLocation(String loc) throws IOException {
		this.dataPointsLoc = loc;
		Scanner scanner = new Scanner(new FileInputStream(loc));
		try {
			while (scanner.hasNextLine()) {
				double sim = Double.parseDouble(scanner.nextLine().trim());
				if (this.getSupportType().equals(KernelSupportType.KDE_WITHOUT_SUPPORT)) {
					sampleDataX.add(sim);
				} else if (this.getSupportType().equals(KernelSupportType.KDE_WITH_SUPPORT)) {
					//transform it and then add it to the list
					transDataY.add( this.getT().t(sim) );
				}			
		    }//end while
		
		} finally {
			scanner.close();
		}	
	}//end readDataPointsFromLocation()	
	
	/**
	 * Send the lists that hold the data points to the garbage collector
	 */
	public void deleteSampleDataX() {
		if (sampleDataX != null)
			sampleDataX = null;
	}
	
	public void deleteTransDataY() {
		if (transDataY != null)
			transDataY = null;
	}	
	
	/**********************
	 * Getter / Setters Methods
	 *********************/	
	public AbstractKernelFunction getKernel() {
		return kernel;
	}

	public KernelEstimatorType getKdeType() {
		return kdeType;
	}

	public void setKdeType(KernelEstimatorType kdeType) {
		this.kdeType = kdeType;
	}

	public void setKernel(AbstractKernelFunction kernel) {
		this.kernel = kernel;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getLower_bound() {
		if (t == null)
			return -2;
		
		return this.t.getLower_bound();
	}

	public double getUpper_bound() {
		if (t == null)
			return 1;
		
		return this.t.getUpper_bound();
	}

	public KernelSupportType getSupportType() {
		return supportType;
	}

	public void setSupportType(KernelSupportType supportType) {
		this.supportType = supportType;
	}

	public AbstractTransformationFunction getT() {
		return t;
	}

	public void setT(AbstractTransformationFunction t) {
		this.t = t;
	}	
}//end class
