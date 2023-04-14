package me.ehp246.test.embedded.reqres.action;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
@ForJmsType
class Inc {
    public int invoke(int i) {
        return i++;
    }
}
