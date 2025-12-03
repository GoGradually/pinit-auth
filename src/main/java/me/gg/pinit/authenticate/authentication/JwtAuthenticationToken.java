package me.gg.pinit.authenticate.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String token;

    public JwtAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.principal = null;
        this.token = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException("인증 상태 설정은 생성자에서만 할 수 있습니다.");
        }
        super.setAuthenticated(false);
    }
}
