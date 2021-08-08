package me.ehp246.aufjms.api.jms;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgPortProvider {
    MsgPort get(MsgPortDestinationSupplier destinationSupplier);
}
