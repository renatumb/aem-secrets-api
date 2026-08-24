package com.renatobonfim.aemblogbackend.notification;

public record ContactEmailRequestedEvent(String name, String email, String message) {
}
