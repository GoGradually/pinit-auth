package me.gg.pinit.service;

import me.gg.pinit.domain.oidc.Oauth2Provider;

public interface Oauth2ProviderMapper {
    Oauth2Provider get(String providerType);
}
