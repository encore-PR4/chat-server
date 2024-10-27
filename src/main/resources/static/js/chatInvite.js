document.addEventListener("DOMContentLoaded", function () {
    // 참여자 목록 버튼 클릭 시 참여자 목록 로드
    const memberListElement = document.getElementById('member-list');
    if (memberListElement) {
        memberListElement.addEventListener('click', function () {
            if (selectedChatRoomId) fetchChatRoomParticipants(selectedChatRoomId);
        });
    }

    // 사용자 초대 입력 필드의 값이 변경될 때 이메일로 사용자 검색
    const inviteUserInput = document.getElementById("inviteUserId");
    if (inviteUserInput) {
        inviteUserInput.addEventListener("input", function () {
            const emailQuery = this.value.trim();
            if (emailQuery.length >= 3) searchUsersByEmail(emailQuery);
        });
    }
});

// 선택된 채팅방의 참여자 목록 불러오기
function fetchChatRoomParticipants(chatRoomId) {
    fetch(`/api/v1/chat/chatroom/participant?chatRoomId=${chatRoomId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("참여자 목록 불러오기 실패");
            }
            return response.json();
        })
        .then(participants => {
            console.log("참여자 목록:", participants); // 디버깅용 로그
            updateParticipantList(participants);
        })
        .catch(error => {
            console.error("참여자 목록 불러오는 중 오류 발생:", error);
            alert("참여자 목록을 불러오는 중 오류가 발생했습니다.");
        });
}

// 참여자 목록 UI 업데이트
function updateParticipantList(participants) {
    const userList = document.getElementById("userList");
    if (!userList) {
        console.error("userList 요소를 찾을 수 없습니다.");
        return;
    }
    userList.innerHTML = ""; // 기존 목록 초기화

    if (participants.length === 0) {
        userList.innerHTML = "<p>참여자가 없습니다.</p>";
    } else {
        participants.forEach(participant => {
            const userCard = document.createElement("div");
            userCard.classList.add("user-card");

            // 프로필 이미지가 base64 형식인지 URL인지에 따라 src 값 설정
            const profileImageSrc = participant.profileImage?.startsWith("iVBORw0KGgo")
                ? `data:image/png;base64,${participant.profileImage}`
                : participant.profileImage || "/default-profile.png";

            userCard.innerHTML = `
                <img src="${profileImageSrc}" alt="${participant.name}" class="user-img">
                <p>${participant.name}</p>
            `;
            userList.appendChild(userCard);
        });
    }
}

// 이메일로 사용자 검색 (자기 자신은 제외하고 클릭 시 초대)
function searchUsersByEmail(emailQuery) {
    fetch(`/api/v1/chat/member/search?email=${emailQuery}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("사용자 검색 실패");
            }
            return response.json();
        })
        .then(users => {
            const inviteUserList = document.getElementById("inviteUserList");
            inviteUserList.innerHTML = ""; // 기존 검색 결과 초기화

            // 현재 로그인된 사용자의 ID와 일치하는 사용자 제외
            const filteredUsers = users.filter(user => user.id !== loggedInUser.id);

            filteredUsers.forEach(user => {
                const userCard = document.createElement("div");
                userCard.classList.add("invite-user-card");

                // 프로필 이미지가 base64 형식인지 URL인지에 따라 src 값 설정
                const profileImageSrc = user.profileImage?.startsWith("iVBORw0KGgo")
                    ? `data:image/png;base64,${user.profileImage}`
                    : user.profileImage || "/default-profile.png";

                userCard.innerHTML = `
                    <img src="${profileImageSrc}" alt="${user.name}" class="invite-user-img">
                    <div class="invite-user-info">
                        <p class="invite-user-email">${user.email}</p>
                        <p class="invite-user-name">${user.name}</p>
                    </div>
                `;

                // 사용자 카드를 클릭하면 초대가 되도록 이벤트 리스너 추가
                userCard.addEventListener("click", () => inviteUserToChatRoom(user.id));

                inviteUserList.appendChild(userCard);
            });
        })
        .catch(error => console.error("이메일로 사용자 검색 중 오류 발생:", error));
}

// 사용자를 선택된 채팅방에 초대
function inviteUserToChatRoom(userId) {
    fetch(`/api/v1/chat/invite?chatRoomId=${selectedChatRoomId}&memberId=${userId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            console.log("Invite API Response Status:", response.status);
            if (!response.ok) {
                return response.json().then(data => {
                    console.error("Invite API Server Error Response:", data);
                    throw new Error(data.error || "초대 실패");
                });
            }
            // 응답을 text로 읽음
            return response.text();
        })
        .then(data => {
            console.log("초대 성공:", data);
            document.getElementById("inviteUserList").innerHTML = ""; // 검색 결과 초기화
            fetchChatRoomParticipants(selectedChatRoomId); // 초대 후 참여자 목록 새로고침
            alert(data); // 성공 메시지를 알림으로 표시
        })
        .catch(error => console.error("사용자 초대 중 오류 발생:", error));
}



// 모달이 열릴 때 참여자 목록 로드
document.getElementById("userListModal").addEventListener("show.bs.modal", function () {
    if (selectedChatRoomId) {
        fetchChatRoomParticipants(selectedChatRoomId);
    } else {
        console.error("선택된 채팅방이 없습니다.");
    }
});
