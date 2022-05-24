package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.jms.Message;

/**
 * 
 * Specified the value for {@linkplain Message#setJMSExpiration(long)}.
 * <p>
 * Can be applied to a parameter or a method on a {@linkplain ByJms} interface.
 * <p>
 * When applied to a parameter, only {@linkplain Duration} type is supported.
 * <p>
 * When applied to a method, Spring property placeholder is supported.
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
