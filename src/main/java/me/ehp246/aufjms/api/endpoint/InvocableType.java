package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public interface InvocableType {
    Class<?> instanceType();

    Method method();

    InstanceScope scope();

    InvocationModel invocationModel();
}
