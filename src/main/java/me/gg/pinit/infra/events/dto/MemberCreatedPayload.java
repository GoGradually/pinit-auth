package me.gg.pinit.infra.events.dto;


import java.time.LocalDateTime;

public record MemberCreatedPayload(Long memberId, String nickname, LocalDateTime occurredAt) {
}
