package me.ehp246.aufjms.util;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Consumer;

import org.assertj.core.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufjms.api.jms.Invocation;

/**
 * @author Lei Yang
 *
 */
public class TestUtil {

    @SuppressWarnings("unchecked")
    public static <T> T newProxy(final Class<T> t, final Consumer<Invocation> consumer) {
        return (T) (Proxy.newProxyInstance(TestUtil.class.getClassLoader(), new Class[] { t },
                (proxy, method, args) -> {
                    consumer.accept(new Invocation() {

                        @Override
                        public Object target() {
                            return proxy;
                        }

                        @Override
                        public Method method() {
                            return method;
                        }

                        @Override
                        public List<?> args() {
                            return args == null ? List.of() : Arrays.asList(args);
                        }
                    });
                    return null;
                }));
    }

    @SuppressWarnings("unchecked")
    public static <T> InvocationCaptor<T> newCaptor(final Class<T> t) {
        final var captured = new Invocation[1];
        final var proxy = (T) (Proxy.newProxyInstance(TestUtil.class.getClassLoader(), new Class[] { t },
                (target, method, args) -> {
                    captured[0] = new Invocation() {

                        @Override
                        public Object target() {
                            return target;
                        }

                        @Override
                        public Method method() {
                            return method;
                        }

                        @Override
                        public List<?> args() {
                            return args == null ? List.of() : Arrays.asList(args);
                        }
                    };
                    return null;
                }));

        return new InvocationCaptor<T>() {

            @Override
            public T proxy() {
                return proxy;
            }

            @Override
            public Invocation invocation() {
                return captured[0];
            }
        };
    }

    public interface InvocationCaptor<T> {
        T proxy();

        Invocation invocation();
    }

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
    .registerModule(new ParameterNamesModule());
}
