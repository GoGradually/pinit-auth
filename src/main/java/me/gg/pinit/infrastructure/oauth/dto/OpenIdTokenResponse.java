package me.gg.pinit.infrastructure.oauth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.gg.pinit.domain.oidc.Oauth2Token;
import me.gg.pinit.infrastructure.oauth.exception.OIDCException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class OpenIdTokenResponse {
    private String refresh_token;
    private String access_token;
    private String token_type;
    private String result;
    private int expires_in;
    private String error;
    private String error_description;

    public List<Oauth2Token> compute() {
        if (error != null && !error.isEmpty()) {
            throw new OIDCException(error + ": " + error_description);
        }
        List<Oauth2Token> tokens = new ArrayList<>();
        if (refresh_token != null && !refresh_token.isEmpty()) {
            tokens.add(new Oauth2Token(refresh_token, Oauth2Token.Role.REFRESH_TOKEN, expires_in, token_type));
        }
        if (access_token != null && !access_token.isEmpty()) {
            tokens.add(new Oauth2Token(access_token, Oauth2Token.Role.ACCESS_TOKEN, expires_in, token_type));
        }
        return tokens;
    }
}
