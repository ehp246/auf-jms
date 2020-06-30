package org.ehp246.aufjms.core.bymsg;

import org.ehp246.aufjms.annotation.ByMsg;
import org.ehp246.aufjms.annotation.OfCorrelationId;
import org.ehp246.aufjms.annotation.OfType;

class TypeTestCase {
	@ByMsg("")
	interface TypeCase001 {
		void m001();

		void m001(@OfCorrelationId Object id);

		@OfType
		void m002(@OfType String type);

		void m003(@OfType("Type001") String type);

		@OfType("Type002")
		void m004(String type);

		@OfType
		void m005();

		@OfType("Type003")
		void m006(@OfType("Type004") String type);

		void m007(@OfType String type, @OfType String type2);
	}

}
