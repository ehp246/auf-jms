package org.ehp246.aufjms.core.configuration;

import java.util.concurrent.ThreadPoolExecutor;

import org.ehp246.aufjms.api.endpoint.ActionExecutor;
import org.ehp246.aufjms.api.endpoint.ActionInvocationBinder;
import org.ehp246.aufjms.core.endpoint.ListenablePoolActionExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * @author Lei Yang
 *
 */
public class ActionExecutorConfiguration {
	/**
	 * The executor must implement CallerRunsPolicy.
	 * 
	 * @param poolSize
	 * @return
	 */
	@Bean()
	public ThreadPoolTaskExecutor actionThreadPool(
			@Value("${org.ehp246.aufjms.actionexecutor.pool-size:10}") int poolSize) {
		if (poolSize <= 0) {
			return null;
		}
		final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(poolSize);
		threadPoolTaskExecutor.setMaxPoolSize(poolSize);
		threadPoolTaskExecutor.setQueueCapacity(-1);
		threadPoolTaskExecutor.setThreadNamePrefix("ActionExecutor-Pool-");
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}

	@Bean
	public ActionExecutor actionExecutor(final ActionInvocationBinder binder,
			final AsyncListenableTaskExecutor executor) {
		return new ListenablePoolActionExecutor(executor, binder);
	}
}
