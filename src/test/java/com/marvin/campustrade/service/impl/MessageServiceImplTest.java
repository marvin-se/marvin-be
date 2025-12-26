package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.message.*;
import com.marvin.campustrade.data.entity.*;
import com.marvin.campustrade.data.mapper.ConversationMapper;
import com.marvin.campustrade.data.mapper.MessageMapper;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private UserService userService;
    @Mock private ConversationMapper conversationMapper;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private MessageMapper messageMapper;
    @Mock private ImageRepository imageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Users currentUser;
    private Users otherUser;
    private Product product;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        currentUser = new Users();
        currentUser.setId(1L);
        currentUser.setEmail("me@itu.edu.tr");
        currentUser.setIsActive(true);

        otherUser = new Users();
        otherUser.setId(2L);
        otherUser.setIsActive(true);

        product = new Product();
        product.setId(10L);

        conversation = new Conversation();
        conversation.setId(100L);
        conversation.setUser1(currentUser);
        conversation.setUser2(otherUser);
        conversation.setProduct(product);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("me@itu.edu.tr", null)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --------------------------------------------------
    // getConversationList
    // --------------------------------------------------

    @Test
    void getConversationList_returnsEmpty_whenNoConversations() {

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(conversationRepository.findByUser1_IdOrUser2_Id(1L, 1L))
                .thenReturn(List.of());

        ConversationList result = messageService.getConversationList();

        assertEquals(0, result.getNumberOfConversations());
        assertTrue(result.getConversations().isEmpty());
    }

    @Test
    void getConversationList_success() {

        Message lastMessage = new Message();
        lastMessage.setConversation(conversation);
        lastMessage.setSender(otherUser);
        lastMessage.setContent("hi");
        lastMessage.setSentAt(LocalDateTime.now());
        lastMessage.setRead(false);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(conversationRepository.findByUser1_IdOrUser2_Id(1L, 1L))
                .thenReturn(List.of(conversation));

        when(messageRepository.findLastMessagesForConversations(List.of(100L)))
                .thenReturn(List.of(lastMessage));

        when(conversationMapper.toConversationDTO(conversation, 1L))
                .thenReturn(new ConversationDTO());

        when(imageRepository.findByProduct(product))
                .thenReturn(List.of());

        ConversationList result = messageService.getConversationList();

        assertEquals(1, result.getNumberOfConversations());
        verify(conversationMapper).toConversationDTO(conversation, 1L);
    }

    // --------------------------------------------------
    // sendMessage
    // --------------------------------------------------

    @Test
    void sendMessage_createsConversationAndMessage() {

        SendMessageRequestDTO request = new SendMessageRequestDTO();
        request.setReceiverId(2L);
        request.setProductId(10L);
        request.setContent("hello");

        Principal principal = () -> "me@itu.edu.tr";

        when(userRepository.findByEmail("me@itu.edu.tr"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(otherUser));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(conversationRepository.findByUsersAndProduct(1L, 2L, 10L))
                .thenReturn(Optional.empty());

        when(conversationRepository.save(any()))
                .thenReturn(conversation);

        when(messageRepository.save(any()))
                .thenAnswer(invocation -> {
                    Message m = invocation.getArgument(0);
                    m.setId(50L);
                    return m;
                });

        SendMessageResponseDTO response =
                messageService.sendMessage(request, principal);

        assertTrue(response.isSent());
        assertEquals(50L, response.getMessageId());
        assertEquals(100L, response.getConversationId());
    }


    // --------------------------------------------------
    // getConversation
    // --------------------------------------------------

    @Test
    void getConversation_returnsConversationWithMessages() {

        Message message = new Message();
        message.setContent("hello");

        when(userRepository.findByEmail("me@itu.edu.tr"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(otherUser));

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(conversationRepository.findByUsersAndProduct(1L, 2L, 10L))
                .thenReturn(Optional.of(conversation));

        when(messageRepository.findByConversationOrderBySentAtAsc(conversation))
                .thenReturn(List.of(message));

        when(conversationMapper.toConversationDTO(conversation, 1L))
                .thenReturn(new ConversationDTO());

        when(messageMapper.toDTO(message))
                .thenReturn(new MessageDTO());

        Image img = new Image();
        img.setImageUrl("url");

        when(imageRepository.findByProduct(product))
                .thenReturn(List.of(img));

        ConversationDTO dto =
                messageService.getConversation(2L, 10L);

        assertNotNull(dto);
        verify(messageRepository).markMessagesAsRead(conversation, 1L);
    }

    // --------------------------------------------------
    // deleteConversation
    // --------------------------------------------------

    @Test
    void deleteConversation_success() {

        when(userRepository.findByEmail("me@itu.edu.tr"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(otherUser));

        when(conversationRepository.findByUsersAndProduct(1L, 2L, 10L))
                .thenReturn(Optional.of(conversation));

        messageService.deleteConversation(2L, 10L);

        verify(messageRepository).deleteByConversation(conversation);
        verify(conversationRepository).delete(conversation);
    }
}
