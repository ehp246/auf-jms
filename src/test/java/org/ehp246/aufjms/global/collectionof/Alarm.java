package org.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.ArrayList;
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

	@Invoking("set")
	public void set(final Instant... instants) {
		this.instants = instants;
	}

	@Invoking
	public void set(@CollectionOf(Instant.class) final Set<Instant> instants) {
		// Type checking
		this.instants = instants.toArray(new Instant[] {});
	}

	@Invoking("get")
	public List<Instant> get() {
		return List.of(instants);
	}

	@Invoking("flatSet")
	public Set<List<Instant>> flatSet(
			@CollectionOf({ ArrayList.class, Instant.class }) final Set<List<Instant>> instants) {
		final var list = instants.stream().flatMap(List::stream).collect(Collectors.toList());
		this.instants = new Instant[list.size()];
		return instants;
	}
}
