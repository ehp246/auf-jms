package org.ehp246.aufjms.api.endpoint;

import org.ehp246.aufjms.api.jms.Msg;
import org.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ExecutedInstance {
	Msg getMsg();

	ResolvedExecutable getInstance();

	InvocationOutcome<?> getOutcome();
}