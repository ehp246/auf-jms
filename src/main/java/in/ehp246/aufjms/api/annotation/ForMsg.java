package in.ehp246.aufjms.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import in.ehp246.aufjms.api.endpoint.InstanceScope;
import in.ehp246.aufjms.api.endpoint.InvocationModel;

/**
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForMsg {
	/**
	 * Regular expression to match Type.
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * Invocation instance resolution instruction.
	 * 
	 * @return
	 */
	InstanceScope scope() default InstanceScope.MESSAGE;

	InvocationModel invocation() default InvocationModel.DEFAULT;
}
