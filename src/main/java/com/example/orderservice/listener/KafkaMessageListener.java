package com.example.orderservice.listener;



import com.example.orderservice.model.OrderEvent;
import com.example.orderservice.model.OrderStatusServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener {

    @Value("${app.kafka.kafkaMessageOrderStatusTopic}")
    private String topicName;
    private final KafkaTemplate<String, OrderStatusServiceResponse> kafkaTemplate;

    @KafkaListener(topics = "${app.kafka.kafkaMessageOrderTopic}",
    groupId = "${app.kafka.kafkaMessageGroupId}",
    containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory")
    public void listen(@Payload OrderEvent message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                       @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp){
        log.info("Received message: {}", message);
        log.info("Key: {}; Partition: {}; Topic: {}; Timestamp: {}", key, partition, topic, timestamp);

        OrderStatusServiceResponse response = new OrderStatusServiceResponse();
        response.setStatus("CREATED");
        response.setDate(Instant.now());
        kafkaTemplate.send(topicName, response);
    }



}
