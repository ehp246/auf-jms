package org.ehp246.aufjms.core.endpoint;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.ehp246.aufjms.core.formsg.EnableForMsg;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 
 * @author Lei Yang
 *
 */
public class ForMsgRegistrar implements ImportBeanDefinitionRegistrar {

	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		final var enablerAttributes = importingClassMetadata
				.getAnnotationAttributes(EnableForMsg.class.getCanonicalName());
		if (enablerAttributes == null) {
			return;
		}

		Stream.of((Object[]) enablerAttributes.get("value")).map(attribute -> ((Map<String, Object>) attribute))
				.forEach(endpoint -> {
					final var beanDefinition = getBeanDefinition(endpoint);

					Set<String> scanThese = null;
					final var base = (Class<?>[]) endpoint.get("scan");
					if (base.length > 0) {
						scanThese = Stream.of(base).map(baseClass -> baseClass.getPackage().getName())
								.collect(Collectors.toSet());
					} else {
						final var baseName = importingClassMetadata.getClassName();
						scanThese = Set.of(baseName.substring(0, baseName.lastIndexOf(".")));
					}

					final var destinationName = endpoint.get("value").toString();
					beanDefinition.setConstructorArgumentValues(getParameters(destinationName, scanThese));

					registry.registerBeanDefinition(destinationName + "@" + importingClassMetadata.getClassName(),
							beanDefinition);
				});
	}

	private GenericBeanDefinition getBeanDefinition(Map<String, Object> annotationAttributes) {
		final var beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(MsgEndpoint.class);
		beanDefinition.setFactoryBeanName(ForMsgEndpointFactory.class.getName());
		beanDefinition.setFactoryMethodName("newMsgEndpoint");

		return beanDefinition;
	}

	private ConstructorArgumentValues getParameters(final Object destination, final Object scans) {
		final var constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue(destination);
		constructorArgumentValues.addGenericArgumentValue(scans);
		return constructorArgumentValues;
	}
}
