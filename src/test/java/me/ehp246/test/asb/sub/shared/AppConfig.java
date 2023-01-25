package me.ehp246.test.asb.sub.shared;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.connection.SingleConnectionFactory;

import jakarta.jms.ConnectionFactory;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.jms.DestinationType;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub(name = "shared", shared = true))),
        @Inbound(value = @From(value = "auf-jms.echo.event", type = DestinationType.TOPIC, sub = @Sub(name = "shared", shared = true))) })
@SpringBootApplication
class AppConfig {
    @Value("${test.servicebus.url}")
    private String url;
    @Value("${test.servicebus.username}")
    private String username;
    @Value("${test.servicebus.password}")
    private String password;

    // @Bean
    public ConnectionFactory jmsConnectionFactoryQpid() {
        final var singleConnectionFactory = new SingleConnectionFactory(
                new JmsConnectionFactory(username, password, url));
        singleConnectionFactory.setClientId("clientShared");
        return singleConnectionFactory;
    }

}
