package me.gg.pinit.application.member;

import me.gg.pinit.domain.event.DomainEventPublisher;
import me.gg.pinit.domain.event.MemberCreatedEvent;
import me.gg.pinit.domain.member.Member;
import me.gg.pinit.domain.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final DomainEventPublisher domainEventPublisher;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, DomainEventPublisher domainEventPublisher) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.domainEventPublisher = domainEventPublisher;
    }

    public Member login(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return member;
    }

    @Transactional
    public Member signup(String username, String password, String nickname) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        Member member = new Member(username, passwordEncoder.encode(password));

        memberRepository.save(member);
        domainEventPublisher.publish(new MemberCreatedEvent(member.getId(), nickname));
        return member;
    }
}
