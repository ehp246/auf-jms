package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.jms.Message;

/**
 * Specifies the binding point of the value for
 * {@linkplain Message#setJMSType(String)}.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByJms} interface.
 * <p>
 * When applied to a parameter, only {@linkplain String} type is supported.
 * <p>
 * When applied to a parameter, the specified value is ignored.
 * <p>
 * When applied to a method, Spring property placeholder is supported.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfType {
    String value() default "";
}
