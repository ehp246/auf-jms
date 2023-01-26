package me.ehp246.test.embedded.inbound.body.view;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jgroups.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.ehp246.test.EmbeddedArtemisConfig;
import me.ehp246.test.embedded.inbound.body.view.AppConfig.Dispatch;
import me.ehp246.test.embedded.inbound.body.view.AppConfig.Dispatch.Account;
import me.ehp246.test.embedded.inbound.body.view.AppConfig.Received;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class, EmbeddedArtemisConfig.class })
class BodyTest {
    @Autowired
    private List<CompletableFuture<Received>> receiving;

    @Autowired
    private Dispatch dispatch;

    @BeforeEach
    void reset() {
        receiving.clear();
        receiving.add(new CompletableFuture<AppConfig.Received>());
    }

    @Test
    void test_01() throws InterruptedException, ExecutionException {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dispatch.send("Default", account);

        final var received = receiving.get(0).get();

        Assertions.assertEquals(account.id(), received.getId());
        Assertions.assertEquals(account.password(), received.getPassword());
    }

    @Test
    void test_02() throws InterruptedException, ExecutionException {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dispatch.send("Id", account);

        final var received = receiving.get(0).get();

        Assertions.assertEquals(account.id(), received.getId());
        Assertions.assertEquals(null, received.getPassword());
    }

    @Test
    void test_03() throws InterruptedException, ExecutionException {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dispatch.send("IdPassword", account);

        final var received = receiving.get(0).get();

        Assertions.assertEquals(account.id(), received.getId());
        Assertions.assertEquals(account.password(), received.getPassword());
    }

    @Test
    void test_04() throws InterruptedException, ExecutionException {
        final var account = new Account(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dispatch.send("None", account);

        final var received = receiving.get(0).get();

        Assertions.assertEquals(null, received.getId());
        Assertions.assertEquals(null, received.getPassword());
    }
}
