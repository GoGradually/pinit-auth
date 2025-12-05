package me.gg.pinit.domain.oidc;

import lombok.Getter;

public class Oauth2Token {
    @Getter
    private final String token;
    @Getter
    private final int expiresIn;
    @Getter
    private final String role;
    @Getter
    private final String tokenType;

    public Oauth2Token(String token, String role, int expiresIn, String tokenType) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.role = role;
        this.tokenType = tokenType;
    }

    public static class Role {
        public static final String ACCESS_TOKEN = "access_token";
        public static final String REFRESH_TOKEN = "refresh_token";
    }

    public static class Type {
        public static final String BEARER = "Bearer";
        public static final String MAC = "MAC";
    }
}
