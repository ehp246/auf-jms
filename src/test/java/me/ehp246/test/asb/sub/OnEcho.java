package me.ehp246.test.asb.sub;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = "echo", scope = InstanceScope.BEAN)
class OnEcho {
    private final CompletableFuture<Integer> echo = new CompletableFuture<>();

    @Invoking
    public void perform() {

    }

    Integer get() throws InterruptedException, ExecutionException {
        return echo.get();
    }
}
