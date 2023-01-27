package me.ehp246.test.embedded.inbound.body.view;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.spi.JmsView;
import me.ehp246.test.embedded.inbound.body.view.AppConfig.Received;

/**
 * @author Lei Yang
 *
 */
class OnMsg {
    @ForJmsType
    static class Default {
        @Autowired
        private List<CompletableFuture<Received>> receiving;

        public void invoke(final Received payload) {
            this.receiving.get(0).complete(payload);
        }
    }

    @ForJmsType
    static class Id extends Default {
        @Override
        public void invoke(@JsonView(JmsView.class) final Received payload) {
            super.invoke(payload);
        }
    }

    @ForJmsType
    static class IdPassword extends Default {
        @Override
        public void invoke(@JsonView(String.class) final Received payload) {
            super.invoke(payload);
        }
    }

    @ForJmsType
    static class None extends Default {
        @Override
        public void invoke(@JsonView(Integer.class) final Received payload) {
            super.invoke(payload);
        }
    }
}
