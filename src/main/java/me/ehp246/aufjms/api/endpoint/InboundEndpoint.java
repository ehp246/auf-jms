package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.DestinationType;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    ExecutableResolver resolver();

    int concurrency();

    String name();

    boolean autoStartup();

    String connectionFactory();

    DeadMsgConsumer deadMsgConsumer();

    interface From {
        String name();

        String selector();

        default DestinationType type() {
            return DestinationType.QUEUE;
        }

        Sub sub();

        interface Sub {
            String name();

            boolean shared();

            boolean durable();

        }
    }
}
