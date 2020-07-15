package org.ehp246.aufjms.global.collectionof;

import org.ehp246.aufjms.api.annotation.EnableByMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg;
import org.ehp246.aufjms.api.annotation.EnableForMsg.At;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At("org.ehp246.aufjms.collectionof"))
public class CollectionOfConfiguration {
}
