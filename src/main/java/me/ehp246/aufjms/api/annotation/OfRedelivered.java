package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.jms.Message;

/**
 * Specifies the binding point of the value of
 * {@linkplain Message#getJMSRedelivered()}.
 * <p>
 * The injection point should be of {@code int} type.
 * <p>
 * Only applicable on {@linkplain ForJmsType}.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfRedelivered {

}
