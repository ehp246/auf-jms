package me.ehp246.aufjms.core.byjms.registrar.case02;

import me.ehp246.aufjms.api.annotation.ByJms;

/**
 * @author Lei Yang
 *
 */
@ByJms(name = RegistrarCase02.NAME, ttl = "PT1S")
public interface RegistrarCase02 {
    final static String NAME = "8c9abb70-7ed0-40ec-9c2d-eb408a2feb09";
}
