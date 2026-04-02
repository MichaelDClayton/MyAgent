package com.concierge.backend.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import com.concierge.backend.model.ChatRequest;
import com.concierge.backend.model.ChatResponse;
import com.concierge.backend.service.ChatProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;


@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    private WebTestClient webTestClient;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatProducerService chatProducerService;
    private ChatRequest testChatRequest;

    @BeforeEach
    void setUp() {
        // Manually bind the WebTestClient to MockMvc
        webTestClient = MockMvcWebTestClient.bindTo(mockMvc).build();
        ChatResponse testChatResponse = new ChatResponse("Message received and queued for processing!");
        testChatRequest = new ChatRequest("Hello World!");

    }

    @Test
    void testChatController_response_is_ok() throws Exception{
        doNothing().when(chatProducerService).sendMessage(anyString());
           webTestClient.post()
                   .uri("/api/chat")
                   .header("Accept","application/json")
                   .bodyValue(testChatRequest)
                   .exchange()
                   .expectStatus().isOk();
    }

    @Test
    void testChatController_assert_response(){
        webTestClient.post()
                .uri("/api/chat")
                .header("Accept","application/json")
                .bodyValue(testChatRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.reply").isEqualTo("Message received and queued for processing!");
    }
}
