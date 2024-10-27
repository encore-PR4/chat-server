package com.codulgi.chatserver.globals;

import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.chat.service.ChatRoomService;
import com.codulgi.chatserver.member.entity.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class WebController {

    private ChatRoomService chatRoomService;

    @RequestMapping("/")
    public String index(HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/chat";
        } else {
            return "login";
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

    @RequestMapping("/cms")
    public String coco(HttpSession session, Model model) {
        // 세션에서 사용자 정보를 가져옴
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/";  // 로그인 페이지로 리디렉션
        }


        // 모델에 사용자 정보와 채팅방 목록 추가
        model.addAttribute("loggedInUser", loggedInUser);

        return "main";  // main 페이지로 이동
    }



}
