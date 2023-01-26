package me.ehp246.aufjms.api.jms;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Internal abstraction for {@linkplain ObjectMapper} operations.
 *
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface FromJson {
    /**
     *
     * @param json
     * @param descriptor Could be <code>null</code>. In which case, it is up to
     *                   {@linkplain ObjectMapper}.
     * @return
     */
    <T> T apply(final String json, final BodyOf<T> descriptor);
}