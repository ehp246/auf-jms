package org.ehp246.aufjms.global.case002;

import org.ehp246.aufjms.api.annotation.EnableByMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg.At;
import org.ehp246.aufjms.global.case002.request.Calculator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At(value = "queue://org.ehp246.aufjms.request", scan = Calculator.class))
class AppConfiguration {
}
