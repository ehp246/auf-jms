package me.ehp246.aufjms.core.endpoint;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import me.ehp246.aufjms.api.endpoint.DeadMsgConsumer;
import me.ehp246.aufjms.api.endpoint.ExecutableResolver;
import me.ehp246.aufjms.api.endpoint.InboundEndpoint;
import me.ehp246.aufjms.api.jms.DestinationType;
import me.ehp246.aufjms.api.spi.PropertyResolver;
import me.ehp246.aufjms.core.util.OneUtil;

/**
 *
 * @author Lei Yang
 * @since 1.0
 */
public final class InboundEndpointFactory {
    private final PropertyResolver propertyResolver;
    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    public InboundEndpointFactory(final AutowireCapableBeanFactory autowireCapableBeanFactory,
            final PropertyResolver propertyResolver) {
        super();
        this.autowireCapableBeanFactory = autowireCapableBeanFactory;
        this.propertyResolver = propertyResolver;
    }

    @SuppressWarnings("unchecked")
    public InboundEndpoint newInstance(final Map<String, Object> inboundAttributes, final Set<String> scanPackages,
            final String beanName) {
        final var fromAttribute = (Map<String, Object>) inboundAttributes.get("value");

        return new InboundEndpoint() {
            private final String atName = propertyResolver.resolve(fromAttribute.get("value").toString());
            private final DestinationType atType = (DestinationType) (fromAttribute.get("type"));

            private final InboundEndpoint.From from = new InboundEndpoint.From() {
                private final String selector = propertyResolver.resolve(fromAttribute.get("selector").toString());
                private final Sub sub = new InboundEndpoint.From.Sub() {
                    private final Map<String, Object> sub = (Map<String, Object>) fromAttribute.get("sub");
                    private final String name = propertyResolver.resolve(sub.get("value").toString());

                    @Override
                    public boolean shared() {
                        return (Boolean) (sub.get("shared"));
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public boolean durable() {
                        return (Boolean) (sub.get("durable"));
                    }
                };

                @Override
                public String name() {
                    return atName;
                }

                @Override
                public DestinationType type() {
                    return atType;
                }

                @Override
                public String selector() {
                    return selector;
                }

                @Override
                public Sub sub() {
                    return sub;
                }

            };

            private final int concurrency = Integer
                    .parseInt(propertyResolver.resolve(inboundAttributes.get("concurrency").toString()));
            private final boolean autoStartup = Boolean
                    .parseBoolean(propertyResolver.resolve(inboundAttributes.get("autoStartup").toString()));
            private final String connectionFactory = propertyResolver
                    .resolve(inboundAttributes.get("connectionFactory").toString());
            private final ExecutableResolver resolver = new AutowireCapableExecutableResolver(
                    autowireCapableBeanFactory, DefaultInvokableResolver.registeryFrom(scanPackages));
            private final DeadMsgConsumer deadMsgConsumer = Optional
                    .ofNullable(inboundAttributes.get("deadMsgConsumer").toString()).filter(OneUtil::hasValue)
                    .map(name -> autowireCapableBeanFactory.getBean(name, DeadMsgConsumer.class)).orElse(null);

            @Override
            public From from() {
                return from;
            }

            @Override
            public ExecutableResolver resolver() {
                return resolver;
            }

            @Override
            public int concurrency() {
                return concurrency;
            }

            @Override
            public String name() {
                return beanName;
            }

            @Override
            public boolean autoStartup() {
                return autoStartup;
            }

            @Override
            public String connectionFactory() {
                return connectionFactory;
            }

            @Override
            public DeadMsgConsumer deadMsgConsumer() {
                return deadMsgConsumer;
            }
        };
    }
}
