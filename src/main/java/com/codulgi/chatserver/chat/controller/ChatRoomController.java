package com.codulgi.chatserver.chat.controller;

import com.codulgi.chatserver.chat.dto.ChatRoomRequest;
import com.codulgi.chatserver.chat.dto.MessageResponse;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.service.ChatRoomService;
import com.codulgi.chatserver.chat.service.MessageService;
import com.codulgi.chatserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberService memberService;

    /* 채팅방 생성 */
    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomRequest request) {
        try {

            log.info("request: {}", request.toString());
            // 채팅방 생성 결과를 담은 ResponseEntity를 반환
            ResponseEntity<?> response = chatRoomService.createRoom(request);
            // 웹소켓으로 채팅방 생성 정보를 전달
            messagingTemplate.convertAndSend("/topic/chat-room", response.getBody());
            return response;
        } catch (RuntimeException e) {
            log.error("채팅방 생성 중 오류 발생: {}", e.getMessage());
            // 에러 응답을 JSON으로 반환
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "채팅방 생성 중 오류 발생: " + e.getMessage()));
        }
    }


    /* 모든 채팅방 조회 */
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        return chatRoomService.getAllRooms();
    }

    /* 내가 가입된 채팅방 조회 */
    @GetMapping("/my-rooms")
    public ResponseEntity<?> getMyRooms(@RequestParam Integer id) {
        try {
            return chatRoomService.getMyRooms(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* 특정 채팅방 제목 조회 */
    @GetMapping("/{chatRoomId}/title")
    public ResponseEntity<?> getChatRoomTitle(@PathVariable Integer chatRoomId) {
        try {
            String title = chatRoomService.getChatRoomTitle(chatRoomId);
            // JSON 형식으로 반환
            return ResponseEntity.ok(Collections.singletonMap("title", title));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* 특정 채팅방 메시지 조회 */
    @GetMapping("/{chatRoomId}/messages")
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

    // 채팅방에 사용자 초대
    @GetMapping("/invite")
    public ResponseEntity<?> inviteUserToChatRoom(@RequestParam Integer memberId, @RequestParam Integer chatRoomId) {
        try {
            ResponseEntity<?> responseEntity = chatRoomService.inviteUser(chatRoomId, memberId);
            return responseEntity;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 초대 실패: " + e.getMessage());
        }
    }

    /* 채팅방에 가입된 사용자 정보 불러오기 */
    @GetMapping("/chatroom/participant")
    public ResponseEntity<?> getChatRoomParticipants(@RequestParam Integer chatRoomId) {
        return chatRoomService.getRoomInParticipantId(chatRoomId);
    }

    /* 이메일로 사용자 검색 */
    @GetMapping("/member/search")
    public ResponseEntity<?> searchUsersByEmail(@RequestParam String email) {
        log.info("검색: {}", email);
        return memberService.searchUsersByEmail(email);
    }

    /* 채팅방 삭제 */
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer roomId) {
        try {
            chatRoomService.deleteRoom(roomId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("채팅방이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
