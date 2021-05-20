package me.ehp246.aufjms.api.endpoint;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;

import javax.jms.Message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.ehp246.aufjms.api.jms.Received;
import me.ehp246.aufjms.core.endpoint.DefaultExecutableBinder;
import me.ehp246.aufjms.core.reflection.ReflectingType;
import me.ehp246.aufjms.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
class DefaultActionInvocationBinderTest {
    private final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ReflectingType<Case001> case001Type = new ReflectingType<Case001>(Case001.class);
    private final DefaultExecutableBinder binder = new DefaultExecutableBinder(new JsonByJackson(objectMapper));

    @Test
    public void binder001() throws Exception {
        final var mq = Mockito.mock(Received.class);
        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m001");
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void binder002() throws Exception {
        final var mq = Mockito.mock(Received.class);
        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m001", Received.class);
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(mq, outcome.getReturned());
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void binder003() throws Exception {
        final var mq = Mockito.mock(Received.class);
        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m001", Received.class, Message.class);
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        final var returned = outcome.getReturned();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(Object[].class, returned.getClass());
        Assertions.assertEquals(mq, ((Object[]) returned)[0]);
        Assertions.assertEquals(null, ((Object[]) returned)[1]);
        Assertions.assertEquals(null, outcome.getThrown());
    }

    @Test
    public void binder005() throws Exception {
        final var mq = Mockito.mock(Received.class);
        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m002");
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        Assertions.assertEquals(true, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
    }

    @Test
    public void binder006() throws Exception {
        final var mq = Mockito.mock(Received.class);
        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m003");
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        Assertions.assertEquals(false, outcome.hasReturned());
        Assertions.assertEquals(null, outcome.getReturned());
        Assertions.assertEquals(RuntimeException.class, outcome.getThrown().getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void binder008() throws JsonProcessingException {
        final var mq = Mockito.mock(Received.class);
        final var msg = Mockito.mock(Message.class);
        Mockito.when(mq.correlationId()).thenReturn("1");
        Mockito.when(mq.message()).thenReturn(msg);

//		Mockito.when(mq.getBodyAsText())
//				.thenReturn(objectMapper
//						.writeValueAsString(new String[] { objectMapper.writeValueAsString(new Integer[] { 3, 2, 3 }),
//								objectMapper.writeValueAsString(new Instant[] { Instant.now(), Instant.now() }) }));

        final Case001 case001 = new Case001();

        final var outcome = binder.bind(new ResolvedExecutable() {

            @Override
            public Method getMethod() {
                return case001Type.findMethod("m001", List.class, Instant[].class, Message.class);
            }

            @Override
            public Object getInstance() {
                return case001;
            }
        }, new InvocationContext() {

            @Override
            public Received getMsg() {
                return mq;
            }
        }).invoke();

        Assertions.assertEquals(true, outcome.hasReturned());
        final var returned = (Object[]) outcome.getReturned();
        Assertions.assertEquals(3, ((List<Integer>) (returned[0])).get(0));
        Assertions.assertEquals(msg, returned[2]);
        Assertions.assertEquals(null, outcome.getThrown());
    }

}
