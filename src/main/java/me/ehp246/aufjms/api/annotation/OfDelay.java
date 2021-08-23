package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD, PARAMETER})
public @interface OfDelay {
    String value() default "";
}
