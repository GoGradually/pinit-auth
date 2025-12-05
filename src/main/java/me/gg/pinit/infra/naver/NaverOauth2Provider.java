package me.gg.pinit.infra.naver;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Slf4j
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
                .baseUrl(NAVER_ID_URL)
                .build();
        profileClient = RestClient.builder()
                .baseUrl(NAVER_APP_URL)
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
        OpenIdTokenRequest request = OpenIdTokenRequest.from(
                command,
                naverRegistrationProperties.getClientId(),
                naverRegistrationProperties.getClientSecret(),
                naverRegistrationProperties.getProvider());
        MultiValueMap<String, String> formData = getMultiValueParams(request);

        return Objects.requireNonNull(loginClient.post()
                        .uri(OAUTH_2_0_TOKEN)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(formData)
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
        return result.getResponse();
    }

    private MultiValueMap<String, String> getMultiValueParams(OpenIdTokenRequest request) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", request.getGrant_type());
        formData.add("client_id", request.getClient_id());
        formData.add("client_secret", request.getClient_secret());
        if (request.getCode() != null) formData.add("code", request.getCode());
        if (request.getState() != null) formData.add("state", request.getState());
        if (request.getRefresh_token() != null) formData.add("refresh_token", request.getRefresh_token());
        if (request.getAccess_token() != null) formData.add("access_token", request.getAccess_token());
        if (request.getService_provider() != null) formData.add("service_provider", request.getService_provider());
        return formData;
    }
}
