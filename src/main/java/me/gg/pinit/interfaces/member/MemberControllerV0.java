package me.gg.pinit.interfaces.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import me.gg.pinit.application.member.MemberService;
import me.gg.pinit.domain.member.Member;
import me.gg.pinit.infrastructure.jwt.JwtTokenProvider;
import me.gg.pinit.infrastructure.jwt.TokenCookieFactory;
import me.gg.pinit.interfaces.member.dto.LoginRequest;
import me.gg.pinit.interfaces.member.dto.LoginResponse;
import me.gg.pinit.interfaces.member.dto.SignupRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping("/v0")
@Tag(name = "회원/인증", description = "아이디/비밀번호 로그인 및 토큰 관리")
public class MemberControllerV0 {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenCookieFactory tokenCookieFactory;

    public MemberControllerV0(MemberService memberService, JwtTokenProvider jwtTokenProvider, TokenCookieFactory tokenCookieFactory) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenCookieFactory = tokenCookieFactory;
    }

    @PostMapping("/login")
    @Operation(
            summary = "아이디/비밀번호 로그인",
            description = "username, password를 받아 access token을 반환하고 refresh token은 httpOnly 쿠키로 설정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "500", description = "자격 증명 오류 등 서버 오류")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), Collections.emptyList());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookieFactory.refreshTokenCookie(refreshToken).toString())
                .body(new LoginResponse(accessToken));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "로컬 계정 회원가입을 수행합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 완료"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        memberService.signup(signupRequest.getUsername(), signupRequest.getPassword(), signupRequest.getNickname());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "refresh_token 쿠키에 담긴 리프레시 토큰만 검증하여 새로운 access/refresh token을 발급합니다. 액세스 토큰이나 다른 값이 들어있을 경우 401을 반환합니다.",
            parameters = {
                    @Parameter(name = "refresh_token", in = ParameterIn.COOKIE, description = "리프레시 토큰", required = true)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "쿠키 없음 또는 토큰 검증 실패")
    })
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return ResponseEntity.status(401).build();
        }

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        Long memberId = jwtTokenProvider.getMemberId(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, Collections.emptyList());


        return ResponseEntity.ok()
                .body(new LoginResponse(newAccessToken));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "refresh_token 쿠키를 만료시켜 로그아웃 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public ResponseEntity<Void> logout() {
        ResponseCookie expiredCookie = tokenCookieFactory.deleteRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "로그인 확인",
            description = "Bearer 토큰이 유효한지 확인합니다.",
            security = {
                    @SecurityRequirement(name = "bearerAuth")
            }
    )
    public ResponseEntity<Void> checkLogin() {
        return ResponseEntity.ok().build();
    }
}
