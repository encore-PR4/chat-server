package com.codulgi.chatserver.member.dto;

import com.codulgi.chatserver.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

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
        private byte[] profileImage;

        public loginMember(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.name = member.getName();
            this.profileImage = member.getProfileImage();
        }

        public String getProfileImageUrl() {
            if (this.profileImage == null || this.profileImage.length == 0) {
                return "/path/to/default/profile/image.png"; // 기본 이미지 경로
            }
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(this.profileImage);
        }
    }

    @Data
    public static class chatUser {
        private Integer id;
        private String email;
        private String name;
        private byte[] profileImage;

        public chatUser(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.name = member.getName();
            this.profileImage = member.getProfileImage();
        }

    }
}