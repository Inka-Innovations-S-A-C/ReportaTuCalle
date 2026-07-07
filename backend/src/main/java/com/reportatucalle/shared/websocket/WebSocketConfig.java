package com.reportatucalle.shared.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar un broker en memoria simple para los clientes (Ciudadanos)
        // Todo lo que empiece con /topic se enviará directamente a los clientes suscritos
        config.enableSimpleBroker("/topic");
        
        // Prefijo para los mensajes que envían los clientes al servidor (Supervisores publicando)
        // Ejemplo: enviar a /app/location.update
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint al que se conectarán los clientes (SockJS)
        registry.addEndpoint("/ws-tracking")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS();
    }
}
