package me.gg.pinit.infrastructure.jwt;

import me.gg.pinit.infrastructure.jwt.config.CookieProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Builds token cookies using the configured domain/sameSite/secure settings.
 */
@Component
public class TokenCookieFactory {
    private final CookieProperties cookieProperties;

    public TokenCookieFactory(CookieProperties cookieProperties) {
        this.cookieProperties = cookieProperties;
    }

    public ResponseCookie refreshTokenCookie(String refreshToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .secure(cookieProperties.isSecure());

        if (StringUtils.hasText(cookieProperties.getDomain())) {
            builder.domain(cookieProperties.getDomain());
        }
        if (StringUtils.hasText(cookieProperties.getSameSite())) {
            builder.sameSite(cookieProperties.getSameSite());
        }

        return builder.build();
    }
}
