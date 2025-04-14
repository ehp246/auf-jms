package me.ehp246.test.embedded.request.case01.action;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.test.embedded.request.case01.Person;

/**
 * @author Lei Yang
 *
 */
@ForJmsType
public class SwapName {
    public Person invoke(final Person person) {
        return new Person(person.lastName(), person.firstName());
    }
}
