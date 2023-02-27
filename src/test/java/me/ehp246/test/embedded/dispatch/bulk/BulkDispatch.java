package me.ehp246.test.embedded.dispatch.bulk;

import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.AtQueue;
import me.ehp246.aufjms.api.jms.JmsDispatch;

/**
 * @author Lei Yang
 *
 */
class BulkDispatch implements JmsDispatch {
    private final AtQueue at = "inbox"::toString;

    private String body;

    @Override
    public At to() {
        return at;
    }

    @Override
    public String type() {
        return "bulkMsg";
    }

    @Override
    public String body() {
        return this.body;
    }

    public BulkDispatch setBody(final String body) {
        this.body = body;
        return this;
    }
}
