package org.ehp246.aufjms.core.endpoint.case001;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;

public class Cases {
	@ForMsg
	public static class Error001 {
		@Executing
		public void m001() {
		}

		@Executing
		public void m002() {
		}
	}

	@ForMsg
	public static class Case001 {
		public void execute() {

		}
	}

	@ForMsg
	public static class Case002 {

		@Executing
		public void m001() {

		}

		public void execute() {

		}
	}
}
