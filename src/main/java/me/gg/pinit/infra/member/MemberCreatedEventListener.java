package me.gg.pinit.infra.member;

import me.gg.pinit.domain.event.MemberCreatedEvent;
import me.gg.pinit.infra.events.outbox.OutboxService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberCreatedEventListener {

    private final OutboxService outboxService;

    public MemberCreatedEventListener(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @EventListener
    public void on(MemberCreatedEvent event) {
        MemberCreatedPayload outbox = new MemberCreatedPayload(event.getMemberId(), event.getNickname(), LocalDateTime.now());
        outboxService.save(outbox);
    }
}
