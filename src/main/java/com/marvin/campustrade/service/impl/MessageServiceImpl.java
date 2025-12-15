package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.message.ConversationDTO;
import com.marvin.campustrade.data.dto.message.ConversationList;
import com.marvin.campustrade.data.dto.message.LastMessageDTO;
import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Message;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ConversationMapper;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.repository.ConversationRepository;
import com.marvin.campustrade.repository.MessageRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.MessageService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;

    @Override
    public ConversationList getConversationList() {
        Users user = userService.getCurrentUser();

        List<Conversation> conversations =
                conversationRepository.findByUser1_IdOrUser2_Id(
                        user.getId(),
                        user.getId()
                );

        List<ConversationDTO> conversationDTOS = new ArrayList<>();

        for (Conversation conversation : conversations) {

            ConversationDTO dto =
                    conversationMapper.toConversationDTO(conversation, user.getId());

            messageRepository
                    .findTopByConversationIdOrderBySentAtDesc(conversation.getId())
                    .ifPresent(lastMessage -> {
                        dto.setLastMessage(
                                LastMessageDTO.builder()
                                        .id(lastMessage.getId())
                                        .senderId(lastMessage.getSender().getId())
                                        .content(lastMessage.getContent())
                                        .isRead(lastMessage.isRead())
                                        .sentAt(lastMessage.getSentAt())
                                        .build()
                        );
                    });

            conversationDTOS.add(dto);
        }

        return ConversationList.builder()
                .conversations(conversationDTOS)
                .numberOfConversations((long) conversationDTOS.size())
                .build();
    }
}