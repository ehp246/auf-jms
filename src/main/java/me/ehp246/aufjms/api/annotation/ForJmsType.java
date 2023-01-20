package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.jms.Message;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.inbound.InvocationModel;

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
     * <p>
     * If no value is specified, the class' simple name, i.e.,
     * {@linkplain Class#getSimpleName()}, is used as the default.
     */
    String[] value() default {};

    /**
     * Specifies how to instantiate the class instance.
     *
     * @see InstanceScope
     */
    InstanceScope scope() default InstanceScope.MESSAGE;

    /**
     * Not implemented.
     */
    InvocationModel invocation() default InvocationModel.DEFAULT;
}
