package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the injection point for the value of JMS header 'JMSXDeliveryCount'
 * on a {@linkplain ForJmsType} object.
 * <p>
 * The injection point should be of {@linkplain Integer} type since the header
 * value could be <code>null</code>. {@code int} might result an exception.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface OfDeliveryCount {
}