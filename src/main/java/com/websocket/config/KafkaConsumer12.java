package com.websocket.config;

import com.websocket.model.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer12 {

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(topics = "my_topic", groupId = "group_id")
    public void listen(String message) {
        template.convertAndSend("/topic/greetings", new Greeting("Kafka says: " + message));
    }
}

