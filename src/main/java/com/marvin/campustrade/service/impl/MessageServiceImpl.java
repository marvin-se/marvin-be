package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.common.IncludeInactiveUsers;
import com.marvin.campustrade.data.dto.message.*;
import com.marvin.campustrade.data.entity.Conversation;
import com.marvin.campustrade.data.entity.Message;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ConversationMapper;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.MessageService;
import com.marvin.campustrade.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.marvin.campustrade.data.mapper.MessageMapper;

import java.security.Principal;
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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MessageMapper messageMapper;
    private final ImageRepository imageRepository;


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

            dto.setImageUrl(imageRepository.findByProduct(conversation.getProduct())
                    .stream().findFirst().get().getImageUrl()
            );
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

    @Override
    @Transactional
    public SendMessageResponseDTO sendMessage(SendMessageRequestDTO request, Principal principal) {

        String email = principal.getName();

        Users sender = userRepository
                .findByEmail(email)
                .orElseThrow();
        //Users sender = userRepository.findById(2L).orElseThrow();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Conversation conversation =
                conversationRepository
                        .findByUsersAndProduct(
                                sender.getId(),
                                request.getReceiverId(),
                                product.getId()
                        )
                        .orElseGet(() ->
                                createConversation(sender, userRepository.findById(request.getReceiverId()).get(), product)
                        );

        //Users receiver = conversation.getUser1().getId().equals(sender.getId())? conversation.getUser2(): conversation.getUser1();


        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(userRepository.findById(request.getReceiverId()).get());
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        Message saved = messageRepository.save(message);

        return SendMessageResponseDTO.builder()
                .sent(true)
                .messageId(saved.getId())
                .conversationId(conversation.getId())
                .sentAt(saved.getSentAt())
                .content(saved.getContent())
                .receiverId(userRepository.findById(request.getReceiverId()).get().getId())
                .build();
    }

    private Conversation createConversation(Users sender, Users receiver, Product product) {
        Conversation conversation = new Conversation();
        conversation.setUser1(sender);
        conversation.setUser2(receiver);
        conversation.setProduct(product);
        conversation.setCreatedAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Override
    @Transactional
    public ConversationDTO getConversation(Long otherUserId, Long productId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Sender user not found"));

        Users otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Conversation conversation =
                conversationRepository
                        .findByUsersAndProduct(
                                currentUser.getId(),
                                otherUser.getId(),
                                productId
                        )
                        .orElseGet(() -> createConversation(
                                currentUser,
                                otherUser,
                                product
                        ));

        List<Message> messages =
                messageRepository.findByConversationOrderBySentAtAsc(conversation);

        ConversationDTO dto =
                conversationMapper.toConversationDTO(
                        conversation,
                        currentUser.getId()
                );

        List<MessageDTO> messageDTOs =
                messages.stream()
                        .map(messageMapper::toDTO)
                        .toList();

        dto.setMessages(messageDTOs);

        dto.setImageUrl(imageRepository.findByProduct(conversation.getProduct())
                .stream().findFirst().get().getImageUrl()
        );

        return dto;
    }

    @Override
    @Transactional
    public void deleteConversation(Long otherUserId, Long productId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Users otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Other user not found"));

        Conversation conversation =
                conversationRepository
                        .findByUsersAndProduct(
                                currentUser.getId(),
                                otherUser.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Conversation not found")
                        );

        boolean isParticipant =
                conversation.getUser1().getId().equals(currentUser.getId())
                        || conversation.getUser2().getId().equals(currentUser.getId());

        if (!isParticipant) {
            throw new RuntimeException("You are not allowed to delete this conversation");
        }

        messageRepository.deleteByConversation(conversation);

        conversationRepository.delete(conversation);
    }
}
