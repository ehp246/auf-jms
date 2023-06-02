package me.ehp246.test.embedded.request.case01.action;

import me.ehp246.aufjms.api.annotation.ForJmsType;

/**
 * @author Lei Yang
 *
 */
@ForJmsType
class Inc {
    public int invoke(int i) {
        return ++i;
    }
}
