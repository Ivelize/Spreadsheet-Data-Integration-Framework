package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Implementation of the Quartic (biweight), kernel function
 * 
 * @author klitos
 */
public class QuarticKernel extends AbstractKernelFunction {
	
	public QuarticKernel() {
		this.setKernelType(KernelType.BIWEIGHT);
	}

	@Override
	public double k(double u) {
		u = Math.abs(u);
		if (u <= 1) {
			double term = (1 - Math.pow(u, 2));
			return (15.0/16.0) * Math.pow(term, 2);
		 }			
		else {
			return 0.0;
		}
	}	
}//end class
