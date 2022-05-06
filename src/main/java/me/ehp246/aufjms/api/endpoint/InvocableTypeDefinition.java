package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a Java type that is invokable by
 * {@linkplain JmsMsg#type()} to be registered in the registry.
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocableTypeDefinition {
    Set<String> types();

    Class<?> instanceType();

    Map<String, Method> methods();

    InstanceScope instanceScope();

    InvocationModel invocationModel();
}
