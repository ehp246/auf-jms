package org.ehp246.aufjms.core.endpoint.case007;

import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.ForMsg;

@ForMsg
public class DuplicateType007 {

	@Invoking("m001")
	public void m001() {

	}

	@Invoking("m001")
	public void m002() {

	}
}