package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.endpoint.InvocationModel;

/**
 * Specifies an annotated class should be invoked on a JMS message according to
 * the JMS type, i.e., {@linkplain Message#getJMSType()}.
 * <p>
 * Regular expression is supported.
 * <p>
 * The library looks for the first match without a defined order. Overlapping
 * regular expressions from multiple {@linkplain ForJmsType}'s might result in
 * un-deterministic behavior.
 * 
 * @author Lei Yang
 * @see Invoking
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForJmsType {
    /**
     * Specifies the JMS types for which the class should be invoked.
     * <p>
     * The matching is done via {@linkplain String#matches(String)} where the
     * <code>this</code> object is from {@linkplain Message#getJMSType()} and the
     * argument is the value specified here which could be a regular expression.
     * <p>
     * When multiple values are specified, any single value could trigger
     * invocation. I.e., multiple types are considered logical <code>||</code>.
     */
    String[] value();

    /**
     * Specifies how to instantiate the invocation instance.
     * 
     */
    InstanceScope scope() default InstanceScope.MESSAGE;

    InvocationModel invocation() default InvocationModel.DEFAULT;
}
