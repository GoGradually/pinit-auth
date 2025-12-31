package me.gg.pinit.interfaces.oauth2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.gg.pinit.application.oauth2.Oauth2ProviderMapper;
import me.gg.pinit.application.oauth2.Oauth2Service;
import me.gg.pinit.domain.member.Member;
import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import me.gg.pinit.infrastructure.jwt.TokenCookieFactory;
import me.gg.pinit.interfaces.member.dto.LoginResponse;
import me.gg.pinit.interfaces.oauth2.dto.OauthLoginSetting;
import me.gg.pinit.interfaces.oauth2.dto.SocialLoginResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/v0")
@Tag(name = "소셜 로그인", description = "외부 OAuth2 공급자(네이버) 로그인 흐름")
public class Oauth2ControllerV0 {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final Oauth2ProviderMapper oauth2ProviderMapper;
    private final TokenCookieFactory tokenCookieFactory;
    private final String oauthCallbackBaseUrl;

    public Oauth2ControllerV0(JwtTokenProvider jwtTokenProvider,
                              Oauth2Service oauth2Service,
                              Oauth2ProviderMapper oauth2ProviderMapper,
                              TokenCookieFactory tokenCookieFactory,
                              @Value("${app.frontend-base-url}") String oauthCallbackBaseUrl) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauth2Service = oauth2Service;
        this.oauth2ProviderMapper = oauth2ProviderMapper;
        this.tokenCookieFactory = tokenCookieFactory;
        this.oauthCallbackBaseUrl = oauthCallbackBaseUrl;
    }

    @GetMapping("/login/oauth2/authorize/{provider}")
    @Operation(
            summary = "소셜 로그인 인가 요청",
            description = "provider에 맞는 인가 URL로 302 리다이렉트합니다.",
            parameters = {
                    @Parameter(name = "provider", in = ParameterIn.PATH, description = "소셜 로그인 공급자", example = "naver", required = true)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "외부 인가 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "500", description = "미지원 provider 등 서버 오류")
    })
    public ResponseEntity<Void> authorize(@PathVariable String provider, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        String state = oauth2Service.generateState(sessionId);

        OauthLoginSetting loginSetting = buildOauthLoginSetting(state, provider, request);
        String authorizationUri = UriComponentsBuilder.fromUri(oauth2Service.getAuthorizationUri(provider, state))
                .queryParam("response_type", loginSetting.getResponse_type())
                .queryParam("client_id", loginSetting.getClient_id())
                .queryParam("redirect_uri", loginSetting.getRedirect_uri())
                .queryParam("state", loginSetting.getState())
                .build()
                .toUriString();


        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, authorizationUri)
                .build();
    }


    @GetMapping("/login/oauth2/code/{provider}")
    @Operation(
            summary = "소셜 로그인 콜백",
            description = "provider 콜백에서 code/state를 받아 로그인 처리 후 토큰을 반환합니다.",
            parameters = {
                    @Parameter(name = "provider", in = ParameterIn.PATH, description = "소셜 로그인 공급자", example = "naver", required = true),
                    @Parameter(name = "code", in = ParameterIn.QUERY, description = "OAuth2 인가 코드"),
                    @Parameter(name = "state", in = ParameterIn.QUERY, description = "CSRF 방지용 state"),
                    @Parameter(name = "error", in = ParameterIn.QUERY, description = "provider 오류 코드"),
                    @Parameter(name = "error_description", in = ParameterIn.QUERY, description = "provider 오류 상세")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 성공"),
            @ApiResponse(responseCode = "400", description = "provider 오류 응답"),
            @ApiResponse(responseCode = "500", description = "state 검증 실패, 토큰 교환 실패 등 서버 오류")
    })
    public ResponseEntity<LoginResponse> socialLogin(@PathVariable String provider, @ModelAttribute SocialLoginResult socialLoginResult, HttpServletRequest request) {
        if (socialLoginResult.getError() != null) {
            return ResponseEntity.badRequest().build();
        }
        HttpSession session = request.getSession(false);
        String sessionId = session != null ? session.getId() : null;
        Member member = oauth2Service.login(provider, sessionId, socialLoginResult.getCode(), socialLoginResult.getState());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), Collections.emptyList());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookieFactory.refreshTokenCookie(refreshToken).toString()).body(new LoginResponse(accessToken));
    }

    private OauthLoginSetting buildOauthLoginSetting(String state, String provider, HttpServletRequest request) {
        OauthLoginSetting loginSetting = new OauthLoginSetting();
        Oauth2Provider oauth2Provider = oauth2ProviderMapper.get(provider);
        loginSetting.setResponse_type("code");
        loginSetting.setClient_id(oauth2Provider.getClientId());
        loginSetting.setRedirect_uri(resolveRedirectUri(oauth2Provider, provider, request));
        loginSetting.setState(state);
        return loginSetting;
    }

    private String resolveRedirectUri(Oauth2Provider provider, String providerString, HttpServletRequest request) {
        String redirectUri = provider.getRedirectUri();
        String baseUrl = StringUtils.hasText(oauthCallbackBaseUrl) ? oauthCallbackBaseUrl : getBaseUrl(request);
        String replace = redirectUri
                .replace("{baseUrl}", baseUrl)
                .replace("{registrationId}", providerString);
        log.info("Resolved redirect URI: {}", replace);
        return replace;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        String requestUri = request.getRequestURI();
        return requestUrl.substring(0, requestUrl.length() - requestUri.length());
    }
}
