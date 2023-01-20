package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import jakarta.jms.JMSProducer;

/**
 * Specifies the binding point of the value for
 * {@linkplain JMSProducer#setTimeToLive(long)}.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByJms} interface.
 * <p>
 * When applied to a parameter, {@linkplain Duration} or {@linkplain String}
 * type is supported.
 * <p>
 * When applied to a parameter, the specified value is ignored.
 * <p>
 * When applied to a method, Spring property placeholder is supported.
 * <p>
 * Only applicable on {@linkplain ByJms} interfaces.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfTtl {
    String value() default "PT0S";
}
