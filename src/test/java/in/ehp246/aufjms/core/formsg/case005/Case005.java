package in.ehp246.aufjms.core.formsg.case005;

import me.ehp246.aufjms.api.annotation.ForMsg;
import me.ehp246.aufjms.api.annotation.Invoking;

/**
 * This class should fail scan because it's abstract and yet not BEAN-scoped.
 *
 * @author Lei Yang
 *
 */
@ForMsg
public abstract class Case005 {
	@Invoking
	public void m001() {

	}
}
