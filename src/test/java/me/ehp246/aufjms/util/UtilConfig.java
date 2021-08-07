package me.ehp246.aufjms.util;

import java.util.UUID;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufjms.api.jms.DestinationNameResolver;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@SpringBootApplication
public class UtilConfig {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
            .registerModule(new ParameterNamesModule());
    public static final ActiveMQConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory();
    public static final Destination TEST_QUEUE = new ActiveMQQueue(UUID.randomUUID().toString());

    static {
        CONNECTION_FACTORY.setBrokerURL("vm://embedded?broker.persistent=false,useShutdownHook=false");
    }

    @Bean
    public ObjectMapper toJson() {
        return OBJECT_MAPPER;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(CONNECTION_FACTORY);
        return factory;
    }

    @Bean
    public Connection connection() throws JMSException {
        return CONNECTION_FACTORY.createConnection();
    }

    public static DestinationNameResolver destinationNameResolver() {
        return (c, d) -> UtilConfig.TEST_QUEUE;
    }
}