package me.ehp246.test.embedded.endpoint.failedmsg.failed;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.inbound.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
public class FailMsg {
    public final RuntimeException ex = new RuntimeException("Let it throw");

    @Invoking
    public void perform(JmsMsg msg) throws InterruptedException, ExecutionException {
        throw ex;
    }
}
