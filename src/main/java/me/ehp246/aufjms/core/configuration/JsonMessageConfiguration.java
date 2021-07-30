package me.ehp246.aufjms.core.configuration;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufjms.api.endpoint.ExecutableBinder;
import me.ehp246.aufjms.api.jms.FromMsgBody;
import me.ehp246.aufjms.api.jms.MessageCreator;
import me.ehp246.aufjms.api.jms.ToJsonMsgBody;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import me.ehp246.aufjms.core.jms.TextMessageCreator;

/**
 * @author Lei Yang
 *
 */
public class JsonMessageConfiguration {
    @Bean
    public MessageCreator<?> textMessageBuilder(final ToJsonMsgBody<String> bodyWriter) {
        return new TextMessageCreator(bodyWriter);
    }

    @Bean
    public ExecutableBinder actionInvocationBinder(final FromMsgBody<String> fromBody) {
        return new DefaultExecutableBinder(fromBody);
    }
}
