package me.ehp246.aufjms.core.endpoint.registrarcases;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.At;

/**
 * @author Lei Yang
 *
 */
public class AtEndpointRegistrarTestCases {
    @EnableForJms
    public static class AtEndpointConfig01 {
    }
    
    @EnableForJms(@At("queue.1"))
    public static class AtEndpointConfig02 {
    }

    @EnableForJms({ @At(value = "queue.1"),
            @At(value = "queue.2", connection = "connection.2", concurrency = "executor.2", name = "atEndpoint.2") })
    public static class AtEndpointConfig03 {
    }
}
