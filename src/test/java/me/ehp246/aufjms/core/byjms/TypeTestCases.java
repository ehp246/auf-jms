package me.ehp246.aufjms.core.byjms;

import java.util.Map;

import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
class TypeTestCases {
    public static final String TYPE = "09bf9d41-d65a-4bf3-be39-75a318059c0d";

    static interface Case01 {
        void m01();

        void m02(Map<String, String> map);

        void type01(@OfType final String type);

        void type01_II(@OfType(TYPE) final String type);

        @OfType(TYPE)
        void type01_III(@OfType final String type);

        @OfType(TYPE)
        void type02();

        @OfType
        void type02_I();
    }

    @OfType(TYPE)
    static class Case02 {

        public void type01(@OfType final String type) {

        }

        @OfType(TYPE)
        public void type02() {

        }

        public void type03() {

        }

    }
}
