package me.ehp246.aufjms.api.endpoint;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
@FunctionalInterface
public interface InvocableBinder {
    BoundInvocable bind(Invocable invocable, MsgContext msgCtx);
}
