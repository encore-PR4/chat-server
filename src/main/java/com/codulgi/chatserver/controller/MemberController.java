package com.codulgi.chatserver.controller;

import com.codulgi.chatserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /* 로그인 */
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
    /* 회원가입 */

}
