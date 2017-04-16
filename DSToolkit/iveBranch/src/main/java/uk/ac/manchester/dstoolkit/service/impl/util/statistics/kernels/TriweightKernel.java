package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Implementation of the Quartic (biweight), kernel function
 * 
 * @author klitos
 */
public class TriweightKernel extends AbstractKernelFunction {
	
	public TriweightKernel() {
		this.setKernelType(KernelType.TRIWEIGHT);
	}

	@Override
	public double k(double u) {
		u = Math.abs(u);
		if (u <= 1) {
			double term = (1 - Math.pow(u, 2));
			return (35.0/32.0) * Math.pow(term, 3);
		 }			
		else {
			return 0.0;
		}
	}	
}//end class
