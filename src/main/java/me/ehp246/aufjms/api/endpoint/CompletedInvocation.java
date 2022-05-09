package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 *
 */
public non-sealed interface CompletedInvocation extends Invoked {
    Object returned();
}
