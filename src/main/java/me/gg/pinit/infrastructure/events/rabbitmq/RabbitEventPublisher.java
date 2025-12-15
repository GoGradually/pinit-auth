package me.gg.pinit.infrastructure.events.rabbitmq;

import me.gg.pinit.infrastructure.events.outbox.Outbox;
import me.gg.pinit.infrastructure.events.outbox.OutboxService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RabbitEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final OutboxService outboxService;

    public RabbitEventPublisher(
            RabbitTemplate rabbitTemplate,
            OutboxService outboxService) {
        this.rabbitTemplate = rabbitTemplate;
        this.outboxService = outboxService;
    }


    @Transactional
    @Retryable(
            includes = Exception.class,
            maxRetries = 4,
            delay = 100,
            multiplier = 2.0,
            maxDelay = 1000,
            jitter = 10
    )
    public void publish(RabbitEvent event) {
        Outbox payload = outboxService.findById(event.id());

        String exchange = payload.exchange();
        String routingKey = payload.routingKey();

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        outboxService.delete(event.id());
    }
}
