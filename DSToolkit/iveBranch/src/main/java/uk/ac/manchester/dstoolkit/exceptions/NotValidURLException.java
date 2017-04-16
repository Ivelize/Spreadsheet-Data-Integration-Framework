package uk.ac.manchester.dstoolkit.exceptions;

/**
 * This exception to be used when checking URL used when reading
 * an online RDF Source
 *
 * @author Klitos
 */
public class NotValidURLException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotValidURLException() {}

	public NotValidURLException(String msg) {
		super(msg);
	}
}//end class
