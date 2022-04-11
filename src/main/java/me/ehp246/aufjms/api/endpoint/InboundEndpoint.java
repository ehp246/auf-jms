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

    int concurrency();

    String name();

    boolean autoStartup();

    String connectionFactory();

    FailedInvocationInterceptor failedInvocationInterceptor();

    interface From {
        At on();

        String selector();

        Sub sub();

        interface Sub {
            String name();

            boolean shared();

            boolean durable();

        }
    }
}
