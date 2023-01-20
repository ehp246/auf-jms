package me.ehp246.test.embedded.dispatch.listener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import({ EmbeddedArtemisConfig.class })
class AppConfig {
    public CompletableFuture<DispatchRecord> onDispatchRef;
    public CompletableFuture<DispatchRecord> preRef;
    public CompletableFuture<DispatchRecord> postRef;
    public CompletableFuture<DispatchRecord> exRef;

    @ByJms(@To("q1"))
    interface BodyCase01 {
        void ping();

        void ping(Map<String, String> map);

        void ping(BodyPublisher pub);
    }

    @Bean
    DispatchListener onDispatch() {
        return new DispatchListener.OnDispatch() {

            @Override
            public void onDispatch(JmsDispatch dispatch) {
                onDispatchRef = new CompletableFuture<>();
                onDispatchRef.complete(new DispatchRecord(dispatch, null, null));
            }
        };
    }

    @Bean
    DispatchListener preSend() {
        return new DispatchListener.PreSend() {
            @Override
            public void preSend(JmsDispatch dispatch, JmsMsg msg) {
                preRef = new CompletableFuture<>();
                preRef.complete(new DispatchRecord(dispatch, msg, null));
            }
        };
    }

    @Bean
    DispatchListener postSend() {
        return new DispatchListener.PostSend() {
            @Override
            public void postSend(JmsDispatch dispatch, JmsMsg msg) {
                postRef = new CompletableFuture<>();
                postRef.complete(new DispatchRecord(dispatch, msg, null));
            }
        };
    }

    @Bean
    DispatchListener onException() {
        return new DispatchListener.OnException() {
            @Override
            public void onException(JmsDispatch dispatch, JmsMsg msg, Exception e) {
                exRef = new CompletableFuture<>();
                exRef.complete(new DispatchRecord(dispatch, msg, e));
            }

        };
    }
}
