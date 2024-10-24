package com.codulgi.chatserver.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MemberRequestDto {
    private int id;
    private String email;
    private String password;
    private String name;
    private Date birthdate;

}
