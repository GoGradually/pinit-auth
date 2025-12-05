package me.gg.pinit.infra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenIdProfileResponse {
    private String sub;
    private String nickname;

    public OpenIdProfileResponse(String sub, String nickname) {
        this.sub = sub;
        this.nickname = nickname;
    }
}
