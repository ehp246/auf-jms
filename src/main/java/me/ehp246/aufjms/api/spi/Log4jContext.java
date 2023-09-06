package me.ehp246.aufjms.api.spi;

import org.apache.logging.log4j.ThreadContext;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class Log4jContext {
    private Log4jContext() {
    }

    private enum MsgContextName {
        AufJmsMsgFrom, AufJmsMsgCorrelationId, AufJmsMsgType, AufJmsLog4jThreadContext;
    }

    private enum DispatchContextName {
        AufJmsDispatchTo, AufJmsDispatchCorrelationId, AufJmsDispatchType;
    }

    public static void set(final JmsMsg msg) {
        if (msg == null) {
            return;
        }

        ThreadContext.put(MsgContextName.AufJmsMsgFrom.name(), OneUtil.toString(msg.destination()));
        ThreadContext.put(MsgContextName.AufJmsMsgType.name(), msg.type());
        ThreadContext.put(MsgContextName.AufJmsMsgCorrelationId.name(), msg.correlationId());

        msg.propertyNames().stream().filter(name -> name.startsWith(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX))
                .forEach(name -> ThreadContext.put(name.replaceFirst(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX, ""),
                        msg.property(name, String.class)));
    }

    public static void set(final JmsDispatch dispatch) {
        if (dispatch == null) {
            return;
        }
        ThreadContext.put(DispatchContextName.AufJmsDispatchTo.name(), OneUtil.toString(dispatch.to()));
        ThreadContext.put(DispatchContextName.AufJmsDispatchType.name(), dispatch.type());
        ThreadContext.put(DispatchContextName.AufJmsDispatchCorrelationId.name(), dispatch.correlationId());
    }

    public static void clearMsg() {
        for (final var value : MsgContextName.values()) {
            ThreadContext.remove(value.name());
        }
    }

    public static void clear(final JmsMsg msg) {
        for (final var value : MsgContextName.values()) {
            ThreadContext.remove(value.name());
        }

        msg.propertyNames().stream().filter(name -> name.startsWith(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX))
                .forEach(name -> ThreadContext.remove(name.replaceFirst(AufJmsConstants.LOG4J_THREAD_CONTEXT_HEADER_PREFIX, "")));
    }

    public static void clear(final JmsDispatch dispatch) {
        for (final var value : DispatchContextName.values()) {
            ThreadContext.remove(value.name());
        }
    }
}
