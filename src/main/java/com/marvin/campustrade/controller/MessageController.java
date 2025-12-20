package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @GetMapping("")
    public ResponseEntity<ConversationList> getConversationList(){
        return ResponseEntity.ok(messageService.getConversationList());

    }
}
