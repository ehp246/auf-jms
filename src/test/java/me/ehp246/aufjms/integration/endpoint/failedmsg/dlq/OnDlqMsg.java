package me.ehp246.aufjms.integration.endpoint.failedmsg.dlq;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
public class OnDlqMsg {
    public final CompletableFuture<JmsMsg> msgRef = new CompletableFuture<>();

    @Invoking
    public void perform(JmsMsg msg) {
        msgRef.complete(msg);
    }
}
