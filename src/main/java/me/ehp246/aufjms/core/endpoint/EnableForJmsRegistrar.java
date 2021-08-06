package me.ehp246.aufjms.core.endpoint;

import java.util.Map;
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

/**
 * 
 * @author Lei Yang
 *
 */
public final class EnableForJmsRegistrar implements ImportBeanDefinitionRegistrar {

    @SuppressWarnings("unchecked")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final var enablerAttributes = importingClassMetadata
                .getAnnotationAttributes(EnableForJms.class.getCanonicalName());
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
        beanDefinition.setBeanClass(AtEndpoint.class);
        beanDefinition.setFactoryBeanName(AtEndpointFactory.class.getName());
        beanDefinition.setFactoryMethodName("newEndpoint");

        return beanDefinition;
    }

    private ConstructorArgumentValues getParameters(final Object destination, final Object scans) {
        final var constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(destination);
        constructorArgumentValues.addGenericArgumentValue(scans);
        return constructorArgumentValues;
    }
}
