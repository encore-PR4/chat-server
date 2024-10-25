package com.codulgi.chatserver.chat.service;

import com.codulgi.chatserver.chat.dto.ChatRoomRequest;
import com.codulgi.chatserver.chat.entity.ChatRoom;
import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.chat.repository.ChatRoomRepository;
import com.codulgi.chatserver.chat.repository.MessageRepository;
import com.codulgi.chatserver.member.dto.MemberResponseDto;
import com.codulgi.chatserver.member.entity.Member;
import com.codulgi.chatserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    /* 채팅방 생성 */
    public ResponseEntity<?> createRoom(ChatRoomRequest requestDto) {
        Member owner = memberRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("소유자를 찾을 수 없습니다."));

        List<Member> participants = memberRepository.findAllById(requestDto.getParticipantIds());

        participants.add(owner);

        ChatRoom chatRoom = new ChatRoom(requestDto.getName(), participants, owner);

        ChatRoom newChatRoom = chatRoomRepository.save(chatRoom);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChatRoom);
    }

    /* 모든 채팅방 조회 */
    public ResponseEntity<?> getAllRooms() {
        return ResponseEntity.ok(chatRoomRepository.findAll());
    }

    /* 내가 가입된 채팅방 조회 */
    public ResponseEntity<?> getMyRooms(Integer memberId) {
        List<ChatRoom> chatRoomsByMemberId = chatRoomRepository.findChatRoomsByMemberId(memberId);
        return ResponseEntity.ok(chatRoomsByMemberId);
    }

    /* 특정 채팅방 제목 가져오기 */
    public String getChatRoomTitle(Integer chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        return chatRoom.getName();
    }

    /* 특정 채팅방 메시지 가져오기 */
    public List<Message> getMessagesByChatRoom(Integer chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    /* 채팅방에 가입된 사용자 불러오기 */
    public ResponseEntity<?> getRoomInParticipantId(Integer roomId) {
        Optional<ChatRoom> byId = chatRoomRepository.findById(roomId);

        if (byId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("채팅방을 찾을 수 없습니다.");
        }

        ChatRoom chatRoom = byId.get();

        List<Member> participants = chatRoom.getParticipants();


        // Member 객체를 MemberResponseDto.chatUser로 변환
        List<MemberResponseDto.chatUser> participantDtos = participants.stream()
                .map(MemberResponseDto.chatUser::new)  // 생성자 참조 사용
                .collect(Collectors.toList());

        return ResponseEntity.ok(participantDtos);
    }

    public void deleteRoom(Integer roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("해당 채팅방을 찾을 수 없습니다."));
        chatRoomRepository.delete(chatRoom);
    }

    /* 채팅방에 사용자 초대 */
    public ResponseEntity<?> inviteUser(Integer chatRoomId, Integer memberId) {
        // 채팅방이 존재하는지 확인
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(chatRoomId);
        if (chatRoomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("채팅방을 찾을 수 없습니다.");
        }

        // 초대할 사용자가 존재하는지 확인
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("초대할 사용자를 찾을 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomOpt.get();
        Member userToInvite = memberOpt.get();

        // 이미 참여 중인지 확인
        if (chatRoom.getParticipants().contains(userToInvite)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("사용자가 이미 채팅방에 참여하고 있습니다.");
        }

        // 채팅방에 사용자 추가
        chatRoom.getParticipants().add(userToInvite);
        chatRoomRepository.save(chatRoom);

        return ResponseEntity.status(HttpStatus.OK).body("사용자가 채팅방에 성공적으로 초대되었습니다.");
    }
}
