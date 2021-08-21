package me.ehp246.aufjms.integration.enablebyjms.case01;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms
@Import(EmbeddedArtemisConfig.class)
public class AppConfig01 {
}
