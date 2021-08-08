package me.ehp246.aufjms.api.jms;

import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJsonMsgBody {
    String to(List<?> values);
}
