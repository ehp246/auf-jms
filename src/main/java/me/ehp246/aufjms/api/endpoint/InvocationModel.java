package me.ehp246.aufjms.api.endpoint;

/**
 * Indication to Executor on how the action should be executed.
 *
 * @author Lei Yang
 * @since 1.0
 */
public enum InvocationModel {
    /**
     * Default. After binding the incoming message to an executable, the dispatcher
     * submits the bound to the executor service for execution. Once handed-off, the
     * message can't be rolled back to the broker. I.e., the message is committed
     * before its execution.
     *
     */
    DEFAULT,
    /**
     * Execute the invocation synchronously on the dispatcher thread. The dispatcher
     * will not accept the next message until this one is finished.
     */
    SYNC
}
