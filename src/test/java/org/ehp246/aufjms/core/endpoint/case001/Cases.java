package org.ehp246.aufjms.core.endpoint.case001;

import org.ehp246.aufjms.annotation.Invoking;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.InvocationModel;
import org.ehp246.aufjms.api.endpoint.InstanceScope;

public class Cases {
	@ForMsg
	public static class Case001 {
		public void execute() {

		}
	}

	@ForMsg
	public static class Case002 {

		/**
		 * This method should take precedence.
		 */
		@Invoking
		public void m001() {

		}

		public void execute() {

		}
	}

	@ForMsg(invocation = InvocationModel.SYNC)
	public static class Case003 {

		/**
		 * This method should take precedence.
		 */
		@Invoking
		public void m001() {

		}
	}

	@ForMsg(scope = InstanceScope.BEAN)
	public static class Case004 {

		@Invoking
		public void m001() {

		}
	}

	@ForMsg
	public static class Case005 {

		@Invoking
		public void m001() {

		}
	}

	@ForMsg(scope = InstanceScope.BEAN)
	public static abstract interface Case006 {
		void execute();
	}
}
