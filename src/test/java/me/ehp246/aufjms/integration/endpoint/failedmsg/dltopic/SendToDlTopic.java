package me.ehp246.aufjms.integration.endpoint.failedmsg.dltopic;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.endpoint.InvocationListener.OnFailed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To(value = "dltopic", type = DestinationType.TOPIC), name = "consumer2")
public interface SendToDlTopic extends OnFailed {
    void send(JmsMsg msg);

    @Override
    default void onFailed(Failed failed) {
        this.send(failed.bound().msg());
    }
}
