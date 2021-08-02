package me.ehp246.aufjms.core.byjms;

import me.ehp246.aufjms.api.jms.ReplyToNameSupplier;

/**
 * @author Lei Yang
 *
 */
public class ReplyToNameSupplierFactory {
    public ReplyToNameSupplier newInstance(final String name) {
        return name != null ? name::toString : () -> null;
    }

}
