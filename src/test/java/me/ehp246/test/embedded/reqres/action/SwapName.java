package me.ehp246.test.embedded.reqres.action;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.test.embedded.reqres.Person;

/**
 * @author Lei Yang
 *
 */
@ForJmsType
class SwapName {
    public Person invoke(final Person person) {
        return new Person(person.lastName(), person.firstName());
    }
}
