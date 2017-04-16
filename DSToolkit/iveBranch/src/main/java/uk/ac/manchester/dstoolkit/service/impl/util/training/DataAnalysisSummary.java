package uk.ac.manchester.dstoolkit.service.impl.util.training;

/***
 * This object will encapsulate the results of the SemEvidenceDataAnalysis, this object captures:
 * 
 * 		- totalPairsEquiClasses
 *      - 
 * 
 * 
 * @author klitos
 *
 */
public class DataAnalysisSummary {
	
	//Total number of pairs that are equivalent for both Classes & Properties
	private long totalPairsEquiClasses;
	private long totalPairsEquiProps;	

	//Total number of pairs that are Non-equivalent for both Classes & Properties
	private long totalPairsNonEquiClasses;
	private long totalPairsNonEquiProps;
	
	public DataAnalysisSummary() {
	}

	public long getTotalPairsEquiClasses() {
		return totalPairsEquiClasses;
	}

	public void setTotalPairsEquiClasses(long totalPairsEquiClasses) {
		this.totalPairsEquiClasses = totalPairsEquiClasses;
	}

	public long getTotalPairsEquiProps() {
		return totalPairsEquiProps;
	}

	public void setTotalPairsEquiProps(long totalPairsEquiProps) {
		this.totalPairsEquiProps = totalPairsEquiProps;
	}

	public long getTotalPairsNonEquiClasses() {
		return totalPairsNonEquiClasses;
	}

	public void setTotalPairsNonEquiClasses(long totalPairsNonEquiClasses) {
		this.totalPairsNonEquiClasses = totalPairsNonEquiClasses;
	}

	public long getTotalPairsNonEquiProps() {
		return totalPairsNonEquiProps;
	}

	public void setTotalPairsNonEquiProps(long totalPairsNonEquiProps) {
		this.totalPairsNonEquiProps = totalPairsNonEquiProps;
	}

	@Override
	public String toString() {
		return "DataAnalysisSummary [totalPairsEquiClasses="
				+ totalPairsEquiClasses + ", totalPairsEquiProps="
				+ totalPairsEquiProps + ", totalPairsNonEquiClasses="
				+ totalPairsNonEquiClasses + ", totalPairsNonEquiProps="
				+ totalPairsNonEquiProps + "]";
	}	
}//end class
