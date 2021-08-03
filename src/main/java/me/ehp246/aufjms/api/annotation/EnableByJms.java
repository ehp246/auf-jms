package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.byjms.ByJmsFactory;
import me.ehp246.aufjms.core.byjms.ByJmsRegistrar;
import me.ehp246.aufjms.core.byjms.DefaultDispatchFnProvider;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * Enables {@link ByJms}-annotated proxy interfaces scanning.
 *
 * @author Lei Yang
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ByJmsRegistrar.class, ByJmsFactory.class, DefaultDispatchFnProvider.class, JsonByJackson.class })
public @interface EnableByJms {
    Class<?>[] scan() default {};

    String replyTo() default "";
}
