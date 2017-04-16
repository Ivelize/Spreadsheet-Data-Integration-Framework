package uk.ac.manchester.dstoolkit.exceptions;

import org.apache.log4j.Logger;

public class DataException extends EvaluatorException {

	private static final long serialVersionUID = 1L;
	protected String queryID;
    protected String sourceURI;
    protected String destinationURI;

    public DataException(int num, Object[] params) {
		super(num, params);
	}
    
    public DataException(Object[] params) {
		super(params);
	}

	public DataException(int num, Object[] params, Throwable ex) {
		super(num, params, ex);
	}
	
	public DataException(Object[] params, Throwable ex) {
		super(params, ex);
	}

    public DataException(String queryID, int num, Object[] params, Throwable ex) {
		super(num, params, ex);
        this.queryID = queryID;
	}
    
    public DataException(String queryID, Object[] params, Throwable ex) {
		super(params, ex);
        this.queryID = queryID;
	}

    public DataException(int num, Object[] params, Throwable ex, Logger logger) {
		super(num, params, ex);        
		if (logger.isDebugEnabled()) {
			logger.debug("num: " + num + " params: " + params + " ex: " + ex);
		}
	}
    
    public DataException(Object[] params, Throwable ex, Logger logger) {
		super(params, ex);        
		if (logger.isDebugEnabled()) {
			logger.debug("params: " + params + " ex: " + ex);
		}
	}

    public DataException(int num, Throwable ex, Logger logger) {
		this(num, (Object[]) null, ex, logger);
	}
    
    public DataException(Throwable ex, Logger logger) {
		this((Object[]) null, ex, logger);
	}
    
    public DataException(int num, Logger logger) {
		this(num, (Object[]) null, null, logger);
	}
    
    public DataException(Logger logger) {
		this((Object[]) null, null, logger);
	}

    public DataException(String queryID, String source, String dest, int num, Object[] params, Throwable ex) {
		super(num, params, ex);
        this.queryID = queryID;
        this.sourceURI = source;
        this.destinationURI = dest;
	}
    
    public DataException(String queryID, String source, String dest, Object[] params, Throwable ex) {
		super(params, ex);
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
