package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

/**
 * When applied to a parameter of a {@linkplain ByJms} interface, it specifies
 * the argument value should be used for
 * {@linkplain Message#setJMSCorrelationID(String)}.
 * <p>
 * Only {@linkplain String} value is supported.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfCorrelationId {

}
