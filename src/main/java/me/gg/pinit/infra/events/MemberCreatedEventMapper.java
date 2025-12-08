package me.gg.pinit.infra.events;

import me.gg.pinit.domain.event.MemberCreatedEvent;
import me.gg.pinit.infra.events.dto.MemberCreatedPayload;
import org.springframework.stereotype.Component;

@Component
public class MemberCreatedEventMapper implements AmqpEventMapper<MemberCreatedEvent> {
    @Override
    public Class<MemberCreatedEvent> eventType() {
        return MemberCreatedEvent.class;
    }

    @Override
    public String exchange() {
        return MemberMessaging.DIRECT_EXCHANGE;
    }

    @Override
    public String routingKey() {
        return MemberMessaging.RK_MEMBER_CREATED;
    }

    @Override
    public Object payload(MemberCreatedEvent event) {
        return new MemberCreatedPayload(event.getMemberId(), event.getNickname());
    }
}
