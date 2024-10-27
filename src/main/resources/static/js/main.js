let selectedChatRoomId = null; // 선택된 채팅방 ID를 저장하는 전역 변수
let stompClient = null; // WebSocket 연결 객체
let previousSenderId = null; // 이전 메시지의 senderId 저장
let messageGroup = null; // 현재 메시지 그룹 컨테이너

document.addEventListener("DOMContentLoaded", function () {
    loadChatRooms();
    setupWebSocket();

    // 메시지 입력 필드에서 엔터 키로 메시지 전송
    const messageInput = document.querySelector(".msg-input");
    messageInput.addEventListener("keyup", function(event) {
        if (event.key === "Enter") {
            sendMessage();
        }
    });
});

// 채팅방 목록 불러오기
function loadChatRooms() {
    fetch(`/api/v1/chat/my-rooms?id=${loggedInUser.id}`)
        .then(response => {
            if (!response.ok) throw new Error("Failed to load chat rooms");
            return response.json();
        })
        .then(chatRooms => {
            const chatRoomList = document.getElementById("chat-room-list");
            chatRoomList.innerHTML = ''; // 기존 목록 초기화

            chatRooms.forEach(room => {
                const roomCard = document.createElement('div');
                roomCard.classList.add('chat-room-card');

                // 클릭 시 해당 채팅방의 메시지 로드
                roomCard.addEventListener("click", () => {
                    selectedChatRoomId = room.id; // 전역 변수에 선택된 채팅방 ID 저장
                    loadMessages(room.id, room.name);  // 채팅방 ID와 이름을 전달
                    subscribeToChatRoom(room.id); // WebSocket 채팅방 구독
                });

                roomCard.innerHTML = `
                    <div class="chat-room-card-info">
                        <div class="chat-room-image"></div>
                        <div class="chat-room-info">
                            <p class="chat-room-name">${room.name}</p>
                            <p class="chat-room-last">${room.lastMessage || 'No messages yet'}</p>
                        </div>
                    </div>
                `;
                chatRoomList.appendChild(roomCard);
            });

            document.getElementById("chat-room-count").textContent = `${chatRooms.length} Open`;
        })
        .catch(error => {
            console.error("Failed to load chat rooms:", error);
        });
}

// WebSocket 설정 및 채팅방 구독
function setupWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket connected");
    });
}

function subscribeToChatRoom(chatRoomId) {
    if (stompClient && selectedChatRoomId) {
        stompClient.subscribe(`/topic/chat-room/${chatRoomId}`, message => {
            const newMessage = JSON.parse(message.body);
            displayMessage(newMessage); // WebSocket을 통해 수신한 메시지를 화면에 표시
        });
    }
}

// 채팅방 메시지 로드
function loadMessages(chatRoomId, chatRoomName) {
    fetch(`/api/v1/messages/${chatRoomId}`)
        .then(response => {
            if (!response.ok) throw new Error("Failed to load messages");
            return response.json();
        })
        .then(messages => {
            const chatContainer = document.querySelector(".chat");
            chatContainer.innerHTML = ''; // 기존 메시지 초기화
            document.querySelector(".chat-title").textContent = chatRoomName; // 채팅방 제목 설정

            // 메시지 목록 표시
            previousSenderId = null; // 초기화
            messages.forEach(message => displayMessage(message));

            chatContainer.scrollTop = chatContainer.scrollHeight; // 스크롤을 맨 아래로 이동
        })
        .catch(error => console.error("Error loading messages:", error));
}

// 채팅 메시지를 화면에 표시
function displayMessage(message) {
    const chatContainer = document.querySelector(".chat");
    const isMyMessage = message.senderId === loggedInUser.id;

    // 이전 메시지와 동일한 senderId인 경우, 기존 messageGroup에 메시지를 추가
    if (message.senderId === previousSenderId && messageGroup) {
        const messagesContainer = messageGroup.querySelector(isMyMessage ? ".chat-messages-me" : ".chat-messages");
        const messageContent = document.createElement("div");
        messageContent.classList.add("chat-message-c");
        messageContent.innerHTML = `
            <p class="${isMyMessage ? "chat-message-me" : "chat-message"}">${message.content}</p>
            <p class="chat-send-time">${new Date(message.timestamp).toLocaleTimeString()}</p>
        `;
        messagesContainer.appendChild(messageContent);
    } else {
        // 다른 senderId인 경우, 새로운 messageGroup 생성
        messageGroup = document.createElement("div");
        messageGroup.classList.add(isMyMessage ? "chat-message-box-me" : "chat-message-box");

        const avatarSrc = message.senderAvatar
            ? `data:image/png;base64,${message.senderAvatar}`
            : '/default-profile.png';

        messageGroup.innerHTML = `
            ${isMyMessage ? "" : `
                <!-- 다른 사용자가 보낸 메시지의 경우 왼쪽에 프로필을 표시 -->
                <div class="member-profile-box">
                    <img src="${avatarSrc}" class="member-profile" alt="">
                </div>
            `}
            <div class="${isMyMessage ? "chat-messages-me" : "chat-messages"}">
                <p class="${isMyMessage ? "member-name-me" : "member-name"}">${message.senderName}</p>
            </div>
            ${isMyMessage ? `
                <!-- 내가 보낸 메시지의 경우 오른쪽에 프로필을 표시 -->
                <div class="member-profile-box">
                    <img src="${avatarSrc}" class="member-profile" alt="">
                </div>
            ` : ""}
        `;

        const messagesContainer = messageGroup.querySelector(isMyMessage ? ".chat-messages-me" : ".chat-messages");
        const messageContent = document.createElement("div");
        messageContent.classList.add("chat-message-c");
        messageContent.innerHTML = `
            ${isMyMessage ? `<p class="chat-send-time">${new Date(message.timestamp).toLocaleTimeString()}</p>` : ''}
            <p class="${isMyMessage ? "chat-message-me" : "chat-message"}">${message.content}</p>
            ${!isMyMessage ? `<p class="chat-send-time">${new Date(message.timestamp).toLocaleTimeString()}</p>` : ''}
        `;

        messagesContainer.appendChild(messageContent);
        chatContainer.appendChild(messageGroup);
    }

    previousSenderId = message.senderId; // 현재 메시지의 senderId를 저장하여 다음 메시지와 비교
    chatContainer.scrollTop = chatContainer.scrollHeight; // 스크롤을 맨 아래로 이동
}

// 메시지 보내기 (WebSocket + 서버 저장)
function sendMessage() {
    const messageInput = document.querySelector(".msg-input");
    const content = messageInput.value.trim();
    if (!content || !selectedChatRoomId) return;

    const messageData = {
        content,
        senderId: loggedInUser.id,
        chatRoomId: selectedChatRoomId
    };

    // WebSocket으로 메시지 전송
    stompClient.send(`/app/chat/send`, {}, JSON.stringify(messageData));

    // 서버에 메시지 저장
    fetch("/api/v1/messages/send", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(messageData)
    })
        .then(response => {
            if (!response.ok) throw new Error("Failed to send message");
            messageInput.value = ""; // 입력창 초기화
        })
        .catch(error => console.error("Error sending message:", error));
}

// 로그아웃
function logout() {
    fetch('/api/v1/member/logout', {method: 'POST'})
        .then(response => {
            if (response.ok) {
                alert("로그아웃 되었습니다.");
                window.location.href = '/';
            } else {
                throw new Error("Logout failed");
            }
        })
        .catch(error => console.error("Error logging out:", error));
}
