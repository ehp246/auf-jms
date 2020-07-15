package org.ehp246.aufjms.global.case002;

import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.annotation.EnableForMsg;
import org.ehp246.aufjms.annotation.EnableForMsg.At;
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
