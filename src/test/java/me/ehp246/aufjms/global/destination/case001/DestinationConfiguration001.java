package me.ehp246.aufjms.global.destination.case001;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByMsg;
import me.ehp246.aufjms.api.annotation.EnableForMsg;
import me.ehp246.aufjms.api.annotation.EnableForMsg.At;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg(replyTo = "topic://${aufjms.reply.topic}")
@EnableForMsg({ @At("queue://${aufjms.request.queue}"), @At("${aufjms.request.queue02}") })
public class DestinationConfiguration001 {
}
