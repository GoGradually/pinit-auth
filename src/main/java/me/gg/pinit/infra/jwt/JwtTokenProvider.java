package me.gg.pinit.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtTokenProvider {
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String issuer;
    private final Duration accessTokenValidity;
    private final Duration refreshTokenValidity;

    public JwtTokenProvider(PrivateKey privateKey, PublicKey publicKey, String issuer, Duration accessTokenValidity, Duration refreshTokenValidity) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.issuer = issuer;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(Long memberId, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity.toMillis());

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(String.valueOf(memberId))
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .claim(CLAIM_ROLES, roles)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity.toMillis());

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(String.valueOf(memberId))
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private JwtParser buildParser() {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build();
    }

    public Claims parse(String token){
        return buildParser()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateAccessToken(String token) {
        return validateTokenOfType(token, TYPE_ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return validateTokenOfType(token, TYPE_REFRESH);
    }

    private boolean validateTokenOfType(String token, String expectedType) {
        try {
            Claims claims = parse(token);
            String tokenType = claims.get(CLAIM_TYPE, String.class);
            if (!expectedType.equals(tokenType)) {
                return false;
            }
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Long getMemberId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        Claims claims = parse(token);
        String roles = claims.get(CLAIM_ROLES, String.class);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
