package me.ehp246.aufjms.core.dispatch;

import java.time.Duration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.dispatch.InvocationDispatchConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.api.jms.At;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.core.reflection.EnabledScanner;

public final class ByJmsRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(ByJmsRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.atTrace().log("Scanning for {}", ByJms.class.getCanonicalName());

        new EnabledScanner(EnableByJms.class, ByJms.class, metadata).perform().forEach(beanDefinition -> {
            final Class<?> proxyInterface;
            try {
                proxyInterface = Class.forName(beanDefinition.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            LOGGER.atTrace().log("Defining {}", beanDefinition.getBeanClassName());

            final var name = proxyInterface.getAnnotation(ByJms.class).name();

            registry.registerBeanDefinition(name.equals("") ? proxyInterface.getSimpleName() : name,
                    this.getProxyBeanDefinition(metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName()),
                            proxyInterface));
        });

        final var enablerAttributes = metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName());

        final var fns = (String[]) enablerAttributes.get("dispatchFns");
        if (fns.length == 0) {
            return;
        }

        LOGGER.atTrace().log("Defining {} on {}", JmsDispatchFn.class.getSimpleName(), fns);
        for (var i = 0; i < fns.length; i++) {
            registry.registerBeanDefinition(JmsDispatchFn.class.getSimpleName() + "-" + i, getFnBeanDefinition(fns[i]));
        }
    }

    private BeanDefinition getFnBeanDefinition(final String name) {
        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(name);

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(JmsDispatchFn.class);
        proxyBeanDefinition.setConstructorArgumentValues(args);
        proxyBeanDefinition.setFactoryBeanName(DefaultDispatchFnProvider.class.getName());
        proxyBeanDefinition.setFactoryMethodName("get");

        return proxyBeanDefinition;
    }

    private BeanDefinition getProxyBeanDefinition(Map<String, Object> map, final Class<?> proxyInterface) {
        final var byJms = proxyInterface.getAnnotation(ByJms.class);
        final var destination = byJms.value().type() == DestinationType.QUEUE ? At.toQueue(byJms.value().value())
                : At.toTopic(byJms.value().value());
        final var replyTo = byJms.replyTo().type() == DestinationType.QUEUE ? At.toQueue(byJms.replyTo().value())
                : At.toTopic(byJms.replyTo().value());

        final var ttl = byJms.ttl().equals("")
                ? (map.get("ttl").toString().equals("") ? Duration.ZERO.toString() : map.get("ttl").toString())
                : byJms.ttl();

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(proxyInterface);
        args.addGenericArgumentValue(new InvocationDispatchConfig() {
            @Override
            public String ttl() {
                return ttl;
            }

            @Override
            public At to() {
                return destination;
            }

            @Override
            public At replyTo() {
                return replyTo;
            }
        });
        args.addGenericArgumentValue(byJms.connectionFactory());

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(proxyInterface);
        proxyBeanDefinition.setConstructorArgumentValues(args);
        proxyBeanDefinition.setFactoryBeanName(ByJmsFactory.class.getName());
        proxyBeanDefinition.setFactoryMethodName("newInstance");

        return proxyBeanDefinition;
    }

}
