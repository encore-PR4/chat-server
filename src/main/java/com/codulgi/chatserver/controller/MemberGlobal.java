package com.codulgi.chatserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MemberGlobal {

    @RequestMapping("/member/register")
    public String index() {
        return "/register.html";
    }
}
