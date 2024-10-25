package com.codulgi.chatserver.chat.dto;

import lombok.Data;

@Data
public class MessageRequest {

    private String content;
    private Integer senderId;  // 발신자 ID
    private Integer chatRoomId;  // 채팅방 ID
}

