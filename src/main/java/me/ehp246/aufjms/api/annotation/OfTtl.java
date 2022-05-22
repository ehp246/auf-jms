package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfTtl {
    /*
     * Supports Spring property placeholder.
     */
    String value() default "PT0S";
}
