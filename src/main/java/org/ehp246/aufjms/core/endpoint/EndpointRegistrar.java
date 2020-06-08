package org.ehp246.aufjms.core.endpoint;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.ehp246.aufjms.annotation.EnableForMsg;
import org.ehp246.aufjms.api.endpoint.MsgEndpoint;
import org.springframework.beans.factory.config.BeanDefinition;
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
public class EndpointRegistrar implements ImportBeanDefinitionRegistrar {

	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Map<String, Object> enablerAttributes = importingClassMetadata
				.getAnnotationAttributes(EnableForMsg.class.getCanonicalName());
		if (enablerAttributes == null) {
			return;
		}

		Stream.of((Object[]) enablerAttributes.get("value")).map(attribute -> ((Map<String, Object>) attribute))
				.forEach(endpoint -> registry.registerBeanDefinition(UUID.randomUUID().toString(),
						getBeanDefination(endpoint)));
	}

	private BeanDefinition getBeanDefination(Map<String, Object> annotationAttributes) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(MsgEndpoint.class);
		beanDefinition.setFactoryBeanName(EndpointFactory.class.getName());
		beanDefinition.setFactoryMethodName("newEndpoint");
		beanDefinition.setConstructorArgumentValues(getEndpointAttributes(annotationAttributes));

		return beanDefinition;
	}

	private ConstructorArgumentValues getEndpointAttributes(Map<String, Object> annotationAttributes) {
		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue(annotationAttributes.get("value"));
		constructorArgumentValues.addGenericArgumentValue(annotationAttributes.get("scanBasePackageClasses"));
		return constructorArgumentValues;
	}
}
