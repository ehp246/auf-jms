package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.Map;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a Java type that is invokable by {@link JmsMsg#type()} to
 * be registered in the registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface MsgInvokableDefinition {
    String getMsgType();

    Class<?> getInstanceType();

    Map<String, Method> getMethods();

    InstanceScope getInstanceScope();

    InvocationModel getInvocationModel();
}
