package me.ehp246.aufjms.core.inbound;

import java.util.List;
import java.util.Map;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import me.ehp246.aufjms.api.annotation.OfCorrelationId;
import me.ehp246.aufjms.api.annotation.OfDeliveryCount;
import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;
import me.ehp246.aufjms.api.annotation.OfProperty;
import me.ehp246.aufjms.api.annotation.OfRedelivered;
import me.ehp246.aufjms.api.annotation.OfThreadContext;
import me.ehp246.aufjms.api.annotation.OfType;
import me.ehp246.aufjms.api.jms.FromJson;
import me.ehp246.aufjms.api.jms.JmsMsg;

interface InvocableBinderTestCases {
    static class ArgCase01 {
        public void m01() {

        }

        public JmsMsg m01(final JmsMsg msg) {
            return msg;
        }

        public Object[] m01(final JmsMsg msg, final Message message) {
            return new Object[] { msg, message };
        }

        public JmsMsg m01(final JmsMsg msg, final FromJson fromJson) {
            return msg;
        }

        public Object[] m01(final List<Integer> integers, final JmsMsg msg) {
            return new Object[] { integers, msg };
        }
    }

    static class MethodCase01 {
        public void m01() {

        }

        public JmsMsg m01(final JmsMsg msg) {
            return msg;
        }

        public Object[] m01(final JmsMsg msg, final Message message) {
            return new Object[] { msg, message };
        }

        public Object[] m01(final JmsMsg msg, final FromJson fromJson) {
            return new Object[] { msg, fromJson };
        }

        public Object[] m01(final List<Integer> integers, final Message message) {
            return new Object[] { integers, message };
        }

        public Void m02() {
            return null;
        }

    }

    static class DeliveryCountCase01 {
        public long m01(@OfDeliveryCount final Long count) {
            return count;
        }

        public int m01(@OfDeliveryCount final Integer count) {
            return count;
        }

        public int m01(@OfDeliveryCount final int count) {
            return count;
        }

        public Integer m02(@OfDeliveryCount final Integer count) {
            return count;
        }
    }

    static class ExceptionCase01 {
        public void m01() {
            throw new IllegalArgumentException();
        }
    }

    static class TypeCase01 {

        public Object[] m01(final JmsMsg msg, @OfType final String type, final String payload) {
            return new Object[] { msg, type, payload };
        }
    }

    static class CorrelationIdCase01 {
        private String field;
        public String setter;
        public String method;

        public String get() {
            return this.field;
        }

        public void set(final String id) {
            setter = id;
        }

        public String[] m01(@OfCorrelationId final String id1, @OfCorrelationId final String id2) {
            return new String[] { id1, id2 };
        }
    }

    static class PropertyCase01 {
        public String m01(@OfProperty("prop1") final String value) {
            return value;
        }

        public String[] m01(@OfProperty("prop1") final String value1, @OfProperty("prop2") final String value2) {
            return new String[] { value1, value2 };
        }

        public Object[] m01(@OfProperty final Map<String, String> value1, @OfProperty("prop1") final String value2) {
            return new Object[] { value1, value2 };
        }

        public Boolean m01(@OfProperty final Boolean prop1) {
            return prop1;
        }

        public PropertyEnum m01(@OfProperty("prop1") final PropertyEnum value) {
            return value;
        }

        enum PropertyEnum {
            Enum1
        }
    }

    static class GroupCase {
        public Group m01(@OfGroupId final String id, @OfGroupSeq final int seq) {
            return new Group(id, seq);
        }

        public Group m01(@OfGroupId final String id, @OfGroupSeq final Integer seq) {
            return new Group(id, seq.intValue());
        }

        record Group(String id, int seq) {
        }
    }

    static class RedeliveredCase {
        public boolean m(@OfRedelivered final boolean redelieverd) {
            return redelieverd;
        }
    }

    static class PerfCase {
        public Object[] m01(final TextMessage textMessage, @OfType final String type, @OfCorrelationId final String id,
                @OfProperty("prop1") final String prop1, final Integer body, final JmsMsg msg,
                final FromJson fromJson) {
            return new Object[] { type, id, prop1, body };
        }

    }

    static class ThreadContextCase {
        public void get() {
        }

        public void get(@OfThreadContext("name") @OfProperty final String firstName,
                @OfThreadContext("name") @OfProperty final String lastName) {
        }

        public void get(@OfThreadContext final String name, @OfThreadContext("SSN") @OfProperty final int id) {
        }

        public void get(@OfThreadContext final String name, @OfThreadContext("SSN") @OfProperty final Integer id) {
        }

        public void getOnBody(@OfThreadContext final Name name) {
        }

        public void getInBody(final Name name) {
        }

        public void getInBody(final Name name, @OfProperty @OfThreadContext final String firstName) {
        }

        public void getInBodyDupped(final DupName name) {
        }

        record Name(@OfThreadContext String firstName, @OfThreadContext String lastName) {
            @OfThreadContext
            String fullName() {
                return firstName + lastName;
            }
        }

        record DupName(@OfThreadContext("name") String firstName, @OfThreadContext("name") String lastName) {
            @OfThreadContext("name")
            String fullName() {
                return firstName + lastName;
            }
        }
    }
}