package com.codulgi.chatserver.member.service;


import com.codulgi.chatserver.member.dto.MemberRequestDto;
import com.codulgi.chatserver.member.dto.MemberResponseDto;
import com.codulgi.chatserver.member.entity.Member;
import com.codulgi.chatserver.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    /* 회원가입 */
    public ResponseEntity<?> registerMember(MemberRequestDto memberRequestDto) {

        /* 이메일 중복 체크 */
        Optional<Member> byEmail = memberRepository.findByEmail(memberRequestDto.getEmail());

        if (byEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 이메일 입니다.");
        }

        /* 사용자 생성 */
        Member member = new Member(memberRequestDto);

        Member newMember = memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

    /**
     * 로그인 기능
     */
    public ResponseEntity<?> login(String email, String password, HttpSession session) {
        /* 이메일로 사용자 찾기 */
        Optional<Member> findMember = memberRepository.findByEmail(email);

        /* 찾는 이메일이 있는지 없는지 검사 */
        if (findMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입되지 않은 이메일입니다.");
        }

        /* 비밀번호 비교 (암호화 없이 단순 문자열 비교) */
        if (!findMember.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 비밀번호입니다.");
        }

        MemberResponseDto.loginMember responseDto = new MemberResponseDto.loginMember(findMember.get());
        session.setAttribute("loggedInUser", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /* 프로필 이미지 수정 */
    public ResponseEntity<?> updateProfileImage(Integer memberId, MultipartFile imageFile, HttpSession session) throws IOException {
        // 사용자 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 이미지 파일이 비었는지 확인
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일이 제공되지 않았습니다.");
        }

        // 이미지 파일을 바이너리 데이터로 변환
        byte[] imageData = imageFile.getBytes();
        member.setProfileImage(imageData);
        memberRepository.save(member);

        // 세션에 업데이트된 사용자 정보 저장
        MemberResponseDto.loginMember responseDto = new MemberResponseDto.loginMember(member);
        session.setAttribute("loggedInUser", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body("프로필 이미지가 성공적으로 수정되었습니다.");
    }

    /* 이메일로 사용자 검색 */
    public ResponseEntity<?> searchUsersByEmail(String email) {
        List<Member> users = memberRepository.searchByEmail(email);

        // 결과가 5개 이상이면 빈 리스트 반환
        if (users.size() > 5) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }

        // MemberResponseDto.chatUser로 변환하여 반환
        List<MemberResponseDto.chatUser> userDtos = users.stream()
                .map(MemberResponseDto.chatUser::new)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }
}
