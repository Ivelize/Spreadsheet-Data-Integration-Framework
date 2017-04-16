package uk.ac.manchester.dstoolkit.domain.models.statistics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;

/**
 * 
 * 
 * @author klitos
 */
@Entity
@Table(name = "SAMPLE_KDE")
public class KDESample extends DomainEntity {
		
	private static final long serialVersionUID = -8468876461740108307L;

	protected static Logger log = Logger.getLogger(KDESample.class);
	
	@Column(name = "SAMPLE_VALUE", nullable = false, updatable = false)
	private double value;
	
	public KDESample() {
		super();
	}
	
	//Create a new value for this data point
	public KDESample(double v) {
		super();
		this.setValue(v);	
	}
	
	@ManyToOne
	@JoinColumn(name = "ESTIMATOR_ID", nullable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "FK_SAMPLE_ESTIMATOR_ID")
	private DensityEstimator sampleOf; //sampleOf estimator

	
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DensityEstimator getSampleOf() {
		return sampleOf;
	}

	public void setSampleOf(DensityEstimator sampleOf) {
		this.sampleOf = sampleOf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((sampleOf == null) ? 0 : sampleOf.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		KDESample other = (KDESample) obj;
		if (sampleOf == null) {
			if (other.sampleOf != null)
				return false;
		} else if (!sampleOf.equals(other.sampleOf))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

}//end class
