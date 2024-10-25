package com.codulgi.chatserver.member.entity;

import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.member.dto.MemberRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> sentMessages;

    @ManyToMany(mappedBy = "participants")
    @JsonBackReference
    private List<ChatRoom> chatRooms;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 프로필 이미지 저장 (바이너리)
    @Setter
    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    /* 회원 생성 할때 쓸 것 */
    public Member(MemberRequestDto memberRequest) {
        this.name = memberRequest.getName();
        this.email = memberRequest.getEmail();
        this.password = memberRequest.getPassword();
        this.birthdate = memberRequest.getBirthdate();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMember(MemberRequestDto memberRequestDto) {
        this.email = memberRequestDto.getEmail();
        this.name = memberRequestDto.getName();
        this.birthdate = memberRequestDto.getBirthdate();
        this.updatedAt = LocalDateTime.now();
    }

    public Member() {
    }
}

