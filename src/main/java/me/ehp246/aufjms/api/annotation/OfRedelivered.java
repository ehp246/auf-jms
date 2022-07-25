package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

/**
 * Specifies the injection point for the value of
 * {@linkplain Message#getJMSRedelivered()} on a {@linkplain ForJmsType} object.
 * <p>
 * The injection point should be of {@code int} type.
 * <p>
 * Not applicable to {@linkplain ByJms} interfaces.
 * 
 * @author Lei Yang
 * @since 1.0
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfRedelivered {

}