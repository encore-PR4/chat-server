FROM openjdk:17

# 사용자 및 그룹 생성
RUN groupadd -r codulki && useradd -r -g codulki codulki

# JAR 파일을 Docker 이미지 내부로 복사
COPY build/libs/chat-server-0.0.1-SNAPSHOT.jar /app.jar

# 컨테이너 실행 시 사용할 사용자 지정
USER codulki

# 컨테이너가 시작될 때 실행될 명령어
CMD ["java", "-jar", "/app.jar"]
