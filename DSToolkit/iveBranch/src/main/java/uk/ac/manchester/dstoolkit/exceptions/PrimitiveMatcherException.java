package uk.ac.manchester.dstoolkit.exceptions;

/**
 * Exceptions thrown for Matchers
 * e.g., 
 *   a primitive matcher cannot have child matchers, use this exception if this is the case
 *
 * @author Klitos
 */
public class PrimitiveMatcherException extends Exception {

	private static final long serialVersionUID = 1L;

	public PrimitiveMatcherException() {}

	public PrimitiveMatcherException(String msg) {
		super(msg);
	}
}//end class
