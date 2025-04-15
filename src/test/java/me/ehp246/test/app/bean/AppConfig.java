package me.ehp246.test.app.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.ehp246.aufjms.api.annotation.EnableByJms;

@EnableByJms
@EnableConfigurationProperties({ AppConfig.TopicConfig.class })
class AppConfig {
    @ConfigurationProperties(prefix = "jms.topic")
    static record TopicConfig(String inbox) {
    }
}
