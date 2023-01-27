package me.ehp246.test.embedded.dispatch.body;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.spi.JmsView;
import me.ehp246.test.TestQueueListener;
import me.ehp246.test.embedded.dispatch.body.Payload.Account;
import me.ehp246.test.embedded.dispatch.body.Payload.Person;
import me.ehp246.test.embedded.dispatch.body.Payload.PersonDob;
import me.ehp246.test.embedded.dispatch.body.Payload.PersonName;

/**
 * @author Lei Yang
 *
 */
@EnableJms
@EnableByJms
class AppConfig {
    @Bean
    ObjectMapper aufJmsObjectMapper() {
        // Don't include anything by default.
        return JsonMapper.builder().configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()
                .setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
                .registerModule(new ParameterNamesModule());
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyCase01 {
        void ping();

        void ping(Map<String, Object> map);

        void ping(Map<String, Object> map, int i);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyPublisherCase01 {
        void send(Supplier<String> pub);

        void send(String text);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface BodyAsTypeCase01 {
        void ping(Person person);

        void ping(PersonName personName);

        void ping(PersonDob personDob);
    }

    @ByJms(@To(TestQueueListener.DESTINATION_NAME))
    interface ViewCase01 {
        void pingWithAll(@JsonView(Payload.class) Account.Request request);
        void pingWithId(@JsonView(JmsView.class) Account.Request request);
    }
}
