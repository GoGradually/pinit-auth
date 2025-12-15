package me.gg.pinit.application.event;

import me.gg.pinit.domain.event.DomainEvent;
import me.gg.pinit.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
