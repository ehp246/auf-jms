package me.ehp246.aufjms.core.endpoint;

import java.util.List;

import javax.jms.Message;

import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.jms.JmsMsg;

class DefaultExecutableBinderTestCases {

    static class MethodCase01 {
        public void m01() {

        }

        public JmsMsg m01(final JmsMsg msg) {
            return msg;
        }

        public Object[] m01(final JmsMsg msg, final Message message) {
            return new Object[] { msg, message };
        }

        public Void m02() {
            return null;
        }

        public void m03() {
            throw new IllegalArgumentException();
        }

        public Object[] m01(final List<Integer> integers, final Message message) {
            return new Object[] { integers, message };
        }
    }



    public void m001(final JmsMsg msg, @OfType final String type, final String str) {

    }

    static class CorrelationIdCase01 {
        @OfCorrelationId
        private String field;
        public String setter;
        public String method;

        public String get() {
            return this.field;
        }

        @OfCorrelationId
        public void set(final String id) {
            setter = id;
        }

        public String m01(@OfCorrelationId final String value) {
            return value;
        }
    }

    static class PropertyCase01 {
        public void m004(@OfProperty("") final String value) {

        }
    }
}