package me.ehp246.aufjms.provider.jackson;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufjms.api.spi.JacksonConfig;
import me.ehp246.aufjms.api.spi.ToJson;
import me.ehp246.aufjms.provider.jackson.JsonByJacksonTestCases.Person;
import me.ehp246.aufjms.provider.jackson.JsonByJacksonTestCases.PersonDob;
import me.ehp246.aufjms.provider.jackson.JsonByJacksonTestCases.PersonName;

class JsonByJacksonTest {
    private final JsonByJackson jackson = new JsonByJackson(new JacksonConfig().objectMapper());

    @Test
    void toType_01() {
        Assertions.assertEquals("{\"lastName\":\"Snow\",\"firstName\":\"John\"}",
                jackson.apply(List
                        .of(new ToJson.From(new Person("John", "Snow", Instant.now()), PersonName.class))));
    }

    @Test
    void toType_02() {
        final var now = Instant.now();
        Assertions.assertEquals("{\"dob\":\"" + now.toString() + "\"}",
                jackson.apply(List.of(new ToJson.From(new Person("John", "Snow", now), PersonDob.class))));
    }
}
