// AJAX 요청 공통 함수
function sendRequest(url, data, onSuccess, onError) {
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                // 실패 시 메시지 추출
                return response.text().then(errorData => {
                    throw new Error(errorData);
                });
            }
            return response.text();
        })
        .then(onSuccess)
        .catch(onError);
}

// Toast 메시지 표시 함수
function showToast(message, isSuccess = true) {
    var toastElement = document.getElementById('signupToast');
    var toastBody = toastElement.querySelector('.toast-body');

    // 메시지 설정
    toastBody.textContent = message;

    // 성공 여부에 따라 색상 변경
    if (isSuccess) {
        toastElement.classList.remove('text-bg-danger');
        toastElement.classList.add('text-bg-success'); // 초록색 배경 (성공)
    } else {
        toastElement.classList.remove('text-bg-success');
        toastElement.classList.add('text-bg-danger'); // 빨간색 배경 (실패)
    }

    var toast = new bootstrap.Toast(toastElement);
    toast.show();
}

// 로그인 이벤트 리스너
document.getElementById('loginButton').addEventListener('click', function () {
    const loginData = {
        email: document.getElementById('login-email').value,
        password: document.getElementById('login-password').value
    };

    sendRequest('/api/v1/member/login', loginData,
        function (data) {
            showToast('로그인 성공!', true); // 성공 Toast
            window.location.href = '/cms'; // 로그인 성공 시 리디렉션
        },
        function (error) {
            console.error('로그인 실패:', error.message);
            showToast(error.message, false); // 서버에서 받은 실패 메시지로 Toast 표시
        });
});

// 회원가입 이벤트 리스너
document.getElementById('submitForm').addEventListener('click', function () {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const name = document.getElementById('name').value;
    const birthdate = document.getElementById('birthdate').value;

    if (!email || !password || !name || !birthdate) {
        showToast('모든 필드를 입력해주세요.', false);
        return;
    }

    const formData = { email, password, name, birthdate };

    sendRequest('/api/v1/member/join', formData,
        function (data) {
            showToast('회원가입 성공!', true); // 성공 Toast
            document.getElementById('exampleModal').classList.remove('show');
            document.body.classList.remove('modal-open');
            document.querySelector('.modal-backdrop').remove();
        },
        function (error) {
            console.error('회원가입 실패:', error.message);
            showToast(error.message, false); // 서버에서 받은 실패 메시지로 Toast 표시
        });
});
