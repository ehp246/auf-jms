package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface InvocableDispatcher {
    void dispatch(final Invocable invocable, final MsgContext msgCtx);
}
