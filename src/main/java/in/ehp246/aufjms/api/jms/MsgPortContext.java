package in.ehp246.aufjms.api.jms;

import javax.jms.Session;

/**
 * 
 * @author Lei Yang
 *
 */
public interface MsgPortContext {
	MsgSupplier getMsgSupplier();

	Session getSession();
}
