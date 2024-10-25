package com.codulgi.chatserver.chat.controller;

import com.codulgi.chatserver.chat.dto.MessageRequest;
import com.codulgi.chatserver.chat.dto.MessageResponse;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.service.MessageService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ServletRequest httpServletRequest;

    /* 메시지 전송 */
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest httpServletRequest) {
        Message message = messageService.sendMessage(messageRequest, httpServletRequest).getBody();

        // 메시지를 DTO로 변환하여 WebSocket으로 전송
        MessageResponse messageDto = new MessageResponse(message);
        messagingTemplate.convertAndSend("/topic/chat-room/" + messageRequest.getChatRoomId(), messageDto);

        return ResponseEntity.ok(message);
    }

    /* 특정 채팅방의 메시지 조회 */
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<Message>> getMessagesByChatRoom(@PathVariable Integer chatRoomId) {
        List<Message> messages = messageService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}

