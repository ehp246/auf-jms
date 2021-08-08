package me.ehp246.aufjms.global.case002;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.At;
import me.ehp246.aufjms.global.case002.request.Calculator;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByJms
@EnableForJms(@At(value = "queue://me.ehp246.aufjms.request", scan = Calculator.class))
class AppConfiguration {
}
