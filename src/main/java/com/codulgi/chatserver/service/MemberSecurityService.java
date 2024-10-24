package com.codulgi.chatserver.service;

import com.codulgi.chatserver.entity.Member;
import com.codulgi.chatserver.repository.MemberRepository;
import com.codulgi.chatserver.repository.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements MemberDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDetails loadMemberByName(String name) throws NameNotFoundException {
        Optional<Member> member = this.memberRepository.findByName(name);
        if (member.isEmpty()) {
            throw new NameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Member member = member.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(name)) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAythority(MemberRole.USER.getValue()));
        }
        return new Member(member.getName(), member.getPassword(), authorities);
    }
}
