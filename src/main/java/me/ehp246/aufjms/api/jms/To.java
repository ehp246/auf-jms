package me.ehp246.aufjms.api.jms;

import javax.jms.Destination;

/**
 * A named {@linkplain Destination}.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public sealed interface To permits ToTopic, ToQueue {
    String name();
}