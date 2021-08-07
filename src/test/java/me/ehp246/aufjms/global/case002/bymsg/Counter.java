package me.ehp246.aufjms.global.case002.bymsg;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.Invoke;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByJms(destination = "queue://me.ehp246.aufjms.request")
@OfType("Calculator")
public interface Counter {
    @Invoke("setMem")
    public Void set(int i);

    @Invoke("addMem")
    public int add(int i);

    @Invoke("getMem")
    public int get();
}
