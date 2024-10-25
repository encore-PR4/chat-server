package com.codulgi.chatserver.globals.kafka.service;

import com.codulgi.chatserver.chat.entity.Message;
import com.codulgi.chatserver.globals.kafka.entity.KafkaMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {

    private static final String TOPIC = "message-topic";
    private final KafkaTemplate<String, KafkaMessageDto> kafkaTemplate;

    public void sendMessageToKafka(Message message, HttpServletRequest request) {
        // Kafka 메시지를 위한 DTO 생성
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto(message, request);

        // CompletableFuture로 비동기 처리
        CompletableFuture<SendResult<String, KafkaMessageDto>> future = kafkaTemplate.send(TOPIC, kafkaMessageDto);

        // 성공 시 처리
        future.thenAccept(result -> {
            log.info("Kafka 메시지 전송 성공: " + kafkaMessageDto);
        });

        // 실패 시 처리
        future.exceptionally(ex -> {
            System.err.println("Kafka 메시지 전송 실패: " + kafkaMessageDto);

            log.error(ex.getMessage());
            return null; // null 반환하여 완료 처리
        });
    }
}


