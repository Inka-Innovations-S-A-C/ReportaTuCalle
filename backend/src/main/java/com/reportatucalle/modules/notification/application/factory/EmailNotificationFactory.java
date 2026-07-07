package com.reportatucalle.modules.notification.application.factory;

import com.reportatucalle.modules.notification.domain.Notification;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmailNotificationFactory implements NotificationFactory {
    
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }

    public static class EmailNotification implements Notification {
        private static final Logger log = LoggerFactory.getLogger(EmailNotification.class);
        
        @Override
        public void send(String message, String recipient) {
            log.info("Sending email to {}: {}", recipient, message);
        }
    }
}
