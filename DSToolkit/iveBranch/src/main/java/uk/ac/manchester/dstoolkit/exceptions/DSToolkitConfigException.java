package uk.ac.manchester.dstoolkit.exceptions;

/**
 * Exception that is thrown when DSToolkit does not configured properly.
 * e.g., the graphviz.properties are not specified properly.
 *
 * @author Klitos
 */
public class DSToolkitConfigException extends Exception {

	private static final long serialVersionUID = 1L;

	public DSToolkitConfigException() {}

	public DSToolkitConfigException(String msg) {
		super(msg);
	}
}//end class
