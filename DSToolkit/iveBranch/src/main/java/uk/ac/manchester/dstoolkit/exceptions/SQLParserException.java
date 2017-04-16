package uk.ac.manchester.dstoolkit.exceptions;

public class SQLParserException extends RuntimeException {

	public SQLParserException() {}

	public SQLParserException(String message) {
		super(message);
	}

	public SQLParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLParserException(Throwable cause) {
		super(cause);
	}

}
