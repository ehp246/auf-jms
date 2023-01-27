package me.ehp246.test.embedded.inbound.property;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.annotation.Invoking;
import me.ehp246.aufjms.api.annotation.OfProperty;

/**
 * @author Lei Yang
 *
 */
@ForJmsType("")
class PropertyCase {
    @Autowired
    private AtomicReference<CompletableFuture<PropertyCase>> ref;

    public Map<String, Object> map;

    @Invoking
    public void invoke(@OfProperty final Map<String, Object> map) {
        this.map = map;
        ref.get().complete(this);
    }
}
