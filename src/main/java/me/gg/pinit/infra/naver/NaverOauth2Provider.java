package me.gg.pinit.infra.naver;

import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.domain.oidc.Oauth2Token;
import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.Profile;
import me.gg.pinit.infra.dto.OpenIdTokenResponse;

import java.util.List;
import java.util.Objects;

public class NaverOauth2Provider implements Oauth2Provider {
    private final NaverClient naverClient;

    public NaverOauth2Provider(NaverClient naverClient) {
        this.naverClient = naverClient;
    }


    @Override
    public List<Oauth2Token> grantToken(OpenIdCommand command) {
        OpenIdTokenResponse result = naverClient.getToken(command);
        return Objects.requireNonNull(result).compute();
    }

    @Override
    public Profile getProfile(OpenIdCommand command) {
        return null;
    }
}
