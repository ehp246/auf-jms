package me.ehp246.aufjms.api.jms;

import javax.jms.Message;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface MsgFn {
    Message apply(Msg msg);
}
