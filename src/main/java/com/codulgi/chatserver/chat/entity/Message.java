package com.codulgi.chatserver.chat.entity;

import com.codulgi.chatserver.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonBackReference  // 순환 참조 방지
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    @JsonBackReference  // 순환 참조 방지
    private ChatRoom chatRoom;

    private LocalDateTime timestamp;

    public Message(String content, Member sender, ChatRoom chatRoom) {
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.timestamp = LocalDateTime.now();
    }

    public Message() {
    }
}
