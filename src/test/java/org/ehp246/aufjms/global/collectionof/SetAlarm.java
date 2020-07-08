package org.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.CollectionOf;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("org.ehp246.aufjms.collectionof")
@OfType("Alarm")
interface SetAlarm {
	void set(Set<Instant> instants);

	@Invoking("set")
	void set(Instant... instants);

	@Invoking("get")
	@CollectionOf(Instant.class)
	Set<Instant> get();

	@Invoking("flatSet")
	@CollectionOf({ Set.class, List.class, Instant.class })
	List<Set<List<Instant>>> flatSet(List<Set<List<Instant>>> instantSet);
}
