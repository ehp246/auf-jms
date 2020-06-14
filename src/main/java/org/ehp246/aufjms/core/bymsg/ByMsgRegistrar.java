package org.ehp246.aufjms.core.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.api.jms.ReplyToNameSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ByMsgRegistrar implements ImportBeanDefinitionRegistrar {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByMsgRegistrar.class);

	@Override
	public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
		final var replyTo = metadata.getAnnotationAttributes(EnableByMsg.class.getCanonicalName()).get("replyTo")
				.toString();

		registry.registerBeanDefinition(ReplyToNameSupplier.class.getCanonicalName(),
				getReplyToSupplier(replyTo.isBlank() ? metadata.getClassName() + ".reply" : replyTo));

		LOGGER.debug("Scanning for {}", ByMsg.class.getCanonicalName());

		new ByMsgScanner(EnableByMsg.class, ByMsg.class, metadata).perform().forEach(beanDefinition -> {
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

	private BeanDefinition getProxyBeanDefinition(BeanDefinition beanDefinition) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(beanDefinition.getBeanClassName());
		} catch (ClassNotFoundException ignored) {
			// Class scanning started this. Should not happen.
			throw new RuntimeException("Class scanning started this. Should not happen.");
		}

		LOGGER.trace("Defining {}", beanDefinition.getBeanClassName());

		final var proxyBeanDefinition = new GenericBeanDefinition();
		proxyBeanDefinition.setBeanClass(clazz);

		final var args = new ConstructorArgumentValues();
		args.addGenericArgumentValue(clazz);

		proxyBeanDefinition.setConstructorArgumentValues(args);

		proxyBeanDefinition.setFactoryBeanName(ByMsgFactory.class.getName());

		proxyBeanDefinition.setFactoryMethodName("newInstance");

		return proxyBeanDefinition;
	}

}
