package com.codulgi.chatserver.globals.kafka.entity;

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
    private Object message;
    private HttpStatusCode status;

    public KafkaMessageDto(Message message, HttpServletRequest request) {
        this.traceId = UUID.randomUUID().toString();
        this.clientId = request.getRemoteAddr();
        this.time = LocalDateTime.now();
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.message = message.getContent();  // 전송할 메시지
        this.status = HttpStatus.OK;
    }
}