package me.ehp246.test.app.objectmapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.test.Jackson;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByJms
    static class Config01 {
        @Bean
        public ObjectMapper aufJmsObjectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }

    @EnableByJms
    static class Config02 {
        @Bean
        public ObjectMapper objectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }

    @EnableByJms
    static class Config03 {
    }

    @EnableByJms
    static class Config04 {
        @Bean
        public ObjectMapper aufJmsObjectMapper() {
            return Jackson.OBJECT_MAPPER;
        }

        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            return Jackson.OBJECT_MAPPER;
        }
    }
}
