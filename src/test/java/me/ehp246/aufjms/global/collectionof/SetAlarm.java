package me.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.CollectionOf;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByJms("me.ehp246.aufjms.collectionof")
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
