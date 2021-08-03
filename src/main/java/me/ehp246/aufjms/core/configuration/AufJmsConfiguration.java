package me.ehp246.aufjms.core.configuration;

import javax.jms.Connection;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.ToJson;
import me.ehp246.aufjms.api.jms.NamedConnectionProvider;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class AufJmsConfiguration {
    @Bean
    public ToJson toJson(final ObjectMapper objectMapper) {
        return new JsonByJackson(objectMapper);
    }

    @Bean
    public NamedConnectionProvider connectionMap(final ListableBeanFactory beanFactory) {
        return name -> beanFactory.getBean(Connection.class);
    }
}
