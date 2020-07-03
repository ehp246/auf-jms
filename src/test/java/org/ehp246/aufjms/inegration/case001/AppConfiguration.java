package org.ehp246.aufjms.inegration.case001;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQQueue;
import org.ehp246.aufjms.activemq.AllQueuesResolver;
import org.ehp246.aufjms.annotation.EnableByMsg;
import org.ehp246.aufjms.api.jms.DestinationNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolver;

/**
 * The application uses AufMq on the client side, JmsListener on the server
 * side.
 * 
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableJms
@EnableByMsg
class AppConfiguration {
	private final Logger LOGGER = LoggerFactory.getLogger(AppConfiguration.class);

	@Autowired
	private AtomicReference<CompletableFuture<Object>> ref1;

	public static void main(String[] args) {
		SpringApplication.run(AppConfiguration.class, args);
	}

	@Bean("ref1")
	public AtomicReference<CompletableFuture<Object>> ref() {
		return new AtomicReference<>();
	}

	@JmsListener(destination = "calc.request", selector = "JMSType = 'Add'")
	public void calc(String body) {
		ref1.get().complete(body);
	}

	@JmsListener(destination = "calc.request", selector = "JMSType = 'Inc'")
	public int calc(int i, Message message) {
		return i++;
	}

	@JmsListener(destination = "calc.request", selector = "JMSType = 'Mem'")
	public void mem(int i) {
		LOGGER.debug("Mem " + Integer.valueOf(i).toString());
		ref1.get().complete(i);
	}

	@JmsListener(destination = "alarm.request", selector = "JMSType = 'Set'")
	public void set(String instant) {
		ref1.get().complete(instant);
	}

	@Bean("calc.request")
	public Destination calcRequest() {
		return new ActiveMQQueue("calc.request");
	}

	@Bean("alarm.request")
	public Destination alarmRequest() {
		return new ActiveMQQueue("alarm.request");
	}

	@Bean
	public DestinationResolver destinationResolver(BeanFactory beanFactory) {
		return new BeanFactoryDestinationResolver(beanFactory);
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final ConnectionFactory connectionFactory,
			final DestinationResolver destinationResolver) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setDestinationResolver(destinationResolver);
		factory.setSessionTransacted(false);
		factory.setConcurrency("1");

		return factory;
	}

	/**
	 * Infrastructure beans
	 * 
	 * @return
	 */

	@Bean
	public DestinationNameResolver destinationNameResolver() {
		return new AllQueuesResolver();
	}
}
