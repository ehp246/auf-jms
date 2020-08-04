package in.ehp246.aufjms.global.collectionof;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import in.ehp246.aufjms.api.annotation.EnableByMsg;
import in.ehp246.aufjms.api.annotation.EnableForMsg;
import in.ehp246.aufjms.api.annotation.EnableForMsg.At;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByMsg
@EnableForMsg(@At("in.ehp246.aufjms.collectionof"))
public class CollectionOfConfiguration {
}
