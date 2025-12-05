package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenIdRefreshCommand extends OpenIdCommand {
    private final String refreshToken;

    public OpenIdRefreshCommand(String refreshToken) {
        super("refresh_token");
        this.refreshToken = refreshToken;
    }

    @Override
    public List<Oauth2Token> execute(Oauth2Provider provider) {
        return provider.grantToken(this);
    }
}
