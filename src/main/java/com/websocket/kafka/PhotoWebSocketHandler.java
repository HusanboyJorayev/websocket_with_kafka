package com.websocket.kafka;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PhotoWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void afterConnectionEstablished(@org.springframework.lang.NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws IOException {
        String base64Payload = message.getPayload();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Payload);

        try (FileOutputStream fileOutputStream = new FileOutputStream("received_photo.jpg")) {
            fileOutputStream.write(decodedBytes);
            kafkaTemplate.send("my_topic", base64Payload);
        }
        session.sendMessage(new TextMessage("Photo received successfully!"));
    }

    @Override
    public void afterConnectionClosed(@org.springframework.lang.NonNull WebSocketSession session, @org.springframework.lang.NonNull CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
        log.info("WebSocket connection closed:{} on session id ", session.getId());
    }

    @KafkaListener(topics = "my_topic", groupId = "group_id")
    public void consume(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("Sent message to WebSocket client: " + message);
            } catch (IOException e) {
                System.err.println("Error sending message to WebSocket client: " + e.getMessage());
            }
        }
    }
}
