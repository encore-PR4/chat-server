package com.codulgi.chatserver.controller;

import com.codulgi.chatserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /* 로그인 */
    @GetMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password) {
        return memberService.login(email, password);

    }

    /* 회원가입 */

}
