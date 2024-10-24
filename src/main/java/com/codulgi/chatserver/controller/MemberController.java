package com.codulgi.chatserver.controller;

import com.codulgi.chatserver.dto.MemberRequestDto;
import com.codulgi.chatserver.entity.Member;
import com.codulgi.chatserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 API
    @PostMapping("/join")
    public ResponseEntity<?> registerMember(@RequestBody MemberRequestDto memberRequestDto) {
        Member newMember = memberService.registerMember(memberRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

}
