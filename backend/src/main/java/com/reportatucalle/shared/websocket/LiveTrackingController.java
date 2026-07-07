package com.reportatucalle.shared.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LiveTrackingController {

    // El supervisor envía su GPS a: /app/supervisor.location
    // El servidor lo retransmite a: /topic/supervisor-locations
    @MessageMapping("/supervisor.location")
    @SendTo("/topic/supervisor-locations")
    public SupervisorLocationMessage broadcastLocation(@Payload SupervisorLocationMessage message) {
        // En una app más estricta, aquí validaríamos que el supervisorId coincida 
        // con el Principal autenticado en el WebSocket.
        // Para este alcance, simplemente retransmitimos la ubicación.
        return message;
    }
}
