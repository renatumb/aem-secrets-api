package com.renatobonfim.aemblogbackend.notification;

public record WelcomeEmailRequestedEvent(String email, String name, String unsubscribeToken) {
}
