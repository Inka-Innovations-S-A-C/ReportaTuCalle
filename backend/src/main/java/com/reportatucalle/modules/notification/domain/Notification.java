package com.reportatucalle.modules.notification.domain;

public interface Notification {
    void send(String message, String recipient);
}
