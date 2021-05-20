package me.ehp246.aufjms.core.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author Lei Yang
 *
 */
public class JsonProviderSelector implements ImportSelector {
    private static final boolean JACKSON_PRESENT;

    static {
        JACKSON_PRESENT = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
                JsonProviderSelector.class.getClassLoader());
    }

    @Override
    public String[] selectImports(final AnnotationMetadata importingClassMetadata) {
        if (JACKSON_PRESENT) {
            return new String[] { "me.ehp246.aufjms.provider.jackson.JacksonConfiguration" };
        }
        return null;
    }

}
