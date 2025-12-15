package me.gg.pinit.infra.events.member;

import me.gg.pinit.domain.event.MemberCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberCreatedEventListener {

    @EventListener
    public void on(MemberCreatedEvent event) {

    }
}
