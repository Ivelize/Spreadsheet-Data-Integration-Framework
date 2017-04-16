package uk.ac.manchester.dstoolkit.exceptions;

public class LookupException extends QueryCompilerException {

    /**
     * Generic lookup exception extended by specific
     * lookup exceptions.  
     * @param message
     */
    public LookupException(String message) {
        super(message);
    }
    
}
