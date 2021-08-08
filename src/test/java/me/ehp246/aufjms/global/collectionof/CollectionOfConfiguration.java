package me.ehp246.aufjms.global.collectionof;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.ehp246.aufjms.api.annotation.EnableByJms;
import me.ehp246.aufjms.api.annotation.EnableForJms;
import me.ehp246.aufjms.api.annotation.EnableForJms.At;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByJms
@EnableForJms(@At("me.ehp246.aufjms.collectionof"))
public class CollectionOfConfiguration {
}
