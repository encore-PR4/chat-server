package com.codulgi.chatserver.chat.controller;

import com.codulgi.chatserver.chat.dto.MessageRequest;
import com.codulgi.chatserver.chat.dto.MessageResponse;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.service.MessageService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ServletRequest httpServletRequest;

    /* 메시지 전송 */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest messageRequest, HttpServletRequest httpServletRequest) {
        // 서비스 계층에서 MessageResponse DTO로 변환된 메시지를 받음
        MessageResponse messageDto = messageService.sendMessage(messageRequest, httpServletRequest);

        // WebSocket으로 전송
        messagingTemplate.convertAndSend("/topic/chat-room/" + messageRequest.getChatRoomId(), messageDto);

        return ResponseEntity.ok(messageDto);
    }


    /* 특정 채팅방의 메시지 조회 */
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<?> getChatRoomMessages(@PathVariable Integer chatRoomId) {
        try {
            // 메시지를 가져옴
            List<Message> messages = messageService.getMessagesByChatRoom(chatRoomId);

            // List<Message>를 List<MessageResponse>로 변환
            List<MessageResponse> messageResponses = messages.stream()
                    .map(MessageResponse::new)  // 각 Message 객체를 MessageResponse로 변환
                    .collect(Collectors.toList());  // 결과를 리스트로 수집

            // 변환된 메시지 리스트를 반환
            return ResponseEntity.ok(messageResponses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

