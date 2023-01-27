package me.ehp246.aufjms.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the binding point of the value for {@code JMSXGroupSeq}.
 * <p>
 * Only applicable when {@linkplain OfGroupId} is specified.
 * <p>
 * {@linkplain int} and {@linkplain Integer} types are supported.
 * <p>
 * In case of {@linkplain Integer}, {@code null} is not supported.
 * <p>
 * Can be applied on both {@linkplain ByJms} and {@linkplain ForJmsType}.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfGroupSeq {
}
