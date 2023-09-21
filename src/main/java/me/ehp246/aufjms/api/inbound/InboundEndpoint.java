package me.ehp246.aufjms.api.inbound;

import org.springframework.util.ErrorHandler;

import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.Session;
import me.ehp246.aufjms.api.jms.At;

/**
 * The definition of a Auf-JMS message listener.
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

    default ErrorHandler errorHandler() {
        return null;
    }

    default ExceptionListener exceptionListener() {
        return null;
    }

    default MsgConsumer defaultConsumer() {
        return null;
    }

    /**
     * Defines the session mode for the endpoint.
     * <p>
     * Defaults to {@linkplain Session#SESSION_TRANSACTED}.
     *
     * @see Connection#createSession(int)
     */
    default int sessionMode() {
        return Session.SESSION_TRANSACTED;
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
