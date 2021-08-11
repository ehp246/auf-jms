package me.ehp246.aufjms.core.endpoint;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.endpoint.AtEndpoint;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * 
 * @author Lei Yang
 *
 */
public final class AtEndpointRegistrar implements ImportBeanDefinitionRegistrar {

    @SuppressWarnings("unchecked")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final var enablerAttributes = importingClassMetadata
                .getAnnotationAttributes(EnableForJms.class.getCanonicalName());
        if (enablerAttributes == null) {
            return;
        }

        for (final var endpoint : Arrays.asList(((Map<String, Object>[]) enablerAttributes.get("value")))) {
            final var beanDefinition = newBeanDefinition(endpoint);

            Set<String> scanThese = null;
            final var base = (Class<?>[]) endpoint.get("scan");
            if (base.length > 0) {
                scanThese = Stream.of(base).map(baseClass -> baseClass.getPackage().getName())
                        .collect(Collectors.toSet());
            } else {
                final var baseName = importingClassMetadata.getClassName();
                scanThese = Set.of(baseName.substring(0, baseName.lastIndexOf(".")));
            }
            final var connection = endpoint.get("connection").toString();
            final var destination = endpoint.get("value").toString();
            final var name = Optional.of(endpoint.get("name").toString()).filter(OneUtil::hasValue)
                    .orElse(destination + "@" + connection);

            final var constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addGenericArgumentValue(connection);
            constructorArgumentValues.addGenericArgumentValue(destination);
            constructorArgumentValues.addGenericArgumentValue(scanThese);
            constructorArgumentValues.addGenericArgumentValue(endpoint.get("concurrency"));
            constructorArgumentValues.addGenericArgumentValue(name);

            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

            registry.registerBeanDefinition(name, beanDefinition);
        }
    }

    private GenericBeanDefinition newBeanDefinition(Map<String, Object> annotationAttributes) {
        final var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(AtEndpoint.class);
        beanDefinition.setFactoryBeanName(AtEndpointFactory.class.getName());
        beanDefinition.setFactoryMethodName("newInstance");

        return beanDefinition;
    }
}
