package me.gg.pinit.infra.events.member;

import me.gg.pinit.domain.event.MemberCreatedEvent;
import me.gg.pinit.infra.events.outbox.OutboxService;
import me.gg.pinit.infra.events.rabbitmq.RabbitEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberCreatedEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxService outboxService;

    public MemberCreatedEventListener(ApplicationEventPublisher applicationEventPublisher, OutboxService outboxService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.outboxService = outboxService;
    }

    @EventListener
    public void on(MemberCreatedEvent event) {
        MemberCreatedPayload outbox = new MemberCreatedPayload(event.getMemberId(), event.getNickname(), LocalDateTime.now());
        outboxService.save(outbox);
        applicationEventPublisher.publishEvent(new RabbitEvent(outbox.getId()));
    }
}
