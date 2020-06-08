package org.ehp246.demo.case002;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.ehp246.aufjms.annotation.EnableForMsg;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.ehp246.aufjms.core.bymsg.ReplyToConfiguration;
import org.ehp246.aufjms.core.jackson.JacksonConfiguration;
import org.springframework.boot.SpringApplication;
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
@Import({ JacksonConfiguration.class, ReplyToConfiguration.class })
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public DestinationNameResolver destinationNameResolver() {
		return new DestinationNameResolver() {

			@Override
			public Destination resolve(String name) {
				return new ActiveMQQueue("default.queue");
			}
		};
	}
}
