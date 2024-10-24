package com.codulgi.chatserver.service;

import com.codulgi.chatserver.dto.MemberResponseDto;
import com.codulgi.chatserver.entity.Member;
import com.codulgi.chatserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /* 로그인 */
    public ResponseEntity<?> login(String email, String password){

        /* 이메일로 사용자 찾기 */
        Optional<Member> findMember = memberRepository.findByEmail(email);

        /* 찾는 이메일이 있는지 없는지 검사 */
        if(findMember.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입 되지 않은 이메일입니다.");
        }

        /* 찾은 사용자의 비밀번호와 입력 받은 비밀번호 검사 */
        if (! findMember.get().getPassword().equals(password)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("옳바르지 않은 비밀번호입니다.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 로그인되었습니다.");
    }
}
