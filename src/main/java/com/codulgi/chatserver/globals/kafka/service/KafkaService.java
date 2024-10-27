package com.codulgi.chatserver.globals.kafka.service;

import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.globals.kafka.entity.KafkaMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture; // import 추가


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private static final String TOPIC = "message-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessageToKafka(Message message, HttpServletRequest request) throws JsonProcessingException {
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto(message, request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원

        String jsonMessage = objectMapper.writeValueAsString(kafkaMessageDto);

        // CompletableFuture로 전송 처리
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, jsonMessage);

        future.thenAccept(result ->
                log.info("Kafka 메시지 전송 성공: {}", jsonMessage)
        ).exceptionally(ex -> {
            log.error("Kafka 메시지 전송 실패 - 메시지: {}, 에러: {}", jsonMessage, ex.getMessage(), ex);
            return null;
        });
    }
}


