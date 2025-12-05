package me.gg.pinit.domain.oidc;

import lombok.Getter;

@Getter
public class Profile {
    private final String id;
    private final String nickname;

    public Profile(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
