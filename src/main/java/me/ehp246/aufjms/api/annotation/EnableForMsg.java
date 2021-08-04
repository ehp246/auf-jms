package me.ehp246.aufjms.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.core.configuration.JsonProviderSelector;
import me.ehp246.aufjms.core.configuration.PooledExecutorConfiguration;
import me.ehp246.aufjms.core.endpoint.MsgEndpointConfigurer;
import me.ehp246.aufjms.core.formsg.AtEndpointFactory;
import me.ehp246.aufjms.core.formsg.EnableForMsgRegistrar;

/**
 *
 * @author Lei Yang
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ EnableForMsgRegistrar.class, AtEndpointFactory.class, MsgEndpointConfigurer.class,
        PooledExecutorConfiguration.class, JsonProviderSelector.class })
public @interface EnableForMsg {
    At[] value() default @At;

    @Retention(RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface At {
        /**
         * Destination name of the incoming message.
         *
         * @return
         */
        String value() default "";

        Class<?>[] scan() default {};
    }
}
