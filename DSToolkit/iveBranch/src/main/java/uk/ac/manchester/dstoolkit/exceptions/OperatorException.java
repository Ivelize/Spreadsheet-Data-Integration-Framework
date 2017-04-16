package uk.ac.manchester.dstoolkit.exceptions;

import org.apache.log4j.Logger;

public class OperatorException extends EvaluatorException {

	private static final long serialVersionUID = 1L;
	protected String operatorID;

    public OperatorException(int num, Object[] params, String operatorID) {
        super(num, params);
        this.operatorID = operatorID;
    }
    
    public OperatorException(Object[] params, String operatorID) {
        super(params);
        this.operatorID = operatorID;
    }

    public OperatorException(int num, Object[] params, Logger logger, String operatorID) {
		super(num, params);
        this.operatorID = operatorID;
		if (logger.isDebugEnabled()) {
			logger.debug("num: " + num + " params: " + params + this);
		}
	}
    
    public OperatorException(Object[] params, Logger logger, String operatorID) {
		super(params);
        this.operatorID = operatorID;
		if (logger.isDebugEnabled()) {
			logger.debug("params: " + params + this);
		}
	}

	public OperatorException(int num, Logger logger, String operatorID) {
		this(num, (Object[]) null, logger, operatorID);
	}
	
	public OperatorException(Logger logger, String operatorID) {
		this((Object[]) null, logger, operatorID);
	}

	public OperatorException(int num, Object[] params, Throwable ex, Logger logger, String operatorID) {
		super(num, params, ex);
        this.operatorID = operatorID;
		if (logger.isDebugEnabled()) {
			logger.debug("num: " + num + " params: " + params + " ex: " + ex);
		}
	}
	
	public OperatorException(Object[] params, Throwable ex, Logger logger, String operatorID) {
		super(params, ex);
        this.operatorID = operatorID;
		if (logger.isDebugEnabled()) {
			logger.debug("params: " + params + " ex: " + ex);
		}
	}

	public OperatorException(int num, Throwable ex, Logger logger, String operatorID) {
		this(num, (Object[]) null, ex, logger, operatorID);
	}
	
	public OperatorException(Throwable ex, Logger logger, String operatorID) {
		this((Object[]) null, ex, logger, operatorID);
	}

	public OperatorException(EvaluatorException e, Logger logger, String operatorID) {
		super(e.getMessageNumber(), e.getParams(), e.getException());
        this.operatorID = operatorID;
		if (logger.isDebugEnabled()) {
			logger.debug("mMessageNumber: " + mMessageNumber + " mParams: " + mParams + "mException: " + mException);
		}
	}
}
