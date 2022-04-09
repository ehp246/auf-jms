package me.ehp246.aufjms.core.util;

import java.util.List;

import me.ehp246.aufjms.api.dispatch.JmsDispatch;
import me.ehp246.aufjms.api.jms.To;

/**
 * @author Lei Yang
 *
 */
public record MockJmsDispatch(To to, String type, String correlationId, List<?> bodyValues, To replyTo)
        implements JmsDispatch {
    public MockJmsDispatch(To to) {
        this(to, null, null, null, null);
    }

    public MockJmsDispatch(To to, String type, String id, Object body) {
        this(to, type, id, List.of(body), null);
    }

    public MockJmsDispatch(To to, String type) {
        this(to, type, null, null, null);
    }
}
