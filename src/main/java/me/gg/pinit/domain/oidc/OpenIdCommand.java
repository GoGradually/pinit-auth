package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class OpenIdCommand {
    private final String grantType;

    public OpenIdCommand(String grantType) {
        this.grantType = grantType;
    }

    public abstract List<Oauth2Token> execute(Oauth2Provider provider);
}