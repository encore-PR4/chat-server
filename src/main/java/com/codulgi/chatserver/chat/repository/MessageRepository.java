package com.codulgi.chatserver.chat.repository;

import com.codulgi.chatserver.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    // 채팅방 ID로 메시지 목록 조회 (타임스탬프 순)
    List<Message> findAllByChatRoomIdOrderByTimestampAsc(Integer chatRoomId);

    List<Message> findByChatRoomId(Integer chatRoomId);
}

