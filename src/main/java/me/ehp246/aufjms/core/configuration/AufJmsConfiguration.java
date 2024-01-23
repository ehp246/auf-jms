package me.ehp246.aufjms.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.jms.ConnectionFactory;
import me.ehp246.aufjms.api.jms.ConnectionFactoryProvider;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.dispatch.DefaultDispatchFnProvider;
import me.ehp246.aufjms.core.dispatch.DispatchLogger;
import me.ehp246.aufjms.core.inbound.NoOpConsumer;
import me.ehp246.aufjms.provider.jackson.JsonByObjectMapper;

/**
 * @author Lei Yang
 * @since 1.0
 */
@Import({ DefaultDispatchFnProvider.class })
public final class AufJmsConfiguration {
    private final static List<String> MODULES = List.of(
            "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
            "com.fasterxml.jackson.module.mrbean.MrBeanModule",
            "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");

    @Bean("2392a7ae-3e11-4eeb-bd8c-cf54f5a1fa4b")
    public DispatchLogger dispatchLogger(
            @Value("${" + AufJmsConstants.DISPATCH_LOGTER + ":false}") final boolean enabled) {
        return enabled ? new DispatchLogger() : null;
    }

    @Bean("2744a1e7-9576-4f2e-8c56-6623247155e7")
    public PropertyResolver propertyResolver(
            final org.springframework.core.env.PropertyResolver springResolver) {
        return springResolver::resolveRequiredPlaceholders;
    }

    @Bean("90462ee7-99cd-4ce9-b299-89c983a8b069")
    public ConnectionFactoryProvider connectionFactoryProvider(final BeanFactory beanFactory) {
        return name -> {
            if (name == null || name.isBlank()) {
                return beanFactory.getBean(ConnectionFactory.class);
            }
            return beanFactory.getBean(name, ConnectionFactory.class);
        };
    }

    @Bean("44fc3968-7eba-47a3-a7b4-54e2b365d027")
    public NoOpConsumer noOpConsumer() {
        return new NoOpConsumer();
    }

    @Bean("ca50e6fd-0737-4cf2-ad54-77a2620c4735")
    public JsonByObjectMapper jsonByObjectMapper(final ApplicationContext appCtx) {
        final var aufJmsObjectMapper = appCtx.getBeansOfType(ObjectMapper.class)
                .get(AufJmsConstants.AUF_JMS_OBJECT_MAPPER);
        if (aufJmsObjectMapper != null) {
            return new JsonByObjectMapper(aufJmsObjectMapper);
        }

        try {
            return new JsonByObjectMapper(appCtx.getBean(ObjectMapper.class));
        } catch (final Exception e) {
            // Can not find a default. Creating private.
        }

        final ObjectMapper newMapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (final var name : MODULES) {
            if (ClassUtils.isPresent(name, this.getClass().getClassLoader())) {
                try {
                    newMapper.registerModule((Module) Class.forName(name)
                            .getDeclaredConstructor((Class[]) null).newInstance((Object[]) null));
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | ClassNotFoundException e) {
                }
            }
        }

        return new JsonByObjectMapper(newMapper);
    }
}
