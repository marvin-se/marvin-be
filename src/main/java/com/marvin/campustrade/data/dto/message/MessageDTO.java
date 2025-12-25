package com.marvin.campustrade.data.dto.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
}
