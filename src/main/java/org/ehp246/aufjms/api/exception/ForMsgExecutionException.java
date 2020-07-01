package org.ehp246.aufjms.api.exception;

/**
 * @author Lei Yang
 *
 */
public class ForMsgExecutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ForMsgExecutionException(String message) {
		super(message);
	}

}
