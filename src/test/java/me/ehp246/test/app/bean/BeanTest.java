package me.ehp246.test.app.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.PlaceholderResolutionException;

import me.ehp246.aufjms.api.spi.ExpressionResolver;

@SpringBootTest(classes = { AppConfig.class }, properties = {
        "jms.topic.inbox=embedded.inbox" }, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
class BeanTest {
    @Autowired
    private ExpressionResolver resolver;
    @Autowired
    private AppConfig.TopicConfig config;

    @Test
    void propertyResolver_01() {
        Assertions.assertEquals(config.inbox(), resolver.resolve("${jms.topic.inbox}"));
    }

    @Test
    void propertyResolver_02() {
        Assertions.assertEquals("prefix." + config.inbox() + ".v1", resolver.resolve("prefix.${jms.topic.inbox}.v1"));
    }

    @Test
    void propertyResolver_03() {
        Assertions.assertEquals(config.inbox(),
                resolver.resolve("#{@'jms.topic-me.ehp246.test.app.bean.AppConfig$TopicConfig'.inbox}"));
    }

    @Test
    void propertyResolver_04() {
        Assertions.assertThrows(PlaceholderResolutionException.class, () -> resolver.resolve("${not.there}"));
    }

    @Test
    void propertyResolver_05() {
        Assertions.assertEquals("prefix." + config.inbox() + ".v1", resolver
                .resolve("#{'prefix.' + @'jms.topic-me.ehp246.test.app.bean.AppConfig$TopicConfig'.inbox + '.v1'}"));
    }
}
