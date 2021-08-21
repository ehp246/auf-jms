package me.ehp246.aufjms.core.bymsg;

import me.ehp246.aufjms.api.annotation.At;
import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.OfType;

@ByJms(value = @At(""))
interface BodyTestCase {
    void m001();

    void m001(int i);

    void m001(Integer i);

    void m001(int i, long l);

    void m001(Object o);

    void m001(Object o, Integer i);

    void m002(@OfType String string);

    void m002(@OfType String string, @OfType String string2, String string3);

    void m003(String string, @OfType String string2, String string3);
}
