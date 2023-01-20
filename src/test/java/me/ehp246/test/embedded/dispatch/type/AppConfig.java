package me.ehp246.test.embedded.dispatch.type;

import java.util.Map;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.test.TestQueueListener;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface OfTypeCase01 {
        void ping();

        void ping(@OfType("default") String type);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface OfPropertyCase01 {
        void ping(@OfProperty("JMSXGroupID") String groupId, @OfProperty("JMSXGroupSeq") int groupSeq);

        void ping(@OfProperty Map<String, Object> map);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface OfCorrelationIdCase01 {
        void ping();

        void ping(@OfCorrelationId String id);
    }
}
