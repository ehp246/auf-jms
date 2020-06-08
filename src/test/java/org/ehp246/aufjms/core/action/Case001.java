package org.ehp246.aufjms.core.action;

import java.time.Instant;
import java.util.List;

import javax.jms.Message;

import org.ehp246.aufjms.annotation.OfCorrelationId;
import org.ehp246.aufjms.annotation.OfType;
import org.ehp246.aufjms.api.jms.Msg;

public class Case001 {
	public void m001() {

	}

	public Msg m001(Msg msg) {
		return msg;
	}
	
	public Object[] m001(Msg msg, Message message) {
		return new Object[] {msg, message};
	}
	
	public String m001(@OfCorrelationId String correlId, int i) {
		return correlId + Integer.valueOf(i).toString();
	}

	public Object[] m001(List<Integer> integers, Instant[] instants, Message message) {
		final var instant = instants[0];
		
		return new Object[] {integers, instant, message};
	}

	public void m001(Msg msg, @OfType String type, String str) {

	}
	
	public Void m002() {
		return null;
	}
	
	public void m003() {
		throw new RuntimeException();
	}
}