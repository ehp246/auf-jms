package me.ehp246.aufjms.core.configuration;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author Lei Yang
 *
 */
public class PooledExecutorConfiguration {
	/**
	 * The executor must implement CallerRunsPolicy.
	 *
	 * @param poolSize
	 * @return
	 */
	@Bean(AufJmsProperties.EXECUTOR_BEAN)
	public ThreadPoolTaskExecutor pooledExecutor(@Value("${" + AufJmsProperties.POOL_SIZE + ":"
			+ AufJmsProperties.POOL_SIZE_DEFAULT + "}") final int poolSize) {
		final var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(poolSize);
		threadPoolTaskExecutor.setMaxPoolSize(poolSize);
		threadPoolTaskExecutor.setQueueCapacity(-1);
		threadPoolTaskExecutor.setThreadNamePrefix("AufJms-Executor-");
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}
