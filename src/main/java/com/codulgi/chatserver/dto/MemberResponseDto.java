package com.codulgi.chatserver.dto;

import com.codulgi.chatserver.entity.Member;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberResponseDto {

    @Data
    public static class SaveMember {
        private Integer id;
        private String email;
        private String name;
        private LocalDate birthdate;
        private LocalDateTime createdAt;

        public SaveMember(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.name = member.getName();
            this.birthdate = member.getBirthdate();
            this.createdAt = member.getCreatedAt();
        }

    }

    @Data
    public static class loginMember {
        private Integer id;
        private String email;
        private String name;

        public loginMember(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.name = member.getName();
        }

    }
}