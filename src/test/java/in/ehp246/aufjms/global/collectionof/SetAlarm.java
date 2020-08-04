package in.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import in.ehp246.aufjms.api.annotation.ByMsg;
import in.ehp246.aufjms.api.annotation.CollectionOf;
import in.ehp246.aufjms.api.annotation.Invoking;
import in.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByMsg("in.ehp246.aufjms.collectionof")
@OfType("Alarm")
interface SetAlarm {
	@Invoking("setCollection")
	void set(Set<Instant> instants);

	@Invoking("setArray")
	void set(Instant... instants);

	@Invoking("get")
	@CollectionOf(Instant.class)
	Set<Instant> get();

	@Invoking("flatSet")
	@CollectionOf({ Set.class, List.class, Instant.class })
	List<Set<List<Instant>>> flatSet(List<Set<List<Instant>>> instantSet);
}
