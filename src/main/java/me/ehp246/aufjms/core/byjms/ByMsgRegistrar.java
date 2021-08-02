package me.ehp246.aufjms.core.byjms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.EnableByMsg;
import me.ehp246.aufjms.api.jms.ReplyToNameSupplier;

public class ByMsgRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(ByMsgRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        final var replyTo = metadata.getAnnotationAttributes(EnableByMsg.class.getCanonicalName()).get("replyTo")
                .toString();

        LOGGER.debug("Scanning for {}", ByJms.class.getCanonicalName());

        new ByMsgScanner(EnableByMsg.class, ByJms.class, metadata).perform().forEach(beanDefinition -> {
            registry.registerBeanDefinition(beanDefinition.getBeanClassName(),
                    this.getProxyBeanDefinition(beanDefinition));
        });
    }

    private BeanDefinition getReplyToSupplier(final String name) {
        final var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ReplyToNameSupplier.class);

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(name);

        beanDefinition.setConstructorArgumentValues(args);
        beanDefinition.setFactoryBeanName(ReplyToNameSupplierFactory.class.getName());
        beanDefinition.setFactoryMethodName("newInstance");

        return beanDefinition;
    }

    private BeanDefinition getProxyBeanDefinition(final BeanDefinition beanDefinition) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(beanDefinition.getBeanClassName());
        } catch (final ClassNotFoundException ignored) {
            // Class scanning started this. Should not happen.
            throw new RuntimeException("Class scanning started this. Should not happen.");
        }

        LOGGER.trace("Defining {}", beanDefinition.getBeanClassName());

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(clazz);

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(clazz);

        proxyBeanDefinition.setConstructorArgumentValues(args);

        proxyBeanDefinition.setFactoryBeanName(ByJmsFactory.class.getName());

        proxyBeanDefinition.setFactoryMethodName("newInstance");

        return proxyBeanDefinition;
    }

}
