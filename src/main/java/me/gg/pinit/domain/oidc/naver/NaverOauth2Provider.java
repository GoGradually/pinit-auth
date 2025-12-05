package me.gg.pinit.domain.oidc.naver;

import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.Profile;

import java.util.List;

public interface NaverOauth2Provider extends Oauth2Provider {
    List<NaverOauth2Token> grantToken(OpenIdCommand command);
    Profile getProfile(OpenIdCommand command);
}
