package org.ehp246.aufjms.inegration.case003;

import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg.At;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg(replyTo = "topic://${aufjms.reply.topic}")
@EnableForMsg({ @At("queue://${aufjms.request.queue}"), @At("${aufjms.request.queue02}") })
public class AppConfiguration {
}
