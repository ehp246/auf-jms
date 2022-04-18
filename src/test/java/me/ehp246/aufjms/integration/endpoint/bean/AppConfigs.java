package me.ehp246.aufjms.integration.endpoint.bean;

import org.jgroups.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
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
                    return new MockFrom(At.toQueue(""));
                }

                @Override
                public ExecutableResolver resolver() {
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
                    return new MockFrom(At.toTopic(""));
                }

                @Override
                public ExecutableResolver resolver() {
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
}