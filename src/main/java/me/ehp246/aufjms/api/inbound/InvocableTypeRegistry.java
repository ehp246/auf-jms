package me.ehp246.aufjms.api.inbound;

import java.util.Map;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * The abstraction of a {@linkplain ForJmsType} registry for an
 * {@linkplain InboundEndpoint}.
 * <p>
 * Updates on the registry should take effect immediately.
 * <p>
 * Must be thread safe.
 *
 * @author Lei Yang
 * @since 1.0
 * @see InboundEndpoint
 */
public interface InvocableTypeRegistry {
    /**
     * Register a new definition.
     */
    void register(InvocableTypeDefinition definition);

    /**
     * Returns an un-modifiable copy of all registered.
     */
    Map<String, InvocableTypeDefinition> registered();

    /**
     * Resolves a {@linkplain JmsMsg} to an {@linkplain InvocableType}.
     *
     * @return <code>null</code> if no match found.
     */
    InvocableType resolve(JmsMsg msg);
}
