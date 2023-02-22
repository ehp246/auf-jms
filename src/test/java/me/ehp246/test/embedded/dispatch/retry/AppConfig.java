package me.ehp246.test.embedded.dispatch.retry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;
import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.aufjms.api.dispatch.DispatchListener;
import me.ehp246.aufjms.api.jms.JmsDispatch;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@EnableForJms(@Inbound(@From("retry")))
@EnableRetry
@Import({ OnMsg.class, EmbeddedArtemisConfig.class })
class AppConfig {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final int[] countRef = new int[] { 0 };

    @Bean
    int[] countRef() {
        return countRef;
    }

    @Bean
    DispatchListener dispatchListener() {
        return new DispatchListener.OnDispatch() {
            @Override
            public void onDispatch(final JmsDispatch dispatch) {
                countRef[0]++;
                LOGGER.atInfo().log("Counting {}", countRef[0]);

                if (countRef[0] < 3 || countRef[0] >= 5) {
                    throw new RuntimeException("Fail it.");
                }
            }
        };
    }

    @ByJms(@To("retry"))
    interface Case01 {
        @Retryable(maxAttempts = 3)
        void fail(String value);
    }
}
