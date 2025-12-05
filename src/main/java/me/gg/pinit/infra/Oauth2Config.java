package me.gg.pinit.infra;

import me.gg.pinit.infra.naver.NaverClient;
import me.gg.pinit.infra.naver.NaverOauth2Provider;
import me.gg.pinit.infra.naver.NaverRegistrationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Oauth2Config {
    @Bean
    public NaverOauth2Provider naverOauth2Provider(NaverRegistrationProperties naverRegistrationProperties) {
        return new NaverOauth2Provider(naverClient(naverRegistrationProperties));
    }

    @Bean
    public NaverClient naverClient(NaverRegistrationProperties naverRegistrationProperties) {
        return new NaverClient(naverRegistrationProperties);
    }
}
