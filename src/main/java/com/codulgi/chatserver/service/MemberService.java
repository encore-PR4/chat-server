package com.codulgi.chatserver.service;

import com.codulgi.chatserver.dto.MemberRequestDto;
import com.codulgi.chatserver.entity.Member;
import com.codulgi.chatserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입 로직
    public Member registerMember(MemberRequestDto memberRequestDto) {
        Member member = new Member(memberRequestDto); // DTO를 이용해 Member 객체 생성
        return memberRepository.save(member); // 데이터베이스에 저장
    }

}
