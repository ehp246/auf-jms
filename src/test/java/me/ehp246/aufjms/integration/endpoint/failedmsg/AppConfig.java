package me.ehp246.aufjms.integration.endpoint.failedmsg;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.FailedMsgConsumer;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.integration.endpoint.failedmsg.dlq.OnDlqMsg;
import me.ehp246.aufjms.integration.endpoint.failedmsg.failed.OnFailedMsg;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("q1"), scan = OnFailedMsg.class, failedMsgConsumer = "ref1"),
        @Inbound(value = @From("q2"), scan = OnFailedMsg.class, failedMsgConsumer = "consumer2"),
        @Inbound(value = @From("DLQ.DLA"), scan = OnDlqMsg.class) })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public CompletableFuture<DeadMsg> ref1 = new CompletableFuture<>();
    public CompletableFuture<JmsMsg> dlqRef = new CompletableFuture<>();
    
    @Bean("ref1")
    FailedMsgConsumer deadMsgConsumer1() {
        return msg -> ref1.complete(new DeadMsg(msg.msg(), msg.exception()));
    }

    @Bean("consumer2")
    FailedMsgConsumer consumer2() {
        return msg -> {
            throw new RuntimeException();
        };
    }

    ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
        return config -> {
            Map<String, AddressSettings> addressesSettings = config.getAddressesSettings();
            final var addressSettings = addressesSettings.get("#");
            addressSettings.setMaxDeliveryAttempts(2);
            addressSettings.setAutoCreateDeadLetterResources(true);
            addressSettings.setDeadLetterAddress(SimpleString.toSimpleString("DLA"));

            config.addAddressesSetting("q2", addressSettings);
        };
    }
}
