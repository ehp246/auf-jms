package me.ehp246.aufjms.core.endpoint;

import java.lang.reflect.Method;
import java.util.List;

import javax.jms.Message;
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

import me.ehp246.aufjms.api.endpoint.Executable;
import me.ehp246.aufjms.api.endpoint.InvocationContext;
import me.ehp246.aufjms.api.jms.JmsMsg;
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
class DefaultExecutableBinderTest {
    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ReflectingType<DefaultExecutableBinderTestCases> case001Type = new ReflectingType<DefaultExecutableBinderTestCases>(
            DefaultExecutableBinderTestCases.class);
    private final DefaultExecutableBinder binder = new DefaultExecutableBinder(new JsonByJackson(objectMapper));

    @Test
    public void method_01() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m01");
            }

            @Override
            public Object getInstance() {
                return case01;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void method_02() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case001 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m01", JmsMsg.class);
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(mq, outcome.getReturned());
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void method_03() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m01", JmsMsg.class, Message.class);
            }

            @Override
            public Object getInstance() {
                return case01;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        final var returned = outcome.getReturned();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(Object[].class, returned.getClass());
        Assertions.assertEquals(mq, ((Object[]) returned)[0]);
        Assertions.assertEquals(null, ((Object[]) returned)[1]);
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void method_05() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m02");
            }

            @Override
            public Object getInstance() {
                return case01;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
    }

    @Test
    public void method_06() throws Exception {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m03");
            }

            @Override
            public Object getInstance() {
                return case01;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        Assertions.assertEquals(false, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
        Assertions.assertEquals(IllegalArgumentException.class, outcome.getThrown().getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void method_08() throws JsonProcessingException {
        final var reflectingType = new ReflectingType<DefaultExecutableBinderTestCases.MethodCase01>(
                DefaultExecutableBinderTestCases.MethodCase01.class);
        final var mq = Mockito.mock(JmsMsg.class);
        final var msg = Mockito.mock(TextMessage.class);

        Mockito.when(mq.msg()).thenReturn(msg);

        Mockito.when(mq.text())
                .thenReturn(objectMapper.writeValueAsString(new Integer[] { 3, 2, 3 }));

        final var case01 = new DefaultExecutableBinderTestCases.MethodCase01();

        final var outcome = binder.bind(new Executable() {

            @Override
            public Method getMethod() {
                return reflectingType.findMethod("m01", List.class, Message.class);
            }

            @Override
            public Object getInstance() {
                return case01;
            }
        }, new InvocationContext() {

            @Override
            public JmsMsg getMsg() {
                return mq;
            }
        }).get();

        Assertions.assertEquals(true, outcome.hasReturned());

        final var returned = (Object[]) outcome.getReturned();

        Assertions.assertEquals(3, ((List<Integer>) (returned[0])).get(0));
        Assertions.assertEquals(2, ((List<Integer>) (returned[0])).get(1));
        Assertions.assertEquals(3, ((List<Integer>) (returned[0])).get(2));

        Assertions.assertEquals(msg, returned[1]);
        Assertions.assertEquals(null, outcome.getThrown());
    }

}
