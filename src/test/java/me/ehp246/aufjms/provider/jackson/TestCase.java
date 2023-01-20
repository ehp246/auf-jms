package me.ehp246.aufjms.provider.jackson;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lei Yang
 *
 */
class TestCase {
    interface PersonName {
        @JsonProperty
        String firstName();

        @JsonProperty
        String lastName();
    }

    interface PersonDob {
        @JsonProperty
        Instant dob();
    }

    record Person(String firstName, String lastName, Instant dob) implements PersonName, PersonDob {
    }
}
