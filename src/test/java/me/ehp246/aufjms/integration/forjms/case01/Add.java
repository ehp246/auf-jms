package me.ehp246.aufjms.integration.forjms.case01;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoke;

/**
 * @author Lei Yang
 *
 */
@ForJms
class Add {
    @Autowired
    private AtomicReference<CompletableFuture<Integer>> ref;

    @Invoke
    public void add(final int i) {
        ref.get().complete(i);
    }
}
