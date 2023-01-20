package me.ehp246.test.embedded.endpoint.failedmsg;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;
import me.ehp246.aufjms.api.inbound.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.inbound.Invoked.Failed;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.embedded.endpoint.failedmsg.dltopic.OnDlqMsg;
import me.ehp246.test.embedded.endpoint.failedmsg.failed.FailMsg;

/**
 * @author Lei Yang
 *
 */
@ComponentScan
@EnableByJms
@EnableForJms({
        @Inbound(value = @From("q1"), scan = FailMsg.class, invocationListener = "consumer1"),
        @Inbound(value = @From("q2"), scan = FailMsg.class, invocationListener = "consumer2"),
        @Inbound(value = @From(value = "dlq", type = DestinationType.TOPIC, sub = @Sub("s1")), scan = OnDlqMsg.class) })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
    public CompletableFuture<Failed> conRef1 = new CompletableFuture<>();
    public CompletableFuture<JmsMsg> dlqRef = new CompletableFuture<>();
    
    @Bean("consumer1")
    OnFailed consumer1() {
        return failed -> conRef1.complete(failed);
    }
}
