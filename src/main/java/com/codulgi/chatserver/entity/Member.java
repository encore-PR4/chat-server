package com.codulgi.chatserver.entity;

import com.codulgi.chatserver.dto.MemberRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    /* 회원 생성 할때 쓸꺼*/
    public Member(MemberRequestDto memberRequest) {

        this.name = memberRequest.getName();
        this.email = memberRequest.getEmail();
        this.password = memberRequest.getPassword();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
