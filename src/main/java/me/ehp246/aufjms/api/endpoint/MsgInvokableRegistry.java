package me.ehp246.aufjms.api.endpoint;

import java.util.List;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface MsgInvokableRegistry {

    void register(MsgInvokableDefinition invokableDefinition);

    List<MsgInvokableDefinition> getRegistered();
}
