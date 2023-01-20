package me.ehp246.aufjms.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the binding point of the value for {@code JMSXGroupID}.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByJms} interface.
 * <p>
 * When applied to a parameter, Only {@linkplain String} type is supported. The
 * specified value on annotation is ignored. {@code null} is accepted.
 * <p>
 * When applied to a method, Spring property placeholder is supported.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface OfGroupId {
    String value() default "";
}
