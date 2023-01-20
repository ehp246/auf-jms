package me.ehp246.test.embedded.endpoint.name;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound.From;
import me.ehp246.test.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableForJms({ @Inbound(@From("d")),
        @Inbound(value = @From("a903988f-89af-42ba-9777-f52831b480ff"), name = "a778506e-a1dc-40c6-aeb3-42114f993c22"),
        @Inbound(@From("d")) })
@Import(EmbeddedArtemisConfig.class)
class AppConfig {
}
