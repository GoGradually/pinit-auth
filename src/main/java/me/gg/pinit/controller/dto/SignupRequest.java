package me.gg.pinit.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SignupRequest {
    @Schema(description = "아이디", example = "newuser")
    private String username;
    @Schema(description = "비밀번호", example = "password1234!")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
