package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

import org.apache.log4j.Logger;

public abstract class AbstractKernelFunction {
	
	private static Logger logger = Logger.getLogger(AbstractKernelFunction.class);

	private KernelType kernelType;	
	
	public abstract double k(double u);

	public KernelType getKernelType() {
		return kernelType;
	}

	public void setKernelType(KernelType kernelType) {
		this.kernelType = kernelType;
	}	
	
}//end class
