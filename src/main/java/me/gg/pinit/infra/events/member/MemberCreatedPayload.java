package me.gg.pinit.infra.events.member;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import me.gg.pinit.infra.events.outbox.Outbox;

import java.time.LocalDateTime;

@Getter
@Entity
@DiscriminatorValue("MemberCreatedPayload")
public class MemberCreatedPayload extends Outbox {
    Long memberId;
    String nickname;
    LocalDateTime occurredAt;

    protected MemberCreatedPayload() {
    }

    public MemberCreatedPayload(Long memberId, String nickname, LocalDateTime occurredAt) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.occurredAt = occurredAt;
    }

    @Override
    public String exchange() {
        return MemberMessaging.DIRECT_EXCHANGE;
    }

    @Override
    public String routingKey() {
        return MemberMessaging.RK_MEMBER_CREATED;
    }
}
