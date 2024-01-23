package me.ehp246.aufjms.api.spi;

import org.slf4j.MDC;

import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.configuration.AufJmsConstants;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public final class MsgMDC {
    private MsgMDC() {
    }

    private enum MsgContextName {
        AufJmsMsgFrom, AufJmsMsgCorrelationId, AufJmsMsgType, AufJmsMsgMDC;
    }

    private enum DispatchContextName {
        AufJmsDispatchTo, AufJmsDispatchCorrelationId, AufJmsDispatchType;
    }

    public static AutoCloseable set(final JmsMsg msg) {
        if (msg == null) {
            return () -> {
            };
        }
        final AutoCloseable closeable = () -> MsgMDC.clear(msg);

        MDC.put(MsgContextName.AufJmsMsgFrom.name(), OneUtil.toString(msg.destination()));
        MDC.put(MsgContextName.AufJmsMsgType.name(), msg.type());
        MDC.put(MsgContextName.AufJmsMsgCorrelationId.name(), msg.correlationId());

        final var propertyNames = msg.propertyNames();
        if (propertyNames == null) {
            return closeable;
        }

        propertyNames.stream()
                .filter(name -> name.startsWith(AufJmsConstants.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.put(
                        name.replaceFirst(AufJmsConstants.MSG_MDC_HEADER_PREFIX, ""),
                        msg.property(name, String.class)));

        return closeable;
    }

    public static AutoCloseable set(final JmsDispatch dispatch) {
        if (dispatch == null) {
            return () -> {
            };
        }

        final AutoCloseable closeable = () -> MsgMDC.clear(dispatch);

        MDC.put(DispatchContextName.AufJmsDispatchTo.name(), OneUtil.toString(dispatch.to()));
        MDC.put(DispatchContextName.AufJmsDispatchType.name(), dispatch.type());
        MDC.put(DispatchContextName.AufJmsDispatchCorrelationId.name(), dispatch.correlationId());

        final var properties = dispatch.properties();
        if (properties == null) {
            return closeable;
        }

        properties.keySet().stream()
                .filter(name -> name.startsWith(AufJmsConstants.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.put(
                        name.replaceFirst(AufJmsConstants.MSG_MDC_HEADER_PREFIX, ""),
                        properties.get(name).toString()));

        return closeable;
    }

    public static void clear(final JmsMsg msg) {
        if (msg == null) {
            return;
        }

        for (final var value : MsgContextName.values()) {
            MDC.remove(value.name());
        }

        final var propertyNames = msg.propertyNames();
        if (propertyNames == null) {
            return;
        }
        propertyNames.stream()
                .filter(name -> name.startsWith(AufJmsConstants.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.remove(
                        name.replaceFirst(AufJmsConstants.MSG_MDC_HEADER_PREFIX, "")));
    }

    public static void clear(final JmsDispatch dispatch) {
        if (dispatch == null) {
            return;
        }
        for (final var value : DispatchContextName.values()) {
            MDC.remove(value.name());
        }

        final var properties = dispatch.properties();
        if (properties == null) {
            return;
        }
        properties.keySet().stream()
                .filter(name -> name.startsWith(AufJmsConstants.MSG_MDC_HEADER_PREFIX))
                .forEach(name -> MDC.remove(
                        name.replaceFirst(AufJmsConstants.MSG_MDC_HEADER_PREFIX, "")));
    }
}
