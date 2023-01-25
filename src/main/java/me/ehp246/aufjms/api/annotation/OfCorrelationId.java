package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.jms.Message;

/**
 * Specifies the binding point for correlation id. The annotation can be applied
 * on both the client side, i.e., {@linkplain ByJms} interfaces, and the server
 * side, i.e., {@linkplain ForJmsType} classes.
 * <p>
 * When applied to a parameter of a {@linkplain ByJms} interface, it specifies
 * the argument value should be used for
 * {@linkplain Message#setJMSCorrelationID(String)}.
 * <p>
 * When applied to a parameter of a method on {@linkplain ForJmsType} class, it
 * binds the parameter to the incoming message's
 * {@linkplain Message#getJMSCorrelationID()}.
 * <p>
 * Only {@linkplain String} value is supported. {@code null} is supported.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfCorrelationId {
}
