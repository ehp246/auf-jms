package me.ehp246.aufjms.integration.endpoint.deadletter;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
class OnMsg {
    public final RuntimeException ex = new RuntimeException();

    @Invoking
    public void perform(JmsMsg msg) throws InterruptedException, ExecutionException {
        throw ex;
    }
}
