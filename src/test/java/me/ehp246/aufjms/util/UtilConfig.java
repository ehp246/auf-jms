package me.ehp246.aufjms.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.ListableBeanFactory;
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

import me.ehp246.aufjms.api.ToJson;
import me.ehp246.aufjms.api.jms.NamedConnectionProvider;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@SpringBootApplication
public class UtilConfig {
    public static final String TEST_QUEUE = "test.queue";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
            .registerModule(new ParameterNamesModule());

    @Bean
    public ToJson toJson() {
        return new JsonByJackson(OBJECT_MAPPER);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://embedded?broker.persistent=false,useShutdownHook=false");
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(final ConnectionFactory connFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connFactory);
        return factory;
    }

    @Bean
    public Connection connection(final ConnectionFactory connFactory) throws JMSException {
        return connFactory.createConnection();
    }

    @Bean
    public NamedConnectionProvider connProvider(final ListableBeanFactory beanFactory) {
        return name -> beanFactory.getBean(Connection.class);
    }
}
