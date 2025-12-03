package me.gg.pinit.controller;

import me.gg.pinit.controller.dto.LoginRequest;
import me.gg.pinit.controller.dto.LoginResponse;
import me.gg.pinit.controller.dto.SignupRequest;
import me.gg.pinit.domain.Member;
import me.gg.pinit.infra.JwtTokenProvider;
import me.gg.pinit.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());

        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), Collections.emptyList());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, getRefreshTokenCookie(refreshToken).toString())
                .body(new LoginResponse(accessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        memberService.signup(signupRequest.getUsername(), signupRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Void> checkLogin() {
        return ResponseEntity.ok().build();
    }

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .build();
    }
}
