package me.ehp246.aufjms.core.configuration;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.PropertyResolver;

import jakarta.jms.ConnectionFactory;

/**
 * @author Lei Yang
 *
 */
class AufJmsConfigurationTest {

    @Test
    void test_01() {
        final var beanFactory = Mockito.mock(BeanFactory.class);

        final var provider = new AufJmsConfiguration().connectionFactoryProvider(beanFactory);

        provider.get(null);

        Mockito.verify(beanFactory).getBean(ConnectionFactory.class);
    }

    @Test
    void test_02() {
        final var beanFactory = Mockito.mock(BeanFactory.class);

        final var provider = new AufJmsConfiguration().connectionFactoryProvider(beanFactory);

        provider.get("");

        Mockito.verify(beanFactory).getBean(ConnectionFactory.class);
    }

    @Test
    void test_03() {
        final var beanFactory = Mockito.mock(BeanFactory.class);

        final var provider = new AufJmsConfiguration().connectionFactoryProvider(beanFactory);

        final var name = UUID.randomUUID().toString();

        provider.get(name);

        Mockito.verify(beanFactory).getBean(name, ConnectionFactory.class);
    }

    @Test
    void test_04() {
        final var mock = Mockito.mock(PropertyResolver.class);

        final var provider = new AufJmsConfiguration().propertyResolver(mock);

        final var name = UUID.randomUUID().toString();

        provider.resolve(name);

        Mockito.verify(mock).resolveRequiredPlaceholders(name);
    }
}
