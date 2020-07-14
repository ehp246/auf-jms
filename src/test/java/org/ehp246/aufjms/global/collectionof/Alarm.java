package org.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.ehp246.aufjms.annotation.CollectionOf;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.api.endpoint.InstanceScope;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.springframework.stereotype.Service;

/**
 * @author Lei Yang
 *
 */
@Service
@ForMsg(scope = InstanceScope.BEAN, invocation = InvocationModel.SYNC)
class Alarm {
	private Instant[] instants;

	@Invoking("setArray")
	public void set(final Instant... instants) {
		this.instants = instants;
	}

	@Invoking("setCollection")
	public void set(@CollectionOf(Instant.class) final Set<Instant> instants) {
		// Type checking
		this.instants = instants.toArray(new Instant[] {});
	}

	@Invoking("get")
	public List<Instant> get() {
		return List.of(instants);
	}

	@Invoking("flatSet")
	public List<Set<List<Instant>>> flatSet(
			@CollectionOf({ Set.class, List.class, Instant.class }) final List<Set<List<Instant>>> instants) {
		final var list = instants.stream().flatMap(Set::stream).flatMap(List::stream)
				.collect(Collectors.<Instant>toList());
		this.instants = new Instant[list.size()];

		for (int i = 0; i < list.size(); i++) {
			this.instants[i] = list.get(i);
		}
		return instants;
	}
}
