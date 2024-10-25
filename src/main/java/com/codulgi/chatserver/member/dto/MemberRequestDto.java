package com.codulgi.chatserver.member.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRequestDto {
    private int id;
    private String email;
    private String password;
    private String name;
    private LocalDate birthdate;

}
