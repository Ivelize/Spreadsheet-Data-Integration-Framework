package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Implementation of the Triangular, kernel function
 * 
 * @author klitos
 */
public class TriangularKernel extends AbstractKernelFunction {
	
	public TriangularKernel() {
		this.setKernelType(KernelType.TRIANGULAR);
	}

	@Override
	public double k(double u) {
		u = Math.abs(u);
		if (u <= 1)
			return 1.0 - u;
		else
			return 0.0; 
	}	
}//end class
