package com.concierge.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;

@Configuration
public class EventMonitor {
    @Bean
public RegistryEventConsumer<CircuitBreaker> myCircuitBreakerLog() {
    return new RegistryEventConsumer<>() {
        @Override
        public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
            entryAddedEvent.getAddedEntry().getEventPublisher()
                .onStateTransition(event -> {
                    System.out.println("🔄 CIRCUIT BREAKER STATE CHANGE: " + event.getStateTransition());
                });
        }
        @Override
        public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {}
        @Override
        public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {}
    };
}
}
