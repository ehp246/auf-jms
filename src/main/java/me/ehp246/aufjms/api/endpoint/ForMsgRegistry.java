package me.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 *
 */
public interface ForMsgRegistry {
    void register(InvokingDefinition invokingDefinition);

    List<InvokingDefinition> getRegistered();
}
