package me.ehp246.test.embedded.dispatch.body;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufjms.api.spi.JmsView;

/**
 * @author Lei Yang
 *
 */
class Payload extends JmsView {
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

    static class Account {
        record Request(@JsonView(JmsView.class) String id, @JsonView(Payload.class) String password) {
        }
    }

}
