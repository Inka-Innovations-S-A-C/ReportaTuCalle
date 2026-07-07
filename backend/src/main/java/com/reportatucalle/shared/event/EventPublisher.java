package com.reportatucalle.shared.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class EventPublisher<T> {
    private final List<EventListener<T>> listeners = new ArrayList<>();

    public void subscribe(EventListener<T> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(EventListener<T> listener) {
        listeners.remove(listener);
    }

    public void publish(T event) {
        for (EventListener<T> listener : listeners) {
            listener.onEvent(event);
        }
    }
}
