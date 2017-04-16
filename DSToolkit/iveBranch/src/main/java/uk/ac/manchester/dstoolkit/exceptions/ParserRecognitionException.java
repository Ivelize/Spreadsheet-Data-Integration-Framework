package uk.ac.manchester.dstoolkit.exceptions;


public class ParserRecognitionException extends RuntimeException {

	public ParserRecognitionException() {}

	public ParserRecognitionException(String message) {
		super(message);
	}

	public ParserRecognitionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserRecognitionException(Throwable cause) {
		super(cause);
	}
}
