package me.ehp246.aufjms.api.inbound;

import java.util.List;

import me.ehp246.aufjms.api.jms.JmsMsg;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface InvocableTypeRegistry {
    void register(InvocableTypeDefinition definition);

    List<InvocableTypeDefinition> registered();

    /**
     * 
     * @return <code>null</code> if no match found.
     */
    InvocableType resolve(JmsMsg msg);
}
