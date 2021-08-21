package me.ehp246.aufjms.integration.enablebyjms.case02;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.integration.enablebyjms.case01.Case01;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(scan = Case01.class)
@Import(EmbeddedArtemisConfig.class)
public class AppConfig02 {
}
