package me.ehp246.aufjms.core.endpoint.registrarcases;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;

/**
 * @author Lei Yang
 *
 */
public class AtEndpointRegistrarTestCases {
    @EnableForJms(@Inbound(@At("")))
    public static class InboundConfig01 {
    }
    
    @EnableForJms(@Inbound(@At("queue.1")))
    public static class InboundConfig02 {
    }

    @EnableForJms({ @Inbound(@At(value = "queue.1")),
            @Inbound(value = @At("queue.2"), concurrency = "executor.2", name = "atEndpoint.2") })
    public static class InboundConfig03 {
    }
}
