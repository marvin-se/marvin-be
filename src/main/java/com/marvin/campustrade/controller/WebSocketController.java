package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.message.SendMessageRequestDTO;
import com.marvin.campustrade.data.dto.message.SendMessageResponseDTO;
import com.marvin.campustrade.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor

public class WebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequestDTO request) {
        SendMessageResponseDTO response = messageService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + response.getConversationId(),
                response
        );
    }
}
