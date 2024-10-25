package com.codulgi.chatserver.chat.service;

import com.codulgi.chatserver.chat.dto.MessageRequest;
import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.repository.ChatRoomRepository;
import com.codulgi.chatserver.chat.repository.MessageRepository;
import com.codulgi.chatserver.globals.kafka.service.KafkaService;
import com.codulgi.chatserver.member.entity.Member;
import com.codulgi.chatserver.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final KafkaService kafkaService;

    /* 메시지 전송 */
    @Transactional
    public ResponseEntity<Message> sendMessage(MessageRequest messageRequest, HttpServletRequest httpServletRequest) {
        // 1. 채팅방이 존재하는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequest.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("해당 채팅방을 찾을 수 없습니다."));

        // 2. 발신자 확인
        Member sender = memberRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new RuntimeException("해당 발신자를 찾을 수 없습니다."));

        // 3. 메시지 생성 및 저장
        Message message = new Message(messageRequest.getContent(), sender, chatRoom);
        messageRepository.save(message); // 메시지를 저장

        // 4. Kafka로 메시지 전송
        try {
            kafkaService.sendMessageToKafka(message, httpServletRequest); // 수정된 메서드
        } catch (Exception e) {
            // Kafka 전송 실패 시 예외 처리
            System.err.println("Kafka 전송 실패: " + e.getMessage());
        }

        // 5. 메시지 응답으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    /* 특정 채팅방의 메시지 조회 */
    public List<Message> getMessagesByChatRoom(Integer chatRoomId) {
        return messageRepository.findAllByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }
}
