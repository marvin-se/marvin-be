

package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;





@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {


    private final MessageService messageService;
    @GetMapping
    public ResponseEntity<ConversationList> getConversationList(){
        return ResponseEntity.ok(messageService.getConversationList());

    }
    @GetMapping("/conversations/{otherUserId}/{productId}")
    public ConversationDTO getConversation(
            @PathVariable Long otherUserId,
            @PathVariable Long productId
    ) {
        return messageService.getConversation(otherUserId, productId);
    }


    @DeleteMapping("/conversations/{otherUserId}/{productId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long otherUserId,
            @PathVariable Long productId
    ) {
        messageService.deleteConversation(otherUserId, productId);
        return ResponseEntity.noContent().build();
    }


}






