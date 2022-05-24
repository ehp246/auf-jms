package me.ehp246.aufjms.core.dispatch;

import java.util.Arrays;
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
import me.ehp246.aufjms.api.dispatch.EnableByJmsConfig;
import me.ehp246.aufjms.api.dispatch.JmsDispatchFn;
import me.ehp246.aufjms.core.reflection.EnabledScanner;

public final class EnableByJmsRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(EnableByJmsRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        final var enableMap = metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName());

        LOGGER.atTrace().log("Registering {} as '{}'", EnableByJmsConfig.class.getCanonicalName(),
                EnableByJmsConfig.class.getSimpleName());

        registry.registerBeanDefinition(EnableByJmsConfig.class.getSimpleName(), getAppConfigBeanDefinition(enableMap));

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
                    this.getProxyBeanDefinition(proxyInterface));
        });

        final var enablerAttributes = metadata.getAnnotationAttributes(EnableByJms.class.getCanonicalName());

        final var fns = (String[]) enablerAttributes.get("dispatchFns");
        if (fns.length == 0) {
            return;
        }

        for (var i = 0; i < fns.length; i++) {
            final var fnBeanName = JmsDispatchFn.class.getSimpleName() + "-" + i;

            LOGGER.atTrace().log("Defining '{}' on '{}'", fnBeanName, fns[i]);

            registry.registerBeanDefinition(fnBeanName, getFnBeanDefinition(fns[i]));
        }
    }

    private BeanDefinition getAppConfigBeanDefinition(final Map<String, Object> map) {
        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(Arrays.asList((Class<?>[]) map.get("scan")));
        args.addGenericArgumentValue(map.get("ttl"));
        args.addGenericArgumentValue(map.get("delay"));
        args.addGenericArgumentValue(Arrays.asList((String[]) map.get("dispatchFns")));

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(EnableByJmsConfig.class);
        proxyBeanDefinition.setConstructorArgumentValues(args);

        proxyBeanDefinition.setFactoryBeanName(EnableByJmsBeanFactory.class.getName());
        proxyBeanDefinition.setFactoryMethodName("enableByJmsConfig");

        return proxyBeanDefinition;
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
