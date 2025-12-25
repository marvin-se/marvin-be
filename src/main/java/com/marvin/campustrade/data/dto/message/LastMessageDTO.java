package com.marvin.campustrade.data.dto.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LastMessageDTO {
    private Long id;
    private Long senderId;
    private String content;
    private boolean isRead;
    private LocalDateTime sentAt;
}
