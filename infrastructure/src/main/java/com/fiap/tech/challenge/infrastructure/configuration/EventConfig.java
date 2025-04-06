package com.fiap.tech.challenge.infrastructure.configuration;

import com.fiap.tech.challenge.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fiap.tech.challenge.infrastructure.configuration.properties.amqp.QueueProperties;
import com.fiap.tech.challenge.infrastructure.services.EventService;
import com.fiap.tech.challenge.infrastructure.services.impl.RabbitEventService;
import com.fiap.tech.challenge.infrastructure.services.local.InMemoryEventService;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EventConfig {

    @Bean
    @VideoCreatedQueue
    @Profile({"development"})
    public EventService localVideoCreatedEventService() {
        return new InMemoryEventService();
    }

    @Bean
    @VideoCreatedQueue
    @ConditionalOnMissingBean
    public EventService videoCreatedEventService(
            @VideoCreatedQueue final QueueProperties props,
            final RabbitOperations ops
    ) {
        return new RabbitEventService(props.getExchange(), props.getRoutingKey(), ops);
    }
}
