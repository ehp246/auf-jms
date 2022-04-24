package me.ehp246.aufjms.integration.enablebyjms;

import org.springframework.context.annotation.Import;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.integration.enablebyjms.case03.PropertyNameCases;
import me.ehp246.aufjms.util.EmbeddedArtemisConfig;

/**
 * @author Lei Yang
 *
 */
@EnableByJms(scan = { PropertyNameCases.class })
@Import(EmbeddedArtemisConfig.class)
class PropertyAppConfig {

}
