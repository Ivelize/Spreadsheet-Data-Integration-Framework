package uk.ac.manchester.dstoolkit.exceptions;

public class QueryEvaluationException extends EvaluatorException {

	private static final long serialVersionUID = 1L;

	public QueryEvaluationException(int num, Object[] params) {
		super(num, params);
	}
	
	public QueryEvaluationException(Object[] params) {
		super(params);
	}

	public QueryEvaluationException(int num, Object[] params, Throwable ex) {
		super(num, params, ex);
	}
	
	public QueryEvaluationException(Object[] params, Throwable ex) {
		super(params, ex);
	}
}
