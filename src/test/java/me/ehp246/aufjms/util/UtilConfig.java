package me.ehp246.aufjms.util;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufjms.api.jms.DestinationProvider;

/**
 * @author Lei Yang
 *
 */
public class UtilConfig {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
            .registerModule(new ParameterNamesModule());
    public static final ActiveMQConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory();
    public static final Destination TEST_QUEUE = new ActiveMQQueue(UUID.randomUUID().toString());

    static {
        try {
            CONNECTION_FACTORY.setBrokerURL("vm://embedded?broker.persistent=false,useShutdownHook=false");
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public static DestinationProvider destinationNameResolver() {
        return (c, d) -> UtilConfig.TEST_QUEUE;
    }
}
