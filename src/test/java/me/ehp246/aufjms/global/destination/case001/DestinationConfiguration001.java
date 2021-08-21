package me.ehp246.aufjms.global.destination.case001;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.Inbound;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByJms()
@EnableForJms({ @Inbound("queue://${aufjms.request.queue}"), @Inbound("${aufjms.request.queue02}") })
public class DestinationConfiguration001 {
}
