package uk.ac.manchester.dstoolkit.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public abstract class EvaluatorException extends Exception {

	//TODO sort out exception hierarchy

	private static final long serialVersionUID = 1L;
	// Logger object for logging in this class
	private static final Logger mLog = Logger.getLogger(EvaluatorException.class);
	/** The index of the exception's message in the message bundle. */
	protected int mMessageNumber;
	/** The parameters to substitute into the exception's message. */
	protected String[] mParams;
	/** The exception which caused this exception to be thrown, if any. */
	protected Throwable mException;

	public EvaluatorException(int num, Object[] params) {
		super("num:" + num + " params:" + params);

		this.mMessageNumber = num;
		this.mParams = convertToStringArray(params);
		this.mException = null;
	}

	public EvaluatorException(Object[] params) {
		super("params:" + params);

		this.mParams = convertToStringArray(params);
		this.mException = null;
	}

	public EvaluatorException(int num, Object[] params, Throwable ex) {
		super("num: " + num + " params: " + params + " ex: " + ex);

		this.mMessageNumber = num;
		this.mParams = convertToStringArray(params);
		this.mException = ex;
	}

	public EvaluatorException(Object[] params, Throwable ex) {
		super("params: " + params + " ex: " + ex);

		this.mParams = convertToStringArray(params);
		this.mException = ex;
	}

	@Override
	public String getLocalizedMessage() {
		int messageNumber = getMessageNumber();
		String[] params = getParams();
		return ("messageNumber: " + messageNumber + " params: " + params);
	}

	/**
	 * Write the log message into the log.
	 */
	public void toLog() {
		try {
			if (getMessage() != null) {
				mLog.error(getMessage());
			}

			if (mException != null) {
				mLog.error(mException.toString(), mException);
			}
		} catch (Exception e) {
			// 1000=Can''t log exception
			System.out.println("1000" + e.toString());
			System.out.println(toString());
		}
	}

	/**
	 * Utility method for retrieving the stacktrace as a String from an exception
	 *
	 * @param  ex The exception
	 * @return    The stacktrace of the exception
	 */
	public static String stackTraceToString(Throwable ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));

		return sw.toString();
	}

	/**
	 * Convert an array of <code>Object</code>s to an array of
	 * <code>String</code>s, of the same length, using each
	 * <code>Object</code>'s <code>toString()</code> method.
	 *
	 * @param array the array of <code>Object</code>s to convert
	 * @return a corresponding array of <code>String</code>s
	 */
	private String[] convertToStringArray(Object[] array) {
		if (array == null) {
			return null;
		}
		if (array instanceof String[]) {
			return (String[]) array;
		}

		String[] strings = new String[array.length];

		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				strings[i] = null;
			} else {
				strings[i] = array[i].toString();
			}
		}

		return strings;
	}

	/**
	 * Return the exception which caused this exception to be thrown.
	 *
	 * @return the exception, or <code>null</code> if none was specified
	 */
	public Throwable getException() {
		return mException;
	}

	/**
	 * Return the number of the message from the message bundle.
	 *
	 * @return the message number
	 */
	public int getMessageNumber() {
		return mMessageNumber;
	}

	/**
	 * Return the parameters to be substituted into the message at the
	 * appropriate positions.
	 *
	 * @return the message's parameters
	 */
	public String[] getParams() {
		return mParams;
	}
}
