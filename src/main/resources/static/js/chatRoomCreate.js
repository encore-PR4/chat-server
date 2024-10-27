document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("createChatRoomBtn").addEventListener("click", createChatRoom);
});

function createChatRoom() {
    const chatRoomName = document.getElementById("chatRoomName").value.trim();
    const chatRoomParticipantsInput = document.getElementById("chatRoomParticipants").value.trim();

    const chatRoomParticipants = chatRoomParticipantsInput
        ? chatRoomParticipantsInput.split(',').map(id => parseInt(id.trim(), 10))
        : [];

    if (!chatRoomName) {
        alert("채팅방 이름을 입력해주세요.");
        return;
    }

    const chatRoomData = {
        name: chatRoomName,
        ownerId: loggedInUser.id,
        participantIds: chatRoomParticipants
    };

    console.log("Sending chat room data:", chatRoomData); // 전송 데이터 로그

    fetch('/api/v1/chat/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(chatRoomData)
    })
        .then(response => {
            console.log("Raw response:", response); // 응답 원본 로그
            if (!response.ok) {
                // 오류 응답 처리
                return response.json().then(data => {
                    console.error("Server error response:", data); // 서버에서 반환한 에러
                    throw new Error(data.message || "Failed to create chat room");
                });
            }
            // 성공 응답 처리
            return response.json().then(data => data || {});
        })
        .then(data => {
            console.log("Chat room created:", data);
            loadChatRooms();
            const modal = bootstrap.Modal.getInstance(document.getElementById('staticBackdrop'));
            modal.hide();
        })
        .catch(error => {
            console.error("Error creating chat room:", error);
            alert("채팅방 생성 중 오류가 발생했습니다.");
        });
}
