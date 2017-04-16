package uk.ac.manchester.dstoolkit.exceptions;

import org.apache.log4j.Logger;

public class MessageException extends EvaluatorException {

	private static final long serialVersionUID = 1L;
	protected String queryID;
    protected String sourceURI;
    protected String destinationURI;
    
    public MessageException(int num, Object[] params) {
		super(num, params);
	}
    
    public MessageException(Object[] params) {
		super(params);
	}

	public MessageException(int num, Object[] params, Throwable ex) {
		super(num, params, ex);
	}
	
	public MessageException(Object[] params, Throwable ex) {
		super(params, ex);
	}

    public MessageException(String queryID, int num, Object[] params, Throwable ex) {
		super(num, params, ex);
        this.queryID = queryID;
	}
    
    public MessageException(String queryID, Object[] params, Throwable ex) {
		super(params, ex);
        this.queryID = queryID;
	}

    public MessageException(int num, Object[] params, Throwable ex, Logger logger) {
		super(num, params, ex);        
		if (logger.isDebugEnabled()) {
			logger.debug("num: " + num + " params: " + params + " ex: " + ex);
		}
	}
    
    public MessageException(Object[] params, Throwable ex, Logger logger) {
		super(params, ex);        
		if (logger.isDebugEnabled()) {
			logger.debug("params: " + params + " ex: " + ex);
		}
	}

    public MessageException(int num, Throwable ex, Logger logger) {
		this(num, (Object[]) null, ex, logger);
	}

    public MessageException(Throwable ex, Logger logger) {
		this((Object[]) null, ex, logger);
	}
    
    public MessageException(String queryID, String source, String dest, int num, Object[] params, Throwable ex) {
		super(num, params, ex);
        this.queryID = queryID;
        this.sourceURI = source;
        this.destinationURI = dest;
	}

    public String getQueryID() {
        return queryID;
    }

    public void setQueryID(String queryID) {
        this.queryID = queryID;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String getDestinationURI() {
        return destinationURI;
    }

    public void setDestinationURI(String destinationURI) {
        this.destinationURI = destinationURI;
    }
}
