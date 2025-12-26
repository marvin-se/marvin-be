package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.message.SendMessageRequestDTO;
import com.marvin.campustrade.data.dto.message.SendMessageResponseDTO;
import com.marvin.campustrade.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void sendMessage_shouldSendMessageToConversationTopic() {
        // --------------------
        // GIVEN
        // --------------------
        SendMessageRequestDTO request = new SendMessageRequestDTO();
        request.setProductId(5L);
        request.setContent("Hello there!");
        request.setReceiverId(12L);

        Principal principal = () -> "testUser";

        SendMessageResponseDTO response = SendMessageResponseDTO.builder()
                .sent(true)
                .messageId(100L)
                .sentAt(LocalDateTime.now())
                .content("Hello there!")
                .conversationId(42L)
                .receiverId(12L)
                .build();

        when(messageService.sendMessage(request, principal))
                .thenReturn(response);

        // --------------------
        // WHEN
        // --------------------
        webSocketController.sendMessage(request, principal);

        // --------------------
        // THEN
        // --------------------
        verify(messageService).sendMessage(request, principal);

        ArgumentCaptor<String> destinationCaptor =
                ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor =
                ArgumentCaptor.forClass(Object.class);

        verify(messagingTemplate).convertAndSend(
                destinationCaptor.capture(),
                payloadCaptor.capture()
        );

        // Verify topic
        assertThat(destinationCaptor.getValue())
                .isEqualTo("/topic/conversations/42");

        // Verify payload
        SendMessageResponseDTO sentPayload =
                (SendMessageResponseDTO) payloadCaptor.getValue();

        assertThat(sentPayload.isSent()).isTrue();
        assertThat(sentPayload.getContent()).isEqualTo("Hello there!");
        assertThat(sentPayload.getConversationId()).isEqualTo(42L);
        assertThat(sentPayload.getReceiverId()).isEqualTo(12L);
        assertThat(sentPayload.getMessageId()).isEqualTo(100L);
    }
}
