package me.ehp246.aufjms.integration.dispatch.listener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
@Import({ TestQueueListener.class, EmbeddedArtemisConfig.class })
class AppConfig {
    public CompletableFuture<DispatchRecord> preRef;
    public CompletableFuture<DispatchRecord> postRef;
    public CompletableFuture<DispatchRecord> exRef;

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyCase01 {
        void ping();

        void ping(Map<String, String> map);

        void ping(BodyPublisher pub);
    }


    @Bean
    DispatchListener dispatchListener() {
        return new DispatchListener() {

            @Override
            public void preSend(JmsDispatch dispatch, JmsMsg msg) {
                preRef = new CompletableFuture<>();
                preRef.complete(new DispatchRecord(dispatch, msg, null));
            }

            @Override
            public void postSend(JmsDispatch dispatch, JmsMsg msg) {
                postRef = new CompletableFuture<>();
                postRef.complete(new DispatchRecord(dispatch, msg, null));
            }

            @Override
            public void onException(JmsDispatch dispatch, JmsMsg msg, Exception e) {
                exRef = new CompletableFuture<>();
                exRef.complete(new DispatchRecord(dispatch, msg, e));
            }

        };
    }
}
