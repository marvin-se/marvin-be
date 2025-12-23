package com.marvin.campustrade.data.dto.message;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SendMessageResponseDTO {
    private boolean sent;
    private Long messageId;
    private LocalDateTime sentAt;
    private String content;
    private Long conversationId;
    private Long receiverId;
}