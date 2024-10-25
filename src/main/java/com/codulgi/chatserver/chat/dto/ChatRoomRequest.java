package com.codulgi.chatserver.chat.dto;

import com.codulgi.chatserver.member.entity.Member;
import lombok.Data;

import java.util.List;

@Data
public class ChatRoomRequest {

    private String name;
    private Integer ownerId;
    private List<Integer> participantIds;
}
