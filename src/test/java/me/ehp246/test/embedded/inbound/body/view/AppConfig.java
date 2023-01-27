package me.ehp246.test.embedded.inbound.body.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Bean;

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
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.spi.JmsView;
import me.ehp246.test.TestQueueListener;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms({ @Inbound(@From(TestQueueListener.DESTINATION_NAME)) })
class AppConfig {
    @Bean
    List<CompletableFuture<Received>> receiving() {
        return new ArrayList<>();
    }

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
    interface Dispatch {
        void sendToDefault(Account account);

        void sendToId(Account account);

        void sendToIdPassword(Account account);

        void send(@OfType String type, Account account);

        record Account(String id, String password) {
        }
    }

    public interface Received {
        @JsonView({ JmsView.class, String.class })
        String getId();

        @JsonView({ String.class })
        String getPassword();
    }
}
