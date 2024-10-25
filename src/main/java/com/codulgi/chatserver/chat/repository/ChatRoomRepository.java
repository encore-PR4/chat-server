package com.codulgi.chatserver.chat.repository;


import com.codulgi.chatserver.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("SELECT c FROM ChatRoom c JOIN c.participants p WHERE p.id = :memberId")
    List<ChatRoom> findChatRoomsByMemberId(@Param("memberId") Integer memberId);
}
