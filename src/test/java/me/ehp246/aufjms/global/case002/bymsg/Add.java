package me.ehp246.aufjms.global.case002.bymsg;

import me.ehp246.aufjms.api.annotation.ByMsg;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("queue://me.ehp246.aufjms.request")
public interface Add {
    @OfType("Calculator")
    public int add(int i, int j);
}
