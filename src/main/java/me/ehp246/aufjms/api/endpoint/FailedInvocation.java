package me.ehp246.aufjms.api.endpoint;

/**
 * @author Lei Yang
 * @since 0.7.0
 */
public non-sealed interface FailedInvocation extends Invoked {
    Throwable thrown();
}
