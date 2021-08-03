package me.ehp246.aufjms.global.collectionof;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForMsg;
import me.ehp246.aufjms.api.annotation.EnableForMsg.At;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByJms
@EnableForMsg(@At("me.ehp246.aufjms.collectionof"))
public class CollectionOfConfiguration {
}
