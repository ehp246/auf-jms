package me.ehp246.aufjms.global.case002;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByMsg;
import me.ehp246.aufjms.api.annotation.EnableForMsg;
import me.ehp246.aufjms.api.annotation.EnableForMsg.At;
import me.ehp246.aufjms.global.case002.request.Calculator;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At(value = "queue://me.ehp246.aufjms.request", scan = Calculator.class))
class AppConfiguration {
}
