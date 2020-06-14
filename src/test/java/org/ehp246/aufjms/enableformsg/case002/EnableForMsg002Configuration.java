package org.ehp246.aufjms.enableformsg.case002;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.core.formsg.EnableForMsg;
import org.ehp246.aufjms.core.formsg.EnableForMsg.Endpoint;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.ehp246.aufjms.enableformsg.case002.endpoint002.Calc002;
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
@EnableForMsg({ @Endpoint, @Endpoint(value = "endpoint002", scan = Calc002.class) })
@Import({ JacksonConfiguration.class })
class EnableForMsg002Configuration {
	public final static ActiveMQQueue DefaultQueue = new ActiveMQQueue("default.queue");

	@Bean
	public DestinationNameResolver destinationNameResolver() {
		return new DestinationNameResolver() {

			@Override
			public Destination resolve(String name) {
				return DefaultQueue;
			}
		};
	}

}
