package me.ehp246.test.embedded.endpoint.topic.sub;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
public class OnMsg {
    private final CompletableFuture<JmsMsg> ref = new CompletableFuture<>();

    public void invoke(final JmsMsg msg) {
        ref.complete(msg);
    }

    public JmsMsg take() throws InterruptedException, ExecutionException {
        return ref.get();
    }
}
