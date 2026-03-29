package com.concierge.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.concierge.backend.model.FailedMessage;
import com.concierge.backend.repository.FailedMessageRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
@Service
public class ChatProducerService {
    @Autowired
    private FailedMessageRepository repository;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "bookings";

    public ChatProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retry(name = "kafkaProducerRetry") // 1. Try a few times
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "kafkaFallback") // 2. If it still fails, trip the breaker
    public void sendMessage(String message) {
        try {
            // Use .get(5, TimeUnit.SECONDS) to ensure we don't hang forever
            kafkaTemplate.send(TOPIC, message).get(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            // We THROW the error so Resilience4j knows it failed
            throw new RuntimeException("Kafka unreachable", e);
        }
    }

    public void kafkaFallback(String message, Throwable t) {
        // 3. ONLY save to the DB here
        System.out.println("Fallback Triggered! Reason: "+t.getMessage());
        System.out.println("🛡️ Fallback: Saving to Postgres to prevent data loss.");
        FailedMessage failed = new FailedMessage();
        failed.setPayload(message);
        failed.setStatus("PENDING_RETRY");
        repository.save(failed);
    }
}