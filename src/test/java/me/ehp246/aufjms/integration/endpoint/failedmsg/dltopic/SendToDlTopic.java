package me.ehp246.aufjms.integration.endpoint.failedmsg.dltopic;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.endpoint.FailedInvocation;
import me.ehp246.aufjms.api.endpoint.FailedInvocationInterceptor;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To(value = "dltopic", type = DestinationType.TOPIC), name = "consumer2")
public interface SendToDlTopic extends FailedInvocationInterceptor {
    void send(JmsMsg msg);

    @Override
    default void accept(FailedInvocation failedMsg) {
        this.send(failedMsg.msg());
    }
}
