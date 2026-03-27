package com.concierge.backend.model;

public record ChatRequest(String message) {
    // Basic validation: ensure the message isn't null or blank
    public ChatRequest {
        if (message == null || message.isBlank()) {
            message = "empty message";
        }
    }
}