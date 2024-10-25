package com.codulgi.chatserver.globals.kafka.config;

import java.util.HashMap;
import java.util.Map;

import com.codulgi.chatserver.globals.kafka.entity.KafkaMessageDto;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


@Configuration
public class KafkaProducerConfig {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    /**
     * 메시지 발송 모듈에서 사용할 KafkaTemplate
     *
     * @return
     */
    @Bean
    public KafkaTemplate<String, KafkaMessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, KafkaMessageDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // String 형태로 직렬화

        return new DefaultKafkaProducerFactory<>(configProps);
    }
}