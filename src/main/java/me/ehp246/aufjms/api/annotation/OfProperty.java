package me.ehp246.aufjms.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.jms.Message;

/**
 * When applied to a parameter of a {@linkplain ByJms} interface, it specifies
 * the name and argument for
 * {@linkplain Message#setObjectProperty(String, Object)}.
 * <p>
 * When applied to a method, Spring property placeholder is supported.
 * <p>
 * When applied to a parameter of a {@linkplain Invoking} method, it specifies
 * the injection point for the value of the named property.
 * <p>
 * All properties will be set/get via
 * {@linkplain Message#setObjectProperty(String, Object)} or
 * {@linkplain Message#getObjectProperty(String)} No type checking, conversion
 * or validation will be done.
 * 
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfProperty {
    /**
     * The name of the value. Required.
     */
    String value() default "";
}
