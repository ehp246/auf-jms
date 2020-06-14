package org.ehp246.formsg;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.ehp246.aufjms.annotation.EnableForMsg;
import org.ehp246.aufjms.api.jms.GoToNameResolver;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * The application demonstrates MqAction on the server side.
 * 
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableForMsg
@Import({ JacksonConfiguration.class })
public class EnableForMsg001Configuration {
	@Bean
	public GoToNameResolver destinationNameResolver() {
		return new GoToNameResolver() {
			private final ActiveMQQueue activeMQQueue = new ActiveMQQueue("default.queue");

			@Override
			public Destination resolve(String name) {
				return activeMQQueue;
			}
		};
	}

}
