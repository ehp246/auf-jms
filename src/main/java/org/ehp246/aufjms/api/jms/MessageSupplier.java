package org.ehp246.aufjms.api.jms;

import java.util.List;
import java.util.Map;

public interface MessageSupplier {
	String getCorrelationId();

	String getType();

	List<?> getBodyValue();

	default Long getTtl() {
		return null;
	}

	default String getGroupId() {
		return null;
	}

	default Map<String, String> getPropertyMap() {
		return null;
	}

	default boolean isException() {
		return false;
	}
}
