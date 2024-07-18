package com.websocket.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketKafkaService extends TextWebSocketHandler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        kafkaTemplate.send("my_topic", message.getPayload());
        session.sendMessage(new TextMessage("Starting processing tutorial " + message.getPayload()));
        System.out.println("Received message from WebSocket: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
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

