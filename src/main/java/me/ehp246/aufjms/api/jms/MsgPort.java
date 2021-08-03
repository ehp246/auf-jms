package me.ehp246.aufjms.api.jms;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgPort {
    JmsMsg accept(MsgSupplier msgSupplier);
}
