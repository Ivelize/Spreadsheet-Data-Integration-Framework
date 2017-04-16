package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/***
 * Implementation of the Normal (Gaussian), kernel function
 * 
 * @author klitos
 */
public class GaussianKernel extends AbstractKernelFunction {
	
	public GaussianKernel() {
		this.setKernelType(KernelType.GAUSSIAN);
	}	
	
	@Override
	public double k(double u) {
		return Math.exp(-Math.pow(u, 2)/2) / Math.sqrt(2*Math.PI);		
	}	
}//end class
