package me.ehp246.test.asb.sub.durable;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.SingleConnectionFactory;

import jakarta.jms.ConnectionFactory;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@EnableForJms({
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub(name = "durable$c1$D", durable = true))) })
@SpringBootApplication
class AppConfig {
    @Value("${test.servicebus.url}")
    private String url;
    @Value("${test.servicebus.username}")
    private String username;
    @Value("${test.servicebus.password}")
    private String password;

    @Bean
    public ConnectionFactory jmsConnectionFactoryQpid() {
        final var singleConnectionFactory = new SingleConnectionFactory(
                new JmsConnectionFactory(username, password, url));
        singleConnectionFactory.setClientId("c1");
        return singleConnectionFactory;
    }
}
