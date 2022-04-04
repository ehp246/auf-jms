package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ResolvedInstanceType {
    Class<?> instanceType();

    Method method();

    InstanceScope scope();

    InvocationModel invocationModel();
}
