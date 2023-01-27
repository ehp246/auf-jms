package me.ehp246.aufjms.api.inbound.action;

import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
@ForJmsType
public class InboundEndpointSwitch {
    private final ApplicationContext appCtx;

    public InboundEndpointSwitch(final ApplicationContext appCtx) {
        super();
        this.appCtx = appCtx;
    }

    public void invoke(final Payload payload) {
        final var registry = this.appCtx.getBean(JmsListenerEndpointRegistry.class);
        if (payload.newState()) {
            registry.getListenerContainer(payload.name()).start();
        } else {
            registry.getListenerContainer(payload.name()).stop();
        }
    }

    public record Payload(String name, boolean newState) {
    }
}
