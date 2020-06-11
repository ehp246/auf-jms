package org.ehp246.aufjms.core.endpoint.case001;

import org.ehp246.aufjms.annotation.Executing;
import org.ehp246.aufjms.annotation.ForMsg;
import org.ehp246.aufjms.api.endpoint.ExecutionModel;

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
}
