package me.ehp246.aufjms.core.endpoint;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From.Sub;

/**
 * @author Lei Yang
 *
 */
class AtEndpointRegistrarTestCases {
    @EnableForJms(@Inbound(value = @From(""), name = "96df151f-e6aa-419a-ab38-8de1a28c1d2e"))
    static class InboundConfig01 {
    }
    
    @EnableForJms(@Inbound(@From("queue.1")))
    static class InboundConfig02 {
    }

    @EnableForJms({ @Inbound(@From(value = "queue.1")),
            @Inbound(value = @From("queue.2"), concurrency = "executor.2", name = "atEndpoint.2") })
    static class InboundConfig03 {
    }

    @EnableForJms(@Inbound(@From(value = "", selector = "", sub = @Sub(value = "", shared = true, durable = false))))
    static class InboundConfig04 {
    }
}
