package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
public interface FailedMsgConsumer {
    void accept(FailedMsg failedMsg);
}
