package in.ehp246.aufjms.api.endpoint;

import in.ehp246.aufjms.api.jms.Msg;
import in.ehp246.aufjms.core.reflection.InvocationOutcome;

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