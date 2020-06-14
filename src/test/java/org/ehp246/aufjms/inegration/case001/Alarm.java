package org.ehp246.aufjms.inegration.case001;

import java.time.Instant;

import org.ehp246.aufjms.annotation.ByMsg;

@ByMsg("alarm.request")
public interface Alarm {
	void set(Instant... instant);
}
