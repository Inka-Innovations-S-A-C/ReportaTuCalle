package com.reportatucalle.shared.event;

public interface EventListener<T> {
    void onEvent(T event);
}
