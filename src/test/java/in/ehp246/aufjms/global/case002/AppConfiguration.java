package in.ehp246.aufjms.global.case002;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import in.ehp246.aufjms.api.annotation.EnableByMsg;
import in.ehp246.aufjms.api.annotation.EnableForMsg;
import in.ehp246.aufjms.api.annotation.EnableForMsg.At;
import in.ehp246.aufjms.global.case002.request.Calculator;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At(value = "queue://in.ehp246.aufjms.request", scan = Calculator.class))
class AppConfiguration {
}
