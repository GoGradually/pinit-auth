package me.gg.pinit.authenticate.provider;

import me.gg.pinit.authenticate.authentication.JwtAuthenticationToken;
import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationProvider  implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof JwtAuthenticationToken)){
            return null;
        }

        String token = (String) authentication.getCredentials();
        if (!jwtTokenProvider.validateAccessToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        Long memberId = jwtTokenProvider.getMemberId(token);
        Collection<? extends GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

        return new JwtAuthenticationToken(memberId, token, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
