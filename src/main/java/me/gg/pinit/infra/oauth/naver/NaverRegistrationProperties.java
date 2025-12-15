package me.gg.pinit.infra.oauth.naver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.client.registration.naver")
@Getter
@Setter
public class NaverRegistrationProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String provider;
}
