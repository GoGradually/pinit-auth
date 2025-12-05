package me.gg.pinit.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginResult {
    @Schema(description = "OAuth2 인가 코드")
    private String code;
    @Schema(description = "CSRF 방지용 state 값")
    private String state;
    @Schema(description = "provider가 전달하는 오류 코드")
    private String error;
    @Schema(description = "provider가 전달하는 오류 상세 메시지")
    private String error_description;
}
