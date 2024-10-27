document.addEventListener("DOMContentLoaded", function() {
    loadMessages();
});

async function loadMessages() {
    try {
        const chatRoomId = getChatRoomId(); // 현재 채팅방 ID 가져오기 (예: URL이나 세션에서 가져올 수 있음)
        const response = await fetch(`/api/v1/messages/${chatRoomId}`);
        if (!response.ok) {
            throw new Error("Failed to load messages");
        }

        const messages = await response.json();
        const chatContainer = document.querySelector(".chat");
        chatContainer.innerHTML = ""; // 기존 메시지 초기화

        messages.forEach(message => {
            const messageBox = document.createElement("div");
            messageBox.classList.add(message.senderId === loggedInUser.id ? "chat-message-box-me" : "chat-message-box");

            messageBox.innerHTML = `
                <div class="member-profile-box">
                    <img src="${message.senderProfileImageUrl || '/default-profile.png'}" class="member-profile" alt="">
                </div>
                <div class="chat-messages">
                    <p class="member-name">${message.senderName}</p>
                    <div class="chat-message-c">
                        <p class="chat-message">${message.content}</p>
                        <p class="chat-send-time">${new Date(message.timestamp).toLocaleTimeString()}</p>
                    </div>
                </div>
            `;
            chatContainer.appendChild(messageBox);
        });

        chatContainer.scrollTop = chatContainer.scrollHeight; // 스크롤을 맨 아래로 이동
    } catch (error) {
        console.error("Error loading messages:", error);
    }
}

async function sendMessage() {
    const messageInput = document.getElementById("messageInput");
    const content = messageInput.value.trim();
    if (!content) return;

    try {
        const chatRoomId = getChatRoomId();
        const response = await fetch("/api/v1/messages/send", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                content,
                senderId: loggedInUser.id,
                chatRoomId: chatRoomId
            })
        });

        if (!response.ok) {
            throw new Error("Failed to send message");
        }

        messageInput.value = ""; // 입력창 초기화
        loadMessages(); // 메시지 목록 새로고침
    } catch (error) {
        console.error("Error sending message:", error);
    }
}

// 현재 채팅방 ID를 가져오는 헬퍼 함수 (필요에 맞게 구현)
function getChatRoomId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("chatRoomId");
}
