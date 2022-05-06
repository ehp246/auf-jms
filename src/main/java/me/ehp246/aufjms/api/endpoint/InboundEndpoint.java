package me.ehp246.aufjms.api.endpoint;

import me.ehp246.aufjms.api.jms.At;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public interface InboundEndpoint {
    From from();

    ExecutableResolver resolver();

    String name();

    default int concurrency() {
        return 0;
    }

    default boolean autoStartup() {
        return true;
    }

    default String connectionFactory() {
        return null;
    }

    default CompletedInvocationListener completedInvocationConsumer() {
        return null;
    }

    default FailedInvocationInterceptor failedInvocationInterceptor() {
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
