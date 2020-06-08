package org.ehp246.aufjms.api.jms;

import javax.jms.Session;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgSinkContext {
	MsgSupplier getMsgSupplier();

	Session getSession();
}
