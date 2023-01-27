package me.ehp246.aufjms.api.inbound;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import jakarta.jms.Message;

/**
 * The definition of a Java type that is invokable by
 * {@linkplain Message#getJMSType()} and to be registered in a
 * {@linkplain InvocableTypeRegistry}.
 *
 * @author Lei Yang
 * @since 1.0
 */
public record InvocableTypeDefinition(Set<String> msgTypes, Class<?> type, Map<String, Method> methods,
        InstanceScope scope, InvocationModel model) {
    public InvocableTypeDefinition(final Set<String> msgTypes, final Class<?> type, final Map<String, Method> methods) {
        this(msgTypes, type, methods, InstanceScope.MESSAGE, InvocationModel.DEFAULT);
    }
}
