package org.ehp246.aufjms.core.endpoint.case001;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;
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
		@Executing
		public void m001() {

		}

		public void execute() {

		}
	}

	@ForMsg(execution = ExecutionModel.SYNC)
	public static class Case003 {

		/**
		 * This method should take precedence.
		 */
		@Executing
		public void m001() {

		}
	}

	@ForMsg(scope = InstanceScope.BEAN)
	public static class Case004 {

		@Executing
		public void m001() {

		}
	}

	@ForMsg
	public static class Case005 {

		@Executing
		public void m001() {

		}
	}

	@ForMsg(scope = InstanceScope.BEAN)
	public static abstract interface Case006 {
		void execute();
	}
}
