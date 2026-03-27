package com.concierge.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concierge.backend.model.ChatRequest;
import com.concierge.backend.model.ChatResponse;
import com.concierge.backend.service.ChatProducerService;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatProducerService producerService;

    public ChatController(ChatProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/chat")
    public ChatResponse handleChat(@RequestBody ChatRequest request) {

        // 1. Send to Kafka (Self-Healing Pipeline Entry)
        producerService.sendMessage(request.message());

        // 2. Return the UI response
        return new ChatResponse("Message received and queued for processing!");


    }
}