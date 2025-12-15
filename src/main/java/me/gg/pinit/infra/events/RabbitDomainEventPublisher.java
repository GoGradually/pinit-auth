package me.gg.pinit.infra.events;

import me.gg.pinit.domain.event.DomainEvent;
import me.gg.pinit.domain.event.DomainEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RabbitDomainEventPublisher implements DomainEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final Map<Class<? extends DomainEvent>, AmqpEventMapper<? extends DomainEvent>> mappers;

    public RabbitDomainEventPublisher(
            RabbitTemplate rabbitTemplate,
            List<AmqpEventMapper<?>> mapperList
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.mappers = mapperList.stream()
                .collect(Collectors.toMap(
                        this::resolveEventType,
                        m -> m
                ));
    }

    private Class<? extends DomainEvent> resolveEventType(AmqpEventMapper<?> mapper) {
        return mapper.eventType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(DomainEvent event) {
        AmqpEventMapper<DomainEvent> mapper = (AmqpEventMapper<DomainEvent>) mappers.get(event.getClass());
        if (mapper == null) {
            return;
        }

        String exchange = mapper.exchange();
        String routingKey = mapper.routingKey();
        Object payload = mapper.payload(event);

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
