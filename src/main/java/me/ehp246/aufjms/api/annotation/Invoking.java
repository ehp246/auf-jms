package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies which method should be invoked on a {@linkplain ForJmsType} class.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ForJmsType
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface Invoking {
    String value() default "";
}
