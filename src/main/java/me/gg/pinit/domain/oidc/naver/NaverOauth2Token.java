package me.gg.pinit.domain.oidc.naver;

import lombok.Getter;
import me.gg.pinit.domain.oidc.Oauth2Token;

public class NaverOauth2Token extends Oauth2Token {
    @Getter
    private String accessToken;
    @Getter
    private String refreshToken;
    @Getter
    private Long expiresIn;
    @Getter
    private String tokenType;

    public NaverOauth2Token(String accessToken, String refreshToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

}
