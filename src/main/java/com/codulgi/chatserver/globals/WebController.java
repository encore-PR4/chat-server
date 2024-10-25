package com.codulgi.chatserver.globals;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping("/")
    public String index(HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/chat";
        } else {
            return "index";
        }
    }

    @RequestMapping("/chat")
    public String chat(HttpSession session, Model model) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";
        }
        model.addAttribute("loggedInUser", loggedInUser);  // 세션의 사용자 정보를 모델에 추가
        return "chat";
    }


}
