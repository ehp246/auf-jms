package org.ehp246.aufjms.api.jms;

import javax.jms.Session;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgPortContext {
	MessageSupplier getMsgSupplier();

	Session getSession();
}
