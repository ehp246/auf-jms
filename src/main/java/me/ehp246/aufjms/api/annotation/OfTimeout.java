package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({METHOD, PARAMETER})
/**
 * @author Lei Yang
 *
 */
public @interface OfTimeout {
    /**
     * Timeout waiting for a reply. It has no impact on the TTL of the request
     * message.
     * <p>
     * Default is no timeout. I.e., the caller will wait for a reply indefinitely.
     *
     * @see <a href='https://en.wikipedia.org/wiki/ISO_8601#Durations'>ISO
     *      Durations</a>
     */
    String value();
}
