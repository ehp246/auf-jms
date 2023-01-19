package me.ehp246.aufjms.integration.endpoint.bean;

import java.util.concurrent.CompletableFuture;

import jakarta.annotation.PreDestroy;
import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForJmsType(value = ".*")
class OnMsg {
    private final CompletableFuture<Boolean> closeFuture;

    OnMsg(final CompletableFuture<Boolean> closeFuture) {
        super();
        this.closeFuture = closeFuture;
    }

    @Invoking
    public void onMsg() {
    }

    @PreDestroy
    void close() {
        closeFuture.complete(Boolean.TRUE);
    }
}
