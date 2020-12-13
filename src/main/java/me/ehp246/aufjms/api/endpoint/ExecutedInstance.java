package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.Msg;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

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