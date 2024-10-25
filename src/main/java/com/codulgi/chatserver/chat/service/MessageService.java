package com.codulgi.chatserver.chat.service;

import com.codulgi.chatserver.chat.dto.MessageRequest;
import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.repository.ChatRoomRepository;
import com.codulgi.chatserver.chat.repository.MessageRepository;
import com.codulgi.chatserver.member.entity.Member;
import com.codulgi.chatserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /* 메시지 전송 */
    @Transactional
    public Message sendMessage(MessageRequest messageRequest) {
        // 채팅방이 존재하는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequest.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("해당 채팅방을 찾을 수 없습니다."));

        // 발신자 확인
        Member sender = memberRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new RuntimeException("해당 발신자를 찾을 수 없습니다."));

        // 메시지 생성 및 저장
        Message message = new Message(messageRequest.getContent(), sender, chatRoom);
        return messageRepository.save(message);
    }


    /* 특정 채팅방의 메시지 조회 */
    public List<Message> getMessagesByChatRoom(Integer chatRoomId) {
        return messageRepository.findAllByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }
}
