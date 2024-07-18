package com.websocket.kafka;


import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class FileWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws IOException {
        String base64Payload = message.getPayload();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Payload);

        try (FileOutputStream fileOutputStream = new FileOutputStream("file_sss.txt")) {
            fileOutputStream.write(decodedBytes);
        }
        session.sendMessage(new TextMessage("File received successfully!"));
    }
}


