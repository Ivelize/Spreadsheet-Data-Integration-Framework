package uk.ac.manchester.dstoolkit.service.impl.util.mutation;

/***
 * Object that hold the configuration for the mutation algorithm
 * 
 * @author klitos
 *
 */
public class MutationParameter {

	private MutationParameterType mutationParameterType;
	private String value;
	
	//Constuctor
	public MutationParameter(MutationParameterType t, String value) {
		this.mutationParameterType = t;
		this.value = value;
	}

	public MutationParameterType getMutationParameterType() {
		return mutationParameterType;
	}

	public void setMutationParameterType(MutationParameterType t) {
		this.mutationParameterType = t;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}//end class
