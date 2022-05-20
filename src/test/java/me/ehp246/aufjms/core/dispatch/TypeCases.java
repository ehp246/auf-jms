package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
class TypeCases {
    public static final String TYPE_I = "09bf9d41-d65a-4bf3-be39-75a318059c0d";

    public final static String TYPE_II = "6f7779af-8c3e-4684-8a12-537415281b89";

    interface Case01 {
        void type01();

        void type01(@OfType final String type);

        @OfType("09bf9d41-d65a-4bf3-be39-75a318059c0d")
        void type02();

        @OfType
        void type03();
    }

    @OfType("09bf9d41-d65a-4bf3-be39-75a318059c0d")
    static interface Case02 {
        void type01(@OfType final String type);

        @OfType("6f7779af-8c3e-4684-8a12-537415281b89")
        void type02();

        void type03();

        @OfType
        void type04();
    }

    @OfType
    static interface Case03 {
        void m01();

        @OfType
        void m02();
    }
}
