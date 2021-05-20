package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.Received;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ExecutedInstance {
    Received getMsg();

    ResolvedExecutable getInstance();

    InvocationOutcome<?> getOutcome();
}