package me.ehp246.aufjms.core.endpoint;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.ehp246.aufjms.api.endpoint.Invoked;
import me.ehp246.aufjms.api.endpoint.Invoked.Completed;
import me.ehp246.aufjms.api.endpoint.Invoked.Failed;
import me.ehp246.aufjms.api.endpoint.MsgContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.api.jms.JmsNames;
import me.ehp246.aufjms.api.spi.FromJson;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinderTestCases.ArgCase01;
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.core.util.OneUtil;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;
import me.ehp246.aufjms.util.MockJmsMsg;
import me.ehp246.aufjms.util.MockTextMessage;

/**
 * @author Lei Yang
 *
 */
class DefaultInvocableBinderTest {
    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final JsonByJackson jackson = new JsonByJackson(objectMapper);
    private final FromJson fromJson = jackson;
    private final ToJson toJson = jackson;
    private final DefaultInvocableBinder binder = new DefaultInvocableBinder(fromJson);
    private final TextMessage message = new MockTextMessage();
    private final JmsMsg msg = Mockito.mock(JmsMsg.class);
    private final MsgContext ctx = new MsgContext() {

        @Override
        public Session session() {
            return null;
        }

        @Override
        public JmsMsg msg() {
            return msg;
        }
    };
    private final ArgCase01 arg01 = new ArgCase01();

    @Test
    void bound_01() {
        final var method = new ReflectingType<>(DefaultExecutableBinderTestCases.ArgCase01.class).findMethod("m01");
        final var invocable = new InvocableRecord(arg01, method);
        final var bound = binder.bind(invocable, ctx);

        Assertions.assertEquals(arg01, bound.invocable().instance());
        Assertions.assertEquals(method, bound.invocable().method());
        Assertions.assertEquals(0, bound.arguments().size());
        Assertions.assertEquals(invocable.invocationModel(), bound.invocable().invocationModel());
    }

    @Test
    void arg_01() {
        final var method = new ReflectingType<>(DefaultExecutableBinderTestCases.ArgCase01.class).findMethod("m01",
                JmsMsg.class);
        final var bound = binder.bind(new InvocableRecord(arg01, method), ctx);

        Assertions.assertEquals(1, bound.arguments().size());
        Assertions.assertEquals(msg, bound.arguments().get(0));
    }

    @Test
    void arg_02() {
        final var method = new ReflectingType<>(DefaultExecutableBinderTestCases.ArgCase01.class).findMethod("m01",
                JmsMsg.class, Message.class);
        Mockito.when(msg.message()).thenReturn(message);

        final var bound = binder.bind(new InvocableRecord(arg01, method), ctx);

        Assertions.assertEquals(2, bound.arguments().size());
        Assertions.assertEquals(msg, bound.arguments().get(0));
        Assertions.assertEquals(message, (bound.arguments().get(1)));
    }

    @Test
    void arg_03() {
        final var method = new ReflectingType<>(DefaultExecutableBinderTestCases.ArgCase01.class).findMethod("m01",
                MsgContext.class, FromJson.class);

        final var bound = binder.bind(new InvocableRecord(arg01, method), ctx);

        Assertions.assertEquals(2, bound.arguments().size());
        Assertions.assertEquals(ctx, bound.arguments().get(0));
        Assertions.assertEquals(fromJson, bound.arguments().get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    void arg_04() {
        final var method = new ReflectingType<>(DefaultExecutableBinderTestCases.ArgCase01.class).findMethod("m01",
                List.class, JmsMsg.class);

        Mockito.when(msg.text()).thenReturn(toJson.from(List.of(1, 2, 3)));

        final var bound = binder.bind(new InvocableRecord(arg01, method), ctx);

        Assertions.assertEquals(2, bound.arguments().size());

        final var firstArg = (List<Integer>) bound.arguments().get(0);

        Assertions.assertEquals(3, firstArg.size());
        Assertions.assertEquals(1, firstArg.get(0));
        Assertions.assertEquals(2, firstArg.get(1));
        Assertions.assertEquals(3, firstArg.get(2));

        Assertions.assertEquals(msg, bound.arguments().get(1));
    }

    @Test
    void method_01() throws Exception {
        final var mq = Mockito.mock(JmsMsg.class);
        final var outcome = Invoked.invoke(binder.bind(
                new InvocableRecord(new DefaultExecutableBinderTestCases.MethodCase01(),
                        new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                                DefaultExecutableBinderTestCases.MethodCase01.class).findMethod("m01")),
                new MsgContext() {

                    @Override
                    public Session session() {
                        return null;
                    }

                    @Override
                    public JmsMsg msg() {
                        return mq;
                    }
                }));

        Assertions.assertEquals(true, outcome instanceof Completed);
        Assertions.assertEquals(null, ((Completed) outcome).returned());
    }

    @Test
    void method_02() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.MethodCase01(),
                        reflectingType.findMethod("m01", JmsMsg.class)), new MsgContext() {

                            @Override
                            public Session session() {
                                return null;
                            }

                            @Override
                            public JmsMsg msg() {
                                return mq;
                            }
                        }));

        Assertions.assertEquals(true, outcome instanceof Completed);
        Assertions.assertEquals(mq, ((Completed) outcome).returned());
    }

    @Test
    public void method_03() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = Invoked.invoke(
                binder.bind(new InvocableRecord(case01, reflectingType.findMethod("m01", JmsMsg.class, Message.class)),
                        new MsgContext() {

                            @Override
                            public Session session() {
                                return null;
                            }

                            @Override
                            public JmsMsg msg() {
                                return mq;
                            }
                        }));

        final var returned = ((Completed) outcome).returned();

        Assertions.assertEquals(Object[].class, returned.getClass());
        Assertions.assertEquals(mq, ((Object[]) returned)[0]);
        Assertions.assertEquals(null, ((Object[]) returned)[1]);
    }

    @Test
    public void method_04() throws Exception {
        final var mq = Mockito.mock(MsgContext.class);
        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.MethodCase01(),
                        ReflectingType.reflect(DefaultExecutableBinderTestCases.MethodCase01.class).findMethod("m01",
                                MsgContext.class)),
                        mq));

        Assertions.assertEquals(mq, ((Completed) outcome).returned());
    }

    @Test
    public void method_05() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(case01, reflectingType.findMethod("m02")), new MsgContext() {

                    @Override
                    public Session session() {
                        return null;
                    }

                    @Override
                    public JmsMsg msg() {
                        return mq;
                    }
                }));

        Assertions.assertEquals(null, ((Completed) outcome).returned());
    }

    @SuppressWarnings("unchecked")
    @Test
    void method_08() throws JsonProcessingException {
        final var reflectingType = new ReflectingType<>(DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var msg = Mockito.mock(TextMessage.class);

        Mockito.when(mq.message()).thenReturn(msg);

        Mockito.when(mq.text()).thenReturn(objectMapper.writeValueAsString(new Integer[] { 3, 2, 3 }));

        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = Invoked.invoke(
                binder.bind(new InvocableRecord(case01, reflectingType.findMethod("m01", List.class, Message.class)),
                        new MsgContext() {

                            @Override
                            public Session session() {
                                return null;
                            }

                            @Override
                            public JmsMsg msg() {
                                return mq;
                            }
                        }));

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(3, ((List<Integer>) (returned[0])).get(0));
        Assertions.assertEquals(2, ((List<Integer>) (returned[0])).get(1));
        Assertions.assertEquals(3, ((List<Integer>) (returned[0])).get(2));

        Assertions.assertEquals(msg, returned[1]);
    }

    @Test
    public void method_09() throws Exception {
        final var mq = Mockito.mock(MsgContext.class);
        final var session = Mockito.mock(Session.class);
        Mockito.when(mq.session()).then(i -> session);

        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.MethodCase01(),
                        ReflectingType.reflect(DefaultExecutableBinderTestCases.MethodCase01.class).findMethod("m01",
                                Session.class, FromJson.class)),
                        mq));

        final var returned = (Object[]) ((Completed) outcome).returned();
        Assertions.assertEquals(session, returned[0]);
        Assertions.assertEquals(fromJson, returned[1]);
    }

    @Test
    public void ex_01() throws Exception {
        final var mq = Mockito.mock(MsgContext.class);

        final var outcome = Invoked
                .invoke(binder.bind(
                        new InvocableRecord(new DefaultExecutableBinderTestCases.ExceptionCase01(), ReflectingType
                                .reflect(DefaultExecutableBinderTestCases.ExceptionCase01.class).findMethod("m01")),
                        mq));

        Assertions.assertEquals(IllegalArgumentException.class, ((Failed) outcome).thrown().getClass());
    }

    @Test
    void correlId_01() {
        final var mq = new MockJmsMsg();
        final var case01 = new DefaultExecutableBinderTestCases.CorrelationIdCase01();

        final var outcome = Invoked.invoke(binder.bind(new InvocableRecord(case01,
                new ReflectingType<>(DefaultExecutableBinderTestCases.CorrelationIdCase01.class).findMethod("m01",
                        String.class, String.class)),
                mq));

        final var returned = (String[]) ((Completed) outcome).returned();
        Assertions.assertEquals(mq.correlationId(), returned[0]);
        Assertions.assertEquals(mq.correlationId(), returned[1]);
    }

    @Test
    void type_01() {
        final var type = UUID.randomUUID().toString();
        final var mq = new MockJmsMsg(type) {

            @Override
            public String text() {
                return OneUtil.orThrow(() -> objectMapper.writeValueAsString(type));
            }

        };
        final var case01 = new DefaultExecutableBinderTestCases.TypeCase01();

        final var outcome = Invoked.invoke(binder.bind(
                new InvocableRecord(case01, new ReflectingType<>(DefaultExecutableBinderTestCases.TypeCase01.class)
                        .findMethod("m01", JmsMsg.class, String.class, String.class)),
                mq));

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0] == mq);
        Assertions.assertEquals(true, returned[1].equals(type));
        Assertions.assertEquals(true, returned[2].equals(type));
    }

    @Test
    void property_01() {
        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.PropertyCase01(),
                ReflectingType.reflect(DefaultExecutableBinderTestCases.PropertyCase01.class).findMethod("m01",
                        String.class)),
                new MockJmsMsg());

        Assertions.assertEquals(true, ((Completed) Invoked.invoke(bound)).returned() == null);
        Assertions.assertEquals(1, bound.arguments().size());
        Assertions.assertEquals(null, bound.arguments().get(0));
    }

    @Test
    void property_02() {
        final var map = Map.of("prop1", UUID.randomUUID().toString());
        final var mq = new MockJmsMsg() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                return (T) map.get(name);
            }

        };
        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.PropertyCase01(),
                new ReflectingType<>(DefaultExecutableBinderTestCases.PropertyCase01.class).findMethod("m01",
                        String.class, String.class)),
                mq);

        final var outcome = Invoked.invoke(bound);

        final var returned = (String[]) ((Completed) outcome).returned();

        Assertions.assertEquals(map.get("prop1"), returned[0]);
        Assertions.assertEquals(null, returned[1]);

        Assertions.assertEquals(2, bound.arguments().size());
        Assertions.assertEquals(map.get("prop1"), bound.arguments().get(0));
        Assertions.assertEquals(null, bound.arguments().get(1));
    }

    @Test
    void property_03() {
        final var map = Map.of("prop1", UUID.randomUUID().toString(), "prop2", UUID.randomUUID().toString());
        final var mq = new MockJmsMsg() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                return (T) map.get(name);
            }

        };

        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.PropertyCase01(),
                new ReflectingType<>(DefaultExecutableBinderTestCases.PropertyCase01.class).findMethod("m01",
                        String.class, String.class)),
                mq);
        final var outcome = Invoked.invoke(bound);

        final var returned = (String[]) ((Completed) outcome).returned();

        Assertions.assertEquals(map.get("prop1"), returned[0]);
        Assertions.assertEquals(map.get("prop2"), returned[1]);

        Assertions.assertEquals(2, bound.arguments().size());

        Assertions.assertEquals(map.get("prop1"), bound.arguments().get(0));
        Assertions.assertEquals(map.get("prop2"), bound.arguments().get(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    void property_04() throws JMSException {
        final var map = Map.of("prop1", UUID.randomUUID().toString(), "prop2", UUID.randomUUID().toString());
        final var msg = Mockito.mock(TextMessage.class);
        Mockito.when(msg.getObjectProperty(Mockito.anyString())).then(i -> map.get(i.getArgument(0).toString()));

        final var mq = new MockJmsMsg() {

            @Override
            public <T> T property(String name, Class<T> type) {
                return (T) map.get(name);
            }

            @Override
            public Set<String> propertyNames() {
                return map.keySet();
            }

            @Override
            public TextMessage message() {
                return msg;
            }

        };

        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.PropertyCase01(),
                new ReflectingType<>(DefaultExecutableBinderTestCases.PropertyCase01.class).findMethod("m01", Map.class,
                        String.class)),
                new MsgContext() {

                    @Override
                    public Session session() {
                        return null;
                    }

                    @Override
                    public JmsMsg msg() {
                        return mq;
                    }
                });
        final var outcome = Invoked.invoke(bound);

        final var returned = (Object[]) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned[0] instanceof Map);
        Assertions.assertEquals(map.get("prop2"), ((Map<String, Object>) returned[0]).get("prop2"));
        Assertions.assertEquals(map.get("prop1"), returned[1]);

        Assertions.assertEquals(2, bound.arguments().size());
        Assertions.assertEquals(map.get("prop2"), ((Map<String, Object>) bound.arguments().get(0)).get("prop2"));
        Assertions.assertEquals(map.get("prop1"), bound.arguments().get(1));
    }

    @Test
    void property_05() {
        final var mq = new MockJmsMsg() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                return (T) Boolean.TRUE;
            }

        };
        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.PropertyCase01(),
                new ReflectingType<>(DefaultExecutableBinderTestCases.PropertyCase01.class).findMethod("m01",
                        Boolean.class)),
                mq);
        final var outcome = Invoked.invoke(bound);

        final var returned = (Boolean) ((Completed) outcome).returned();

        Assertions.assertEquals(true, returned);

        Assertions.assertEquals(1, bound.arguments().size());
        Assertions.assertEquals(true, bound.arguments().get(0));
    }

    @Test
    void deliveryCount_01() {
        final var mq = new MockJmsMsg() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                if (name.equals(JmsNames.DELIVERY_COUNT)) {
                    return (T) Long.valueOf(123);
                }
                return null;
            }

        };
        final var bound = binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.DeliveryCountCase01(),
                new ReflectingType<>(DefaultExecutableBinderTestCases.DeliveryCountCase01.class).findMethod("m01",
                        Long.class)),
                mq);
        final var outcome = Invoked.invoke(bound);

        final var returned = (long) ((Completed) outcome).returned();

        Assertions.assertEquals(123, returned);
        Assertions.assertEquals(123L, (Long) (bound.arguments().get(0)));
    }

    @Test
    void deliveryCount_02() {
        final var mq = new MockJmsMsg() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                if (name.equals(JmsNames.DELIVERY_COUNT)) {
                    return (T) Integer.valueOf(123);
                }
                return null;
            }

        };
        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.DeliveryCountCase01(),
                        new ReflectingType<>(DefaultExecutableBinderTestCases.DeliveryCountCase01.class)
                                .findMethod("m01", Integer.class)),
                        mq));

        final var returned = (int) ((Completed) outcome).returned();

        Assertions.assertEquals(123, returned);
    }

    @Test
    void deliveryCount_03() {
        final var mq = new MockJmsMsg() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T property(String name, Class<T> type) {
                if (name.equals(JmsNames.DELIVERY_COUNT)) {
                    return (T) Integer.valueOf(123);
                }
                return null;
            }

        };
        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.DeliveryCountCase01(),
                        new ReflectingType<>(DefaultExecutableBinderTestCases.DeliveryCountCase01.class)
                                .findMethod("m01", int.class)),
                        mq));

        final var returned = (int) ((Completed) outcome).returned();

        Assertions.assertEquals(123, returned);
    }

    @Test
    void deliveryCount_04() {
        final var mq = new MockJmsMsg();
        final var outcome = Invoked
                .invoke(binder.bind(new InvocableRecord(new DefaultExecutableBinderTestCases.DeliveryCountCase01(),
                        new ReflectingType<>(DefaultExecutableBinderTestCases.DeliveryCountCase01.class)
                                .findMethod("m02", Integer.class)),
                        mq));

        final var returned = (Integer) ((Completed) outcome).returned();

        Assertions.assertEquals(null, returned);
    }
}
