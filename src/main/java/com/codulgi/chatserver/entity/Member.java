package com.codulgi.chatserver.entity;

import com.codulgi.chatserver.dto.MemberRequestDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

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

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date birthdate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    /* 회원 생성 할때 쓸 것*/
    public Member(MemberRequestDto memberRequest) {

        this.name = memberRequest.getName();
        this.email = memberRequest.getEmail();
        this.password = memberRequest.getPassword();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // 기존에 있는 정보에 추가
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
