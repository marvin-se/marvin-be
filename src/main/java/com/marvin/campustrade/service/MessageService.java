package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.dto.message.SendMessageRequestDTO;
import com.marvin.campustrade.data.dto.message.SendMessageResponseDTO;

import java.security.Principal;

public interface MessageService {
    ConversationList getConversationList();
    SendMessageResponseDTO sendMessage(SendMessageRequestDTO request, Principal principal);
}
