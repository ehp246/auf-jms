package me.ehp246.test.embedded.inbound.bean;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import jakarta.jms.Session;
import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.inbound.InboundEndpoint;
import me.ehp246.aufjms.api.inbound.InvocableTypeRegistry;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.mock.MockFrom;

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
                public InvocableTypeRegistry typeRegistry() {
                    return null;
                }

                @Override
                public String name() {
                    return "endpoint01";
                }

            };
        }

        @Bean
        InboundEndpoint endpoint02() {
            return new InboundEndpoint() {

                @Override
                public From from() {
                    return new MockFrom(At.toTopic("t"));
                }

                @Override
                public InvocableTypeRegistry typeRegistry() {
                    return null;
                }

                @Override
                public int sessionMode() {
                    return Session.DUPS_OK_ACKNOWLEDGE;
                }

                @Override
                public String name() {
                    return "endpoint02";
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
                public InvocableTypeRegistry typeRegistry() {
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

    @EnableForJms(value = @Inbound(@From("q1")), defaultConsumer = "")
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig04 {
    }

    @EnableForJms(@Inbound(@From("q1")))
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig05 {
    }

    @EnableForJms(@Inbound(value = @From("q1"), sessionMode = Session.CLIENT_ACKNOWLEDGE))
    @Import(EmbeddedArtemisConfig.class)
    static class AppConfig06 {
    }
}