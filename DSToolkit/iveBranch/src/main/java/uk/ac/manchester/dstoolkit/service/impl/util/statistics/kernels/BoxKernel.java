package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Implementation of the Box (Uniform), kernel function
 * 
 * @author klitos
 */
public class BoxKernel extends AbstractKernelFunction {
	
	public BoxKernel() {
		this.setKernelType(KernelType.BOX);
	}

	@Override
	public double k(double u) {
		u = Math.abs(u);
		if (u <= 1)
			return 1.0 / 2.0;
		else
			return 0.0; 
	}	
}//end class
