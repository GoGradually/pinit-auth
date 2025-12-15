package me.gg.pinit.interfaces.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @Schema(description = "아이디", example = "newuser")
    private String username;
    @Schema(description = "비밀번호", example = "password1234!")
    private String password;
    @Schema(description = "별명", example = "열정")
    private String nickname;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
