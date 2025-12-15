package me.gg.pinit.infra.events.outbox;

import me.gg.pinit.infra.events.rabbitmq.RabbitEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OutboxService(OutboxRepository outboxRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.outboxRepository = outboxRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void save(Outbox outbox) {
        outboxRepository.save(outbox);
        applicationEventPublisher.publishEvent(new RabbitEvent(outbox.getId()));
    }

    @Transactional
    public void delete(Long eventId) {
        outboxRepository.deleteById(eventId);
    }

    @Transactional
    public Outbox findById(Long eventId) {
        return outboxRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + eventId));
    }
}
