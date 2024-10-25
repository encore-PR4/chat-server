package com.codulgi.chatserver.member.controller;

import com.codulgi.chatserver.member.dto.MemberRequestDto;
import com.codulgi.chatserver.member.entity.Member;
import com.codulgi.chatserver.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /* 회원가입 */
    @PostMapping("/join")
    public ResponseEntity<?> registerMember(@RequestBody MemberRequestDto memberRequestDto) {
        return memberService.registerMember(memberRequestDto);
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody MemberRequestDto memberRequestDto,
            HttpSession session) {
        ResponseEntity<?> login = memberService.login(memberRequestDto.getEmail(), memberRequestDto.getPassword(), session);
        log.info("what?");
        log.info(session.getAttribute("loggedInUser").toString());
        return login;
    }

    /* 프로필 이미지 수정 */
    @PostMapping("/{memberId}/update-profile-image")
    public ResponseEntity<?> updateProfileImage(@PathVariable Integer memberId,
                                                @RequestParam("profileImage") MultipartFile profileImage,
                                                HttpSession session) {
        try {
            return memberService.updateProfileImage(memberId, profileImage, session);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 처리 중 오류가 발생했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


}
