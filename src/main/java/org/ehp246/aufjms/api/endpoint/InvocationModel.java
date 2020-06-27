package org.ehp246.aufjms.api.endpoint;

/**
 * Indication to Executor on how the action should be executed.
 * 
 * @author Lei Yang
 *
 */
public enum InvocationModel {
	/**
	 * Default. No special treatment required. Determined by the executor service.
	 */
	DEFAULT,
	/**
	 * Run the action synchronously. Don't take next message until it's done.
	 */
	SYNC
}
