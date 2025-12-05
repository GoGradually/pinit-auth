package me.gg.pinit.domain.oidc;

import lombok.Getter;

public class Oauth2Token {
    @Getter
    private String token;
    @Getter
    private Long expiresIn;
    @Getter
    private String tokenType;
}
