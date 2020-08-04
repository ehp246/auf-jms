package in.ehp246.aufjms.core.formsg.case001;

import in.ehp246.aufjms.api.annotation.ForMsg;
import in.ehp246.aufjms.api.annotation.Invoking;

/**
 * @author Lei Yang
 *
 */
@ForMsg
public class Case001 {

	@Invoking
	public void m001() {
	}

	@Invoking
	public void m003() {
	}

	@Invoking("m001-1")
	public void m001(final int i) {
	}

	@Invoking("m002")
	public void m002(final int i) {
	}

	/**
	 * Should not be registered
	 */
	public void m002() {
	}
}
