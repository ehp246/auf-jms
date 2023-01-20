package me.ehp246.aufjms.core.dispatch;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.core.reflection.EnabledScanner;
import me.ehp246.aufjms.core.util.OneUtil;

public final class EnableByJmsRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(EnableByJmsRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        final var enableMap = metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName());

        registry.registerBeanDefinition(beanName(EnableByJmsConfig.class), getAppConfigBeanDefinition(enableMap));

        LOGGER.atTrace().log("Scanning for {}", ByJms.class::getCanonicalName);

        for (final var found : new EnabledScanner(EnableByJms.class, ByJms.class, metadata).perform()
                .collect(Collectors.toList())) {
            LOGGER.atTrace().log("Registering {}", found::getBeanClassName);

            final Class<?> proxyInterface;
            try {
                proxyInterface = Class.forName(found.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            final var beanName = Optional.ofNullable(proxyInterface.getAnnotation(ByJms.class).name())
                    .filter(OneUtil::hasValue).orElseGet(() -> beanName(proxyInterface));

            registry.registerBeanDefinition(beanName, this.getProxyBeanDefinition(proxyInterface));
        }

        final var enablerAttributes = metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName());

        final var fns = (String[]) enablerAttributes.get("dispatchFns");
        if (fns.length == 0) {
            return;
        }

        for (var i = 0; i < fns.length; i++) {
            registry.registerBeanDefinition("dispatchFn-" + i, getFnBeanDefinition(fns[i]));
        }
    }

    private String beanName(final Class<?> type) {
        final char c[] = type.getSimpleName().toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }

    private BeanDefinition getAppConfigBeanDefinition(final Map<String, Object> map) {
        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(Arrays.asList((Class<?>[]) map.get("scan")));
        args.addGenericArgumentValue(map.get("ttl"));
        args.addGenericArgumentValue(map.get("delay"));
        args.addGenericArgumentValue(Arrays.asList((String[]) map.get("dispatchFns")));

        final var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(EnableByJmsConfig.class);
        beanDefinition.setConstructorArgumentValues(args);

        beanDefinition.setFactoryBeanName(EnableByJmsBeanFactory.class.getName());
        beanDefinition.setFactoryMethodName("enableByJmsConfig");

        return beanDefinition;
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

    private BeanDefinition getProxyBeanDefinition(final Class<?> proxyInterface) {
        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(proxyInterface);

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(proxyInterface);
        proxyBeanDefinition.setConstructorArgumentValues(args);
        proxyBeanDefinition.setFactoryBeanName(ByJmsProxyFactory.class.getName());
        proxyBeanDefinition.setFactoryMethodName("newByJmsProxy");

        return proxyBeanDefinition;
    }

}
