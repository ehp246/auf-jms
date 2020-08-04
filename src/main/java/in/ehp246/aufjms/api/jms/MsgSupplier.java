package in.ehp246.aufjms.api.jms;

import java.util.List;
import java.util.Map;

public interface MsgSupplier {
	String getCorrelationId();

	String getType();

	String getInvoking();

	List<?> getBodyValues();

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
