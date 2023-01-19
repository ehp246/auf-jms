package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import jakarta.jms.MessageProducer;

/**
 * Specifies the argument to be passed to
 * {@linkplain MessageProducer#setDeliveryDelay(long)}.
 * <p>
 * If applied to multiple parameters, only the first is accepted.
 * <p>
 * When applied to a parameter on a {@linkplain ByJms} interface, its type must
 * be {@linkplain String} or {@linkplain Duration}. The value of the annotation
 * is ignored.
 * <p>
 * Can be applied to a method.
 * <p>
 * When applied to method, Spring property placeholder is supported.
 * <p>
 * Only applicable to {@linkplain ByJms} interfaces.
 *
 * @author Lei Yang
 * @since 1.0
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
public @interface OfDelay {
    String value() default "PT0S";
}
