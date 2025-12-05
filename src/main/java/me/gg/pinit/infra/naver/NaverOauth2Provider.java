package me.gg.pinit.infra.naver;

import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.domain.oidc.Oauth2Token;
import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.Profile;
import me.gg.pinit.infra.Provider;
import me.gg.pinit.infra.dto.OpenIdProfileResponse;
import me.gg.pinit.infra.dto.OpenIdTokenRequest;
import me.gg.pinit.infra.dto.OpenIdTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Provider("naver")
public class NaverOauth2Provider implements Oauth2Provider {
    public static final String NAVER_ID_URL = "https://nid.naver.com";
    public static final String NAVER_APP_URL = "https://openapi.naver.com";
    private static final String OAUTH_2_0_TOKEN = "/oauth2.0/token";
    public static final String OPENID_PROFILE = "/v1/nid/me";
    private static final String OAUTH_2_0_AUTHORIZE = "/oauth2.0/authorize";
    private final RestClient loginClient;
    private final RestClient profileClient;
    private final NaverRegistrationProperties naverRegistrationProperties;

    public NaverOauth2Provider(NaverRegistrationProperties naverRegistrationProperties) {
        loginClient = RestClient.builder()
                .baseUrl(NAVER_ID_URL).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        profileClient = RestClient.builder()
                .baseUrl(NAVER_APP_URL).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.naverRegistrationProperties = naverRegistrationProperties;
    }

    @Override
    public URI getAuthorizationUrl() {
        return URI.create(NAVER_ID_URL + OAUTH_2_0_AUTHORIZE);
    }

    @Override
    public String getRedirectUri() {
        return naverRegistrationProperties.getRedirectUri();
    }

    @Override
    public String getClientId() {
        return naverRegistrationProperties.getClientId();
    }

    @Override
    public List<Oauth2Token> grantToken(OpenIdCommand command) {
        return Objects.requireNonNull(loginClient.post()
                        .uri(OAUTH_2_0_TOKEN)
                        .body(OpenIdTokenRequest.from(
                                command,
                                naverRegistrationProperties.getClientId(),
                                naverRegistrationProperties.getClientSecret(),
                                naverRegistrationProperties.getProvider()))
                        .retrieve()
                        .body(OpenIdTokenResponse.class))
                .compute();
    }

    @Override
    public Profile getProfile(Oauth2Token accessToken) {
        if (!accessToken.getRole().equals(Oauth2Token.Role.ACCESS_TOKEN))
            throw new IllegalArgumentException("AccessToken이 아닙니다.");
        OpenIdProfileResponse result = profileClient.get()
                .uri(OPENID_PROFILE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken())
                .retrieve()
                .body(OpenIdProfileResponse.class);
        Objects.requireNonNull(result);
        return new Profile(result.getSub(), result.getNickname());
    }
}
