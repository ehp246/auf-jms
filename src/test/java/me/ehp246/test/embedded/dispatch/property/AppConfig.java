package me.ehp246.test.embedded.dispatch.property;

import java.util.Map;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface Case01 {
        void ping();

        void ping(@OfProperty String appName);

        void ping(@OfProperty Map<String, Object> map);

        void ping(@OfProperty final String appName, @OfProperty("appName") String appName2);
    }

    @ByJms(value = @To(TestQueueListener.DESTINATION_NAME), properties = { "appName", "AufJms", "appVersion",
            "${app.version}" })
    interface Case02 {
        void ping();

        void ping(@OfProperty String appName);

        void ping(@OfProperty Map<String, Object> map);
    }
}
