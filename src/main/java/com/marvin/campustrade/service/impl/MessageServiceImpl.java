package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.common.IncludeInactiveUsers;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@IncludeInactiveUsers
public class MessageServiceImpl implements MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ConversationMapper conversationMapper;

    @Transactional
    @Override
    public ConversationList getConversationList() {

        Users currentUser = userService.getCurrentUser();

        List<Conversation> conversations =
                conversationRepository.findByUser1_IdOrUser2_Id(
                        currentUser.getId(),
                        currentUser.getId()
                );

        if (conversations.isEmpty()) {
            return ConversationList.builder()
                    .conversations(Collections.emptyList())
                    .numberOfConversations(0L)
                    .build();
        }

        List<Long> conversationIds = conversations.stream()
                .map(Conversation::getId)
                .toList();

        List<Message> lastMessages =
                messageRepository.findLastMessagesForConversations(conversationIds);

        Map<Long, Message> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        m -> m.getConversation().getId(),
                        m -> m
                ));

        List<ConversationDTO> dtos = new ArrayList<>();

        for (Conversation conversation : conversations) {

            // Determine other participant
            Users otherUser = conversation.getUser1().equals(currentUser)
                    ? conversation.getUser2()
                    : conversation.getUser1();

            // Optional: hide inactive users (remove if not desired)
            if (!otherUser.getIsActive()) {
                continue;
            }

            ConversationDTO dto =
                    conversationMapper.toConversationDTO(conversation, currentUser.getId());

            Message lastMessage = lastMessageMap.get(conversation.getId());

            if (lastMessage != null) {

                boolean readByCurrentUser =
                        lastMessage.isRead() ||
                                lastMessage.getSender().getId().equals(currentUser.getId());

                dto.setLastMessage(
                        LastMessageDTO.builder()
                                .id(lastMessage.getId())
                                .senderId(lastMessage.getSender().getId())
                                .content(lastMessage.getContent())
                                .isRead(readByCurrentUser)
                                .sentAt(lastMessage.getSentAt())
                                .build()
                );
            }

            dtos.add(dto);
        }

        dtos.sort(
                Comparator.comparing(
                        dto -> dto.getLastMessage() != null
                                ? dto.getLastMessage().getSentAt()
                                : LocalDateTime.MIN,
                        Comparator.reverseOrder()
                )
        );

        return ConversationList.builder()
                .conversations(dtos)
                .numberOfConversations((long) dtos.size())
                .build();
    }
}
