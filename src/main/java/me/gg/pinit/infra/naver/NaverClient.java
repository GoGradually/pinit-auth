package me.gg.pinit.infra.naver;

import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.infra.dto.OpenIdTokenRequest;
import me.gg.pinit.infra.dto.OpenIdTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class NaverClient {
    private static final String OAUTH_2_0_TOKEN = "/oauth2.0/token";
    private final RestClient restClient;
    private final NaverRegistrationProperties naverRegistrationProperties;

    public NaverClient(NaverRegistrationProperties naverRegistrationProperties) {
        restClient = RestClient.builder()
                .baseUrl("https://nid.naver.com").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.naverRegistrationProperties = naverRegistrationProperties;
    }

    public OpenIdTokenResponse getToken(OpenIdCommand command) {
        return restClient.post()
                .uri(OAUTH_2_0_TOKEN)
                .body(OpenIdTokenRequest.from(
                        command,
                        naverRegistrationProperties.getClientId(),
                        naverRegistrationProperties.getClientSecret(),
                        naverRegistrationProperties.getProvider()))
                .retrieve()
                .body(OpenIdTokenResponse.class);
    }

}
