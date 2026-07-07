package com.reportatucalle.modules.notification.application.service;

import com.reportatucalle.modules.notification.application.factory.NotificationFactory;
import com.reportatucalle.modules.notification.domain.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private final NotificationFactory notificationFactory;

    public NotificationService(NotificationFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }

    public void notifyUser(String message, String recipient) {
        Notification notification = notificationFactory.createNotification();
        notification.send(message, recipient);
    }
}
