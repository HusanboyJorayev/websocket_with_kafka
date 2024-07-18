package com.websocket.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getWebSocketHandler(), "/tutorial").setAllowedOrigins("*");
        registry.addHandler(getFileWebSocketHandler(), "/file").setAllowedOrigins("*");
        registry.addHandler(getFileWebSocketHandler(), "/photo").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler getWebSocketHandler() {
        return new WebSocketKafkaService();
    }

    @Bean
    public WebSocketHandler getFileWebSocketHandler() {
        return new FileWebSocketHandler();
    }

    @Bean
    public WebSocketHandler getPhotoWebSocketHandler() {
        return new PhotoWebSocketHandler();
    }
}

