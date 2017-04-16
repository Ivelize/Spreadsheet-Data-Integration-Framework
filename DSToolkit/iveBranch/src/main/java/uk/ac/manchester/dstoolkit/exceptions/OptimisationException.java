package uk.ac.manchester.dstoolkit.exceptions;

public class OptimisationException extends QueryCompilerException {

    /**
     * Construct a new optimisation exception with
     * the given message.
     */
    public OptimisationException(String message) {
        super(message);   
    }
    
    /**
     * Construct a new optimisation exception with 
     * the given message and cause.
     */
    public OptimisationException(String message, Throwable cause) {
        super(message,cause);   
    }
    
}
