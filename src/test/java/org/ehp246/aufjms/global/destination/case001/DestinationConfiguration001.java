package org.ehp246.aufjms.global.destination.case001;

import org.ehp246.aufjms.api.annotation.EnableByMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg.At;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg(replyTo = "topic://${aufjms.reply.topic}")
@EnableForMsg({ @At("queue://${aufjms.request.queue}"), @At("${aufjms.request.queue02}") })
public class DestinationConfiguration001 {
}
