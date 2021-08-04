package me.ehp246.aufjms.api;

import java.util.List;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ToJson {
    String apply(final List<?> bodyValue);
}
