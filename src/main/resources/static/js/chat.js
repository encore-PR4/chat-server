document.addEventListener('DOMContentLoaded', function () {
    let currentChatRoomId = null; // 현재 선택된 채팅방 ID
    let stompClient = null;
    let currentSubscription = null; // 현재 구독 ID 저장

    // 로그인 여부 확인하여 채팅창 표시 결정
    if (!loggedInUserId) {
        document.querySelector('.chat-container').style.display = 'none'; // 로그인 안 된 경우 채팅창 숨기기
        return; // 로그인되지 않으면 이후 코드 실행 중단
    }

    // 로그인된 경우 채팅 컨테이너 보이기
    document.querySelector('.chat-container').style.display = 'block';

    // 페이지 로드 시 내가 가입된 채팅방 목록을 가져옴
    fetchMyChatRooms();

    // WebSocket 설정 및 메시지 수신 처리
    function setupWebSocket() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('WebSocket 연결됨:', frame);
        });
    }

    // WebSocket 초기 설정 호출
    setupWebSocket();

    function enterChatRoom(chatRoomId) {
        console.log('입장된 채팅방 아이디:', chatRoomId); // 클릭한 채팅방 ID 확인

        if (currentSubscription) {
            currentSubscription.unsubscribe();
        }

        currentChatRoomId = chatRoomId;

        // chat-room에 visible-chat-room 클래스 추가
        const chatRoomElement = document.getElementById('chat-room');
        chatRoomElement.classList.add('visible-chat-room');
        console.log('채팅방이 보여야 합니다.');

        fetchChatRoomTitle(chatRoomId);
        fetchChatRoomMessages(chatRoomId);

        // 새로운 채팅방 구독
        currentSubscription = stompClient.subscribe(`/topic/chat-room/${currentChatRoomId}`, function (message) {
            console.log('Message received:', message);
            const newMessage = JSON.parse(message.body);
            appendMessageToChat(newMessage); // 새 메시지 추가
        });
    }


    // 채팅방 제목 불러오기
    function fetchChatRoomTitle(chatRoomId) {
        fetch(`/api/v1/chat/${chatRoomId}/title`)
            .then(handleResponse)
            .then(chatRoomData => {
                document.querySelector('.chat-room-title').textContent = chatRoomData.title;
            })
            .catch(error => {
                console.error('채팅방 제목 불러오기 오류:', error);
                alert('채팅방 제목을 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 채팅방 메시지 불러오기
    function fetchChatRoomMessages(chatRoomId) {
        fetch(`/api/v1/chat/${chatRoomId}/messages`)
            .then(handleResponse)
            .then(messages => {
                if (Array.isArray(messages)) {
                    updateChatContents(messages);
                } else {
                    console.error('메시지 배열 기대, 실제:', messages);
                }
            })
            .catch(error => {
                console.error('채팅 메시지 불러오기 오류:', error);
                alert('채팅 메시지를 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 채팅 메시지 목록을 화면에 업데이트
    function updateChatContents(messages) {
        const chatContents = document.querySelector('.chat-contents');
        chatContents.innerHTML = ''; // 기존 메시지 초기화

        messages.forEach(message => appendMessageToChat(message));

        // 스크롤을 맨 아래로 설정
        chatContents.scrollTop = chatContents.scrollHeight;
    }

    // 메시지를 chat-contents에 추가
    function appendMessageToChat(message) {
        const chatContents = document.querySelector('.chat-contents');
        const messageDiv = document.createElement('div');

        if (message.senderId === loggedInUserId) {
            messageDiv.classList.add('chat-content-me'); // 내가 보낸 메시지
        } else {
            messageDiv.classList.add('chat-content-not-me'); // 다른 사용자가 보낸 메시지
            const senderSpan = document.createElement('span');
            senderSpan.textContent = message.senderName;
            messageDiv.appendChild(senderSpan);
        }

        const messageP = document.createElement('p');
        messageP.textContent = message.content;
        messageDiv.appendChild(messageP);
        chatContents.appendChild(messageDiv);

        // 스크롤을 맨 아래로 이동
        chatContents.scrollTop = chatContents.scrollHeight;
    }

    // 채팅방 참여자 목록 불러오기
    function fetchChatRoomParticipants(chatRoomId) {
        fetch(`/api/v1/chat/chatroom/participant?chatRoomId=${chatRoomId}`)
            .then(handleResponse)
            .then(participants => updateParticipantList(participants))
            .catch(error => {
                console.error('참여자 목록 불러오기 오류:', error);
                alert('참여자 목록을 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 참여자 목록을 모달에 업데이트
    function updateParticipantList(participants) {
        const userList = document.getElementById('userList');
        userList.innerHTML = ''; // 기존 목록 초기화

        participants.forEach(participant => {
            const userCard = document.createElement('div');
            userCard.classList.add('user-card');

            const userImg = document.createElement('img');
            userImg.src = participant.profileImage || '/default-profile.png';
            userImg.alt = participant.name;

            const userName = document.createElement('p');
            userName.textContent = participant.name;

            userCard.appendChild(userImg);
            userCard.appendChild(userName);

            userList.appendChild(userCard);
        });
    }

    // 채팅방 참여자 목록 아이콘 클릭 이벤트
    document.getElementById('member-list').addEventListener('click', function () {
        if (currentChatRoomId) {
            fetchChatRoomParticipants(currentChatRoomId); // 현재 채팅방의 참여자 목록 불러오기
        } else {
            alert('채팅방이 선택되지 않았습니다.');
        }
    });

    // WebSocket으로 메시지 전송
    function sendMessageViaWebSocket(messageContent) {
        if (stompClient && currentChatRoomId && messageContent) {
            const message = {
                senderId: loggedInUserId,
                content: messageContent,
                chatRoomId: currentChatRoomId
            };
            stompClient.send(`/app/chat/send`, {}, JSON.stringify(message));
        }
    }

    // 서버에 메시지 저장
    function saveMessageToServer(messageContent) {
        fetch(`/api/v1/messages/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: messageContent,
                senderId: loggedInUserId,
                chatRoomId: currentChatRoomId
            })
        })
            .then(response => response.json())
            .then(data => {
                console.log('Message saved:', data);
            })
            .catch(error => {
                console.error('Error saving message:', error);
            });
    }

    // 메시지 전송
    function sendMessage() {
        const messageInput = document.querySelector('.chat-message-input');
        const messageContent = messageInput.value.trim();

        if (messageContent) {
            sendMessageViaWebSocket(messageContent); // WebSocket 전송
            saveMessageToServer(messageContent); // 서버에 저장
            messageInput.value = ''; // 입력란 초기화
        }
    }

    // 전송 버튼 클릭 이벤트
    document.getElementById('sendMessageBtn').addEventListener('click', sendMessage);

    // 엔터 키로 메시지 전송
    document.querySelector('.chat-message-input').addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });

    // 채팅방 생성
    document.getElementById('createChatRoomBtn').addEventListener('click', function () {
        const chatRoomName = document.getElementById('chatRoomName').value;
        const chatRoomParticipants = document.getElementById('chatRoomParticipants').value
            .split(',')
            .map(id => id.trim());

        if (chatRoomName && chatRoomParticipants.length > 0) {
            const chatRoomData = {
                name: chatRoomName,
                ownerId: loggedInUserId,
                participantIds: chatRoomParticipants
            };

            fetch('/api/v1/chat/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(chatRoomData)
            })
                .then(response => response.json())
                .then(data => {
                    console.log('Chat room created:', data);
                    fetchMyChatRooms();
                })
                .catch(error => {
                    console.error('채팅방 생성 중 오류 발생:', error);
                    alert('채팅방 생성 중 오류가 발생했습니다.');
                });
        }
    });

    // 내가 가입된 채팅방 목록 가져오기
    function fetchMyChatRooms() {
        fetch(`/api/v1/chat/my-rooms?id=${loggedInUserId}`)
            .then(handleResponse)
            .then(chatRooms => updateChatRooms(chatRooms))
            .catch(error => {
                console.error('채팅방 목록 불러오기 오류:', error);
                alert('채팅방 목록을 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 채팅방 목록 업데이트
    function updateChatRooms(chatRooms) {
        const chatCardContainer = document.querySelector('.chat-card-container');
        chatCardContainer.innerHTML = ''; // 기존 내용 초기화

        chatRooms.forEach(chatRoom => {
            const chatButton = document.createElement('button');
            chatButton.classList.add('chat-card');
            chatButton.textContent = chatRoom.name;
            chatButton.addEventListener('click', () => enterChatRoom(chatRoom.id));
            chatCardContainer.appendChild(chatButton);
        });
    }

    // 서버 응답 처리
    function handleResponse(response) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return response.text().then(text => {
            throw new Error(`Expected JSON but got text: ${text}`);
        });
    }
});
