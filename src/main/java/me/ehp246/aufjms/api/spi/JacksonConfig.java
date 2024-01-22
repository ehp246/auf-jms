package me.ehp246.aufjms.api.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Lei Yang
 *
 */
public final class JacksonConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(JacksonConfig.class);

    private final static List<String> MODULES = List.of(
            "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
            "com.fasterxml.jackson.module.mrbean.MrBeanModule",
            "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        for (final var name : MODULES) {
            if (ClassUtils.isPresent(name, this.getClass().getClassLoader())) {
                try {
                    objectMapper.registerModule((Module) Class.forName(name)
                            .getDeclaredConstructor((Class[]) null).newInstance((Object[]) null));
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException
                        | ClassNotFoundException e) {
                    LOGGER.atWarn().setCause(e).setMessage("Failed to register module {}:{}")
                            .addArgument(name::toString).addArgument(e::getMessage).log();
                }
            }
        }

        return objectMapper;
    }
}
