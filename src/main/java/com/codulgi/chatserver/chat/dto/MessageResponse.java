package com.codulgi.chatserver.chat.dto;

import com.codulgi.chatserver.chat.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Integer id;
    private String content;
    private Integer senderId;
    private String senderName;
    private byte[] senderAvatar;
    private LocalDateTime timestamp;

    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getName();
        this.senderAvatar = message.getSender().getProfileImage();
        this.timestamp = message.getTimestamp();

    }
}
