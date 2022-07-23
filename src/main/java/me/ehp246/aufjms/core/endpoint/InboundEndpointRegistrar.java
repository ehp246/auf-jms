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
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 * 
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointRegistrar implements ImportBeanDefinitionRegistrar {

    @SuppressWarnings("unchecked")
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final var enablerAttributes = importingClassMetadata
                .getAnnotationAttributes(EnableForJms.class.getCanonicalName());
        if (enablerAttributes == null) {
            return;
        }

        final var defaultConsumer = (String) enablerAttributes.get("defaultConsumer");

        final var inbounds = Arrays.asList(((Map<String, Object>[]) enablerAttributes.get("value")));
        for (int i = 0; i < inbounds.size(); i++) {
            final var inbound = inbounds.get(i);
            final var beanDefinition = newBeanDefinition(inbound);

            Set<String> scanThese = null;
            final var base = (Class<?>[]) inbound.get("scan");
            if (base.length > 0) {
                scanThese = Stream.of(base).map(baseClass -> baseClass.getPackage().getName())
                        .collect(Collectors.toSet());
            } else {
                final var baseName = importingClassMetadata.getClassName();
                scanThese = Set.of(baseName.substring(0, baseName.lastIndexOf(".")));
            }

            final var beanName = Optional.of(inbound.get("name").toString()).filter(OneUtil::hasValue)
                    .orElse(InboundEndpoint.class.getSimpleName() + "-" + i);
            final var constructorArgumentValues = new ConstructorArgumentValues();
            constructorArgumentValues.addGenericArgumentValue(inbound);
            constructorArgumentValues.addGenericArgumentValue(scanThese);
            constructorArgumentValues.addGenericArgumentValue(beanName);
            constructorArgumentValues.addGenericArgumentValue(defaultConsumer);

            beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    private GenericBeanDefinition newBeanDefinition(Map<String, Object> annotationAttributes) {
        final var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(InboundEndpoint.class);
        beanDefinition.setFactoryBeanName(InboundEndpointFactory.class.getName());
        beanDefinition.setFactoryMethodName("newInstance");

        return beanDefinition;
    }
}
