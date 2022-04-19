package me.ehp246.aufjms.integration.dispatch;

import java.util.Map;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.integration.dispatch.JsonAsType.Person;
import me.ehp246.aufjms.integration.dispatch.JsonAsType.PersonDob;
import me.ehp246.aufjms.integration.dispatch.JsonAsType.PersonName;
import me.ehp246.aufjms.util.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyCase01 {
        void ping();

        void ping(Map<String, Object> map);

        void ping(Map<String, Object> map, int i);
    }

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

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface ToJsonAsTypeCase01 {
        void ping(Person person);

        void ping(PersonName personName);

        void ping(PersonDob personDob);
    }
}
