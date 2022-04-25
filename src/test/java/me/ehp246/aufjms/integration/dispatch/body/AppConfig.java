package me.ehp246.aufjms.integration.dispatch.body;

import java.util.Map;

import org.springframework.jms.annotation.EnableJms;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.dispatch.BodyPublisher;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.Person;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.PersonDob;
import me.ehp246.aufjms.integration.dispatch.body.JsonAsType.PersonName;
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
    interface BodyPublisherCase01 {
        void send(BodyPublisher pub);

        void send(String text);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyAsTypeCase01 {
        void ping(Person person);

        void ping(PersonName personName);

        void ping(PersonDob personDob);
    }
}
