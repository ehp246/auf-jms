package me.ehp246.aufjms.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

import jakarta.jms.Message;

/**
 * Specifies the binding point for a JMS property. The annotation can be applied
 * on both the client side, i.e., {@linkplain ByJms} interfaces, and the server
 * side, i.e., {@linkplain ForJmsType} classes.
 * <p>
 * On the client side, applied to a parameter on a {@linkplain ByJms} interface,
 * it specifies the name and argument of the JMS property for the out-going
 * message.
 * <p>
 * On the server side, applied to a parameter of a {@linkplain ForJmsType}
 * {@linkplain Invoking} method, it specifies the injection point for the value
 * of the named property of the in-coming message.
 * <p>
 * All properties will be set/get via
 * {@linkplain Message#setObjectProperty(String, Object)} or
 * {@linkplain Message#getObjectProperty(String)}. No type checking, conversion
 * or validation will be done.
 * <p>
 * When the value is not specified, the property name is inferred from the
 * parameter name with the first letter capitalized. For this to work properly,
 * '<code>-parameters</code>' compiler option is probably desired.
 * <p>
 * Only application properties are supported, no JMS headers.
 *
 * @author Lei Yang
 * @since 1.0
 * @see Parameter#getName()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfProperty {
    /**
     * The name of the property.
     */
    String value() default "";
}
