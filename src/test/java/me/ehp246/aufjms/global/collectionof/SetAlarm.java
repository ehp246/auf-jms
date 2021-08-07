package me.ehp246.aufjms.global.collectionof;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.CollectionOf;
import me.ehp246.aufjms.api.annotation.Invoke;
import me.ehp246.aufjms.api.annotation.OfType;

/**
 * @author Lei Yang
 *
 */
@ByJms(destination = "me.ehp246.aufjms.collectionof")
@OfType("Alarm")
interface SetAlarm {
    @Invoke("setCollection")
    void set(Set<Instant> instants);

    @Invoke("setArray")
    void set(Instant... instants);

    @Invoke("get")
    @CollectionOf(Instant.class)
    Set<Instant> get();

    @Invoke("flatSet")
    @CollectionOf({ Set.class, List.class, Instant.class })
    List<Set<List<Instant>>> flatSet(List<Set<List<Instant>>> instantSet);
}
