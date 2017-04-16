package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Implementation of the Epanechnikov, kernel function
 * 
 * @author klitos
 */
public class EpanechnikovKernel extends AbstractKernelFunction {
	
	public EpanechnikovKernel() {
		this.setKernelType(KernelType.EPANECHNIKOV);
	}

	@Override
	public double k(double u) {
		u = Math.abs(u);
		if (u <= 1)
			return (3.0 - (3.0 * Math.pow(u, 2)) ) / 4.0 ;
		else
			return 0.0; 
	}	
}//end class
