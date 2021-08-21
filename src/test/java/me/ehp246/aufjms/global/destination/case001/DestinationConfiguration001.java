package me.ehp246.aufjms.global.destination.case001;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.At;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByJms()
@EnableForJms({ @At("queue://${aufjms.request.queue}"), @At("${aufjms.request.queue02}") })
public class DestinationConfiguration001 {
}
