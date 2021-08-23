package me.ehp246.aufjms.util;

import static com.azure.spring.utils.ApplicationId.AZURE_SPRING_SERVICE_BUS;

import javax.jms.ConnectionFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.azure.spring.autoconfigure.jms.AzureServiceBusJMSProperties;
import com.azure.spring.autoconfigure.jms.SpringServiceBusJmsConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactorySettings;

import me.ehp246.aufjms.api.jms.ContextProvider;

/**
 * @author Lei Yang
 *
 */
@EnableConfigurationProperties(AzureServiceBusJMSProperties.class)
public class SimpleServiceBusConfig {
    @Bean
    public ConnectionFactory jmsConnectionFactory(AzureServiceBusJMSProperties serviceBusJMSProperties) {
        final String connectionString = serviceBusJMSProperties.getConnectionString();
        final String clientId = serviceBusJMSProperties.getTopicClientId();
        final int idleTimeout = serviceBusJMSProperties.getIdleTimeout();

        final ServiceBusJmsConnectionFactorySettings settings = new ServiceBusJmsConnectionFactorySettings(idleTimeout,
                false);
        settings.setShouldReconnect(false);
        final SpringServiceBusJmsConnectionFactory springServiceBusJmsConnectionFactory = new SpringServiceBusJmsConnectionFactory(
                connectionString, settings);
        springServiceBusJmsConnectionFactory.setClientId(clientId);
        springServiceBusJmsConnectionFactory.setCustomUserAgent(AZURE_SPRING_SERVICE_BUS);

        return springServiceBusJmsConnectionFactory;
    }
    @Bean
    public ContextProvider contextProvider(final ConnectionFactory connectionFactory) {
        final var context = connectionFactory.createContext();
        return name -> context;
    }

    @Bean
    ObjectMapper objectMapper() {
        return TestUtil.OBJECT_MAPPER;
    }
}
