package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;

/**
 * Specifies the class should be invoked on a JMS message according to the type,
 * i.e., {@linkplain Message#getJMSType()}.
 * 
 * @author Lei Yang
 * @see Invoking
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForJmsType {
    /**
     * Specifies the JMS types, for which the class should be invoked.
     * <p>
     * The matching is done via {@linkplain String#matches(String)}.
     * <p>
     * When multiple values are specified, a single matching value would trigger
     * invocation.
     */
    String[] value();

    /**
     * Specifies the property names and values in pairs to match the incoming
     * message. The values must be in pairs. Un-paired values will trigger an
     * exception.
     * <p>
     * Multiple properties are treated as logical <code>||</code> between them. If
     * incoming message does not have a property, it is considered a no-match.
     * <p>
     * Only properties from {@linkplain Message#getPropertyNames()} are considered.
     * <p>
     * Property matching applies after type matching. I.e., type matching and
     * property matching are logical <code>&&</code>.
     * <p>
     * The matching is done via {@linkplain String#matches(String)}.
     * <p>
     * Spring property placeholder is supported on values but not on names.
     * 
     */
    String[] properties() default {};

    /**
     * Specifies how to instantiate the invocation instance.
     * 
     */
    InstanceScope scope() default InstanceScope.MESSAGE;

    InvocationModel invocation() default InvocationModel.DEFAULT;
}
