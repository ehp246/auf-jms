package in.ehp246.aufjms.api.jms;

/**
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgPort {
	Msg accept(MsgSupplier msgSupplier);
}
