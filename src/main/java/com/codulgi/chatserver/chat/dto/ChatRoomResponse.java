package com.codulgi.chatserver.chat.dto;

import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.member.dto.MemberResponseDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatRoomResponse {

    private Integer id;
    private String name;
    private MemberResponseDto.loginMember owner;
    private List<MemberResponseDto.chatUser> participants;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.name = chatRoom.getName();
        this.owner = new MemberResponseDto.loginMember(chatRoom.getOwner());
        this.participants = chatRoom.getParticipants().stream()
                .map(MemberResponseDto.chatUser::new)
                .collect(Collectors.toList());
    }
}
