package me.ehp246.aufjms.api.inbound;

import me.ehp246.aufjms.api.jms.At;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    InvocableTypeRegistry typeRegistry();

    default String name() {
        return null;
    }

    default int concurrency() {
        return 0;
    }

    default boolean autoStartup() {
        return true;
    }

    default String connectionFactory() {
        return null;
    }

    default InvocationListener invocationListener() {
        return null;
    }

    default MsgConsumer defaultConsumer() {
        return null;
    }

    interface From {
        At on();

        default String selector() {
            return null;
        }

        Sub sub();

        interface Sub {
            String name();

            default boolean shared() {
                return true;
            }

            default boolean durable() {
                return true;
            }

        }
    }
}