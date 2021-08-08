package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ResolvedInstanceType {
    Class<?> getInstanceType();

    Method getMethod();

    InstanceScope getScope();

    InvocationModel getInvocationModel();
}
