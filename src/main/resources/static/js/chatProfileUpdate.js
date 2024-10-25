document.addEventListener('DOMContentLoaded', function () {
    const profileImage = document.getElementById('profileImage'); // 이미지 요소
    const profileImageInput = document.getElementById('profileImageInput'); // 파일 입력 요소
    const memberId =
    // 이미지 클릭 시 파일 입력 요소 클릭
    profileImage.addEventListener('click', function () {
        profileImageInput.click(); // 숨겨진 파일 입력 요소 열기
    });

    // 파일 선택 시 자동으로 업로드 요청
    profileImageInput.addEventListener('change', function () {
        const file = profileImageInput.files[0];
        if (file) {
            const formData = new FormData();
            formData.append("profileImage", file);

            // 서버로 파일 업로드 요청
            fetch(`/api/v1/member/${loggedInUserId}/update-profile-image`, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("프로필 이미지를 업데이트하는 데 실패했습니다.");
                    }
                    return response.text();
                })
                .then(data => {
                    alert("프로필 이미지가 성공적으로 업데이트되었습니다.");
                    // 업데이트된 프로필 이미지를 화면에 반영
                    profileImage.src = URL.createObjectURL(file);
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("프로필 이미지를 업데이트하는 중 오류가 발생했습니다.");
                });
        }
    });
});
