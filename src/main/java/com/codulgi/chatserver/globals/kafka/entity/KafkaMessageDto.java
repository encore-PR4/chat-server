package com.codulgi.chatserver.globals.kafka.entity;

import com.codulgi.chatserver.chat.dto.MessageRequest;
import com.codulgi.chatserver.chat.dto.MessageResponse;
import com.codulgi.chatserver.chat.entity.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
public class KafkaMessageDto {

    private String traceId;
    private String clientId;
    private LocalDateTime time;
    private String path;
    private String method;
    private MessageRequest request;
    private MessageResponse response;
    private int status;

    public KafkaMessageDto(MessageRequest messageRequest, Message message, HttpServletRequest request) {
        this.traceId = UUID.randomUUID().toString();
        this.clientId = request.getRemoteAddr();
        this.time = LocalDateTime.now();
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.request = messageRequest;
        this.response = new MessageResponse(message);  // 전송할 메시지
        this.status = HttpStatus.OK.value();  // 상태 코드를 정수로 저장
    }
}
