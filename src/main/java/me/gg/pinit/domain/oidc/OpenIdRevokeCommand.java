package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenIdRevokeCommand extends OpenIdCommand {
    private final String accessToken;

    public OpenIdRevokeCommand(String accessToken) {
        super("delete");
        this.accessToken = accessToken;
    }

    @Override
    public List<Oauth2Token> execute(Oauth2Provider provider) {
        return provider.grantToken(this);
    }
}
