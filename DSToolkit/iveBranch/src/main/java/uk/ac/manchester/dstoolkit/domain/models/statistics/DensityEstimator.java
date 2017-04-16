package uk.ac.manchester.dstoolkit.domain.models.statistics;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelType;

/**
 * Hold some data for the Kernel Density Estimators
 * 
 * Reference: http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/#entity-hibspec-collection
 * 
 * @author klitos
 */
@Entity
@Table(name = "KERNEL_DENSITY_ESTIMATORS")
public class DensityEstimator extends DomainEntity {

	private static final long serialVersionUID = 4626243905580879316L;

	@Enumerated(EnumType.STRING)
	@Column(name = "ESTIMATOR_NAME", nullable = false)
	protected String estimatorName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ESTIMATOR_TYPE")
	protected KernelEstimatorType typeOfEstimator;
	
	@Column(name = "ESTIMATOR_IS_BOUNDED")
	private Boolean isBounded;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "KERNEL_TYPE")
	protected KernelType typeOfKernel;	

	//bandwidth, smoothing parameter
	@Column(name = "KERNEL_SMOOTHING")
	private double h;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CASE_TYPE")
	protected KernelCaseType typeOfCase;	
	
	@OneToMany(mappedBy = "sampleOf", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<KDESample> sampleDataPoints = new LinkedHashSet<KDESample>();

	/***
	 * Constructors
	 */	
	//Add default no-args constructor for Hibernate
	public DensityEstimator() {
		super();
	}
	
	public DensityEstimator(String name, KernelEstimatorType eType, boolean isBounded, KernelType kernel,
															  double kernelSmoothing, KernelCaseType typeOfCase) {
		super();
		this.setEstimatorName(name);
		this.setTypeOfEstimator(eType);
		this.setIsBounded(isBounded);
		this.setTypeOfKernel(kernel);
		this.setH(kernelSmoothing);
		this.setTypeOfCase(typeOfCase); //is it for the TP/ FP case
	}	
	
	//Create a KDE and store the data points used for this KDE
	public DensityEstimator(String name, KernelEstimatorType eType, boolean isBounded, KernelType kernel, double kernelSmoothing,
			                                                  KernelCaseType typeOfCase, Set<KDESample> dataPoints) {
		super();
		this.setEstimatorName(name);
		this.setTypeOfEstimator(eType);
		this.setIsBounded(isBounded);
		this.setTypeOfKernel(kernel);
		this.setH(kernelSmoothing);
		this.setTypeOfCase(typeOfCase); //is it for the TP/ FP case
		this.addDataPoints(dataPoints);
	}
	
	/***
	 * Getter/ Setter methods
	 */
	public Set<KDESample> getSampleDataPoints() {
		return sampleDataPoints;
	}

	public void setSampleDataPoints(Set<KDESample> sampleDataPoints) {
		this.sampleDataPoints = sampleDataPoints;
	}
	
	public void addDataPoint(KDESample point) {
		this.sampleDataPoints.add(point);
	}
	
	public void addDataPoints(Set<KDESample> sampleDataPoints) {
		for (KDESample point : sampleDataPoints) {
			this.addDataPoint(point);
		}//end for
	}
	
	/**
	 * Other Getter/Setter methods
	 */	
	public String getEstimatorName() {
		return estimatorName;
	}

	public void setEstimatorName(String estimatorName) {
		this.estimatorName = estimatorName;
	}

	public KernelEstimatorType getTypeOfEstimator() {
		return typeOfEstimator;
	}

	public void setTypeOfEstimator(KernelEstimatorType typeOfEstimator) {
		this.typeOfEstimator = typeOfEstimator;
	}

	public Boolean getIsBounded() {
		return isBounded;
	}

	public void setIsBounded(Boolean isBounded) {
		this.isBounded = isBounded;
	}

	public KernelType getTypeOfKernel() {
		return typeOfKernel;
	}	

	public void setTypeOfKernel(KernelType typeOfKernel) {
		this.typeOfKernel = typeOfKernel;
	}
	
	//bandwidth, smoothing parameter
	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public KernelCaseType getTypeOfCase() {
		return typeOfCase;
	}

	public void setTypeOfCase(KernelCaseType typeOfCase) {
		this.typeOfCase = typeOfCase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((estimatorName == null) ? 0 : estimatorName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((isBounded == null) ? 0 : isBounded.hashCode());
		result = prime * result
				+ ((typeOfCase == null) ? 0 : typeOfCase.hashCode());
		result = prime * result
				+ ((typeOfEstimator == null) ? 0 : typeOfEstimator.hashCode());
		result = prime * result
				+ ((typeOfKernel == null) ? 0 : typeOfKernel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DensityEstimator other = (DensityEstimator) obj;
		if (estimatorName == null) {
			if (other.estimatorName != null)
				return false;
		} else if (!estimatorName.equals(other.estimatorName))
			return false;
		if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
			return false;
		if (isBounded == null) {
			if (other.isBounded != null)
				return false;
		} else if (!isBounded.equals(other.isBounded))
			return false;
		if (typeOfCase != other.typeOfCase)
			return false;
		if (typeOfEstimator != other.typeOfEstimator)
			return false;
		if (typeOfKernel != other.typeOfKernel)
			return false;
		return true;
	}


}//end class