package me.ehp246.aufjms.integration.dispatch;

import java.util.Map;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @ByJms(@At(TestQueueListener.DESTINATION_NAME))
    interface BodyCase01 {
        void ping();

        void ping(Map<String, Object> map);

        void ping(Map<String, Object> map, int i);
    }

    @ByJms(@At(TestQueueListener.DESTINATION_NAME))
    interface OfTypeCase01 {
        void ping();

        void ping(@OfType("default") String type);
    }

    @ByJms(@At(TestQueueListener.DESTINATION_NAME))
    interface OfPropertyCase01 {
        void ping(@OfProperty("JMSXGroupID") String groupId, @OfProperty("JMSXGroupSeq") int groupSeq);

        void ping(@OfProperty Map<String, Object> map);
    }
}
