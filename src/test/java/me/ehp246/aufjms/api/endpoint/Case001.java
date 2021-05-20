package me.ehp246.aufjms.api.endpoint;

import java.time.Instant;
import java.util.List;

import javax.jms.Message;

import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.jms.Received;

class Case001 {
    public void m001() {

    }

    public Received m001(final Received msg) {
        return msg;
    }

    public Object[] m001(final Received msg, final Message message) {
        return new Object[] { msg, message };
    }

    public Object[] m001(final List<Integer> integers, final Instant[] instants, final Message message) {
        final var instant = instants[0];

        return new Object[] { integers, instant, message };
    }

    public void m001(final Received msg, @OfType final String type, final String str) {

    }

    public Void m002() {
        return null;
    }

    public void m003() {
        throw new RuntimeException();
    }
}