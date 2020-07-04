package org.ehp246.aufjms.integration.case002;

import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg.At;
import org.ehp246.aufjms.integration.case002.request.Calculator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At(scan = Calculator.class))
class AppConfiguration {
}
