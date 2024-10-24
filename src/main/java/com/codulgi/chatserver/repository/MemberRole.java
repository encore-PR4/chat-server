package com.codulgi.chatserver.repository;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_MEMBER")

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
