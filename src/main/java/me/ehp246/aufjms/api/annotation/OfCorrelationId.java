package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

/**
 * When applied to a parameter of a {@linkplain ByJms} interface, it specifies
 * the parameter argument value should be used for
 * {@linkplain Message#setJMSCorrelationID(String)}.
 * <p>
 * Only {@linkplain String} value is supported via
 * {@linkplain Object#toString()}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
public @interface OfCorrelationId {

}
