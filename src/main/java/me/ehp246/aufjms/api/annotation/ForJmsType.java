package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.jms.Message;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.inbound.InvocationModel;

/**
 * Indicates that the class defines methods that should be invoked on a message
 * by matching on message's JMS type, i.e., {@linkplain Message#getJMSType()}.
 * <p>
 * The method that is to be invoked is determined by the following lookup
 * process:
 * <ul>
 * <li>a <code>public</code> method annotated by {@linkplain Invoking}. If not
 * found, then...
 * <li>a <code>public</code> method named <code>invoke</code>.
 * </ul>
 * The signature and the declaration order are not considered. The first found
 * is accepted. If no method is found, it's an exception.
 * <p>
 * If the incoming's {@linkplain Message#getJMSReplyTo()} is specified and the
 * invocation is successful, a reply message will be sent to the destination.
 * The message will have:
 * <ul>
 * <li>the same type
 * <li>the same correlation id
 * <li>the return value as body. <code>null</code> if the method has no return.
 * </ul>
 *
 * @author Lei Yang
 * @since 1.0
 * @see Invoking
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForJmsType {
    /**
     * Specifies the message types for which a method of the class should be
     * invoked.
     * <p>
     * The matching is done via {@linkplain String#matches(String)} where the
     * <code>this</code> object is from {@linkplain Message#getJMSType()} and the
     * argument is the value specified here which could be a regular expression.
     * <p>
     * When multiple values are specified, the matching follows the declaration
     * order. Any single value could trigger invocation. I.e., multiple expressions
     * are considered logical <code>||</code>.
     * <p>
     * If no value is specified, the class' simple name, i.e.,
     * {@linkplain Class#getSimpleName()}, is used as the default.
     * <p>
     * The type matching is done without a defined order. Overlapping expressions
     * from multiple {@linkplain ForJmsType}'s might result in un-deterministic
     * behavior.
     */
    String[] value() default {};

    /**
     * Specifies how to instantiate an instance of the class.
     *
     * @see InstanceScope
     */
    InstanceScope scope() default InstanceScope.MESSAGE;

    /**
     * Not implemented.
     */
    InvocationModel invocation() default InvocationModel.DEFAULT;
}
