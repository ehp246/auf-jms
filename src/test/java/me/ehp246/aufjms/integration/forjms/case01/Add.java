package me.ehp246.aufjms.integration.forjms.case01;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufjms.api.annotation.ForJms;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJms("Add")
class Add {
    @Autowired
    private AtomicReference<CompletableFuture<?>> ref;

    @Invoking
    public int add() {
        ref.get().complete(null);
        return 1;
    }
}
