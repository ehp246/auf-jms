package me.ehp246.test.app.objectmapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.provider.jackson.JsonByObjectMapper;
import me.ehp246.test.Jackson;

/**
 * @author Lei Yang
 *
 */
class ObjectMapperTest {
    private AnnotationConfigApplicationContext appCtx;

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
            appCtx = null;
        }
    }

    @Test
    void provided_01() {
        appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.Config01.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(JsonByObjectMapper.class) != null);
    }

    @Test
    void provided_02() {
        appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.Config04.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(JsonByObjectMapper.class) != null);
    }

    @Test
    void provided_03() {
        appCtx = new AnnotationConfigApplicationContext();

        appCtx.register(AppConfig.Config05.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(JsonByObjectMapper.class) != null);
    }

    @Test
    void provided_04() {
        appCtx = new AnnotationConfigApplicationContext();

        appCtx.register(AppConfig.Config06.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(JsonByObjectMapper.class) != null);
    }

    @Test
    void provided_05() {
        appCtx = new AnnotationConfigApplicationContext();

        appCtx.register(AppConfig.Config08.class);
        appCtx.refresh();
    }

    @Test
    void primary_01() {
        appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.Config02.class);
        appCtx.refresh();

        Assertions.assertEquals(Jackson.OBJECT_MAPPER, appCtx.getBean(ObjectMapper.class));
    }

    @Test
    void primary_02() {
        appCtx = new AnnotationConfigApplicationContext();

        appCtx.register(AppConfig.Config07.class);
        appCtx.refresh();

        Assertions.assertDoesNotThrow(() -> appCtx.getBean(JsonByObjectMapper.class));
    }

    @Test
    void default_01() {
        appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.Config03.class);
        appCtx.refresh();

        Assertions.assertEquals(true, appCtx.getBean(JsonByObjectMapper.class) != null);
    }
}
