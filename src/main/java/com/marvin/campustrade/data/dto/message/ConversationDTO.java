package com.marvin.campustrade.data.dto.message;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.dto.message.LastMessageDTO;
import com.marvin.campustrade.data.entity.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private ProductDTO.Response product;
    private Long userId;              // other participant
    private LastMessageDTO lastMessage;
}
