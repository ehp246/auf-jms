package me.ehp246.aufjms.integration.enablebyjms.case02;

import me.ehp246.aufjms.api.annotation.ByJms;
import me.ehp246.aufjms.api.annotation.ByJms.To;

/**
 * @author Lei Yang
 *
 */
@ByJms(value = @To("d"), name = Case02.NAME)
public interface Case02 {
    static final String NAME = "c2e961d8-1b3d-4d3e-8efb-e03ae7a3c6d8";
}
