package me.ehp246.aufjms.integration.endpoint.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJms
class Add {
    @Autowired
    private AtomicReference<CompletableFuture<Integer>> ref;

    @Invoking
    public void add(final int i) {
        ref.get().complete(i);
    }
}
