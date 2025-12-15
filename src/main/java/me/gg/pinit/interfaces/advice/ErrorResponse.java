package me.gg.pinit.interfaces.advice;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "오류 메시지", example = "아이디 또는 비밀번호가 올바르지 않습니다.")
        String message
) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
