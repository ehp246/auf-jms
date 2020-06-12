package org.ehp246.aufjms.core.endpoint.case007;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;

@ForMsg
public class DuplicateType007 {

	@Executing({ "m001" })
	public void m001() {

	}

	@Executing({ "m001" })
	public void m002() {

	}
}