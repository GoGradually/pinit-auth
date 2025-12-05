package me.gg.pinit.service;

import me.gg.pinit.domain.member.Member;
import me.gg.pinit.domain.member.OauthAccount;
import me.gg.pinit.domain.member.OauthAccountId;
import me.gg.pinit.domain.member.OauthAccountRepository;
import me.gg.pinit.domain.oidc.Oauth2Provider;
import me.gg.pinit.domain.oidc.Oauth2Token;
import me.gg.pinit.domain.oidc.OpenIdPublishCommand;
import me.gg.pinit.domain.oidc.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class Oauth2Service {
    private final Oauth2ProviderMapper oauth2ProviderMapper;
    private final OauthAccountRepository oauthAccountRepository;
    private final MemberService memberService;
    private final Oauth2StateService oauth2StateService;

    public Oauth2Service(Oauth2ProviderMapper oauth2ProviderMapper, OauthAccountRepository oauthAccountRepository, MemberService memberService, Oauth2StateService oauth2StateService) {
        this.oauth2ProviderMapper = oauth2ProviderMapper;
        this.oauthAccountRepository = oauthAccountRepository;
        this.memberService = memberService;
        this.oauth2StateService = oauth2StateService;
    }

    // Todo 리다이렉트 준비 로직 추가

    public String generateState(String sessionId) {
        return oauth2StateService.createAndStoreState(sessionId);
    }

    public URI getAuthorizationUri(String provider, String state) {
        Oauth2Provider oauth2Provider = oauth2ProviderMapper.get(provider);
        return oauth2Provider.getAuthorizationUrl();
    }


    @Transactional
    public Member login(String provider, String currentSessionId, String code, String state) {
        Oauth2Provider oauth2Provider = oauth2ProviderMapper.get(provider);

        oauth2StateService.verifyAndConsumeState(state, currentSessionId);

        List<Oauth2Token> tokens = oauth2Provider.grantToken(new OpenIdPublishCommand(code, state));
        Oauth2Token accessToken = tokens.stream().filter(token -> token.getRole().equals(Oauth2Token.Role.ACCESS_TOKEN)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No access token found"));

        Profile profile = oauth2Provider.getProfile(accessToken);

        OauthAccountId oauthAccountId = new OauthAccountId("", profile.getId());

        OauthAccount oauthAccount = oauthAccountRepository.findById(oauthAccountId)
                .orElseGet(() -> signup(oauthAccountId));

        return oauthAccount.getMember();
    }

    private OauthAccount signup(OauthAccountId oauthAccountId) {
        Member member = memberService.signup(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        member.setSocialLogin(true);
        OauthAccount oauthAccount = new OauthAccount(oauthAccountId, member);

        // TODO 계정 생성 이벤트 발행 -> 옆동네 Profile 생성 및 별명 생성 처리

        return oauthAccountRepository.save(oauthAccount);
    }
}
