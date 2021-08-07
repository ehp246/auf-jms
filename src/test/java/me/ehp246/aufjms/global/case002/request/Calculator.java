package me.ehp246.aufjms.global.case002.request;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJms(scope = InstanceScope.BEAN)
public class Calculator {
    public AtomicReference<Integer> mem = new AtomicReference<>();

    @Invoke("setMem")
    public void setMem(int i) {
        this.mem.set(i);
    }

    @Invoke("addMem")
    public int addMem(int i) {
        this.mem.set(this.mem.get() + i);
        return mem.get();
    }

    @Invoke("getMem")
    public int getMem() {
        return this.mem.get();
    }

    @Invoke
    public int add(int i, int j) {
        return i + j;
    }
}
