package org.ehp246.aufjms.core.bymsg;

import java.util.Map;

import org.ehp246.aufjms.api.endpoint.ResolvedInstance;
import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Caffeine;

public class ReqResConfiguration {
	public static final String BEAN_NAME_CORRELATION_MAP = "215904e1-b1c5-4754-8dd2-62c64497b204";

	@Bean(name = BEAN_NAME_CORRELATION_MAP)
	public Map<String, ResolvedInstance> correlMap() {
		return Caffeine.newBuilder().weakValues().<String, ResolvedInstance>build().asMap();
	}
}
