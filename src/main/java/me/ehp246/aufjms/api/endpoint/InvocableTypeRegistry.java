package me.ehp246.aufjms.api.endpoint;

import java.util.List;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocableTypeRegistry {
    void register(InvocableTypeDefinition definition);

    List<InvocableTypeDefinition> getRegistered();

    InvocableType resolve(JmsMsg msg);
}
