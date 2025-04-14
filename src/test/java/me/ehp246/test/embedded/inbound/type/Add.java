package me.ehp246.test.embedded.inbound.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType("Add")
public class Add {
    @Autowired
    private AtomicReference<CompletableFuture<Integer>> ref;

    @Invoking
    public void add(final int i) {
        ref.get().complete(i);
    }
}
