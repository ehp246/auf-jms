package me.ehp246.aufjms.api.inbound;

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
public record InvocableTypeDefinition(Set<String> msgTypes, Class<?> type, Map<String, Method> methods,
        InstanceScope scope, InvocationModel model) {
}
