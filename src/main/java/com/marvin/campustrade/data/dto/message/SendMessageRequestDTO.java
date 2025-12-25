package com.marvin.campustrade.data.dto.message;
import com.marvin.campustrade.data.entity.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SendMessageRequestDTO {
    private Long productId;
    private String content;
    private Long receiverId;

}
