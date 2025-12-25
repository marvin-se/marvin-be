package com.marvin.campustrade.data.dto.message;

import com.marvin.campustrade.data.dto.ProductDTO;
import com.marvin.campustrade.data.dto.message.LastMessageDTO;
import com.marvin.campustrade.data.entity.Message;
import com.marvin.campustrade.data.entity.Product;
import com.marvin.campustrade.data.entity.Users;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDTO {
    private Long id;
    private String username;
    private ProductDTO.Response product;
    private Long userId;              // other participant
    private LastMessageDTO lastMessage;
    private List<MessageDTO> messages;
    private String imageUrl;
}
