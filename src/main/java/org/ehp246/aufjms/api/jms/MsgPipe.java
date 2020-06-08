package org.ehp246.aufjms.api.jms;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgPipe {
	Msg take(MsgSupplier msgSupplier);
}
