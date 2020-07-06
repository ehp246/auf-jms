package org.ehp246.aufjms.global.collectionof;

import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg.At;
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