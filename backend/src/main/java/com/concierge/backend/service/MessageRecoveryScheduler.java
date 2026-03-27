package com.concierge.backend.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.concierge.backend.model.FailedMessage;
import com.concierge.backend.repository.FailedMessageRepository;

import java.util.List;

@Service
public class MessageRecoveryScheduler {

    private final FailedMessageRepository repository;
    private final ChatProducerService producerService;
    private final CircuitBreaker kafkaCircuitBreaker;

    public MessageRecoveryScheduler(FailedMessageRepository repository, 
                                    ChatProducerService producerService,
                                    CircuitBreakerRegistry registry) {
        this.repository = repository;
        this.producerService = producerService;
        // Connect to the same 'kafkaProducer' instance from your YAML
        this.kafkaCircuitBreaker = registry.circuitBreaker("kafkaProducer");
    }

    @Scheduled(fixedDelay = 30000) // Runs every 30 seconds
    public void retryFailedMessages() {
        // Only attempt recovery if the "Door is Closed" (Kafka is Healthy)
        if (kafkaCircuitBreaker.getState() == CircuitBreaker.State.CLOSED) {
            List<FailedMessage> pending = repository.findByStatus("PENDING_RETRY");
            
            if (!pending.isEmpty()) {
                System.out.println("🔄 Self-Healing: Found " + pending.size() + " messages to re-process.");
                
                for (FailedMessage msg : pending) {
                    try {
                        producerService.sendMessage(msg.getPayload());
                        msg.setStatus("PROCESSED");
                        repository.save(msg);
                    } catch (Exception e) {
                        System.err.println("❌ Recovery failed for message ID " + msg.getId());
                        break; // Stop loop if Kafka hits another error
                    }
                }
            }
        }
    }
}
