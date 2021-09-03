package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.Map;

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

    static class TypeCase01 {

        public Object[] m01(final JmsMsg msg, @OfType final String type, final String payload) {
            return new Object[] { msg, type, payload };
        }
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

        public String[] m01(@OfCorrelationId final String id1, @OfCorrelationId final String id2) {
            return new String[] { id1, id2 };
        }
    }

    static class PropertyCase01 {
        public String[] m01(@OfProperty("prop1") final String value1, @OfProperty("prop2") final String value2) {
            return new String[] { value1, value2 };
        }

        public Object[] m01(@OfProperty final Map<String, String> value1, @OfProperty("prop1") final String value2) {
            return new Object[] { value1, value2 };
        }

        public Boolean m01(@OfProperty final Boolean value) {
            return value;
        }
    }
}