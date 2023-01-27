package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Specifies which method should be invoked on a {@linkplain ForJmsType} class.
 * <p>
 * The method must be <code>public</code> and directly declared by the
 * {@linkplain ForJmsType} class.
 * <p>
 * De-serializing by {@linkplain JsonView} is supported on message payload.
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
