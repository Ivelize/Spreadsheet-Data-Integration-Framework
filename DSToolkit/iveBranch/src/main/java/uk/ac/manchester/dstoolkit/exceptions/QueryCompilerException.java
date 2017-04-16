package uk.ac.manchester.dstoolkit.exceptions;

/**
 * This exception is used to mark access violations.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class QueryCompilerException
	extends RuntimeException {

	public QueryCompilerException() {}

	public QueryCompilerException(String message) {
		super(message);
	}

	public QueryCompilerException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryCompilerException(Throwable cause) {
		super(cause);
	}
}
