package me.gg.pinit.domain.oidc;


import java.net.URI;
import java.util.List;

public interface Oauth2Provider {
    List<Oauth2Token> grantToken(OpenIdCommand command);
    Profile getProfile(Oauth2Token token);

    URI getAuthorizationUrl();

    String getRedirectUri();

    String getClientId();
}
