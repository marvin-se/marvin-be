package com.marvin.campustrade.data.dto.message;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationList {
    private List<ConversationDTO> conversations;
    private Long numberOfConversations;
}
