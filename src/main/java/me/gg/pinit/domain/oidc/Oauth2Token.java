package me.gg.pinit.domain.oidc;

import lombok.Getter;

public class Oauth2Token {
    @Getter
    private final String token;
    @Getter
    private final int expiresIn;
    @Getter
    private final String tokenType;

    public Oauth2Token(String token, String tokenType, int expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    public static class Type {
        public static final String ACCESS_TOKEN = "access_token";
        public static final String REFRESH_TOKEN = "refresh_token";
    }
}
