package org.ehp246.aufjms.inegration.collectionof;

import java.time.Instant;
import java.util.Set;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.CollectionOf;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("org.ehp246.aufjms.inegration.collectionof.AppConfiguration.request")
@OfType("Alarm")
interface SetAlarm {
	void set(Set<Instant> instants);

	@Invoking("set")
	void set(Instant... instants);

	@Invoking("get")
	@CollectionOf(Instant.class)
	Set<Instant> get();
}
