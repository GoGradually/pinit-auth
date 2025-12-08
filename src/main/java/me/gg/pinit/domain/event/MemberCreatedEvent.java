package me.gg.pinit.domain.event;

import lombok.Getter;

@Getter
public class MemberCreatedEvent implements DomainEvent {
    private final Long memberId;
    private final String nickname;

    public MemberCreatedEvent(Long memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
