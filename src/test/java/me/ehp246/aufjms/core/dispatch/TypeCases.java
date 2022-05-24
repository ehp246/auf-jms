package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
class TypeCases {
    interface Case01 {
        void type01();

        void type01(@OfType final String type);

        @OfType("09bf9d41-d65a-4bf3-be39-75a318059c0d")
        void type02();

        @OfType
        void type03();
    }
}
