package me.gg.pinit.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
