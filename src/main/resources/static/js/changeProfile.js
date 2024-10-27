function triggerFileInput() {
    document.getElementById("fileInput").click();
}

function updateProfileImage(event) {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("profileImage", file);

    fetch(`/api/v1/member/${loggedInUser.id}/update-profile-image`, {
        method: "POST",
        body: formData
    })
        .then(response => response.text()) // 텍스트 응답으로 처리
        .then(message => {
            alert(message); // 서버 응답 메시지 표시
            window.location.reload(); // 화면 새로고침
        })
        .catch(error => {
            console.error("이미지 업로드 오류:", error);
            alert("이미지 업로드에 실패했습니다.");
        });
}


