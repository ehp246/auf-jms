package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.InvocationOutcome;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ExecutedInstance {
    JmsMsg getMsg();

    Executable getInstance();

    InvocationOutcome<?> getOutcome();
}