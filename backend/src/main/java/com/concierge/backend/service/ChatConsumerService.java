package com.concierge.backend.service;

import com.concierge.backend.model.ChatRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatConsumerService {

    @KafkaListener(topics = "bookings", groupId = "concierge-group")
    public void consume(ChatRequest request) {
        // Since your ChatRequest uses record-style accessors or getters
        String messageContent = request.message();

        System.out.println("📥 Received message from Kafka: " + messageContent);

        // This is where you would trigger your AI logic or business workflow
    }
}