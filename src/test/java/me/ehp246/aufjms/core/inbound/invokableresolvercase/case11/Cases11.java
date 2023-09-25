package me.ehp246.aufjms.core.inbound.invokableresolvercase.case11;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
public class Cases11 {
    @ForJmsType
    public static class Case1 {
        public void apply() {
        }
    }

    @ForJmsType
    public static class Case2 {
        public void apply() {
        }

        // Should take this one
        public void invoke() {
        }
    }

    /**
     * Error.
     */
    @ForJmsType
    public static class Case3 {
        public void apply() {
        }

        public void apply(final int i) {
        }
    }

    @ForJmsType
    public static class Case4 {
        @Invoking
        public void apply() {
        }

        public void invoke() {
        }
    }
}
