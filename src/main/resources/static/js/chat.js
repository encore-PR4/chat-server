document.addEventListener('DOMContentLoaded', function () {
    let currentChatRoomId = null; // 현재 선택된 채팅방 ID
    let stompClient = null;
    let currentSubscription = null; // 현재 구독 ID를 저장

    // 페이지 로드 시 내가 가입된 채팅방 목록을 가져옴
    fetchMyChatRooms();

    /** WebSocket 설정 및 메시지 수신 처리 */
    function setupWebSocket() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('WebSocket connected:', frame);
        });
    }

    // WebSocket 설정을 먼저 한 번 호출해줌
    setupWebSocket();

    /** 채팅방 클릭 시 chat-room 보이게 하고 이전 채팅 불러오기 */
    function enterChatRoom(chatRoomId) {
        // 구독을 해제하여 이전 채팅방 구독을 방지
        if (currentSubscription) {
            currentSubscription.unsubscribe();
        }

        currentChatRoomId = chatRoomId;
        document.getElementById('chat-room').style.display = 'block'; // chat-room 보이게 설정

        fetchChatRoomTitle(chatRoomId);
        fetchChatRoomMessages(chatRoomId);

        // 새로운 채팅방을 구독
        currentSubscription = stompClient.subscribe(`/topic/chat-room/${currentChatRoomId}`, function (message) {
            const newMessage = JSON.parse(message.body);
            appendMessageToChat(newMessage);  // 새 메시지 추가
        });
    }

    /** 채팅방 제목 불러오기 */
    function fetchChatRoomTitle(chatRoomId) {
        fetch(`/api/v1/chat/${chatRoomId}/title`)
            .then(handleResponse)
            .then(chatRoomData => {
                document.querySelector('.chat-room-title').textContent = chatRoomData.title;
            })
            .catch(error => {
                console.error('Error fetching chat room title:', error);
                alert('채팅방 제목을 불러오는 중 오류가 발생했습니다.');
            });
    }

    /** 채팅방 메시지 불러오기 */
    function fetchChatRoomMessages(chatRoomId) {
        fetch(`/api/v1/chat/${chatRoomId}/messages`)
            .then(handleResponse)
            .then(messages => {
                if (Array.isArray(messages)) {
                    updateChatContents(messages);
                } else {
                    console.error('Expected an array of messages but received:', messages);
                }
            })
            .catch(error => {
                console.error('Error fetching chat messages:', error);
                alert('채팅 메시지를 불러오는 중 오류가 발생했습니다.');
            });
    }

    /** 채팅 메시지 목록을 화면에 업데이트 */
    function updateChatContents(messages) {
        const chatContents = document.querySelector('.chat-contents');
        chatContents.innerHTML = ''; // 기존 메시지 초기화

        messages.forEach(message => appendMessageToChat(message));

        // 스크롤을 맨 아래로 설정
        chatContents.scrollTop = chatContents.scrollHeight;
    }

    /** 메시지를 chat-contents에 추가 */
    function appendMessageToChat(message) {
        const chatContents = document.querySelector('.chat-contents');
        const messageDiv = document.createElement('div');

        console.log(message, loggedInUserId);

        // 메시지의 발신자 ID와 현재 로그인된 사용자의 ID 비교
        if (message.senderId === loggedInUserId) {
            // 내가 보낸 메시지일 경우 오른쪽에 표시
            messageDiv.classList.add('chat-content-me');
        } else {
            // 다른 사용자가 보낸 메시지일 경우 왼쪽에 표시
            messageDiv.classList.add('chat-content-not-me');
            const senderSpan = document.createElement('span');
            senderSpan.textContent = message.senderName;  // 발신자 이름 표시
            messageDiv.appendChild(senderSpan);
        }

        const messageP = document.createElement('p');
        messageP.textContent = message.content;  // 서버에서 메시지의 'content' 속성 확인
        messageDiv.appendChild(messageP);

        chatContents.appendChild(messageDiv);

        // 새 메시지가 추가되었을 때 스크롤을 맨 아래로 이동
        chatContents.scrollTop = chatContents.scrollHeight;
    }

    /** 참여자 목록 불러오기 */
    function fetchChatRoomParticipants(chatRoomId) {
        fetch(`/api/v1/chat/chatroom/participant?chatRoomId=${chatRoomId}`)
            .then(handleResponse)
            .then(participants => {
                if (Array.isArray(participants)) {
                    updateParticipantList(participants);
                } else {
                    console.error('Expected an array of participants but received:', participants);
                }
            })
            .catch(error => {
                console.error('Error fetching participants:', error);
                alert('참여자 목록을 불러오는 중 오류가 발생했습니다.');
            });
    }

    /** 참여자 목록을 모달에 업데이트 */
    function updateParticipantList(participants) {
        const userList = document.getElementById('userList');
        userList.innerHTML = ''; // 기존 목록 초기화

        participants.forEach(participant => {
            const userCard = document.createElement('div');
            userCard.classList.add('user-card');

            // 이미지와 이름을 포함한 사용자 정보 표시
            const userImg = document.createElement('img');
            userImg.src = participant.profileImage || '/default-profile.png'; // 기본 프로필 이미지 설정
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
            fetchChatRoomParticipants(currentChatRoomId);  // 현재 채팅방의 참여자 목록 불러오기
        } else {
            alert('채팅방이 선택되지 않았습니다.');
        }
    });

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



    function sendMessage() {
        const messageInput = document.querySelector('.chat-message-input');
        const messageContent = messageInput.value.trim();

        if (messageContent) {
            // 1. WebSocket을 통해 실시간 메시지 전송
            sendMessageViaWebSocket(messageContent);

            // 2. HTTP API를 통해 서버에 메시지 저장
            saveMessageToServer(messageContent);

            // 메시지 입력란 비우기
            messageInput.value = '';
        }
    }


    // 전송 버튼 클릭 이벤트
    document.getElementById('sendMessageBtn').addEventListener('click', function () {
        sendMessage();
    });

// 엔터 키로 메시지 전송 이벤트 추가
    document.querySelector('.chat-message-input').addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            sendMessage();  // 엔터를 누르면 메시지를 전송
        }
    });
    /** 채팅방 생성 */
    document.getElementById('createChatRoomBtn').addEventListener('click', function () {
        const chatRoomName = document.getElementById('chatRoomName').value;
        const chatRoomParticipants = document.getElementById('chatRoomParticipants').value
            .split(',')
            .map(id => id.trim());  // 참가자 ID를 콤마로 구분하여 배열로 변환

        if (chatRoomName && chatRoomParticipants.length > 0) {
            const chatRoomData = {
                name: chatRoomName,
                ownerId: loggedInUserId,
                participantIds: chatRoomParticipants
            };

            // 서버로 채팅방 생성 요청
            fetch('/api/v1/chat/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(chatRoomData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('채팅방 생성 실패');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Chat room created:', data);
                    fetchMyChatRooms();  // 새로운 채팅방을 만들고 목록 갱신
                })
                .catch(error => {
                    console.error('Error creating chat room:', error);
                    alert('채팅방 생성 중 오류가 발생했습니다.');
                });
        }
    });

    /** 내가 가입된 채팅방 목록을 가져오는 함수 */
    function fetchMyChatRooms() {
        fetch(`/api/v1/chat/my-rooms?id=${loggedInUserId}`)
            .then(handleResponse)
            .then(chatRooms => updateChatRooms(chatRooms))
            .catch(error => {
                console.error('Error fetching chat rooms:', error);
                alert('채팅방 목록을 불러오는 중 오류가 발생했습니다.');
            });
    }

    /** 채팅방 목록을 화면에 업데이트 */
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

    /** 사용자 검색 및 초대 관련 추가 기능 */
    const inviteUserIdInput = document.getElementById('inviteUserId');
    const inviteUserList = document.getElementById('inviteUserList');

    inviteUserIdInput.addEventListener('input', function () {
        const query = inviteUserIdInput.value.trim();

        if (query.length > 1) {  // 최소 2글자 이상 입력해야 검색 시작
            fetch(`/api/v1/chat/member/search?email=${query}`)
                .then(response => response.json())
                .then(users => {
                    displayUserSearchResults(users);
                })
                .catch(error => {
                    console.error('사용자 검색 중 오류:', error);
                });
        } else {
            inviteUserList.innerHTML = '';  // 입력이 없으면 결과 창 비우기
        }
    });

    // 검색 결과를 리스트로 표시
    function displayUserSearchResults(users) {
        inviteUserList.innerHTML = ''; // 기존 검색 결과 제거

        // 검색 결과가 5개 이상이면 표시하지 않음
        if (users.length > 5) {
            inviteUserList.innerHTML = '';  // 결과 리스트 비우기
            console.warn('검색 결과가 너무 많습니다.');
            return;
        }

        // 검색 결과를 표시
        users.forEach(user => {
            const userDiv = document.createElement('div');
            userDiv.classList.add('invite-find-user');

            const userImg = document.createElement('img');
            userImg.src = user.profileImage || 'default-profile.png';  // 기본 이미지 경로 추가
            userImg.alt = '프로필 이미지';
            userImg.classList.add('invite-user-profile');

            const userInfoDiv = document.createElement('div');
            userInfoDiv.classList.add('invite-user-info');

            const userEmailP = document.createElement('p');
            userEmailP.classList.add('invite-user-email');
            userEmailP.textContent = user.email;

            const userNameP = document.createElement('p');
            userNameP.classList.add('invite-user-name');
            userNameP.textContent = user.name;

            userInfoDiv.appendChild(userEmailP);
            userInfoDiv.appendChild(userNameP);
            userDiv.appendChild(userImg);
            userDiv.appendChild(userInfoDiv);

            // 클릭 시 사용자 초대
            userDiv.addEventListener('click', function () {
                // 사용자 바로 초대 (쿼리 스트링으로 memberId와 chatRoomId 전달)
                fetch(`/api/v1/chat/invite?memberId=${user.id}&chatRoomId=${currentChatRoomId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            return response.text();  // 성공 시 문자열 응답 처리
                        } else {
                            // 오류 발생 시 응답 본문을 텍스트로 처리
                            return response.text().then(text => {
                                throw new Error(text);
                            });
                        }
                    })
                    .then(data => {
                        alert(data);  // 성공 메시지 출력
                    })
                    .catch(error => {
                        // 오류 발생 시 토스트로 메시지 출력
                        showToast(error.message);
                        console.error('사용자 초대 중 오류 발생:', error);
                    });

                inviteUserList.innerHTML = '';  // 검색 결과 목록 비우기
            });

            inviteUserList.appendChild(userDiv);
        });
    }

    function showToast(message) {
        let toastElement = document.getElementById('errorToast');

        // 만약 toastElement가 없을 경우 동적으로 추가
        if (!toastElement) {
            const toastContainer = document.createElement('div');
            toastContainer.classList.add('toast-container', 'position-fixed', 'bottom-0', 'end-0', 'p-3');
            toastContainer.innerHTML = `
            <div id="errorToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="me-auto">오류 발생</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body"></div>
            </div>
        `;
            document.body.appendChild(toastContainer);
            toastElement = document.getElementById('errorToast');
        }

        const toastBody = toastElement.querySelector('.toast-body');

        // 오류 메시지를 토스트에 설정
        toastBody.textContent = message;

        // Bootstrap 토스트를 초기화하고 보이기
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
    }


    /** 서버 응답 처리 */
    function handleResponse(response) {
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json(); // JSON 응답 처리
        }
        return response.text().then(text => {
            throw new Error(`Expected JSON but got text: ${text}`);
        });
    }
});
