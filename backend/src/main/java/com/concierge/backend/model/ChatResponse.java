package com.concierge.backend.model;

import java.time.Instant;

public record ChatResponse(String reply, Instant timestamp) {
    // Convenience constructor so you don't have to manually pass 'now' every time
    public ChatResponse(String reply) {
        this(reply, Instant.now());
    }
}