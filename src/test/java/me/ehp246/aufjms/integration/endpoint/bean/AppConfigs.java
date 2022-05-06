package me.ehp246.aufjms.integration.endpoint.bean;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.endpoint.InvocableResolver;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;
import me.ehp246.aufjms.util.MockFrom;

/**
 * @author Lei Yang
 *
 */

class AppConfigs {
    @EnableForJms({})
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig01 {
        public static final String NAME = UUID.randomUUID().toString();
        @Bean
        InboundEndpoint endpoint01() {
            return new InboundEndpoint() {

                @Override
                public From from() {
                    return new MockFrom(At.toQueue("q"));
                }

                @Override
                public InvocableResolver resolver() {
                    return null;
                }

                @Override
                public String name() {
                    return NAME;
                }

            };
        }
    }

    @EnableForJms({})
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig02 {
        public static final String NAME = UUID.randomUUID().toString();

        @Bean
        InboundEndpoint endpoint01() {
            return new InboundEndpoint() {

                @Override
                public From from() {
                    return new MockFrom(At.toTopic("t"));
                }

                @Override
                public InvocableResolver resolver() {
                    return null;
                }

                @Override
                public String name() {
                    return NAME;
                }

                @Override
                public boolean autoStartup() {
                    return false;
                }
            };
        }
    }

    @EnableByJms
    @EnableForJms(@Inbound(@From("q1")))
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig03 {
        public static final CompletableFuture<Boolean> closeFuture = new CompletableFuture<>();
        @Bean
        CompletableFuture<Boolean> closeFuture() {
            return closeFuture;
        };

        @ByJms(@To("q1"))
        interface Send {
            void send();
        }
    }
}