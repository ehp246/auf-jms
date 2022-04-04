package me.ehp246.aufjms.integration.endpoint.failedmsg;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.FailedMsg;
import me.ehp246.aufjms.api.endpoint.FailedMsgConsumer;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.integration.endpoint.failedmsg.dltopic.OnDlTopicMsg;
import me.ehp246.aufjms.integration.endpoint.failedmsg.failed.FailMsg;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("q1"), scan = FailMsg.class, failedMsgConsumer = "consumer1"),
        @Inbound(value = @From("q2"), scan = FailMsg.class, failedMsgConsumer = "consumer2"),
        @Inbound(value = @From(value = "dltopic", type = DestinationType.TOPIC), scan = OnDlTopicMsg.class) })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public CompletableFuture<FailedMsg> conRef1 = new CompletableFuture<>();
    public CompletableFuture<JmsMsg> dlqRef = new CompletableFuture<>();
    
    @Bean("consumer1")
    FailedMsgConsumer consumer1() {
        return msg -> conRef1.complete(msg);
    }
}
