package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.logging.log4j.ThreadContext;

/**
 * Specifies the binding point for a Log4j {@linkplain ThreadContext} value. The
 * annotation can be applied on both the client side, i.e., {@linkplain ByJms}
 * interfaces, and the server side, i.e., {@linkplain ForJmsType} classes.
 * <p>
 * On the client side, applied to a parameter on a {@linkplain ByJms} interface,
 * it specifies the key and value for {@linkplain ThreadContext}.
 * <p>
 * On the server side, applied to a parameter of a {@linkplain ForJmsType}
 * {@linkplain Invoking} method, it specifies the supplier parameter for the
 * value of the named context.
 * <p>
 * When applied to a parameter, the context value will be supplied by the
 * argument via {@linkplain Object#toString()}.
 * <p>
 * The annotation can also be applied to a supplier method defined by the type
 * of the body parameter on either the client side and the server side.
 *
 * The supplier method must
 * <ul>
 * <li>be declared on the type directly, no recursion or inheritance</li>
 * <li><code>public</code></li>
 * <li>have no parameter</li>
 * <li>return a value</li>
 * </ul>
 * The return value, if not <code>null</code>, will be converted to
 * {@linkplain String} via {@linkplain Object#toString()}. If no name is
 * specified by the annotation, the method name will be used as the context key.
 * <p>
 * Note that there is only one {@linkplain ThreadContext} for each thread. If
 * there is an existing context on the thread, it will be overwritten by the new
 * value from the annotation. After execution, the all keys will be removed
 * resulting the lose of the original values.
 * <p>
 * In case of a name collision, the following defines the precedence from high
 * to low:
 * <ul>
 * <li>supplier methods from body argument</li>
 * <li>body argument itself</li>
 * <li>other arguments, e.g., headers and properties</li>
 * </ul>
 *
 * @author Lei Yang
 * @since 2.3.1
 * @see ThreadContext
 * @see Parameter#getName()
 * @see Method#getName()
 * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
 *      Names at Runtime</a>
 * @see <a href=
 *      'https://logging.apache.org/log4j/2.x/manual/thread-context.html'>Log4j
 *      2 Thread Context</a>
 *
 */
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
public @interface OfLog4jContext {
    /**
     * Specifies the name of the {@linkplain ThreadContext}.
     * <p>
     * When no value is specified, the context name is inferred from the parameter
     * name. For this to work properly, '<code>-parameters</code>' compiler option
     * is desired.
     */
    String value() default "";

    /**
     * Specifies the operation of the annotation.
     */
    OP op() default OP.Default;

    enum OP {
        /**
         * Specifies to use the parameter's {@linkplain Object#toString()} as the value.
         */
        Default,
        /**
         * Specifies to look for {@linkplain OfLog4jContext}-annotated supplier methods
         * for values instead of the argument itself.
         */
        Introspect
    }
}
