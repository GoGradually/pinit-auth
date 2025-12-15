package me.gg.pinit.infrastructure.events.rabbitmq;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
public class RabbitEventListener {
    private final RabbitEventPublisher rabbitEventPublisher;

    public RabbitEventListener(RabbitEventPublisher rabbitEventPublisher) {
        this.rabbitEventPublisher = rabbitEventPublisher;
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void on(RabbitEvent event) {
        rabbitEventPublisher.publish(event);
    }
}
