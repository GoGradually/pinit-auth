package me.gg.pinit.interfaces.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {
    private final String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    @Schema(description = "발급된 액세스 토큰(JWT)", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    public String getToken() {
        return accessToken;
    }
}
