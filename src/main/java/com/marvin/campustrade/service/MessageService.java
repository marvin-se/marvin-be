package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.dto.message.SendMessageRequestDTO;
import com.marvin.campustrade.data.dto.message.SendMessageResponseDTO;

import java.security.Principal;

public interface MessageService {
    ConversationList getConversationList();
    ConversationDTO getConversation(Long otherUserId, Long productId);
    void deleteConversation(Long otherUserId, Long productId);
    SendMessageResponseDTO sendMessage(SendMessageRequestDTO request, Principal principal);
}
