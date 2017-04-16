package uk.ac.manchester.dstoolkit.exceptions;

public class KeyNotPresentException extends LookupException {

    public KeyNotPresentException(String table) {
        super(table);   
    }
    
}
