package me.ehp246.aufjms.core.inbound;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;

/**
 * @author Lei Yang
 *
 */
class TestCases {
    @EnableForJms(@Inbound(value = @From(""), name = "96df151f-e6aa-419a-ab38-8de1a28c1d2e"))
    static class Config01 {
    }

    @EnableForJms(@Inbound(@From("queue.1")))
    static class Config02 {
    }

    @EnableForJms({ @Inbound(@From(value = "queue.1")),
            @Inbound(value = @From("queue.2"), concurrency = "executor.2", name = "atEndpoint.2"),
            @Inbound(@From(value = "queue.3")) })
    static class Config03 {
    }

    @EnableForJms({ @Inbound(value = @From(""), name = "1"), @Inbound(value = @From("queue.1"), name = "1") })
    static class Config04 {
    }
}
