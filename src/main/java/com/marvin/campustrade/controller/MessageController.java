

package com.marvin.campustrade.controller;
import com.marvin.campustrade.data.dto.message.SendMessageResponseDTO;

import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.dto.message.SendMessageRequestDTO;
import com.marvin.campustrade.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;




@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    private final MessageService messageService;
    @GetMapping("")
    public ResponseEntity<ConversationList> getConversationList(){
        return ResponseEntity.ok(messageService.getConversationList());

    }


    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequestDTO request) {
        SendMessageResponseDTO response = messageService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + response.getConversationId(),
                response
        );
    }

}

