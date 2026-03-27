package com.concierge.backend.service;

import org.springframework.kafka.annotation.KafkaListener;

public class DeadLetterLIstener {
    @KafkaListener(topics = "bookings.DLT", groupId = "concierge-repair-group")
    public void listenToDLT(String message) {
        System.err.println("🚨 POISON PILL DETECTED: " + message);
        System.err.println("Self-healing: Message moved to DLT. Main pipeline is still healthy!");
    }
}
